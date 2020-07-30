package public_pkg;


import com.gary.ros.message.Message;
import com.gary.ros.message.MessageType;
/**
 *3.1通用读服务定义 请求
 */
@MessageType(string = "public_pkg/read_srvRequest")
public class read_srvRequest extends Message {
    public String srcNodeType;//源节点类型
    public String srcNodeName;//源节点名/源进程名
    public String args;//扩展参数：使用json格式，允许为空
}
