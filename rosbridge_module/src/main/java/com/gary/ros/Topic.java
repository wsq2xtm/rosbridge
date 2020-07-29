/**
 * Copyright (c) 2014 Jilk Systems, Inc.
 * 
 * This file is part of the Java ROSBridge Client.
 *
 * The Java ROSBridge Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java ROSBridge Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Java ROSBridge Client.  If not, see http://www.gnu.org/licenses/.
 * 
 */
package com.gary.ros;

import com.gary.ros.message.Message;
import com.gary.ros.rosbridge.FullMessageHandler;
import com.gary.ros.rosbridge.operation.Advertise;
import com.gary.ros.rosbridge.operation.Operation;
import com.gary.ros.rosbridge.operation.Publish;
import com.gary.ros.rosbridge.operation.Subscribe;
import com.gary.ros.rosbridge.operation.Unadvertise;
import com.gary.ros.rosbridge.operation.Unsubscribe;

import java.util.concurrent.LinkedBlockingQueue;


public class Topic<T extends Message> extends LinkedBlockingQueue<T> implements FullMessageHandler {
    protected String topic;
    private Class<? extends T> type;
    private String messageType;
    private ROSClient client;
    private Thread handlerThread;
    
    public Topic(String topic, Class<? extends T> type, ROSClient client) {
        this.topic = topic;
        this.client = client;
        this.type = type;
        messageType = Message.getMessageType(type);
        handlerThread = null;
    }

    //add方法在添加元素的时候，若超出了度列的长度会直接抛出异常：
    //put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
    //offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false。
    @Override
    public void onMessage(String id, Message message) {
        add((T) message);
    }
    
    
    // warning: there is a delay between the completion of this method and 
    //          the completion of the subscription; it takes longer than
    //          publishing multiple other messages, for example.    
    public void subscribe(MessageHandler<T> handler) {
        startRunner(handler);
        subscribe();
    }
    
    public String subscribe() {
        client.register(Publish.class, topic, type, this);
        Operation operation=new Subscribe(topic, messageType);
        send(operation);
        return operation.id;
    }
    
    public void unsubscribe() {
        // need to handle race conditions in incoming message handler
        //    so that once unsubscribe has happened the handler gets no more
        //    messages
        send(new Unsubscribe(topic));        
        client.unregister(Publish.class, topic);
        stopRunner();
    }
    
    private void startRunner(MessageHandler<T> handler) {
        stopRunner();
        handlerThread = new Thread(new MessageRunner(handler));
        handlerThread.setName("Message handler for " + topic);
        handlerThread.start();
    }
    
    private void stopRunner() {
        if (handlerThread != null) {
            handlerThread.interrupt();
            clear();
            handlerThread = null;
        }
    }
    
    
    public void advertise() {
        send(new Advertise(topic, messageType));
    }
    
    public String publish(T message) {
        Operation operation=new Publish(topic, message);
        send(operation);
        return operation.id;
    }
    
    public void unadvertise() {
        send(new Unadvertise(topic));
    }
    
    private void send(Operation operation) {
        client.send(operation);
    }
    
    public void verify() throws InterruptedException {

        boolean hasTopic = false;
        String[] strs=client.getTopics();
        //com.orhanobut.logger.Logger.e(strs.toString());
        for (String s : strs) {
            if (s.equals(topic)) {
                hasTopic = true;
                break;
            }
        }
        if (!hasTopic)
            throw new RuntimeException("Topic \'" + topic + "\' not available.");

        client.typeMatch(client.getTopicMessageDetails(topic), type);
    }

    private class MessageRunner implements Runnable {
        private MessageHandler<T> handler;

        public MessageRunner(MessageHandler<T> handler) {
            this.handler = handler;
        }             
        
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    handler.onMessage(take());
                }
                catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }
    
}
