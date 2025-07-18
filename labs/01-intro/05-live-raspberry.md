# Lab 01. Intro to AOSP. Building & testing on RPI5

## 1.5. Going Live on Raspberry Pi!

### Make the big SD card image

Raspberry PI requires a specific partitioning scheme: a FAT32-formatted bootable
partition with some Broadcom firmware files, the Linux kernel and device tree,
then any other partitions (as we'll see later, Android has several!).

Raspberry-Vanilla project gives us a script to build a ready-to-burn SD card
image (you have a `rpi5-mkimg.sh` symlink inside `/build` if you observe
carefully!).

By default, this script creates a 16GB file, which is a bit too much to always
download from the build server over WiFi and burn onto a slow SD card. So edit
it and set `IMGSIZE=8697308000` (~8GB). Much better!

Invoke `rpi5-mkimg.sh` and let's go on with our next challenge.

### Downloading the image

You might ask yourself now: how do I bring that ~16GB file from that virtual
machine in the _cloud_ to your physical laptop/PC where the SD card is
connected?

Well, `scp` (or `rsync`), of course! Take 1 minute to read
[`man scp` or its online alternative](https://linux.die.net/man/1/scp).

The source argument is going to be the VM server's SSH address (`user@host`) +
port (use `-P 220X` instead of `-p`, scp has changed this parameter from SSH!),
then `:` then the path to the file on the VM, but wait: `/build` will be EMPTY
since we're not inside our special mount namespace...

So `mv` that file to your home and fetch that instead (the easy way)!

<details>
  <summary><i>[Or, if you want to do it the hard way...]</i></summary>

You'll need `rsync` installed (theoretically, you could do this with `scp` but
we haven't tried it).

The trick is to invoke `builder-enter.sh` when rsync connects to the server and
tries to run itself there! It has an option for that, so basically:

```sh
rsync -vh --rsync-path="sudo builder-enter.sh rsync" -e 'ssh -p 220X' \
    your-user@aosp2025.root.sx:/build/output/path-to-image ~/Downloads/
```

</details>

### Flashing the image to SD card

1. If you are on Linux, use `dd` (but BE EXTRA CAREFUL! read on).

   First, use `lsblk` and find which one is your SD device (`/dev/sdX`). DO NOT
   MISINTERPRET THIS OR YOU'LL FORMAT YOUR MAIN STORAGE (if you have an older,
   SATA-based disk, anyway). Ask an assistant if in doubt...

   Then run something like:

   ```sh
   sudo dd if=~/Downloads/your-android-image.raw of=/dev/sdX bs=1M status=progress
   ```

2. Windows users, either download [Balena Etcher](https://etcher.balena.io/) or
   [Raspberry Pi Imager](https://www.raspberrypi.com/software/) (which is
   actually harder to use) and flash the raw image to your card (a kid could do
   it)!

Unfortunately, on cheap SD cards, this can take ~10-15 minutes :|

### Booting the Raspberry

Connect the Hub's USB-C cord to your laptop, the Raspberry Pi to one of the
Hub's ports and, finally, power it on by plugging the AC adapter's USB-C cable
inside the USB-C/PD port.

If you want to be sure the Raspberry boots, connect the UART adapter both to USB
and to the Raspberry PI (ask an assistant first!). You'll need a special
software to receive the serial data (search for a serial terminal in Windows,
for Linux I recommend `picocom`).

After several minutes (Android runs some heavy first-time initialization
routine), the display should turn on with Android's logo! Wait some more minutes
for it to fully boot.

### Missing config.txt tweaks workaround

Sometimes, `raspberry-vanilla`'s vendor makefile doesn't properly rebuild the
boot partition (especially `config.txt`). So even if you edit it in
`device/brcm/rpi5/boot/config.txt`, it won't get updated into the final image.

As easy workaround, simply delete this output directory:

```sh
rm -rf out/target/product/rpi5/rpiboot
```

Re-build and the boot partition should be updated!
