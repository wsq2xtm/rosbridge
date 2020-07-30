package com.dadac.testrosbridge.services;


import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;

@MessageType(string = "com.dadac.testrosbridge.services/LongTestRequest")
public class LongTestRequest extends Message {
    public int a;
    public int b;
}
