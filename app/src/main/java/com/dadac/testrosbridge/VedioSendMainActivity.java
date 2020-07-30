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

package com.dadac.testrosbridge;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dadac.testrosbridge.camera.RosCameraPreviewView;
import com.example.dadac.testrosbridge.R;
import com.gary.ros.ROSClient;
import com.gary.ros.rosbridge.ROSBridgeClient;
import java.util.List;

/**
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class VedioSendMainActivity extends Activity implements View.OnClickListener{

    private Button bt_capture;
    private Button bt_publish;
    private Button bt_subscribe;
    private Button bt_advertise;
    private ROSBridgeClient client;
    private int cameraId;
    private RosCameraPreviewView rosCameraPreviewView;
    String ip = "192.168.3.77";   //虚拟机的 IP
    String port = "9090";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_camera);
        onConnect(ip, port);
        subMenuShow();
    }
    //初始化界面的参数
    private void subMenuShow() {
        rosCameraPreviewView = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view);
        bt_capture = findViewById(R.id.bt_capture);
        bt_capture.setOnClickListener(this);
        bt_publish = findViewById(R.id.bt_publish);
        bt_publish.setOnClickListener(this);
        bt_subscribe = findViewById(R.id.bt_subscribe);
        bt_subscribe.setOnClickListener(this);
        bt_advertise = findViewById(R.id.bt_advertise);
        bt_advertise.setOnClickListener(this);

    }
    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VedioSendMainActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @Function: 建立连接
     * @Return:
     */
    public void onConnect(String ip, String port) {

        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        boolean conneSucc = client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                client.setDebug(false);
                showTip("Connect ROS success");
                Log.d("dachen", "Connect ROS success");
                cameraId = 0;
                rosCameraPreviewView.setCamera(getCamera());
                rosCameraPreviewView.setClient(client);
            }


            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                showTip("ROS disconnect");
                Log.d("dachen", "ROS disconnect");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showTip("ROS communication error");
                Log.d("dachen", "ROS communication error");
            }
        });
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int numberOfCameras = Camera.getNumberOfCameras();
            final Toast toast;
            if (numberOfCameras > 1) {
                cameraId = (cameraId + 1) % numberOfCameras;
                rosCameraPreviewView.releaseCamera();
                rosCameraPreviewView.setCamera(getCamera());
                showmsg("Switching cameras.");
            } else {
                showmsg("No alternative cameras to switch to.");
            }
        }
        return true;
    }

    private void showmsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VedioSendMainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Camera getCamera() {
        Camera cam = Camera.open(cameraId);
        Camera.Parameters camParams = cam.getParameters();
        //continuous-video   continuous-picture
        List<String> list = camParams.getSupportedFocusModes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (list.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (list.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }
        cam.setParameters(camParams);
        return cam;
    }

    @Override
    public void onClick(View v) {

    }
}
