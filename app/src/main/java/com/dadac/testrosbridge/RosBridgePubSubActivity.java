package com.dadac.testrosbridge;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dadac.testrosbridge.services.LongTestRequest;
import com.dadac.testrosbridge.services.LongTestResponse;
import com.example.dadac.testrosbridge.R;
import com.gary.ros.message.StdMsg;
import com.gary.ros.message.Time;
import com.gary.ros.rosbridge.implementation.OperationEvent;
import com.orhanobut.logger.Logger;
import com.gary.ros.Service;
import com.rosbridge.utils.RosbridgeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import public_pkg.Write_srvRequest;
import public_pkg.Write_srvResponse;
import public_pkg.cmd_header_msg;
import public_pkg.cmd_msg;
import public_pkg.cmd_rsp_msg;
import public_pkg.heart_beat_msg;
import public_pkg.read_srvRequest;
import public_pkg.read_srvResponse;
import public_pkg.status_analog_msg;
import public_pkg.status_digital_msg;


/**
 * 消息订阅和发送
 */
public class RosBridgePubSubActivity extends Activity implements View.OnClickListener {

    String ip = "192.168.3.32";
    String port = "9090";

    //主题
    final String testTopic="testTopic";//模拟量主题
    final String analogTopic="/nari/szrd/dnrobot/yc";//模拟量主题
    final String digitalTopic="/nari/szrd/dnrobot/yx";//数字量主题
    final String cmdTopic="/nari/szrd/dnrobot/cmd";//4.3机器人控制消息主题
    final String cmd_rspTopic="/nari/szrd/dnrobot/cmdRsp";//4.4机器人控制返回消息主题
    //服务
    final String read_srvName="read_srvName";//4.5机器人读服务 服务名
    final String write_srvName="write_srvName";//4.6机器人写服务 服务名

    private Button DC_Button_Service;
    private Button DC_Button_Subscrible;
    private Button DC_Button_Publish;
    private TextView tv_ShowData1;
    private TextView tv_ShowData2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rosdatashow);
        EventBus.getDefault().register(this);
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
            public void onDisconnect(boolean remote, String reason, int code) {
                showTip("ROS disconnect");
                Log.e("connectStatus","onDisconnect normal:"+remote+" reason:"+reason+" code:"+code);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("connectStatus","onError :"+ex.getMessage());
                showTip("ROS communication error:"+ex.getMessage());
            }
        });
    }

    //初始化界面的参数
    private void subMenuShow() {
        DC_Button_Service = findViewById(R.id.DC_Button_Service);
        DC_Button_Service.setOnClickListener(this);
        DC_Button_Subscrible = findViewById(R.id.DC_Button_Subscrible);
        DC_Button_Subscrible.setOnClickListener(this);
        DC_Button_Publish =  findViewById(R.id.DC_Button_Publish);
        DC_Button_Publish.setOnClickListener(this);
        tv_ShowData1 = findViewById(R.id.tv_ShowData1);
        tv_ShowData2 =findViewById(R.id.tv_ShowData2);
    }

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RosBridgePubSubActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final OperationEvent event) {
        String operationName=event.name;
        switch (operationName){
            case analogTopic:
                parseAnalogTopic(event);
                break;
            case digitalTopic:
                parseDigitalTopic(event);
                break;
            case cmd_rspTopic:
                parseDigitalTopic(event);
                break;
                case testTopic:
                parseDigitalTopic(event);
                break;
            case read_srvName:
                parseService(event);
                break;
            case write_srvName:
                parseService(event);
                break;
            default:
                break;
        }
        Logger.e("接收："+event.msg);
    }

    private void parseDigitalTopic(OperationEvent event) {
        try {
            JSONObject jsonObject = JSON.parseObject(event.msg);
            final String jsondata = jsonObject.toString();
            tv_ShowData2.setText(jsondata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }private void parseAnalogTopic(OperationEvent event) {
        try {
            JSONObject jsonObject = JSON.parseObject(event.msg);
            final String jsondata = jsonObject.toString();
            tv_ShowData1.setText(jsondata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseService(OperationEvent event) {
        try {
            JSONObject jsonObject = JSON.parseObject(event.msg);
            final String jsondata = jsonObject.toString();
            tv_ShowData2.setText(jsondata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseChatterTopic(OperationEvent event) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DC_Button_Subscrible:
                //subscribeAnalogTopic();
                //subscribeDigitalTopic();
                testSubscribe();
                //subscribeCmd_rsp_msgTopic();
                break;
            case R.id.DC_Button_Publish:
//                Student student=new Student();
//                student.setStudentAge(22+test);
//                student.setStudentName("alibaba"+test);
//                String str= com.alibaba.fastjson.JSON.toJSONString(student);
//                Student student2=new Student();
//                student2.setStudentAge(32+test);
//                student2.setStudentName("33libaba"+test);
//                String str1= com.alibaba.fastjson.JSON.toJSONString(student2);
//                testPublisshTopic(str,str1);
               // publishCmdTopic();
                testTopic();
               // test++;
                break;
            case R.id.DC_Button_Service:
                readService();
                //writeService();
                //testService();
                break;
            default:
                break;
        }
    }

    Service readService;
    public void readService(){
        try {
            if(readService==null){
                readService = instance.getNewServiceObject(read_srvName,
                        read_srvRequest.class,
                        read_srvResponse.class);
            }
            read_srvRequest read_srvRequest=new read_srvRequest();
            read_srvRequest.args="args";
            read_srvRequest.srcNodeName="srcNodeName";
            read_srvRequest.srcNodeType="srcNodeType";
            String id=instance.callAsynService(readService,read_srvRequest);
            instance.callAsynService(readService,read_srvRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Service writeService;
    public void writeService(){
        try {
            if(writeService==null){
                writeService = instance.getNewServiceObject(write_srvName,
                        Write_srvRequest.class,
                        Write_srvResponse.class);
            }
            Write_srvRequest write_srvRequest=new Write_srvRequest();
            write_srvRequest.args="args";
            write_srvRequest.srcNodeName="srcNodeName";
            write_srvRequest.srcNodeType="srcNodeType";
            write_srvRequest.type=3;
            instance.callAsynService(writeService,write_srvRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Service<LongTestRequest, LongTestResponse> timeService;
    int a=33;
    int b=44;
    public void testService(){
        try {
            if(timeService==null){
                timeService = instance.getNewServiceObject("/add_two_ints",
                        LongTestRequest.class,
                        LongTestResponse.class);
            }
            LongTestRequest longTestRequest=new LongTestRequest();
            longTestRequest.a=a++;
            longTestRequest.b=b++;
            instance.callAsynService(timeService,longTestRequest);
            //timeService.call(longTestRequest);
            //timeService.verify();
           //String[] strings1=timeService.getAllServices();
//            //System.out.println("Time (secs): " + timeService.callBlocking(new Empty()).time.sec);
//
//            Service<com.jilk.ros.rosapi.message.Service, Type> serviceTypeService =
//                    new Service<com.jilk.ros.rosapi.message.Service, Type>("/rosapi/service_type",
//                            com.jilk.ros.rosapi.message.Service.class, Type.class, client);
//            serviceTypeService.verify();
//            String[] strings2=serviceTypeService.getAllServices();
//            String type = serviceTypeService.callBlocking(new com.jilk.ros.rosapi.message.Service("/rosapi/service_response_details")).type;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int test=0;

    com.gary.ros.Topic<heart_beat_msg> testSubscribe;
    public void testSubscribe() {
        if(testSubscribe==null){//获取要订阅的主题实例
            testSubscribe = instance.getNewTopicObject(testTopic, heart_beat_msg.class);
        }
        if(testSubscribe==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }
        String id=instance.subscribeTopic(testSubscribe);//开始订阅主题
        String ied=instance.subscribeTopic(testSubscribe);//开始订阅主题
    }

    com.gary.ros.Topic<status_analog_msg> analogTopicInstance;
    public void subscribeAnalogTopic() {
        if(analogTopicInstance==null){//获取要订阅的主题实例
            analogTopicInstance = instance.getNewTopicObject(analogTopic, status_analog_msg.class);
        }
        if(analogTopicInstance==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }
        instance.subscribeTopic(analogTopicInstance);//开始订阅主题
    }

    com.gary.ros.Topic<status_digital_msg> digitalTopicInstance;
    public void subscribeDigitalTopic() {
        if(digitalTopicInstance==null){//获取要订阅的主题实例
            digitalTopicInstance = instance.getNewTopicObject(digitalTopic, status_digital_msg.class);
        }
        if(digitalTopicInstance==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }
        instance.subscribeTopic(digitalTopicInstance);//开始订阅主题
    }

    com.gary.ros.Topic<cmd_rsp_msg> cmd_rsp_msgTopicInstance;
    public void subscribeCmd_rsp_msgTopic() {
        if(cmd_rsp_msgTopicInstance==null){//获取要订阅的主题实例
            cmd_rsp_msgTopicInstance = instance.getNewTopicObject(cmd_rspTopic, cmd_rsp_msg.class);
        }
        if(cmd_rsp_msgTopicInstance==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }
        instance.subscribeTopic(cmd_rsp_msgTopicInstance);//开始订阅主题
    }

    com.gary.ros.Topic<StdMsg> clockTopic;
    public void testPublisshTopic(String str,String str1) {
        if(clockTopic==null){//获取要发布的主题实例
            clockTopic = instance.getNewTopicObject("/listener", StdMsg.class);
        }
        if(clockTopic==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }
        StdMsg msg=new StdMsg();
        msg.data=str;
        instance.sendTopicObject(clockTopic,msg);//开始发布主题
    }

    com.gary.ros.Topic<cmd_msg> cmd_msgTopic;
    public void publishCmdTopic() {
        if(cmd_msgTopic==null){//获取要发布的主题实例
            cmd_msgTopic = instance.getNewTopicObject(cmdTopic, cmd_msg.class);
        }
        if(cmd_msgTopic==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }
        Time msgTime=new Time();
        msgTime.nsecs=11;
        msgTime.secs=22;

        cmd_header_msg header=new cmd_header_msg();
        header.msgID=1222;
        header.dstNodeName="dstNodeName";
        header.dstNodeType="dstNodeType";
        header.msgTime=msgTime;
        header.msgType="msgType";
        header.srcNodeName="srcNodeName";
        header.srcNodeType="srcNodeType";

        cmd_msg msg=new cmd_msg();
        msg.content="content";
        msg.header=header;
        msg.object=1;
        msg.type=2;
        instance.sendTopicObject(cmd_msgTopic,msg);//开始发布主题
    }

    com.gary.ros.Topic<heart_beat_msg> testTopicObject;
    public void testTopic() {
        if(testTopicObject==null){//获取要发布的主题实例
            testTopicObject = instance.getNewTopicObject(testTopic, heart_beat_msg.class);
        }
        if(testTopicObject==null){//如果没连接到服务器，获取的主题实例为空
            showTip("未连接到master");
            return;
        }

        Time msgTime=new Time();
        msgTime.nsecs=11;
        msgTime.secs=22;
        heart_beat_msg msg=new heart_beat_msg();
        msg.msgTime=msgTime;
        msg.msgType="public_pkg/heart_beat_msg";
        msg.srcNodeName="srcNodeName";
        msg.srcNodeType="srcNodeType";
        msg.processID=2;
        String id=instance.sendTopicObject(testTopicObject,msg);//开始发布主题
        instance.sendTopicObject(testTopicObject,msg);//开始发布主题
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

