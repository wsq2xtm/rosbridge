package public_pkg;


import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
/**
 * 3.2通用写服务定义  请求
 */
@MessageType(string = "public_pkg/Write_srvRequest")
public class Write_srvRequest extends Message {
    public String srcNodeType;//源节点类型
    public String srcNodeName;//源节点名/源进程名
    public short type;//写服务类型
    public String args;//写内容参数：使用json格式
}
