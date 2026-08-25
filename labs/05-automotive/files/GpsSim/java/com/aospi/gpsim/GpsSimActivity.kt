package com.aospi.gpsim

import android.app.Activity
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.util.Xml
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.xmlpull.v1.XmlPullParser
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Simulated GPS: installs the platform "gps" as a test provider and plays
 * a GPX track at a constant pace, one fix per second.
 *
 * Build in-tree at packages/apps/GpsSim (see Android.bp), then:
 *   adb shell dumpsys location | grep -i provider
 *
 * TODO: fill in the marked spots
 */
class GpsSimActivity : Activity() {

    private lateinit var lm: LocationManager
    private val points = ArrayList<FloatArray>() // each: lat, lon
    private var idx = 0.0        // fractional index along the track
    private var running = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var status: TextView
    private val mPerFix = 10f     // metres advanced per 1 s fix => 36 km/h

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            emitFix()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lm = getSystemService(LOCATION_SERVICE) as LocationManager

        // Yeah, this is an ugly, vibe-coded UI layout with 3 buttons
        status = TextView(this)
        loadTrack()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val play = Button(this).apply { text = "Play" }
        val stop = Button(this).apply { text = "Stop" }
        val teleport = Button(this).apply { text = "Teleport" }
        play.setOnClickListener {
            installProvider()
            running = true
            tick.run()
        }
        stop.setOnClickListener {
            running = false
            status.text = "stopped"
        }
        teleport.setOnClickListener {
            // TODO: jump to the start of the loop (see idx) then emit one fix
            status.text = "teleported"
        }
        layout.addView(status)
        layout.addView(play); layout.addView(stop); layout.addView(teleport)
        setContentView(layout)
    }

    private fun installProvider() {
        try {
            // TODO: call lm.addTestProvider()
            // REF: https://developer.android.com/reference/android/location/LocationManager#addTestProvider(java.lang.String,%20android.location.provider.ProviderProperties)
            // Note: DO NOT require network / satellite / cell etc.
        } catch (e: UnsupportedOperationException) {
            // already registered as test provider — nothing to do
        }
        // TODO: call lm.setTestProviderEnabled()
    }

    private fun loadTrack() {
        val parser = Xml.newPullParser()
        resources.openRawResource(R.raw.route_cluj).use { stream ->
            // the parse loop must run INSIDE use { } — the stream is
            // closed as soon as the block exits
            parser.setInput(stream, null)
            var ev = parser.eventType
            while (ev != XmlPullParser.END_DOCUMENT) {
                if (ev == XmlPullParser.START_TAG && parser.name == "trkpt") {
                    val lat = parser.getAttributeValue(null, "lat")!!.toFloat()
                    val lon = parser.getAttributeValue(null, "lon")!!.toFloat()
                    val first = points.firstOrNull()
                    if (first != null && first[0] == lat && first[1] == lon) {
                        // drop the closing duplicate; the loop wraps itself
                    } else {
                        points.add(floatArrayOf(lat, lon))
                    }
                }
                ev = parser.next()
            }
        }
        status.text = "loaded ${points.size} waypoints"
    }

    private fun emitFix() {
        val n = points.size
        val i0 = idx.toInt() % n
        val i1 = (i0 + 1) % n
        val f = (idx - idx.toInt()).toDouble()
        val a = points[i0]; val b = points[i1]
        val lat = (a[0] + (b[0] - a[0]) * f).toDouble()
        val lon = (a[1] + (b[1] - a[1]) * f).toDouble()

        val loc = Location(LocationManager.GPS_PROVIDER).apply {
            this.latitude = lat
            this.longitude = lon
            this.bearing = bearing(a[0].toDouble(), a[1].toDouble(), lat, lon)
            this.speed = mPerFix   // 10 m/s = 36 km/h
            this.accuracy = 2f
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = System.nanoTime()
        }
        lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, loc)
        status.text = String.format("fix %d/%d: %.5f, %.5f", i0, n, lat, lon)
        idx = (idx + mPerFix / segmentLength(i0)) % n
    }

    private fun bearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val dLon = Math.toRadians(lon2 - lon1)
        val y = sin(dLon) * cos(Math.toRadians(lat2))
        val x = cos(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) -
                sin(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(dLon)
        return Math.toDegrees(atan2(y, x)).toFloat().let { (it + 360) % 360 }
    }

    private fun segmentLength(i: Int): Float {
        val a = points[i]; val b = points[(i + 1) % points.size]
        return haversine(a[0].toDouble(), a[1].toDouble(),
                         b[0].toDouble(), b[1].toDouble()).toFloat()
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val h = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        return 2 * r * asin(sqrt(h))
    }
}
