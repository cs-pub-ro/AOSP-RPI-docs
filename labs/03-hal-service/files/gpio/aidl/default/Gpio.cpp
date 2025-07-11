#define LOG_TAG "Gpio"

#include <android-base/logging.h>
#include "Gpio.h"

namespace aidl::android::hardware::gpio {

::ndk::ScopedAStatus Gpio::setGpio(int32_t in_portn, int32_t in_state)
{
	(void)(in_portn);
  // ::gpiod::chip chip("gpiochip0");
  //
  // auto line = chip.get_line(port);
  // line.request({"android.gpio", gpiod::line_request::DIRECTION_OUTPUT, 0}, value);  

  return ::ndk::ScopedAStatus::ok();
}

::ndk::ScopedAStatus Gpio::getGpio(int32_t in_portn, int32_t* _aidl_return)
{
	(void)(in_portn);
  return ::ndk::ScopedAStatus::ok();
}

}

