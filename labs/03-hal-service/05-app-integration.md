# Lab 03. Android HAL Service

## 3.5. Integrating with Android Apps

We must now go back to Android App development and write some code to actually
call or new HAL API!

Here's
[the official documentation](https://developer.android.com/develop/background-work/services/aidl#Calling)
for calling AIDL services, and here's a random example of using a custom HAL
service:
https://github.com/alvenan/AOSPbenchmark/blob/main/timer_manager/src/vendor/alvenan/timermanager/TimerManager.java

The problem is: you cannot use `android.os.ServiceManager` without having system
app privileges..

There are solutions to abstract away the service calls by creating another
service for a new Android framework API, but it would require
[exporting the AOSP as a new Android SDK](https://kwagjj.wordpress.com/2017/10/27/building-custom-android-sdk-from-aosp-and-adding-it-to-android-studio/),
but it requires lots of extra work.

An easier workaround is to build this app in-tree on AOSP's build directory by
copying your app to new `packages/apps` inside the Android source directory.

You will then need to write a new `Android.bp` Soong blueprint to give it
instructions on how to build your app. Check
[the apps/ClassicSummerApp directory](../../apps/ClassicSummerApp) for an
example.

Note that your `manifest.xml` should contain the `package` attribute, so make
sure yo modify it as well:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.aospi.summer.classic" >
    ...
```

Finally, make sure to include your application inside
`device/brcm/rpi5/device.mk` (by app directory name), e.g.:

```mk
PRODUCT_PACKAGES += ClassicSummerApp
```

After building the entire android
(`make ... <with everything just to be sure>`), you may find the APK file inside
`out/target/product/rpi5/system/product/app/ClassicSummerApp/ClassicSummerApp.apk`
(i.e., a path on the generated System partition -- that's also where installed
Android app files reside!).

Download the `.apk` using `scp` and install it using
`adb install <path-to-apk-file>`.

GL HF!
