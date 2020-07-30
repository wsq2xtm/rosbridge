/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dadac.testrosbridge.camera;

import android.content.Context;
import android.util.AttributeSet;

import com.gary.ros.rosbridge.ROSBridgeClient;

/**
 * Displays and publishes preview frames from the camera.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class RosCameraPreviewView extends CameraPreviewView  {
  private Context context;
  public RosCameraPreviewView(Context context) {
    super(context);
    this.context=context;
  }

  public void setClient(ROSBridgeClient client){
    setRawImageListener(new CompressedImagePublisher(context,client));
  }

  public RosCameraPreviewView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RosCameraPreviewView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
