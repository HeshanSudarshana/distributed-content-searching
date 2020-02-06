package node;

public class NodeData {
    private String nodeName;
    private String ip;
    private String sendPort;
    private String recvPort;

    public NodeData(String nodeName, String ip, String sendPort, String recvPort) {
        this.nodeName = nodeName;
        this.ip = ip;
        this.sendPort = sendPort;
        this.recvPort = recvPort;
    }

    public NodeData(String ip, String recvPort) {
        this.ip = ip;
        this.recvPort = recvPort;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSendPort() {
        return sendPort;
    }

    public void setSendPort(String sendPort) {
        this.sendPort = sendPort;
    }

    public String getRecvPort() {
        return recvPort;
    }

    public void setRecvPort(String recvPort) {
        this.recvPort = recvPort;
    }

    public boolean isEqual(NodeData nodeData) {
        if (this.ip.equals(nodeData.getIp()) && this.recvPort.equals(nodeData.getRecvPort())) {
            return true;
        } else {
            return false;
        }
    }
}
