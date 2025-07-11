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

Some hints:

- Raspberry PI only has `gpiochip0`, so you can safely hardcode this.
- implement `setGpio` first; we have no buttons to use `getGpio` (for input), so
  we can leave it a stub;
- remember our interface: `setGpio(portn, state)`; `portn` is the number of the
  GPIO pin index (misnamed a bit, I know), while `state` is either 0 or 1 (the
  logic level we wish to set to our GPIO);
- second argument of `gpiod_line_request_output()` is a name you wish to be
  identified by as GPIO consumer, you can set `"gpio-service"` for this purpose;
  the third argument is the state (0 or 1).
