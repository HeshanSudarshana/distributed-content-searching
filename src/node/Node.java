package node;

import request.RegReq;
import utils.OpsUDP;

import java.io.IOException;
import java.util.ArrayList;

public class Node {
    private BootstrapServer bootstrapServer;
    private NodeData nodeData;
    private ArrayList<Node> neighbours;
    private ArrayList<String> files;
    private OpsUDP opsUDP;
    private boolean isRegistered;

    public Node(BootstrapServer bootstrapServer, NodeData nodeData) {
        this.bootstrapServer = bootstrapServer;
        this.nodeData = nodeData;
        //this.neighbours = neighbours;
        //this.files = files;
        opsUDP = new OpsUDP(nodeData.getSendPort(), nodeData.getRecvPort());
        isRegistered = false;
    }

    public NodeData getNodeData() {
        return nodeData;
    }

    public void setNodeData(NodeData nodeData) {
        this.nodeData = nodeData;
    }

    public BootstrapServer getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(BootstrapServer bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
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

    //registers the current Node in Boostrep Server
    public void regToBS() throws IOException {
        RegReq registerRequest = new RegReq(nodeData.getIp(), nodeData.getRecvPort(), nodeData.getNodeName());
        opsUDP.sendRequest(registerRequest, bootstrapServer.getIp(), bootstrapServer.getPort());
    }
}
