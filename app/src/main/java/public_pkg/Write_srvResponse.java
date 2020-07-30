package public_pkg;

import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
/**
 * 3.2通用写服务定义  响应
 */
@MessageType(string = "public_pkg/Write_srvResponse")
public class Write_srvResponse extends Message {
    public int result;//写服务状态，例如成功或失败
    public int errCode;//写服务错误码
    public String content;//服务返回值内容
}
