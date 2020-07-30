package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;

/**
 * 2.3通用数字量消息定义
 */
@MessageType(string = "public_pkg/status_digital_msg")
public class status_digital_msg extends Message {
    public status_header_msg header;//消息头
    public String content;//数字量消息内容
}
