# Lab 04. Implementing GPIO functionality

## 4.2. Using gpiod in C/C++

Let's go back to our `default` service in `hardware/interfaces/gpio/aidl` we've
worked in the previous lab.

Modify `Android.bp` (the one from `default`, right?) and add our new library to
the `shared_libs` list (among the others).

[Try to re-build](../01-intro/03-build-android.md) the project!

You shall receive a new error, something about the `vndk.enabled` property being
unrecognized... edit `libgpiod`'s blueprint and simply delete that (we don't
need it).

Next, re-build and receive new errors: _overriding commands for target
'.../gpiodetect'_. Those executable conflict with the ones inside Android's own
`toybox` binary suite, so we'll also delete them all from `libgpiod` (yes!
delete all `cc_binary` stuff, we only need the library).

Build should now succeed, else **go panic**.

### Hardest part: writing code

Let's write some code to actually use this library.

Since gpiod's [official documentation](https://libgpiod.readthedocs.io/) is
pretty bad, you can
[inspire from tutorials](https://www.ics.com/blog/gpio-programming-exploring-libgpiod-library).

Basically:

- use
  [`gpiod_chip_open()`](https://www.rankinlawfirm.com/dev/c/libgpiod-dev/group__chips.html#ga1bd8b7231810364c711a14e85e9f3cc7)
  to open the GPIO device (`gpiochip0`);
- afterwards, you should open the port using
  [`gpiod_chip_get_line()`](https://www.rankinlawfirm.com/dev/c/libgpiod-dev/group__chips.html#gaba9fe5b1cf0c33cd9d7ee911a9c2d4b1);
- after opening the line, you must issue
  [`gpiod_line_request_output()`](https://www.rankinlawfirm.com/dev/c/libgpiod-dev/group__line__request.html#ga303bc402be82ed1bd95b7b72fd1d54fa)
  to setup the GPIO for output and obtain exclusive access to it (no other
  programs will be able to use it until released!);
- while the chip & line are open, use gpiod line set/get functions to freely
  control it, e.g.
  [`gpiod_line_set_value()`](https://www.rankinlawfirm.com/dev/c/libgpiod-dev/group__line__value.html#ga3b9ba90f0f451361657923db0c0a7f5d);
- either make sure to release / close the GPIO line & descriptor or cache &
  reuse them for all future calls, since you only have exclusive access while
  using their original pointers!

We mainly wish to implement `setGpio` since we have no buttons to use `getGpio`
for input, so we can leave it a stub for now.

Some final notes:

- Raspberry PI only has `gpiochip0` pins exposed, so you can safely hardcode
  this; the full path to the file is ofc `/dev/gpiochip0` (make sure to explore
  the filesystem to check this)!
- remember our interface: `setGpio(portn, state)`; `portn` is the number of the
  GPIO pin index (misnamed a bit, I know), while `state` is either 0 or 1 (the
  logic level we wish to set to our GPIO);
- second argument of `gpiod_line_request_output()` is a name you wish to be
  identified by as GPIO consumer, you can set `"gpio-service"` for this purpose;
  the third argument is the state (0 or 1).
