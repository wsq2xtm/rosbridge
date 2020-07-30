package com.dadac.testrosbridge.services;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;

@MessageType(string = "com.dadac.testrosbridge.services/LongTestResponse")
public class LongTestResponse extends Message {
    public int sum;
}
