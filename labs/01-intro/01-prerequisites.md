# Lab 01. Intro to AOSP. Building & testing on RPI5

## 1.1. Prerequisites. Connecting to the cloud server.

Since building AOSP requires many CPU cores, 64GB RAM & 300GB of free space
([yep, for real!](https://source.android.com/docs/setup/start/requirements)), we
will be using some rather powerful VMs hosted inside the Faculty’s private cloud
(OpenStack).

It’s going to be a headless (no graphics) server-like environment, so Linux
command line knowledge is required. Don’t worry, you have all the tools you need
in there (plus a tweaked Zsh, TMux for running persistent/multiplexed terminals
and vim with AstroNvim config for enthusiasts).

But first, you will need to gain access to this VM. To do this, you must create
a SSH private/public key pair and give the public counterpart to the superviser
for account creation & authorization (do this on your personal PC / laptop in a
Linux `sh`-compatible shell or Windows Powershell), generate a public/private
SSH keypair:

```sh
# first, check if a keypair already exists
you@laptop:~# ls ~/.ssh/
# if the files `id_ed25519` and `id_ed25519.pub` already exist, SKIP everything else!
you@laptop:~# ssh-keygen -t ed25519  # Edwards curves: better & shorter keys!
# answer the defaults (aka just press enter multiple times)
you@laptop:~# ls -l ~/.ssh/
-rw------- 1 root root 2602 Oct 15 12:59 id_ed25519     # <-- THE PRIVATE KEY, keep secret!
-rw-r--r-- 1 root root  571 Oct 15 12:59 id_ed25519.pub # <-- your PUBLIC key to give away!
# print and copy the private key (including the 'ssh-ed25519' prefix!):
you@laptop:~# cat ~/.ssh/id_ed25519.pub
ssh-ed25519 AAAAB3NzaC...  # a longish line containing the public key number + email
```

Now copy and paste your **public key** and give it to a supervisor (together
with a preferred username to be created) to gain access to the build machine and
wait for confirmation...

<details>
  <summary><i>[For lab supervisors]</i></summary>
  
  Supervisors will allocate the user a shared VM using a shared spreadsheet, 
  then configure a new user:
  ```sh
  # first, SSH into the target machine (where $IDX is the VM's index)
  ssh admin@aosp2025.root.sx -p $(( 2200 + IDX ))
  # e.g., ssh aosp2025.root.sx -p 2201
  ````
  Then, after connecting to the VM:
  ```sh
  sudo builder-add-user.sh $NEW_USER_NAME
  sudo builder-add-key.sh $NEW_USER_NAME "$STUDENT_PUBLIC_KEY"
  ```

</details>

Finally, try to connect to your assigned VM and port, e.g.:

```sh
ssh your-user@aosp2025.root.sx -p 2210
```
