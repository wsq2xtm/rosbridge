package com.dadac.testrosbridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.dadac.testrosbridge.R;
import com.gary.ros.ROSClient;
import com.gary.ros.message.CompressedImage;
import com.gary.ros.message.StdMsg;
import com.gary.ros.rosbridge.ROSBridgeClient;
import com.gary.ros.rosbridge.implementation.OperationEvent;
import com.gary.ros.rosbridge.operation.Operation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import temperature_recognition.app_msg;

/**
 * 视频界面
 */
public class VedioReceivedMainActivity extends Activity implements View.OnClickListener {
    ROSBridgeClient client;
    String ip = "192.168.3.12";   //虚拟机的 IP
    String port = "9090";

    private Button bt_capture;
    private Button bt_publish;
    private Button bt_subscribe;
    private Button bt_advertise;

    private boolean iswifi = false;
    private boolean ismobile = false;
    private boolean isNetworkAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        EventBus.getDefault().register(this);
        onConnect(ip, port);
        subMenuShow();
        //监听网络
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        connectReceiver = new ConnectReceiver();
//        registerReceiver(connectReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        // unregisterReceiver(connectReceiver);
        super.onDestroy();
    }

    //初始化界面的参数
    private void subMenuShow() {
        iv_picture = findViewById(R.id.iv_picture);
        bt_capture = findViewById(R.id.bt_capture);
        bt_capture.setOnClickListener(this);
        bt_publish = findViewById(R.id.bt_publish);
        bt_publish.setOnClickListener(this);
        bt_subscribe = findViewById(R.id.bt_subscribe);
        bt_subscribe.setOnClickListener(this);
        bt_advertise = findViewById(R.id.bt_advertise);
        bt_advertise.setOnClickListener(this);
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
//                ROSBridgeWebSocketClient.cameraCallback = new ROSBridgeWebSocketClient.CameraCallback() {
//                    @Override
//                    public void cameraCallback(Operation operation, String name, String content) {
//                        onEvent(operation, name, content);
//                    }
//                };
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

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VedioReceivedMainActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OperationEvent event) {
        if ("/nari/temperature_recognition/result_app".equals(event.name)) {
            //parseChatterTopic(event);
            parseChatterTopic(event.msg);
            return;
        }else if("/nari/image_correction/image_raw/compressed".equals(event.name)){
            parseCameraTopic(event.msg);
        }
        //Logger.e("接收：" + event.msg);
    }


    public void onEvent(Operation operation, String name, String content) {
        if ("/nari/temperature_recognition/result_app".equals(name)) {
            parseChatterTopic(content);
            return;
        }
        //Logger.e("接收：" + content);
    }

    private void parseCameraTopic(final String content) {
        try {
            com.alibaba.fastjson.JSONObject jsonObject=JSON.parseObject(content);
            String jsondata = jsonObject.getString("data");
            final Bitmap mp = BitmapUtils.base64ToBitmap(jsondata);
            iv_picture.setImageBitmap(mp);

        } catch (Exception e) {
            e.printStackTrace();
           // Logger.e(e.getMessage());
        }
    }
    private void parseChatterTopic(final String content) {
        try {
            com.alibaba.fastjson.JSONObject jsonObject=JSON.parseObject(content);
            com.alibaba.fastjson.JSONObject jsonimage = jsonObject.getJSONObject("recognitionImage");
            String jsondata = jsonimage.getString("data");
            final Bitmap mp = BitmapUtils.base64ToBitmap(jsondata);
            iv_picture.setImageBitmap(mp);

        } catch (Exception e) {
            e.printStackTrace();
            //Logger.e(e.getMessage());
        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    JSONParser parser = new JSONParser();
//                    org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(content);
//                    String jsondata = (String) jsonObject.get("data");
//                    final Bitmap mp = BitmapUtils.base64ToBitmap(jsondata);
//                    iv_picture.setImageBitmap(mp);
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void parseChatterTopic(OperationEvent event) {

    }

    /**
     * 设置拍照的照片保存本地的路径
     *
     * @param context
     * @return
     */
    public Uri getSavePhotoUri(Context context) {
        //生成路径
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirName = "冷柜集装箱相册";
        File appDir = new File(root, dirName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //文件名为时间
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");
        String sd = sdf.format(new Date(timeStamp));
        String fileName = sd + ".jpg";

        //获取文件
        File file = new File(appDir, fileName);
        Uri ImageUri = Uri.fromFile(file);
        return ImageUri;
    }

    /**
     * 相机拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        iv_picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    ImageView iv_picture;
    private Bitmap bitmap;
    private Uri uri;
    public final int TAKE_PHOTO = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_capture:
                uri = getSavePhotoUri(VedioReceivedMainActivity.this);
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.bt_publish:
                if (bitmap == null) {
                    showMsg("请先拍照");
                    return;
                }

                publishTopic(BitmapUtils.bitmapToBase64(bitmap));
                break;
            case R.id.bt_subscribe:
                subscribeTopic1();
                break;
            case R.id.bt_advertise:
                advertiseTopic();
                break;
            default:
                break;
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(VedioReceivedMainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private com.gary.ros.Topic<StdMsg> clockTopic;

    private void advertiseTopic() {
        if (clockTopic == null) {
            clockTopic = new com.gary.ros.Topic<StdMsg>("/chatter", StdMsg.class, client);
        }
        clockTopic.advertise();
        showMsg("已注册主题");
    }

    private void publishTopic(String str) {
        if (clockTopic == null) {
            clockTopic = new com.gary.ros.Topic<StdMsg>("/chatter", StdMsg.class, client);
        }
        StdMsg msg = new StdMsg();
        msg.data = str;
        clockTopic.advertise();
        clockTopic.publish(msg);
        showMsg("已发布主题");
    }

    private void subscribeTopic() {
        if (clockTopic == null) {
            clockTopic = new com.gary.ros.Topic<StdMsg>("/chatter", StdMsg.class, client);
        }
        clockTopic.subscribe();
        showMsg("已订阅主题");
    }
    com.gary.ros.Topic<app_msg> clockTopic1;
    com.gary.ros.Topic<CompressedImage> clockTopic2;
    public void subscribeTopic1() {
        if(clockTopic2==null){
            //clockTopic1 = new com.jilk.ros.Topic<app_msg>("/nari/temperature_recognition/result_app", app_msg.class, client);
            clockTopic2 = new com.gary.ros.Topic<CompressedImage>("/nari/image_correction/image_raw/compressed", CompressedImage.class, client);
        }
        clockTopic2.subscribe();
    }
}

