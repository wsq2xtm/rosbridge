package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;

/**
 * 2.2通用模拟量消息定义
 */
@MessageType(string = "public_pkg/status_analog_msg")
public class status_analog_msg extends Message {
    public status_header_msg header;//消息头
    public String content;//模拟量消息内容
}
