package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
import com.gary.ros.message.Time;

/**
 * 2.7.心跳消息定义
 */
@MessageType(string = "public_pkg/heart_beat_msg")
public class heart_beat_msg extends Message {
    public String srcNodeType;//源节点类型
    public String srcNodeName;//源节点名
    public String msgType;//消息类型
    public Time msgTime;//消息时间戳
    public int processID;//源节点进程ID
}
