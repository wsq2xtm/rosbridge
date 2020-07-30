package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
/**
 *3.1通用读服务定义 响应
 */
@MessageType(string = "public_pkg/read_srvResponse")
public class read_srvResponse extends Message {
    public int result;//读服务状态，例如成功或失败
    public int errCode;//读服务错误码
    public String content;//服务返回值内容
}
