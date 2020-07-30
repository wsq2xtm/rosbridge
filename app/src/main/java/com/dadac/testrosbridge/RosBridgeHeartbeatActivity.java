package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dadac.testrosbridge.R;
import com.rosbridge.utils.RosbridgeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import public_pkg.NodeMsg;


/**
 * 消息订阅和发送
 */
public class RosBridgeHeartbeatActivity extends Activity implements View.OnClickListener {

    String ip = "192.168.3.130";
    String port = "9090";

    //节点名
    final String subscribe_name1="ClampWireTool";//
    final String subscribe_name2="claw_tool";//
    final String subscribe_name3="cut_wire_tool";//

    private Button start_subscribe;
    private Button subscribe1;
    private Button subscribe2;
    private Button subscribe3;
    private Button cancelSubscribe1;
    private Button cancelSubscribe2;
    private Button cancelSubscribe3;
    private TextView tv_ShowData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartbeat);
        EventBus.getDefault().register(RosBridgeHeartbeatActivity.this);
        subMenuShow();
        instance=RosbridgeUtils.getInstance();
        connectMaster(ip, port);
    }

    private RosbridgeUtils instance;
    private void connectMaster(String ip, String port) {
        instance.onConnect(ip, port, new RosbridgeUtils.ConnectCallback() {
            @Override
            public void onConnect() {
                showTip("Connect ROS success");
            }

            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                showTip("ROS disconnect");
            }

            @Override
            public void onError(Exception ex) {
                showTip("ROS communication error:"+ex.getMessage());
            }
        });
    }

    //初始化界面的参数
    private void subMenuShow() {
        cancelSubscribe1 = findViewById(R.id.cancelSubscribe1);
        cancelSubscribe1.setOnClickListener(this);
        cancelSubscribe2 = findViewById(R.id.cancelSubscribe2);
        cancelSubscribe2.setOnClickListener(this);
        cancelSubscribe3 = findViewById(R.id.cancelSubscribe3);
        cancelSubscribe3.setOnClickListener(this);
        start_subscribe = findViewById(R.id.start_subscribe);
        start_subscribe.setOnClickListener(this);
        subscribe1 = findViewById(R.id.subscribe1);
        subscribe1.setOnClickListener(this);
        subscribe2 = findViewById(R.id.subscribe2);
        subscribe2.setOnClickListener(this);
        subscribe3 = findViewById(R.id.subscribe3);
        subscribe3.setOnClickListener(this);
        tv_ShowData = findViewById(R.id.tv_ShowData);
    }

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RosBridgeHeartbeatActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_subscribe:
                RosbridgeUtils.getInstance().subscribeHeartbeatTopic();
                break;

            case R.id.subscribe1:
                NodeMsg nodeMsg1=new NodeMsg();
                nodeMsg1.srcNodeName=subscribe_name1;
                RosbridgeUtils.getInstance().subscribeHeartbeat(nodeMsg1);
                break;
            case R.id.subscribe2:
                NodeMsg nodeMsg2=new NodeMsg();
                nodeMsg2.srcNodeName=subscribe_name2;
                RosbridgeUtils.getInstance().subscribeHeartbeat(nodeMsg2);
                break;

                case R.id.cancelSubscribe1:
                NodeMsg cancelNodeMsg1=new NodeMsg();
                    cancelNodeMsg1.srcNodeName=subscribe_name1;
                RosbridgeUtils.getInstance().cancelSubscribeHeartbeat(cancelNodeMsg1);
                break;

                case R.id.cancelSubscribe2:
                NodeMsg cancelNodeMsg2=new NodeMsg();
                    cancelNodeMsg2.srcNodeName=subscribe_name2;
                RosbridgeUtils.getInstance().cancelSubscribeHeartbeat(cancelNodeMsg2);
                break;

                case R.id.cancelSubscribe3:
                NodeMsg cancelNodeMsg3=new NodeMsg();
                    cancelNodeMsg3.srcNodeName=subscribe_name3;
                RosbridgeUtils.getInstance().cancelSubscribeHeartbeat(cancelNodeMsg3);
                break;
            case R.id.subscribe3:
                NodeMsg nodeMsg3=new NodeMsg();
                nodeMsg3.srcNodeName=subscribe_name3;
                RosbridgeUtils.getInstance().subscribeHeartbeat(nodeMsg3);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(RosBridgeHeartbeatActivity.this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NodeMsg nodeMsg) {
        tv_ShowData.setText(nodeMsg.srcNodeName+"  :已断线");
    }
}

