# Lab 03. Android HAL Service

## 3.5. Integrating with Android Apps

We must now go back to Android App development and write some code to actually
call or new HAL API!

Here's
[the official documentation](https://developer.android.com/develop/background-work/services/aidl#Calling)
for calling AIDL services, and here's a random example of using a custom HAL
service:
https://github.com/alvenan/AOSPbenchmark/blob/main/timer_manager/src/vendor/alvenan/timermanager/TimerManager.java

Note that you will need to
[import the AIDL file in your Android Studio](https://stackoverflow.com/questions/17836234/how-can-i-add-the-aidl-file-to-android-studio-from-the-in-app-billing-example)
project for it to generate your IGpio stubs.

GL HF!
