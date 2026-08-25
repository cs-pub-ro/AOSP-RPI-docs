# Lab 05. Android Automotive & Location Services

## The project — "SummerDrive"

You have spent four days building, flashing and customizing AOSP on a
Raspberry Pi 5, writing apps with Jetpack Compose, and wiring an AIDL HAL
service. This last day pulls it all together:

- **Build & flash an Android Automotive (AAOS) image** on the same RPi5 —
  Raspberry Vanilla ships an `aosp_rpi5_car` target, so the car runs on real
  hardware, not just a laptop emulator.
- **Drive it**: RPi5 has no GPS hardware, so you write `GpsSim`, a
  privileged platform app that injects mock GPS fixes and plays a GPX loop
  around the campus.
- **Build a car app** (`SummerDrive`): a Jetpack Compose dashboard showing
  live position, speed and heading, plus a second instance aimed at the
  car's instrument cluster display.

## Prerequisites

- The hardware so far;
- A prebuilt `aosp_rpi5_car` target!

## Tasks

- [5.1. Building & Flashing Android Car](./01-android-car.md)
- [5.2. Writing a GPS Simulator service](./02-gps-sim.md)
- [5.3. Using location services inside Android apps](./03-drive-app.md)

