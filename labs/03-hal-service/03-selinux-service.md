# Lab 03. Android HAL Service

## 3.3. SELinux trouble

If we were to reflash and run our new service, the init system would complain
that it doesn't have any SELinux policies annotated and refuse to run it!

SELinux is a Mandatory Access Control layer: a kernel module which
enforces strict security policies and denies unallowed actions (even if you're
root!).

As it is not our scope to understand how SELinux works, it will suffice to just
take them for granted and copy them inside our device build.

We recommend copying the [sepolicy](./files/gpio/sepolicy) directory to
`hardware/interfaces/gpio/` and including it from the
`device/brcm/rpi5/BoardConfig.mk` file, e.g. add the following:

```mk
# Search for an existing BOARD_SEPOLICY_DIRS assignment, then add:
BOARD_SEPOLICY_DIRS += hardware/interfaces/gpio/sepolicy
```

