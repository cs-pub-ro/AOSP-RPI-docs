# Lab 01. Intro to AOSP. Building & testing on RPI5

## 1.4. Customizing AOSP.

### Source Code Structure

Navigating the AOSP source tree can be a bit difficult at first, so let's try to
clarify this first (enumerated from higher to lower levels):

- `packages/`: contains the stock Android applications that ship with the OS,
  such as the Launcher, Settings, Contacts, and SystemUI (which manages the
  status bar and navigation bar); if you wanted to modify a default app, I'd
  start here;
- `frameworks/`: implementation of all core Android libraries from an
  application developer's perspective, written in Java and C++; it contains some
  system services (like the
  [Activity Manager](https://developer.android.com/reference/android/app/ActivityManager),
  [Window Manager](https://developer.android.com/reference/android/view/WindowManager)
  and the Android APIs that apps use);
- `art/`: the
  [Android Runtime Environment (ART)](https://source.android.com/docs/core/runtim),
  Dalvik's successor, which is responsible for executing _Java_-like bytecode
  (_please don't sue, Oracle_); this directory includes the compiler and the
  runtime environment + standard APIs which all apps must use;
- `device/`: this holds all the device-specific configurations; each
  manufacturer (_vendor_) and device model has its own subdirectory (e.g.,
  `device/brcm/rpi5/` in our case!); it has the makefiles (`.mk`) / Soong
  blueprints (`.bp`) and configuration files needed to build AOSP for that
  specific piece of hardware;
- `bionic/`: this is
  [Android's custom C library](https://android.googlesource.com/platform/bionic/)
  (with math & dynamic linker); it's designed to be lightweight and efficient
  for embedded devices;
- `system/`: some low-level Linux system components; this includes core
  libraries, system daemons, and native executables that are fundamental to
  Android's operation but are not part of the C library itself (SELinux,
  Logging, `reboot` tool etc.);
- `hardware/`: contains the Hardware Abstraction Layers (HALs) which provide the
  standard interface that allows the higher-level Android framework to
  communicate with kernel drivers without knowing the specifics of the hardware
  implementation, e.g., audio, camera, and sensors here (this is where things
  will get interesting!);
- `kernel/`: yep, this is The Linux Kernel! which, fortunately, is prebuilt by
  Google, because this would have been an extra step for us ;)
- `build/`: contains the core of the Android build system; the envsetup.sh
  script and the core makefiles that orchestrate the entire compilation process
  are located here (mostly, the next-gen Soong & Bazel build systems);
- `out/`: hey, you've met it before! it's the directory containing the artifacts
  created during the build process, including the final system images
  (system.img, boot.img, etc.) that you flash to the device (though we have one
  more step!).

_Note: some were omitted for simplicity_

### Enabling the Raspberry Touch Display

As we've mentioned earlier, we need to alter some of the AOSP device
configuration in order to activate our fancy MIPI DSI display.

Fortunately, this is documented by
[this raspberry-vanilla wiki page](https://github.com/raspberry-vanilla/android_local_manifest/wiki/DSI-display),
just make sure to scroll down (we have the _official Raspberry Pi 7" Touch
Display 2_). As expected, we only need to touch the files inside the
device-specific directory: `device/brcm/rpi5/`!

### Personalize your build

Let's do one more small thing: customize our device's name (it's actually the
_product model_). Surely you know where to look... Can you find and edit it?

<details>
  <summary><i>[Spoiler Alert -- Do not click]</i></summary>

Try `device/brcm/rpi5/aosp_rpi5.mk` ;)

</details>

Invoke `make` again with the appropriate arguments and let's finally proceed
with bringing Android to our devices!
