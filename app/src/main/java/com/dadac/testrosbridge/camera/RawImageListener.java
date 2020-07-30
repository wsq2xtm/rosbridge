package com.dadac.testrosbridge.camera;

import android.hardware.Camera.Size;

interface RawImageListener {

  void onNewRawImage(byte[] data, Size size);

}