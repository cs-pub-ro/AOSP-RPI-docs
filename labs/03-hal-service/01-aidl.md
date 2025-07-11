# Lab 03. Android HAL Service

## 3.1. AIDL

The
[Android Interface Definition Language](https://source.android.com/docs/core/architecture/aidl)
(AIDL) is an
[Interface Description Language](https://en.wikipedia.org/wiki/Interface_description_language)
used by all (most) Android components to describe their APIs and offer stability
guarantees.

Here's an example of a _calculator_ service:

```java
package com.example.myapp;

// Declare the interface
interface ICalculatorService {
    int add(int num1, int num2);
}
```

We can declare interfaces, data structures, enums and mostly any other
non-implementation types available in most OOP languages. Please take note of
the naming convention, especially the `I` prefix for _Interface_ (it is
mandatory to respect this).

There are `.aidl` files throughout Android's source code, especially inside the
`frameworks/` (defining the core Android APIs), `hardware/` (which we're mainly
interested into!) and `packages` (for app-provided services).

[Inter-process Communication](https://en.wikipedia.org/wiki/Inter-process_communication)
is the main method of communication between Android processes. Android mainly
uses the
[Binder IPC](https://medium.com/@ledubmurukp/android-system-ipc-mechanisms-3d3b46affa3c)
for this purpose, providing features such as security identification of the
caller process, message packing/unpacking, thread management, efficient passing
via shared memory between local processes and many other communication goodness.

### Hardware interfaces

Android's HAL interfaces and implementations use a well-defined file structure.

Most core hardware resides inside the `hardware/interfaces` source directory. In
here, you can find the `audio`, `power`, `camera`, `drm` (Direct Rendering
Manager responsible for graphics) and many other HALs you'd expect from a
abstract OS.

Unfortunately, for Android HAL, there are actually two API description languages
in use even today: the deprecated HIDL (_can you guess what H stands for?_)
which is being phased out, and the generic AIDL used throughout the rest of the
Android codebase. The developers started a long transition to phase out the
former but it hasn't finished yet.

So, if you take a look inside one of the hw components, we'll see this:

```txt
➜ ls -l hardware/interfaces/bluetooth
total 4.0K
drwxr-xr-x 4 admin builders 126 Jun 24 12:51 1.0
drwxr-xr-x 4 admin builders 109 Jun 24 12:51 1.1
drwxr-xr-x 6 admin builders 101 Jun 24 12:51 aidl        # <-- this is AIDL
drwxr-xr-x 3 admin builders  89 Jun 24 12:51 async
drwxr-xr-x 7 admin builders  80 Jun 24 12:51 audio
drwxr-xr-x 3 admin builders  18 Jun 24 12:51 finder
...
```

We notice that there is a single subdirectory called `aidl`, the rest are old
versions written in the old HIDL so we can safely ignore them.

### Building our HAL API

We propose to model our simple API for manipulating Raspberry PI GPIOs (General
Purpose Input/Output, i.e., those
[numerous electrical pins](https://pinout.xyz/) you see exposed on the board).

We will name our module simply `gpio` (it won't conflict with anything else).

So create the following subfolders (relative from the build dir):
`hardware/interfaces/gpio/aidl/`. You can change to this directory for now to
make it easier to see the structure.

Now prepare for an intricate file structure!

First, create another three subdirectories inside our `gpio/aidl` parent:
`android/hardware/gpio` (like the Java packaging convention for
`android.hardware.gpio` -- actually this is our AIDL package name!) finally
containing a [`IGpio.aidl`](./files/gpio/aidl/android/hardware/gpio/IGpio.aidl)
file with the following contents:

```java
/* wasn't kidding, this WAS the package name: */
package android.hardware.gpio;

/* we'll explain this Vintf stuff later... */
@VintfStability
interface IGpio {
  void setGpio(in int portn, in int state);
  int getGpio(in int portn);
}
```

Our interface has a `setGpio` method which we want to use to control the
_output_ voltage level for a GPIO, and a `getGpio` method to query the voltage
of an _input_ GPIO

We will also need to create a Soong Blueprint file containing build instructions
for our new AIDL... [fetch it from here](./files/gpio/Android.bp) (+ note the
relative paths in GitHub, they mirror the ones inside AOSP's
`hardware/interfaces`)!

Recall that AIDL is versioned... we'll need to freeze our API and generate the
Binder definitions (in Java, C++/NDK and Rust). Unfortunately, we must change
directory (or use a different terminal / tmux) back to `/build`, then:

```sh
# note: m is alias for make; we have many more created by build/envsetup.sh
m android.hardware.gpio-update-api
# afterwards, let's build our module with `mmm` (yep, it's triple!):
mmm hardware/interfaces/gpio
```

### Inspect the generated Binder stubs

Since we'll need to implement some interfaces in Java/C++/Rust (ofc, we'll take
the C++ choice), we must inspect the generated class code (how will be the
methods named? what are their arguments, etc.).

We don't know the path to these, so we'll use our Linux friend, `find`:

```
➜  find out/ -name gpio
out/soong/.intermediates/hardware/interfaces/gpio
out/soong/.intermediates/hardware/interfaces/gpio/aidl/android.hardware.gpio_interface/dump/android/hardware/gpio
out/soong/.intermediates/hardware/interfaces/gpio/aidl/android.hardware.gpio-V1-java-source/gen/android/hardware/gpio
out/soong/.intermediates/hardware/interfaces/gpio/aidl/android.hardware.gpio-V1-ndk-source/gen/android/hardware/gpio
out/soong/.intermediates/hardware/interfaces/gpio/aidl/android.hardware.gpio-V1-ndk-source/gen/include/aidl/android/hardware/gpio
out/soong/.intermediates/hardware/interfaces/gpio/aidl/android.hardware.gpio-V1-ndk-source/gen/include/staging/aidl/android/hardware/gpio
...
```

Hey, we recognize those! Since we'll be implementing a Binder class using NDK,
we explore the `ndk-source`-suffixed directories. Find and open the `BnGpio.h`
file in there (mind the case!)!

Explore the names of the class and methods:

```cpp
/* ... */
  ::ndk::ScopedAStatus setGpio(int32_t in_portn, int32_t in_state) override {
    return _impl->setGpio(in_portn, in_state);
  }
/* ... */
```

Now let's implement this as a service!
