package com.gary.ros.message;

import org.jboss.netty.buffer.ChannelBuffer;

@MessageType(string = "sensor_msgs/CompressedImage")
public class CompressedImage extends Message{
    public Header header;
    public String format;
    public ChannelBuffer data;


//    public ChannelBuffer format;
//    ChannelBuffer getData();
//
//    void setData(ChannelBuffer var1);
}
