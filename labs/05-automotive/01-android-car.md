# Lab 05. Android Automotive & Location Services

## 5.1. Build & Flash the Android Car Image

### Build environment preparations

Same commands as in [Lab 1](../01-intro/), only the `lunch` target changed:

```sh
sudo builder-enter.sh
cd /build
source build/envsetup.sh
# note the new `_car` suffix
lunch aosp_rpi5_car-cp2a-userdebug
make bootimage systemimage vendorimage -j4
# finally, make the new image
./rpi5-mkimg.sh
```

Then copy the generated image to your PC using `scp`.

### Flash & boot (you)

Flash the image with `dd` or Balena Etcher.

Your RPi5 should boot into a new in-vehicle screen, **CarLauncher**, instead of 
the classic UI!

### Explore using ADB

```sh
adb shell getprop ro.build.flavor          # aosp_rpi5_car-cp2a-userdebug
adb shell dumpsys car_service | head -50   # CarService: audio, vehicle, power

# display topology (car targets define multiple display groups)
adb shell dumpsys display | grep -i mDisplay 

# car service commands
adb shell cmd car_service
# we can change/simulate some properties!
adb shell cmd car_service emulate-driving-state park
adb shell cmd car_service get-driving-state
```

Questions to answer with `dumpsys` + the AOSP source tree:

- Which service is `CarService` (search the source tree for `CarService.java`),
  and how does it get started?
- How many display groups does the car target define?
  (`device/brcm/rpi5/` overlays for the `_car` product)

### Install a map app

Plain AOSP has no Google Play Services, so no Google Maps - but the open-source **OsmAnd**
works out of the box. You'll use it later to *watch* your simulated GPS drive
the campus loop instead of staring at coordinates.

You can use app services such as UpToDown to [download the OsmAnd
apk](https://osmand.en.uptodown.com/android), then use `adb` to install it:

```sh
adb install OsmAnd.apk
```

Alternatively, if it stalls for too long (it's quite huge, `> 300MB`), you may
wish to push the file to sd card then install it:
```sh
# set to the actual apk name!
adb push OsmAnd.apk /sdcard/
adb shell pm install /sdcard/OsmAnd.apk
```

Launch it, download a map tile region for Romania / Cluj, and note that there is no
location yet - there is no GPS hardware on the RPi5 (which we'll see about in 
the next task).
