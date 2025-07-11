# Lab 03. Android HAL Service

## 3.2. A Default Service

Create a `default/` directory inside `hardware/interfaces/gpio/aidl/`.

It's a good time to showcase our final file structure:

```
➜ tree hardware/interfaces/gpio/
hardware/interfaces/gpio/
└── aidl
    ├── aidl_api
    │   └── android.hardware.gpio
    │       ├── 1/android/hardware/gpio/IGpio.aidl
    │       └── current/android/hardware/gpio/IGpio.aidl
    ├── android/hardware/gpio
    │   └── IGpio.aidl
    ├── Android.bp
    └── default   # warninig: spoilers below!
        ├── Android.bp
        ├── android.hardware.gpio-service.rc
        ├── android.hardware.gpio-service.xml
        ├── Gpio.cpp
        ├── Gpio.h
        └── service.cpp
```

First, note that we didn't create the `aidl_api` sub-directory, but remember
what we did? This was `*-update-api` and `*-freeze-api` scripts' result!

Let's copy the rest of the files in the `default/`. You can find them
[here](./files/gpio/aidl/default/) (if you haven't already).

Let's take a look at each of those components:

### GPIO Binder implementation

Remember the `BnGpio.h` abstract class we've seen generated from the AIDL?

We need to implement all of our declared methods, and
[`Gpio.cpp`](./files/gpio/aidl/default/Gpio.cpp) +
[`Gpio.h`](./files/gpio/aidl/default/Gpio.h) is the place to do it.

The naming conventions are roughly
[documented here](https://source.android.com/docs/core/architecture/aidl/aidl-backends).

As your first coding task for today, place logging calls inside both
`setGpio/getGpio` methods so we can test whether they are actually called at
runtime.

Check out the official
[NDK logging documentation here](https://developer.android.com/ndk/guides/stable_apis#logging),
which sends us to
[logging.h](https://cs.android.com/android/platform/superproject/+/master:system/libbase/include/android-base/logging.h)
implementing a usable C++ stream class. Use the `INFO` log level.

### The service's main entrypoint

A service is an executable. Check out
[`service.cpp`](./files/gpio/aidl/default/service.cpp) to see its `main()`
implementation. Copied from a sample, ofc!

Note it configures to have max. 0 threads in pool, so we don't have to do any
thread synchronization stuff!

### The service .rc file

Next, in
[android.hardware.gpio-service.rc](./files/gpio/aidl/default/android.hardware.gpio-service.rc),
we declared the service to start at boot. This is a resource configuration file
for Android's custom init system.

### The Vendor Interface XML manifest (Vintf)

The bulk of AOSP's components are glued together with XMLs.

In [this case](./files/gpio/aidl/default/android.hardware.gpio-service.rc), it's
a vendor interface definition file to make our versioned AIDL usable from vendor
partition components. Otherwise, Android's init will complain and refuse to run
our HAL service!

### Soong build definition

Finally, the last piece to bind them all together is the `Android.bp` file.

Note: it's different from the one in the parent directory: this is for building
the service and will be included by the vendor device (the _brcm/rpi5_ model).

Note that we declare the shared libraries we link to, notably, our
`android.hardware.gpio-V1-ndk` that was generated from the AIDL file!
