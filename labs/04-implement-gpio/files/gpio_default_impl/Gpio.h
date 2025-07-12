#pragma once

#include <cstdint>
#include <gpiod.h>

#include <aidl/android/hardware/gpio/BnGpio.h>

namespace aidl::android::hardware::gpio {

#define GPIO_PIN_COUNT  40

class Gpio : public BnGpio {

protected:
	struct gpiod_chip *gpiochip = NULL;
	struct {
		struct gpiod_line *gpioline;
		int state;
	} gpio_ports[GPIO_PIN_COUNT];

	void openGpioChip();

public:
  	Gpio();
  	~Gpio();

	::ndk::ScopedAStatus setGpio(int32_t in_portn, int32_t in_state) override;
	::ndk::ScopedAStatus getGpio(int32_t in_portn, int32_t* _aidl_return) override;

};

}
