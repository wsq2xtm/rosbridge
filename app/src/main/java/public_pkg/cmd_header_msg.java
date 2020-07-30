package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
import com.gary.ros.message.Time;

/**
 *2.4通用控制量消息头定义
 */
@MessageType(string = "public_pkg/cmd_header_msg")
public class cmd_header_msg extends Message {
    public long msgID;//消息序列号（用于双方对控制命令和控制返回进行核对）
    public String srcNodeType;//源节点类型（5.1.1）
    public String srcNodeName;//源节点名/源进程名
    public String dstNodeType;//目标节点类型（5.1.1）
    public String dstNodeName;//目标节点名/目标进程名
    public String msgType;//消息类型public_pkg/cmd_msg  public_pkg/cmd_rsp_msg
    public Time msgTime;//消息时间戳
}
