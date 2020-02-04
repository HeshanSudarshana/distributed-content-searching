package node;

import java.util.ArrayList;

public class Node {
    private BootstrapServer bootstrapServer;
    private NodeData nodeData;
    private ArrayList<Node> neighbours;
    private ArrayList<String> files;

    public Node(BootstrapServer bootstrapServer, NodeData nodeData, ArrayList<Node> neighbours, ArrayList<String> files) {
        this.bootstrapServer = bootstrapServer;
        this.nodeData = nodeData;
        this.neighbours = neighbours;
        this.files = files;
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
}
