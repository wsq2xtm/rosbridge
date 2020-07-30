package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dadac.testrosbridge.utils.JsInterface;
import com.example.dadac.testrosbridge.R;
import com.gary.ros.ROSClient;
import com.gary.ros.Service;
import com.gary.ros.rosbridge.implementation.OperationEvent;
import com.gary.ros.rosbridge.operation.Operation;
import com.rosbridge.utils.RosbridgeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import public_pkg.read_srvRequest;
import public_pkg.read_srvResponse;

public class MapActivityOld extends Activity implements View.OnClickListener {

    public WebView webview;
    public Button bt_refresh;
    private String ip = "192.168.3.32";
    private String port = "9090";

    public ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        EventBus.getDefault().register(this);
        bt_refresh=findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(this);
       // bt_refresh.setVisibility(View.GONE);
        webview=findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //JS调用原生方法
        webview.addJavascriptInterface(new JsInterface(),"android");
        //原生调用js
        webview.setWebChromeClient(new WebChromeClient());

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl("file:///android_asset/ros_web/navigation.html");

        initRosbridge();
    }

    private void initRosbridge() {
        threadPool.execute(connectRosRunnable);
    }

    ConnectRosRunnable connectRosRunnable = new ConnectRosRunnable();

    public class ConnectRosRunnable implements Runnable {
        @Override
        public void run() {
            connectRos();
        }
    }

    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MapActivityOld.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectRos() {
        RCApplication.getInstance().rosbridgeUtils.onConnect(ip, port, new RosbridgeUtils.ConnectCallback() {
            @Override
            public void onConnect() {
                showMsg("Connect ROS success ");
            }

            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                showMsg("ROS disconnect");
            }

            @Override
            public void onError(Exception ex) {
                showMsg("Connect ROS error" );
            }
        }, new ROSClient.MessageCallback() {
            @Override
            public void messageCallback(Operation operation, String topicOrServiceName, String content) {
                Log.d("rosjson", "topicOrServiceName::"+topicOrServiceName + "-message:--" + content);
            }
        });
    }

    public static final String THIS_NODE_NAME = "map_hmi_client";
    public static final String THIS_NODE_TYPE = "service_server";
    public final String MOVE_ARM_SERVICE = "/map_hmi_server/readparam";
    Service move_arm_service;
    private void initService(){
        move_arm_service= RCApplication.getInstance().rosbridgeUtils.getNewServiceObject(MOVE_ARM_SERVICE, read_srvRequest.class, read_srvResponse.class);

        read_srvRequest request = new read_srvRequest();
        request.srcNodeName = THIS_NODE_NAME;
        request.srcNodeType = THIS_NODE_TYPE;
        //request.args = JSON.toJSONString(moveArmArgsModel, SerializerFeature.WriteNullNumberAsZero);
        request.args = "";
        RosbridgeUtils.getInstance().callAsynService(move_arm_service, request);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final OperationEvent event) {
        Log.d("rosjson", event.name + "---" + event.msg);
        switch (event.name) {
            case MOVE_ARM_SERVICE:
                parseSvgService(event, "解析svg图");
                break;
        }
    }

    private void parseSvgService(OperationEvent event, String tip) {
        read_srvResponse read_srvResponse = JSON.parseObject(event.msg, read_srvResponse.class);
        if (read_srvResponse.result == 1) {
            String jsMethod="drawSvg('"+read_srvResponse.content+"')";
            webview.loadUrl("javascript:"+jsMethod);
            //powerManageServiceRspEvent.setResult(tip + "成功");
        } else {
            //powerManageServiceRspEvent.setResult(tip + "失败");
        }
        //EventBus.getDefault().post(powerManageServiceRspEvent);
        //EventBus.getDefault().post(response);
    }


    @Override
    public void onClick(View v) {
       initService();
//       String svgString ="'<?xml version=\"1.0\" standalone=\"no\"?> <svg width=\"100%\" height=\"100%\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">  <text x=\"1089\" y=\"1025\" fill=\"red\">wayPoint_one</text> <rect x=\"1089\" y=\"1035\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"red\" />  <rect x=\"1060\" y=\"1102\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"green\" /> </svg>'";
//       String svgString ="'<?xml version=\"1.0\" standalone=\"no\"?><svg width=\"100%\" height=\"100%\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"><text x=\"1089\" y=\"1025\" fill=\"red\">wayPoint_one</text><rect x=\"1089\" y=\"1035\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"red\" /><rect x=\"1060\" y=\"1102\" height=\"10\" width=\"20\" stroke=\"blue\" stroke-width=\"1\" fill=\"green\" /></svg>'";
//        String jsMethod="drawSvg("+svgString+")";
//        webview.loadUrl("javascript:"+jsMethod);
        //webview.loadUrl("javascript:refresh()");
       // webview.loadUrl("javascript:alertMessage(\""+"66666"+"\")");
//        int a=30;
//        int b=80;
//        int with=10;
//        int height=8;
//        String c="\'站1\'";
//        String dd="drawSemantics("+a+","+b+","+with+","+height+","+c+")";
//        String ddd="drawLine("+a+","+b+","+with+","+height+","+c+")";
//        webview.loadUrl("javascript:"+dd);
//        webview.loadUrl("javascript:"+ddd);
//        webview.evaluateJavascript(dd,new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Toast.makeText(MapActivity.this,"js返回结果："+value,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//        String ddd="drawLine("+a+","+b+","+with+","+height+","+c+")";
//        webview.evaluateJavascript(ddd,new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Toast.makeText(MapActivity.this,"js返回结果："+value,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
