package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
import com.gary.ros.message.Time;

/**
 * 2.1通用模拟量和数字量消息头定义
 */
@MessageType(string = "public_pkg/status_header_msg")
public class status_header_msg extends Message {
    public String srcNodeType;//源节点类型（5.1.1节点类型）
    public String srcNodeName;//源节点名
    public String msgType;//消息类型public_pkg/status_analog_msg  public_pkg/status_digital_msg
    public Time msgTime;//消息时间戳
}
