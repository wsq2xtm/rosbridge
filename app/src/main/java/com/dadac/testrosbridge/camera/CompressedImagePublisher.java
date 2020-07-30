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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;

import com.dadac.testrosbridge.BitmapUtils;
import com.gary.ros.message.StdMsg;
import com.gary.ros.rosbridge.ROSBridgeClient;

import java.io.ByteArrayOutputStream;

/**
 * Publishes preview frames.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
class CompressedImagePublisher implements RawImageListener {

    private com.gary.ros.Topic<StdMsg> clockTopic;

    private void advertiseTopic() {
        if (clockTopic == null) {
            clockTopic = new com.gary.ros.Topic<StdMsg>("/chatter", StdMsg.class, client);
        }
        clockTopic.advertise();
    }

    private void publishTopic(String str) {
        if (clockTopic == null) {
            clockTopic = new com.gary.ros.Topic<StdMsg>("/chatter", StdMsg.class, client);
        }
        StdMsg msg = new StdMsg();
        msg.data = str;
        clockTopic.publish(msg);
    }

    private byte[] rawImageBuffer;
    private Size rawImageSize;
    private YuvImage yuvImage;
    private Rect rect;
    ROSBridgeClient client;
    private Context context;
    private ByteArrayOutputStream stream ;

    public CompressedImagePublisher(Context context, ROSBridgeClient client) {
        this.context = context;
        this.client = client;
        this.stream = new ByteArrayOutputStream();
        advertiseTopic();
    }

    @Override
    public void onNewRawImage(byte[] data, Size size) {
        try {
            rawImageBuffer = data;
            rawImageSize = size;
            yuvImage = new YuvImage(rawImageBuffer, ImageFormat.NV21, size.width, size.height, null);
            rect = new Rect(0, 0, size.width, size.height);

            yuvImage.compressToJpeg(rect, 20, stream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            publishTopic(BitmapUtils.bitmapToBase64(bitmap));
            stream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (data != rawImageBuffer || !size.equals(rawImageSize)) {
//            try {
//                rawImageBuffer = data;
//                rawImageSize = size;
//                yuvImage = new YuvImage(rawImageBuffer, ImageFormat.NV21, size.width, size.height, null);
//                rect = new Rect(0, 0, size.width, size.height);
//
//                yuvImage.compressToJpeg(rect, 20, stream);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                publishTopic(BitmapUtils.bitmapToBase64(bitmap));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}