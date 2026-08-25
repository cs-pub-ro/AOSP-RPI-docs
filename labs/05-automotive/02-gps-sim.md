# Lab 05. Android Automotive & Location Services

## 5.3. GpsSim - GPS Location Simulator

RPi5 has no GPS. But Android's `LocationManager` supports **test
providers**: a privileged app can invent a GPS provider and feed it any
fixes. That's how the emulator's "geo fix" works under the hood — you're
now building the on-device version.

### How it works

Check out the skeleton source code at [./files/GpsSim/](./files/GpsSim/java/

Here are the basic test provider API methods we use:

```kotlin
val lm = context.getSystemService(LocationManager::class.java)
lm.addTestProvider(LocationManager.GPS_PROVIDER, ...)
lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
// then, for each simulated event, emit a location fix:
lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
```

- `addTestProvider` needs **signature-level permission**:
  `android.permission.MOCK_LOCATION` (declared inside `AndroidManifest.xml`).
- `setTestProviderEnabled` enables/disables the simulator;
- every ~1s tick, we compute the next position along the GPX track (`route-cluj.gpx`
  resource file) and push the location object using `setTestProviderLocation` 
  with the correct `speed`, `bearing` and `elapsedRealtimeNanos`.

Fill out the `TODO`s and build the app (`m GpsSim` should report success)!

### Build concerns

Note that `GpsSim` must be built as a **platform app**: in-tree, under
`packages/apps/GpsSim`, signed with the platform key (automatically when building 
the image).

The `MOCK_LOCATION` allowlist is shipped with the image by the
`prebuilt_etc` module in `Android.bp` (`privapp_whitelist.xml` →
`/system/etc/permissions/com.aospi.gpsim.xml`) — nothing extra to do.

Rebuild + flash the car image (actually, just the vendor partition would suffice). 

Alternative way to upload just the modified APK as privileged app using `adb`:

```sh
# find & obtain the apk on the build server, usually at:
# out/target/product/rpi5/system/priv-app/GpsSim/GpsSim.apk
# copy it to your PC using scp, then:
adb root && adb remount  # make Android partitions writable!
# uninstall the old one, just in case
adb uninstall com.aospi.gpsim
# upload & install the apk as privileged app:
adb push ~/Downloads/GpsSim.apk /system/priv-app/GpsSim/
# check privileged using package manager:
adb shell pm dump com.aospi.gpsim | grep -E "codePath|flags=|seinfo" | head
```

### Running the simulator 

Start your GpsSim application on the automotive RPi5 and click Play!

Debugging toolkit:

- Check the application logs using `logcat -e GpsSim` (if it crashes);
- verify with `adb shell dumpsys location | grep -i provider` and by running
`AdasLocationTestApp` — it should show your position even before any 
playback.

If `logcat` lists an access denied exception regarding `MOCK_LOCATION`, force-grant 
the permission via `adb`:

```sh
adb root  # if you weren't already
adb shell appops set com.aospi.gpsim MOCK_LOCATION allow
```

## Stretch: a real GPS HAL

`setTestProviderLocation` is an app-layer shortcut. The "real" stack is a
HAL service under `hardware/interfaces/gnss/`. For the stretch:

1. Look at `hardware/interfaces/gnss/aidl/android/hardware/gnss/IGnss.aidl` - 
   the callbacks your HAL would push (`setCallback`);
2. Discussion: could you build `gpsd`-style HAL using the Lab 3 pattern (AIDL +
  `service.cpp` + `.rc` + VINTF + sepolicy) that reads a GPX file and emits fixes
  at the HAL level?
  How about using [a real UART-based GPS
device](https://www.u-blox.com/en/positioning-chips-and-modules) emitting [standard NMEA 0183
location](https://en.wikipedia.org/wiki/NMEA_0183) points?
