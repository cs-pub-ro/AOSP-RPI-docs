# Lab 04. Implementing GPIO functionality

## 4.3. Testing GPIOs

In order to test whether your new GPIO control service works, you will use
either [your app (if ready)](../03-hal-service/05-app-integration.md), or the
[`service call`](../03-hal-service/04-adb-services.md) adb shell utility.

Note that the `portn` parameter inside your AIDL functions represents the GPIO
index on the Raspberry Pi. Check out [a pinout table](https://pinout.xyz/) for
the available pins! Make sure to properly connect the LED's ground to one of the
RPI's ground pins, and each color to a free GPIO pin (note their numbers because
you'll need to set each of them to 1/0 to on/off the LED).

There is one thing to fix (we'll do it manually using `adb`): if you did proper
error code reporting, you will see your service receives EPERM (Permission
Denied) error when trying to open `gpiochip0`. This is because your service
[runs as system user](../03-hal-service/files/gpio/aidl/default/android.hardware.gpio-service.rc),
not as root, and it doesn't have the proper UNIX permissions (`chmod`)!

Here's an easy fix:

```sh
adb root   # re-open ADB server as root
adb shell chmod 777 /dev/gpiochip0
```

Of course, the proper solution would be to customize gpiochip device permissions
by editing AOSP `init.rc` service and rebuilding the system partition:

```sh
vim system/core/rootdir/init.rc
# add `chmod 777 /dev/gpiochip0` somewhere in the "on boot" section
```

Try again! If your implementation is correct, it should work!
