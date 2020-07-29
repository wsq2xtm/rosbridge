package public_pkg;
/**
 * 节点数据，心跳用的
 */
public class NodeMsg{
    public String srcNodeType;//源节点类型
    public String srcNodeName;//数字量消息内容
    public long lastReceivedTime;//最后收到消息的时间
}
