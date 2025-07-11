#pragma once

#include <cstdint>

#include <aidl/android/hardware/gpio/BnGpio.h>

namespace aidl::android::hardware::gpio {

class Gpio : public BnGpio {

protected:

public:
	::ndk::ScopedAStatus setGpio(int32_t in_portn, int32_t in_state) override;
	::ndk::ScopedAStatus getGpio(int32_t in_portn, int32_t* _aidl_return) override;

public:
};

}
