/**
 * Copyright (c) 2014 Jilk Systems, Inc.
 * <p>
 * This file is part of the Java ROSBridge Client.
 * <p>
 * The Java ROSBridge Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * The Java ROSBridge Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with the Java ROSBridge Client.  If not, see http://www.gnu.org/licenses/.
 */
package com.gary.ros.rosbridge;

import com.gary.ros.ROSClient;
import com.gary.ros.Service;
import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
import com.gary.ros.rosapi.message.Empty;
import com.gary.ros.rosapi.message.MessageDetails;
import com.gary.ros.rosapi.message.Nodes;
import com.gary.ros.rosapi.message.Services;
import com.gary.ros.rosapi.message.Topic;
import com.gary.ros.rosapi.message.Topics;
import com.gary.ros.rosapi.message.Type;
import com.gary.ros.rosapi.message.TypeDef;
import com.gary.ros.rosbridge.implementation.ROSBridgeWebSocketClient;
import com.gary.ros.rosbridge.operation.Operation;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

import public_pkg.NodeMsg;

public class ROSBridgeClient extends ROSClient {
    String uriString;
    ROSBridgeWebSocketClient client;

    public ROSBridgeClient(String uriString) {
        this.uriString = uriString;
    }

    public void cancelSubscribeHeartbeat(NodeMsg nodeMsg) {
        if (client != null) {
            client.cancelSubscribeHeartbeat(nodeMsg);
        }
    }

    public void subscribeHeartbeat(NodeMsg nodeMsg) {
        if (client != null) {
            client.subscribeHeartbeat(nodeMsg);
        }
    }

    @Override
    public boolean connect() {
        return connect(null);
    }

    @Override
    public boolean connect(ConnectionStatusListener listener) {
        boolean result = false;
        try {
            if (client != null) {
                client.setListener(null);
                client=null;
            }
            client = ROSBridgeWebSocketClient.create(uriString);
            if (client != null) {
                client.setListener(listener);
                result = client.connectBlocking();
            }
        } catch (Exception ex) {
            Logger.e("连接master出错:" + ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean connect(ConnectionStatusListener listener, MessageCallback messageCallback) {
        boolean result = false;
        client = ROSBridgeWebSocketClient.create(uriString);
        if (client != null) {
            client.setListener(listener);
            client.setMessageCallback(messageCallback);
            try {
                result = client.connectBlocking();
            } catch (InterruptedException ex) {
                Logger.e("连接master:" + ex.getMessage());
            }
        }
        return result;
    }

    @Override
    public void disconnect() {
        try {
            client.closeBlocking();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void send(Operation operation) {
        client.send(operation);
    }

    @Override
    public void send(String json) {
        client.send(json);
        Logger.e("发送：" + json);
    }

    @Override
    public void register(Class<? extends Operation> c,
                         String s,
                         Class<? extends Message> m,
                         FullMessageHandler h) {
        client.register(c, s, m, h);
    }

    @Override
    public void unregister(Class<? extends Operation> c, String s) {
        client.unregister(c, s);
    }

    @Override
    public void setDebug(boolean debug) {
        client.setDebug(debug);
    }

    @Override
    public String[] getNodes() throws InterruptedException {
        Service<Empty, Nodes> nodeService =
                new Service<Empty, Nodes>("/rosapi/nodes", Empty.class, Nodes.class, this);
        return nodeService.callBlocking(new Empty()).nodes;
    }

    @Override
    public String[] getTopics() throws InterruptedException {
        Service<Empty, Topics> topicsService =
                new Service<Empty, Topics>("/rosapi/topics", Empty.class, Topics.class, this);
        return topicsService.callBlocking(new Empty()).topics;
    }

    @Override
    public String[] getServices() throws InterruptedException {
        Service<Empty, Services> servicesService =
                new Service<Empty, Services>("/rosapi/services", Empty.class, Services.class, this);
        return servicesService.callBlocking(new Empty()).services;
    }

    @Override
    public TypeDef getTopicMessageDetails(String topic) throws InterruptedException {
        return getTypeDetails(getTopicType(topic));
    }

    @Override
    public TypeDef[] getTopicMessageList(String topic) throws InterruptedException {
        return getTypeList(getTopicType(topic));
    }

    @Override
    public TypeDef getServiceRequestDetails(String service) throws InterruptedException {
        return getTypeDetails(getServiceType(service), "Request", "/rosapi/service_request_details");
    }

    @Override
    public TypeDef[] getServiceRequestList(String service) throws InterruptedException {
        return getTypeList(getServiceType(service), "Request", "/rosapi/service_request_details");
    }

    @Override
    public TypeDef getServiceResponseDetails(String service) throws InterruptedException {
        return getTypeDetails(getServiceType(service), "Response", "/rosapi/service_response_details");
    }

    @Override
    public TypeDef[] getServiceResponseList(String service) throws InterruptedException {
        return getTypeList(getServiceType(service), "Response", "/rosapi/service_response_details");
    }

    @Override
    public TypeDef[] getTypeList(String type) throws InterruptedException {
        return getTypeList(type, "", "/rosapi/message_details");
    }

    @Override
    public TypeDef getTypeDetails(String type) throws InterruptedException {
        return getTypeDetails(type, "", "/rosapi/message_details");
    }

    private TypeDef[] getTypeList(String type, String suffix, String serviceName) throws InterruptedException {
        Service<Type, MessageDetails> messageDetailsService =
                new Service<Type, MessageDetails>(serviceName,
                        Type.class, MessageDetails.class, this);
        return messageDetailsService.callBlocking(new Type(type)).typedefs;
    }

    private TypeDef getTypeDetails(String type, String suffix, String serviceName) throws InterruptedException {
        Service<Type, MessageDetails> messageDetailsService =
                new Service<Type, MessageDetails>(serviceName,
                        Type.class, MessageDetails.class, this);
        return findType(type + suffix, messageDetailsService.callBlocking(new Type(type)).typedefs);
    }

    private String getTopicType(String topic) throws InterruptedException {
        Service<Topic, Type> topicTypeService =
                new Service<Topic, Type>("/rosapi/topic_type",
                        Topic.class, Type.class, this);
        return topicTypeService.callBlocking(new Topic(topic)).type;
    }

    private String getServiceType(String service) throws InterruptedException {
        Service<com.gary.ros.rosapi.message.Service, Type> serviceTypeService =
                new Service<com.gary.ros.rosapi.message.Service, Type>("/rosapi/service_type",
                        com.gary.ros.rosapi.message.Service.class, Type.class, this);
        return serviceTypeService.callBlocking(new com.gary.ros.rosapi.message.Service(service)).type;
    }

    private TypeDef findType(String type, TypeDef[] types) {
        TypeDef result = null;
        for (TypeDef t : types) {
            if (t.type.equals(type)) {
                result = t;
                break;
            }
        }
        //System.out.println("ROSBridgeClient.findType: ");
        //result.print();
        return result;
    }

    @Override
    public void typeMatch(TypeDef t, Class<? extends Message> c) throws InterruptedException {
        if (c == null)
            throw new RuntimeException("No registered message type found for: " + t.type);
        Field[] fields = c.getFields();
        for (int i = 0; i < t.fieldnames.length; i++) {

            // Field names
            String classFieldName = fields[i].getName();
            String typeFieldName = t.fieldnames[i];
            if (!classFieldName.equals(typeFieldName))
                typeMatchError(t, c, "field name", typeFieldName, classFieldName);

            // Array type of field
            boolean typeIsArray = (t.fieldarraylen[i] >= 0);
            boolean fieldIsArray = fields[i].getType().isArray();
            if (typeIsArray != fieldIsArray)
                typeMatchError(t, c, "array mismatch", typeFieldName, classFieldName);

            // Get base type of field
            Class fieldClass = fields[i].getType();
            if (fieldIsArray)
                fieldClass = fields[i].getType().getComponentType();
            String type = t.fieldtypes[i];

            // Field type for primitivesclient
            if (Message.isPrimitive(fieldClass)) {
                if (!TypeDef.match(type, fieldClass))
                    typeMatchError(t, c, "type mismatch", type, fieldClass.getName());
            }

            // Field type for non-primitive classes, and recurse
            else {
                if (!Message.class.isAssignableFrom(fieldClass))
                    throw new RuntimeException("Member " + classFieldName +
                            " of class " + fieldClass.getName() + " does not extend Message.");
                String fieldClassString = ((MessageType) fieldClass.getAnnotation(MessageType.class)).string();
                if (!type.equals(fieldClassString))
                    typeMatchError(t, c, "message type mismatch", type, fieldClassString);
                typeMatch(getTypeDetails(type), fieldClass);
            }
        }
    }

    private void typeMatchError(TypeDef t, Class<? extends Message> c,
                                String error, String tString, String cString) {
        throw new RuntimeException("Type match error between " +
                t.type + " and " + c.getName() + ": " +
                error + ": \'" + tString + "\' does not match \'" + cString + "\'.");
    }

    @Override
    public Object getUnderlyingClient() {
        return client;
    }

}
