package android.hardware.gpio;

@VintfStability
interface IGpio {
  void setGpio(in int portn, in int state);
  int getGpio(in int portn);
}

