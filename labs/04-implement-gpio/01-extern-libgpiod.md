# Lab 04. Implementing GPIO functionality

## 4.2. Using gpiod in C/C++

Now that we have our dumb NOOP service, it's time to implement the real GPIO
driver interfacing to it!

The bad news is: the old sysfs-based userspace
[is not available anymore](https://sergioprado.blog/new-linux-kernel-gpio-user-space-interface/)
(deprecated since kernel 4.8!). We'll need to add support for the new
[`libgpiod`](https://github.com/brgl/libgpiod) to AOSP...

The good news is: it's really simple. It seems AOSP has the `external/` folder
inside Soong build system's search path, so if any other module wants a library,
it will look in there and compile it!

We still need to write an `Android.bp` for Soong to know how to build our
library, but after some quick search,
[TechNexion has already done it](https://github.com/technexion-android/platform_external_libgpiod)!

But please note this is for an old Android version (ver `9`), but if we look
very close, we can see branches for newer ones, up to `13`!

So make sure to clone the latest `master` (since it's not the default, you will
need to use `-b <branch>` when issuing the git command). Make the destination to
be `external/libgpiod` instead of the default name (NOT
`platform_external_libgpiod`). Reminder of the git clone syntax we'll need to
use:

```sh
git clone -b <branch> <url> <destination>
```

_You can do it!_
