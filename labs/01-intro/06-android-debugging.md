# Lab 01. Intro to AOSP. Building & testing on RPI5

## 1.6. Debugging Android

It's time to relax and play with our new Android:

### ADB

The Android DeBugger is an integrated Android development tool which allows you
to debug your Android device and apps.

[Install it if you haven't already!](https://developer.android.com/tools/adb)

Since you connected the Raspberry PI device over USB-C to a Hub and then to your
laptop, you should then see a new device under your OS. Run `adb devices -l` to
see if it was properly detected.

### Obtaining shell

Since we have a debug build, our device is _rooted_:

```sh
adb shell
ls -l /system
ls -l /data
```

Now, can you modify files in /system? Why?

Let's use `logcat` to view real-time system logs:

```sh
adb logcat
```

Open an app on the device. Can you see log messages related to the app starting?
Try filtering the logs to see messages from a specific app tag since there are
numerous HAL-related errors :D .

Moreover, if you wish to install an application from `.apk` (first,
[find something open-source](https://f-droid.org/en/packages/net.osmand.plus/)
to download -- and make sure it's for the correct CPU architecture, `arm64`!),
then:

```sh
adb install <path_to_apk_file.apk>
adb uninstall <package_name>  # (e.g., com.example.app)
```

### Screen capture / recording

Finally, some fun stuff:

```sh
# screenshot!
adb shell screencap /sdcard/capture.png
# download it to your system:
adb pull /sdcard/capture.png
# same to record video!
adb shell screenrecord /sdcard/cast.mp4
adb pull /sdcard/cast.mp4
```
