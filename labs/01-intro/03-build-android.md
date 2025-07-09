# Lab 01. Intro to AOSP. Building & testing Android on Raspberry Pi 5.

## 1.3. Building AOSP from source.

### The `repo` tool

Android is a huge project (as we've mentioned earlier, it required a `~100GB`
download of source files before building it!), a custom Linux distribution
integrating thousands of different components (mostly open-source, ofc).

Moreover, since any of Android's components are intimately customizable by the
device vendor and the end product is usually a full disk image for a chosen
target (to be flashed on a Raspberry PI SD card, in our case), AOSP can only be
built as a whole!

To help developers manage this, the
[repo](https://source.android.com/docs/setup/reference/repo) tool was created: a
command-line multi-git-project management utility able to efficiently download /
upload changes from a huge list repositories.

Let's take a quick peek to Android's downloading process (**do not run them** as
they were already completed!):

```sh
repo init --partial-clone --no-use-superproject -b android-latest-release -u
https://android.googlesource.com/platform/manifest
```

This downloads (well, not yet, `repo sync` actually starts this long process)
the latest Android release from Google's servers. But what are the components
that will be included? We can answer exactly this by going
[to that release URL](https://android.googlesource.com/platform/manifest): open
this page in a browser, navigate to any release tag and open its
[default.xml](https://android.googlesource.com/platform/manifest/+/refs/heads/android-latest-release/default.xml).
Please take a minute and study it (history note:
[XML](https://en.wikipedia.org/wiki/XML) predates from Java's max-popularity
days!). This is a manifest: it declares all Android's dependency `git` projects
and where to download them!

### Vendor distributions (raspberry-vanilla)

You probably may have heard of Android's greatest advantage, popularity, that's
also its main weakness resulting in _fragmentation_. This stems from the
difficulty of having such a huge project include support for 100-1000s of vastly
different hardware devices. In fact, it didn't even try to do it at first (and,
to some extent, even now)!

If you are a mobile device manufacturer (called _vendor_ in AOSP terminology!)
and wish to use Android as its OS, you must begin a lengthy and laborious
process to port/adapt its lower-level components to your hardware (the
[Linux kernel](https://www.kernel.org/), together with
[Android's _HAL_](https://source.android.com/docs/core/architecture/hal) --
Hardware Abstraction Layer), and customize some of the system applications to
better cater for your market. And as you invest this time and effort, you read
the Android license terms allowing you to keep your code private and decide to
do just that :( .

Thus, end-users are most often stuck with manufacturer's choice of customized
Android, but there are some brave souls trying to offer open-source alternatives
(RIP ~CyanogenMod~, welcome [LineageOS](https://lineageos.org/)).

Since we will be using the all-popular
[Raspberry Pi](https://www.raspberrypi.com/) single-board computers for some
practical experiments, we're in need of such a distribution too! Fortunately,
there is one maintained GitHub project doing just this:
[raspberry-vanilla](https://github.com/raspberry-vanilla).

We're specifically interested in Android manifest XMLs, which can be found at
the
[raspberry-vanilla/android_local_manifest repository](https://github.com/raspberry-vanilla/android_local_manifest).
It also has the instructions we need to build (make sure to check out all
variants! We'll use the main configuration for now, but we can also build `car`
and `tv` variants). Just note the order of the operations:

- download base Android using `repo`; this will create a hidden `.repo`
  directory with base manifest files (+ git placeholders);
- download additional XMLs using `curl` (especially note the
  `remove-projects.xml` which helped us avoid an even greater download!);
- the `repo sync` actually downloads all dependent AOSP projects (we used some
  optimizations: `repo sync -c --no-clone-bundle` avoiding the git history!);
- finally, source & invoke the build scripts (we'll do this next!);

### Time to build AOSP!

Enough talk! Enter your `/build` mount namespace if not already in (using the
`builder-enter.sh` script) and do the following:

```sh
cd /build
ls -l  # you should see AOSP's entire structure
# in sh, `.` is an alias for `source`
# note: you need to do this in every terminal you wish to build AOSP in!
source build/envsetup.sh
# choose a configuration to prepare some environment variables
# you also need to repeat-run this exact command in every terminal, as well,
# since it only manipulates environment variables in your current shell!
lunch aosp_rpi5-bp2a-userdebug
# let's [re]compile everything
make bootimage systemimage vendorimage -j4
# should hopefully/only take 1-5 mins, since the bulk of it was already buit!
```

⚠️⚠️⚠️ **DO NOT _EVER_ USE SUDO WHEN ISSUING BUILD COMMANDS ON THE VM!** ⚠️⚠️⚠️
\
(_build process will restart from scratch & invalidate cache => disk will get
full and you will be flogged_)

All compilation artifacts will be present inside the `out/` directory (do a
listing there and see what you can find!).

We still need to do some customizations (for the Touch Display to work!) before
we finally generate and burn our image to SD cards, so check out the next
section!
