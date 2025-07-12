#define LOG_TAG "Gpio"

#include <android-base/logging.h>
#include <gpiod.h>
#include <cerrno>
#include <cstring>

#include "Gpio.h"

namespace aidl::android::hardware::gpio {

/**
 * Constructor.
 */
Gpio::Gpio() {
    memset(&gpio_ports, 0, sizeof(gpio_ports));
}

/**
 * Denstructor.
 */
Gpio::~Gpio() {
    int i;
    for (i=0; i<GPIO_PIN_COUNT; i++) {
        if (gpio_ports[i].gpioline) {
            gpiod_line_release(gpio_ports[i].gpioline);
            gpio_ports[i].gpioline = NULL;
        }
    }
    if (gpiochip) {
        gpiod_chip_close(gpiochip);
        gpiochip = NULL;
    }
}

void Gpio::openGpioChip() {
    if (!gpiochip) {
        gpiochip = gpiod_chip_open("/dev/gpiochip0");
    }
}

::ndk::ScopedAStatus Gpio::setGpio(int32_t in_portn, int32_t in_state)
{
    char buf[100];
    int ret;

    openGpioChip();
    if (gpiochip == NULL) {
        snprintf(buf, 100, "Unable to open gpiochip: %i!", errno);
    	return ::ndk::ScopedAStatus::fromServiceSpecificErrorWithMessage(-1, buf);
    }

    if (in_portn >= GPIO_PIN_COUNT) {
    	return ::ndk::ScopedAStatus::fromServiceSpecificErrorWithMessage(
    		-1, "Invalid GPIO pin number!"
    	);
    }
    if (!gpio_ports[in_portn].gpioline) {
        gpio_ports[in_portn].gpioline = gpiod_chip_get_line(gpiochip, in_portn);
        if (gpio_ports[in_portn].gpioline == NULL) {
            snprintf(buf, 100, "Unable to get GPIO line: %i!", errno);
    	    return ::ndk::ScopedAStatus::fromServiceSpecificErrorWithMessage(-1, buf);
        }
        ret = gpiod_line_request_output(gpio_ports[in_portn].gpioline, "gpio-service", in_state);
        if (ret != 0) {
            snprintf(buf, 100, "Unable to request GPIO output line: %i!", errno);
    	    return ::ndk::ScopedAStatus::fromServiceSpecificErrorWithMessage(-1, buf);
        }
    }
    ret = gpiod_line_set_value(gpio_ports[in_portn].gpioline, in_state);

	LOG(INFO) << "GPIO was set v2: " << in_portn << " = " << in_state;

   	return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus Gpio::getGpio(int32_t in_portn, int32_t* _aidl_return)
{
	(void)(in_portn);
	*_aidl_return = 0;
	LOG(INFO) << "GPIO_GET: STUB " << in_portn;
   	return ::ndk::ScopedAStatus::ok();
}

}
