package node;

import java.util.ArrayList;

public class Node {
    private BootstrapServer bootstrapServer;
    private String nodeName;
    private String ip;
    private String sendPort;
    private String recvPort;
    private ArrayList<Node> neighbours;
    private ArrayList<String> files;

    public Node(BootstrapServer bootstrapServer, String nodeName, String ip, String sendPort, String recvPort, ArrayList<Node> neighbours, ArrayList<String> files) {
        this.bootstrapServer = bootstrapServer;
        this.nodeName = nodeName;
        this.ip = ip;
        this.sendPort = sendPort;
        this.recvPort = recvPort;
        this.neighbours = neighbours;
        this.files = files;
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

    public BootstrapServer getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(BootstrapServer bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
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

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<Node> neighbours) {
        this.neighbours = neighbours;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }
}
