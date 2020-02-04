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
}
