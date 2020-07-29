package com.rosbridge.utils;

import com.gary.ros.ROSClient;
import com.gary.ros.Service;
import com.gary.ros.Topic;
import com.gary.ros.message.Message;
import com.gary.ros.rosbridge.ROSBridgeClient;
import com.orhanobut.logger.Logger;

import public_pkg.NodeMsg;
import public_pkg.heart_beat_msg;

/**
 * rosbridge通讯工具，连接，发布，订阅，调用服务
 * @param <T>
 */
public class RosbridgeUtils<T extends Message> {
    private ROSBridgeClient client;
    private volatile static RosbridgeUtils instance;
    private RosbridgeUtils(){}

    /**
     * 获取单例
     * @return
     */
    public static RosbridgeUtils getInstance() {
        if (instance == null) {
            synchronized (RosbridgeUtils.class) {
                if (instance == null) {
                    instance = new RosbridgeUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 连接服务器回调
     */
    private ConnectCallback connectCallback;
    public void setConnectCallback(ConnectCallback callback){
        connectCallback=callback;
    }
    public interface ConnectCallback{
        void onConnect();
        void onDisconnect(boolean normal, String reason, int code);
        void onError(Exception ex);
    }
    private ROSClient.ConnectionStatusListener connectionStatusListener=new ROSClient.ConnectionStatusListener(){

        @Override
        public void onConnect() {
            if(connectCallback!=null){
                connectCallback.onConnect();
            }
            Logger.e("Connect ROS success");
        }

        @Override
        public void onDisconnect(boolean normal, String reason, int code) {
            if(connectCallback!=null){
                connectCallback.onDisconnect(normal,reason,code);
            }
            Logger.e("ROS disconnect");
        }

        @Override
        public void onError(Exception ex) {
            if(connectCallback!=null){
                connectCallback.onError(ex);
            }
            Logger.e("ROS communication error:"+ex.getMessage());
        }
    };

    /**
     * 建立连接，此接口没有消息回调，可以供Android开发调用，可以使用eventbus监听收到的消息
     * @param ip
     * @param port
     * @param callback 连接监听回调
     */
    public void onConnect(String ip, String port, ConnectCallback callback) {
        heartbeatTopic=null;
        connectCallback=null;
        connectCallback=callback;
        if(client==null){
            client = new ROSBridgeClient("ws://" + ip + ":" + port);
        }
        client.connect(connectionStatusListener);
    }

    /**
     * 建立连接，此接口有消息回调，可以供java开发调用，从回调函数获取消息
     * @param ip
     * @param port
     * @param callback 连接监听回调
     * @param messageCallback  消息监听回调
     */
    public void onConnect(String ip, String port, ConnectCallback callback, ROSClient.MessageCallback messageCallback) {
        heartbeatTopic=null;
        connectCallback=callback;
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                if(connectCallback!=null){
                    connectCallback.onConnect();
                }
                Logger.e("Connect ROS success");
            }


            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                if(connectCallback!=null){
                    connectCallback.onDisconnect(normal,reason,code);
                }
                Logger.e("ROS disconnect");
            }

            @Override
            public void onError(Exception ex) {
                if(connectCallback!=null){
                    connectCallback.onError(ex);
                }
                Logger.e("ROS communication error:"+ex.getMessage());
            }
        },messageCallback);
    }

    /**
     * 断开连接服务器
     */
    public void disconnect(){
        if(client!=null){
            client.disconnect();
            client=null;
        }
        connectCallback=null;
    }

    /**
     * 获取要发布的主题实例
     * @param topic
     * @param type
     * @return
     */
    public Topic getNewTopicObject(String topic, Class<? extends T> type) {
        if(client!=null){
            return new Topic<>(topic, type, client);
        }
        return null;
    }

    /**
     * 主题发布，此消息类型需要master端已注册的才可以。比如 StdMsg消息就是已注册，可以直接使用
     * @param topic
     * @param message
     */
    public String sendTopicObject(Topic topic, T message) {
        topic.advertise();
        return topic.publish(message);
    }

    /**
     * 取消主题发布
     * @param topic
     */
    public void unadvertiseTopic(Topic topic) {
        if(topic!=null){
            topic.unadvertise();
        }
    }

    /**
     * 主题订阅
     * @param topic
     */
    public String subscribeTopic(Topic topic) {
        if(client!=null&&topic!=null){
            return topic.subscribe();
        }
        return "";
    }

    /**
     * 取消订阅主题
     * @param topic
     */
    public void unSubscribeTopic(Topic topic) {
        if(topic!=null){
            topic.unsubscribe();
        }
    }

    /**
     * 获取服务实例
     * @param service  服务名
     * @param callType  请求参数
     * @param responseType  响应参数
     * @return
     */
    public Service getNewServiceObject(String service, Class<? extends T> callType,
                                       Class<? extends T> responseType) {
        if(client!=null){
            return new Service(service, callType, responseType, client);
        }
        return null;
    }

    /**
     * 调用服务，异步
     * @param service
     * @param message
     * @return  返回id
     */
    public String callAsynService(Service service,T message){
        if(service!=null){
            return service.call(message);
        }
        return "";
    }

    /**
     * 调用服务，同步
     * @param service
     * @param message
     */
    public Message callSynService(Service service, T message) throws InterruptedException {
        if(service!=null){
            return service.callBlocking(message);
        }
        return null;
    }

    /**
     * 订阅心跳
     */
    Topic<heart_beat_msg> heartbeatTopic;
    public void subscribeHeartbeatTopic() {
        if(heartbeatTopic==null){//获取要订阅的主题实例
            heartbeatTopic = getInstance().getNewTopicObject("/nari/szrd/dnrobot/node/heartbeat", heart_beat_msg.class);
        }
        if(heartbeatTopic!=null){
            subscribeTopic(heartbeatTopic);//开始订阅主题
        }
    }

    /**
     * 取消某个节点心跳监听
     * @param nodeMsg
     */
    public void cancelSubscribeHeartbeat(NodeMsg nodeMsg){
        if(heartbeatTopic!=null&&client!=null){
            client.cancelSubscribeHeartbeat(nodeMsg);//取消监听节点
        }
    }

    /**
     * 监听某个节点心跳
     * @param nodeMsg
     */
    public void subscribeHeartbeat(NodeMsg nodeMsg){
        if(heartbeatTopic==null){
            subscribeHeartbeatTopic();//开始订阅主题
        }
        if(heartbeatTopic!=null&&client!=null){
            client.subscribeHeartbeat(nodeMsg);//开始监听节点
        }
    }
}
