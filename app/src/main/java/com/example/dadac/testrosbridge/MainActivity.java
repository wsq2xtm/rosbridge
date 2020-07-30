package com.example.dadac.testrosbridge;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.dadac.testrosbridge.ConnectReceiver;
import com.dadac.testrosbridge.ControlRobotArmActivity;
import com.dadac.testrosbridge.KeyboardTelepRobotActivity;
import com.dadac.testrosbridge.MapActivity;
import com.dadac.testrosbridge.RosBridgeHeartbeatActivity;
import com.dadac.testrosbridge.UrdfRobotActivity;
import com.dadac.testrosbridge.VedioReceivedMainActivity;
import com.dadac.testrosbridge.RosBridgePubSubActivity;
import com.dadac.testrosbridge.VedioSendMainActivity;
import com.orhanobut.logger.Logger;
import com.warning_module.WarningDialog;
import com.warning_module.WarningUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity {

    private Button DC_Button_JumpToRosSubPub;
    private ConnectReceiver connectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        DC_Button_JumpToRosSubPub = (Button) findViewById(R.id.DC_Button_JumpToRosSubPub);
        //监听网络
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        connectReceiver = new ConnectReceiver();
        registerReceiver(connectReceiver,intentFilter);

        EventBus.getDefault().register(this);
    }

    public void JumpToSubPubActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, RosBridgePubSubActivity.class);
        startActivity(myIntentRos);
    }

    public void JumpToCameraReceivedActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, VedioReceivedMainActivity.class);
        startActivity(myIntentRos);
    }

    public void JumpToCameraSendActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, VedioSendMainActivity.class);
        startActivity(myIntentRos);
    }

    public void JumpToTelepArmActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, ControlRobotArmActivity.class);
        startActivity(myIntentRos);
    }

    public void JumpToTelepMoveActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, KeyboardTelepRobotActivity.class);
        startActivity(myIntentRos);
    }

    public void JumpToURDFActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, UrdfRobotActivity.class);
        startActivity(myIntentRos);
    }

    public void JumpTo2DActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, MapActivity.class);
        startActivity(myIntentRos);
//        for(int i=0;i<100;i++){
//
//            WarningUtil.saveMessage(System.currentTimeMillis(),5,"站点1","dd");
//        }
//        Log.e("保存告警信息","保存完成");
//        WarningUtil.showDialog(this);
    }

    public void JumpToHeartbeatActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, RosBridgeHeartbeatActivity.class);
        startActivity(myIntentRos);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindingEvent(String netChange) {
        Logger.e("接收到网络改变");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(connectReceiver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}