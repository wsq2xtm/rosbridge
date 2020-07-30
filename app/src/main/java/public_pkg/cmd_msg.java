package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
/**
 *2.5通用控制量消息定义
 */
@MessageType(string = "public_pkg/cmd_msg")
public class cmd_msg extends Message {
    public cmd_header_msg header;//消息头
    public short object;//控制对象（可以用于说明控制的对象，例如机械臂、滑台或者机械臂关节、机械臂整体等）
    public short type;//控制类型（可以用于说明控制的类型，例如单点遥控、多点遥控、设值等）
    public String content;//控制量消息内容
}
