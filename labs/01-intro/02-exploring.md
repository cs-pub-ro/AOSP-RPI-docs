# Lab 01. Intro to AOSP. Building & testing on RPI5

## 1.2. Exploring the infrastructure.

Now that you connected to the VM, let's explore for a bit.

Since the entire AOSP source code requires downloading ~100GB and building
resulting in another +100GB of artifacts (with a duration of 3-4 hours on all
available CPU cores), we will be sharing a common build directory among multiple
users of that VM.

### Entering mount namespace for the `/build` overlay

In order to do this and still be able to have a private working directory, our
[build VM infrastructure](https://github.com/cs-pub-ro/AOSP-RPI-builder) employs
Linux mount namespaces to mount a
[unique overlay filesystem](https://docs.kernel.org/filesystems/overlayfs.html)
for each student (similar to what Docker does for containers, but simpler to
implement).

Speaking of this, your private working directory will be `/build`.

But, in order to access it, you must use a special `sudo builder-enter.sh`
script to enter:

```sh
sudo builder-enter.sh
# ..nothing happens? => it's expected, read on!
```

The first thing you need to know is whether or not your shell is inside the
builder namespace. For this, you can simply use the `findmnt` command, which
should output this:

```text
TARGET      SOURCE                            FSTYPE      OPTIONS
/           /dev/vda2                         ext4        rw,relatime
...
└─/build    /dev/vda2[/home/_aosp_build/base] ext4        rw,relatime
```

If the `/build` mountpoint is present, then you will be able to see the AOSP
source files inside:

```txt
➜ tree -L 1 /build
/build
├── Android.bp -> build/soong/root.bp
├── art
├── bionic
├── bootable
├── bootstrap.bash -> build/soong/bootstrap.bash
├── build
...
```

Finally, note that the `builder-enter.sh` script is ephemeral: only takes effect
on your current terminal only! If you open another shell, you need to enter the
mount namespace containing `/build` again, otherwise **you won't be able to see
your private overlay**!

### Terminal customizations

You probably noticed that the VM's Linux terminal is fancier. Let's take the
time to learn some tricks!

1. First, your shell is `zsh`, a better-UX alternative to the classic `bash`.
   Try typing `vim /build/vendor/<Tab><Tab>`. You can see the supercharged
   tab-based auto completion: colors highlighting, navigate through the choices
   using arrows etc.;
2. You can use `Ctrl+R` in your shell to display a command history search
   prompt! But wait, there's more: type something you remember from inside a
   command (e.g., a path). It's using a fuzzy finding algorithm
   ([fzf](https://github.com/junegunn/fzf));
3. Now, let's try our `nvim` ([NeoVim](https://neovim.io/) +
   [AstroNVim](https://astronvim.com/))! Nagivate to `/build` and enter `nvim`.
   Press `<Space>+o` (the letter). Pause a little after space and observe the
   help screen on the bottom! You can use it to quickly hint for available
   shortcuts. For example, if you wish to find for a specific file, press
   `<Space> + f + f` (again). A `Telescope` search window should open (it's also
   using the fuzzy finder algorithm!). You can use `<Esc> + :qa!` (type `qa!`
   after seeing a prompt at the bottom of the screen) to quit without saving;
4. Finally, we'll introduce `tmux` (Terminal Multiplexer): it's quite useful
   when building huge projects since its session will remain for the lifetime of
   the server! Start by invoking `tmux` **from a /build-mounted namespace**! You
   will notice a new bottom bar! You can create new windows by typing `<Ctrl>+A`
   (just once, do not keep it pressed!), then `c` (for _create_); navigate
   through the windows with `Ctrl+A, n` (_next_) or by pressing its number after
   `<Ctrl+A>`; [check out a cheatsheet here](https://tmuxcheatsheet.com/), just
   remember: the prefix key is `Ctrl+A`, not `Ctrl+B` (we changed the defaults,
   you can configure it in `~/.config/tmux/tmux.conf`).

If you still wish to use VSCode,
[make sure to configure it for SSH remote setup](https://code.visualstudio.com/docs/remote/ssh)!


<details>
  <summary>It won't actually work due to the need to run `sudo builder-enter.sh` to
actually see your `/build` overlay, there are some workarounds...</summary>
  
See https://github.com/microsoft/vscode/issues/48659#issuecomment-570815118 .
You would need to patch the `.js` extension and add something like this to your
ssh config:

```conf
    RemoteCommand sudo builder-enter.sh bash
```

</details>
