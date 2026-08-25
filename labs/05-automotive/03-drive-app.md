# Lab 05. Android Automotive & Location Services

## 5.3. The `SummerDrive` Car App

A multi-screen Jetpack Compose app (same shape as Lab 02's `SummerApp`):

1. **Dashboard** (main display):
   - Live latitude/longitude,
   - Speed in km/h from `Location.speed` (m/s × 3.6),
   - Heading as a rotating compass arrow from `Location.bearing`.
2. **Trip screen**: odometer (sum of `Location.distanceTo` between fixes)
   and top speed — navigation with `androidx.navigation`.

### Location basics

```kotlin
val lm = getSystemService(LOCATION_SERVICE) as LocationManager

// ...
private val listener = object : LocationListener {
    override fun onLocationChanged(loc: Location) {
        prevFix?.let { distanceM.value += it.distanceTo(loc) }
        prevFix = loc
        topSpeed.value = maxOf(topSpeed.value, loc.speed.toDouble())
        lastFix.value = loc
    }
}
// ...

lm.requestLocationUpdates(
    LocationManager.GPS_PROVIDER, 1000L, 0f, listener, mainLooper
)
```

AOSP has no Play Services: plain `LocationManager` is all you have.
Declare `ACCESS_FINE_LOCATION` and request it at runtime.

While there is no GPS on the device, `Location` updates come from the
*test provider* installed by `GpsSim` - the app does not know
or care that it's simulated. That's the whole point of test providers.

### Bonuses (for vibe-coders!)

- Register `SummerDrive` as the car's **cluster app**: car products pick
  cluster apps through overlay configuration — find the
  `config_clusterActivityList` (or equivalent) entry in the AOSP source and
  add your component. This is pure config, no new code.
- Speed-limit mode: flag every second where speed > 50 km/h, show a
  "driving score" at the end of the loop. Teams race for the highest score.
