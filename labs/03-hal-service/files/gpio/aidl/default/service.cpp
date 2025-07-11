#define LOG_TAG "Gpio"

#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include "Gpio.h"

using aidl::android::hardware::gpio::Gpio;
using std::string_literals::operator""s;

int main() {

    ABinderProcess_setThreadPoolMaxThreadCount(0);
    ABinderProcess_startThreadPool();

    std::shared_ptr<Gpio> gpio = ndk::SharedRefBase::make<Gpio>();
    const std::string name = Gpio::descriptor + "/default"s;

    if (gpio != nullptr) {
        if(AServiceManager_addService(gpio->asBinder().get(), name.c_str()) != STATUS_OK) {
            LOG(ERROR) << "Failed to register Gpio service";
            return -1;
        }
    } else {
        LOG(ERROR) << "Failed to get IGpio instance";
        return -1;
    }

    LOG(ERROR) << "Gpio service starts to join service pool";
    ABinderProcess_joinThreadPool();

    return EXIT_FAILURE;  // should not reached
}

