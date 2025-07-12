package com.aospi.summer.classic

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.os.ServiceManager
import android.hardware.gpio.IGpio
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var gpioStates = arrayOf(false, false, false);
    /* The GPIO number of each GPIO */
    val gpioIds = arrayOf(18, 15, 23)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btns: Array<Button> = arrayOf(
            findViewById(R.id.btnRed),
            findViewById(R.id.btnGreen),
            findViewById(R.id.btnBlue)
        )
        var i = 0
        for (btn in btns) {
            val idx = i
            btn.setOnClickListener( {
                setGpio(gpioIds[idx], gpioStates[idx])
                // if (gpioState) { txt.setText("LED ON"); }
                // else { txt.setText("LED OFF"); }
                gpioStates[idx] = ! gpioStates[idx]
            } )
            i += 1
        }
    }

    fun setGpio(id: Int, state: Boolean) {
        var binder = ServiceManager.getService("android.hardware.gpio.IGpio/default")
        if (binder != null) {
            var service = IGpio.Stub.asInterface(binder)
            if (service != null) {
                service.setGpio(id, if (state) { 1 } else { 0 })
            } else {
                Log.e("GpioApp", "IGpio access error")
            }
        }
        else
            Log.e("GpioApp", "Binder access error!");
    }
}
