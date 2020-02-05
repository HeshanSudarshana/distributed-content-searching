package node;

import request.RegReq;
import utils.Listener;
import utils.OpsUDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Node {
    private BootstrapServer bootstrapServer;
    private NodeData nodeData;
    private ArrayList<NodeData> neighbours;
    private ArrayList<String> files;
    private OpsUDP opsUDP;
    private boolean isRegistered, isRunning;

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

    public ArrayList<NodeData> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<NodeData> neighbours) {
        this.neighbours = neighbours;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    //registers the current Node in Boostrep Server
    private void regToBS() throws IOException {
        RegReq registerRequest = new RegReq(nodeData.getIp(), nodeData.getRecvPort(), nodeData.getNodeName());
        neighbours = opsUDP.RegisterNode(registerRequest, bootstrapServer.getIp(), bootstrapServer.getPort());
    }

    //starts the node functionality
    public void start() throws IOException {
        isRunning = true;
        regToBS();
        if (neighbours != null) {
            printNeighbors();
            startListening();
        }


    }

    //Node will start to listen for the incoming messages
    private void startListening() throws SocketException {
        DatagramSocket receivingSocket = new DatagramSocket(Integer.parseInt(nodeData.getSendPort()));
        receivingSocket.setSoTimeout(5000);
        System.out.println(nodeData.getNodeName() + " started listening on " + nodeData.getIp() + ":" + nodeData.getRecvPort());
        Thread listenerThread = new Thread(new Listener(isRunning, receivingSocket, opsUDP));
        listenerThread.start();
    }

    private void printNeighbors() {
        System.out.println("Neighbors List");
        System.out.println("-----------------------");
        System.out.println("|     IP        |PORT |");
        System.out.println("-----------------------");
        for (NodeData nodeData : neighbours) {
            String ip = nodeData.getIp();
            String port = nodeData.getRecvPort();
            while (ip.length() < 15) {
                ip = ip + " ";
            }
            while (port.length() < 5) {
                port = port + " ";
            }
            System.out.println("|" + ip + "|" + port + "|");
        }
        System.out.println("-----------------------");
    }
}
