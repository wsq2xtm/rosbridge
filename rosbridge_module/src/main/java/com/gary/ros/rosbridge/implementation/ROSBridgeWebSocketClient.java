package com.gary.ros.rosbridge.implementation;

import com.alibaba.fastjson.JSONObject;
import com.gary.ros.ROSClient;
import com.gary.ros.message.Message;
import com.gary.ros.rosbridge.FullMessageHandler;
import com.gary.ros.rosbridge.operation.Operation;
import com.gary.ros.rosbridge.operation.Publish;
import com.gary.ros.rosbridge.operation.ServiceResponse;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import public_pkg.NodeMsg;
import public_pkg.heart_beat_msg;

/**
 * websocket消息发送，心跳监听，消息处理等
 */
public class ROSBridgeWebSocketClient extends WebSocketClient{
    //心跳
    private boolean isNeedContinueCheckHeartbeat=false;
    private final String HEARTBEAT_TOPIC="/nari/szrd/dnrobot/node/heartbeat";//心跳主题
    private final int REPEAT_CHECK_TIME=10000;//每10秒检查一次订阅的节点心跳是否超时
    private ConcurrentHashMap<String,NodeMsg> nodes=new ConcurrentHashMap<>();//所有的订阅节点
    private ExecutorService executorService;

    private Registry<Class> classes;
    private Registry<FullMessageHandler> handlers;
    private boolean debug=false;
    private ROSClient.ConnectionStatusListener listener;
    public ROSClient.MessageCallback messageCallback;

    ROSBridgeWebSocketClient(URI serverURI) {
        super(serverURI);
        classes = new Registry<Class>();
        handlers = new Registry<FullMessageHandler>();
        Operation.initialize(classes);  // note, this ensures that the Message Map is initialized too
        listener = null;
    }

    public static ROSBridgeWebSocketClient create(String URIString) {
        ROSBridgeWebSocketClient client = null;
        try {
            URI uri = new URI(URIString);
            client = new ROSBridgeWebSocketClient(uri);
            //client = new ROSBridgeWebSocketClient(uri,new Draft_17());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return client;
    }

    public void setListener(ROSClient.ConnectionStatusListener listener) {
        this.listener = listener;
    }

    public void setMessageCallback(ROSClient.MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    /**
     * 取消某个节点心跳
     * @param nodeMsg
     */
    public void cancelSubscribeHeartbeat(NodeMsg nodeMsg){
        Logger.e("取消心跳监听：" + nodeMsg.srcNodeName);
        removeNode(nodeMsg);
        checkNodes();
    }

    /**
     * 检查需要检查节点数量，如果为零，就停止心跳，大于零就开始心跳
     */
    private void checkNodes() {
        if(nodes.size()==0){
            Logger.e("检查心跳监听数量为零，关掉线程：" );
            isNeedContinueCheckHeartbeat=false;
            if(executorService!=null){//启动单线程循环检查是否心跳超时
                executorService.shutdown();
                executorService=null;
            }
        }
    }

    /**
     * 移除监听节点心跳
     * @param nodeMsg
     */
    private void removeNode(NodeMsg nodeMsg) {
        if(nodes.containsKey(nodeMsg.srcNodeName)){
            nodes.remove(nodeMsg.srcNodeName);
            Logger.e("移除心跳监听：" + nodeMsg.srcNodeName);
        }
    }

    /**
     * 监听节点心跳
     * @param nodeMsg
     */
    public void subscribeHeartbeat(NodeMsg nodeMsg){
        addNode(nodeMsg);
        if(executorService==null){//启动单线程循环检查是否心跳超时
            Logger.e("第一个心跳监听，开始启动单线程：" + nodeMsg.srcNodeName);
            isNeedContinueCheckHeartbeat=true;
            executorService=Executors.newSingleThreadExecutor();
            executorService.execute(new myRunnable());
        }
    }

    /**
     * 添加需要监听的节点
     * @param nodeMsg
     */
    private void addNode(NodeMsg nodeMsg) {
        if(!nodes.containsKey(nodeMsg.srcNodeName)){
            nodeMsg.lastReceivedTime=System.currentTimeMillis();
            nodes.put(nodeMsg.srcNodeName,nodeMsg);
            Logger.e("增加心跳监听：" + nodeMsg.srcNodeName);
        }
    }

    /**
     * 循环检查是否超时
     */
    class myRunnable implements Runnable {

        @Override
        public void run() {
            while (isNeedContinueCheckHeartbeat){
                long currentTime=System.currentTimeMillis();
                for(String str:nodes.keySet()){
                    if((currentTime-nodes.get(str).lastReceivedTime)>REPEAT_CHECK_TIME){
                        sendDisconnectNodeMsgByEventbus(nodes.get(str));
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 把超时的发布出去
     * @param nodeMsg
     */
    private void sendDisconnectNodeMsgByEventbus(NodeMsg nodeMsg) {
        Logger.e("心跳超时：" + nodeMsg.srcNodeName);
        EventBus.getDefault().post(nodeMsg);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (listener != null)
            listener.onConnect();
    }

    @Override
    public void onMessage(String message) {
        if (debug) {
            System.out.println("<ROS " + message);
        }
        Operation operation = Operation.toOperation(message, classes);

        if (operation instanceof Publish) {
            Publish publish = ((Publish) operation);
            try {
                JSONObject jsonObject =  JSON.parseObject(message);
                String content = jsonObject.get("msg").toString();
                if(HEARTBEAT_TOPIC.equals(publish.topic)){
                    manageReceivedHeartbeatMsg(content);
                }else if(messageCallback!=null){
                    messageCallback.messageCallback(operation, publish.topic, content);
                }else {
                    EventBus.getDefault().post(new OperationEvent(operation, publish.topic, content));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operation instanceof ServiceResponse) {
            ServiceResponse serviceResponse = ((ServiceResponse) operation);
            try {
                JSONObject jsonObject = JSON.parseObject(message);
                String content = jsonObject.get("values").toString();
                if(messageCallback!=null){
                    messageCallback.messageCallback(operation, serviceResponse.service, content);
                }else {
                    EventBus.getDefault().post(new OperationEvent(operation, serviceResponse.service, content));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 收到心跳主题后，更新对应的节点监听时间
     * @param content
     */
    private void manageReceivedHeartbeatMsg(String content) {
        heart_beat_msg heart_msg=JSON.parseObject(content,heart_beat_msg.class);
        if(nodes.containsKey(heart_msg.srcNodeName)){
            Logger.e("收到服务器的心跳节点：" + heart_msg.srcNodeName);
            nodes.get(heart_msg.srcNodeName).lastReceivedTime=System.currentTimeMillis();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (listener != null) {
            //boolean normal = (remote || (code == CloseFrame.NORMAL));
            listener.onDisconnect(remote, reason, code);
        }
    }

    @Override
    public void onError(Exception ex) {
        if (listener != null)
            listener.onError(ex);
        else ex.printStackTrace();
    }

    // There is a bug in V1.2 of java_websockets that seems to appear only in Android, specifically,
    //    it does not shut down the thread and starts using gobs of RAM (symptom is frequent garbage collection).
    //    This method goes into the WebSocketClient object and hard-closes the socket, which causes the thread
    //    to exit (note, just interrupting the thread does not work).
    @Override
    public void closeBlocking() throws InterruptedException {
        super.closeBlocking();
        try {
            Field channelField = this.getClass().getSuperclass().getDeclaredField("channel");
            channelField.setAccessible(true);
            SocketChannel channel = (SocketChannel) channelField.get(this);
            if (channel != null && channel.isOpen()) {
                Socket socket = channel.socket();
                if (socket != null)
                    socket.close();
            }
        } catch (Exception ex) {
            System.out.println("Exception in Websocket close hack.");
            ex.printStackTrace();
        }
    }

    /**
     * 发送消息
     * @param operation
     */
    public void send(Operation operation) {
        String json =  JSON.toJSONString(operation);
        //String json = operation.toJSON();
        Logger.e("ROS> " + json);
       // android.util.Log.e("ROS> ",json);
        send(json);
    }

    public void register(Class<? extends Operation> c,
                         String s,
                         Class<? extends Message> m,
                         FullMessageHandler h) {
        Message.register(m, classes.get(Message.class));
        classes.register(c, s, m);
        if (h != null)
            handlers.register(c, s, h);
    }

    public void unregister(Class<? extends Operation> c, String s) {
        handlers.unregister(c, s);
        // Note that there is no concept of unregistering a class - it can get replaced is all
    }

    public Class<? extends Message> getRegisteredMessage(String messageString) {
        return classes.lookup(Message.class, messageString);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
