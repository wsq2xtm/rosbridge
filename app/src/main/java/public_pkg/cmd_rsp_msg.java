package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;

/**
 *2.6通用控制量返回消息定义
 */
@MessageType(string = "public_pkg/cmd_rsp_msg")
public class cmd_rsp_msg extends Message {
    public cmd_header_msg header;//消息头
    public short object;//控制对象
    public short type;//控制类型
    public int result;//控制结果（用于说明控制的结果，例如成功或失败）
    public int errCode;//控制错误码（用于说明控制错误的原因）
    public String content;//控制量返回消息内容
}
