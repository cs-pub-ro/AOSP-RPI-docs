# Lab 03. Android HAL Service

## 3.4. Testing in ADB

[Generate the image, download it to your PC & flash your SD card](../01-intro/05-live-raspberry.md).

After the Raspberry is live and running, connect a USB-C cable and enter
`adb shell`. We shall test our HAL service:

```sh
# within the ADB shell
service list | grep gpio
# should display something like:
#23      android.hardware.gpio.IGpio/default: [android.hardware.gpio.IGpio]

# also check the logs:
logcat -e Gpio
```

We can now use the
[`service call` sub-command](https://stackoverflow.com/questions/20227326/where-to-find-info-on-androids-service-call-shell-command)
to call our IPC service using AIDL-encoded messages (yep, really)! The syntax of
the command is:

```
service call <service_name> <func_id> <encoded arguments...>
```

For example, try running: `service call activity 1`. See the encoded
(binary-like) output? This is actually the format of the Binder IPC protocol
(called Parcel). It uses UTF-16 strings, which is why each character is 2-bytes
long :(

For our service, we must use `android.hardware.gpio.IGpio/default`, first
function (`1`) is the `setGpio` (in the `.aidl` order starting from _1_). But we
need to supply two parameters of type `int`.

The syntax for an argument is
`service call <name> <func_id> <data_type> <value> ...`.

We have two arguments, thus:

```sh
service call android.hardware.gpio.IGpio/default 1 i32 1 i32 1
# now check the logs and see if setGpio function was called in our implementation:
logcat -e Gpio
```

Also test the second function (only 1 parameter!). Don't worry about the return
value, it will be output in binary.
