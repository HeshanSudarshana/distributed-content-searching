package utils;

import node.Node;
import node.NodeData;
import request.Request;
import response.JoinOK;
import response.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class OpsUDP {

    String sendPort, receivePort;
    Node node;

    public OpsUDP(String sendPort, String receivePort, Node node) {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        this.node = node;
    }

    //this method will register the current Node in boostrap and return the received nodeData from BS
    public ArrayList<NodeData> RegisterNode(Request request, String receiversIP, String receivingPort) throws IOException {
        System.out.println("Sent a " + request.getType() + " request to " + receiversIP + " on " + receivingPort);
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(sendPort));
        InetAddress receivingNodeAddress = InetAddress.getByName(receiversIP);
        byte[] buffer = request.getRequest().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(receivingPort));
        socket.send(packet);
        buffer = new byte[65536];
        String response;
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(2000);
        try {
            socket.receive(incoming);
            byte[] data = incoming.getData();
            response = new String(data, 0, incoming.getLength());
            if (response != null) {
                socket.close();
                return prosessRegOK(response);
            } else {
                System.out.println("Incorrect response from boostrap server...");
                socket.close();
                return null;
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("No response from boostrap server...");
            socket.close();
            return null;
        }

        //TODO read the ack and act upon

    }

    public ArrayList<NodeData> prosessRegOK(String msg) {
        String[] parts = msg.split(" ");
        ArrayList<NodeData> nodes = new ArrayList<>();
        if (parts.length > 3) {
            for (int i = 3; i < parts.length; i += 2) {
                nodes.add(new NodeData(parts[i], parts[i + 1]));
            }
        }
        return nodes;
    }

    public void processMessage(String msg) throws IOException {
        //TODO use this method to processMessages from other Nodes
        StringTokenizer st = new StringTokenizer(msg, " ");
        st.nextToken();
        String command = st.nextToken();
        synchronized (node.getNeighbours()) {
            if (command.equals("JOIN")) {
                System.out.println("Join message received" + msg);
                processJoin(st);
            } else if (command.equals("JoinOK")) {
                // parse join ok message
                System.out.println("JOIN OK message received" + msg);
            }
        }

    }

    public void sendRequest(Request request, NodeData receiver) throws IOException {
        System.out.println("Sent a " + request.getType() + " request to " + receiver.getIp() + " on " + receiver.getRecvPort());
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(sendPort));
        InetAddress receivingNodeAddress = InetAddress.getByName(receiver.getIp());
        byte[] buffer = request.getRequest().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(receiver.getRecvPort()));
        socket.send(packet);
        socket.close();
    }

    public void sendResponse(Response response, NodeData receiver) throws IOException {
        System.out.println("Sent a " + response.getType() + " request to " + receiver.getIp() + " on " + receiver.getRecvPort());
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(sendPort));
        InetAddress receivingNodeAddress = InetAddress.getByName(receiver.getIp());
        byte[] buffer = response.getResponse().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(receiver.getRecvPort()));
        socket.send(packet);
        socket.close();
    }

    private void processJoin(StringTokenizer st) throws IOException {
        boolean isValid = true;
        String joiningIp = null;
        String joiningPort = null;
        try {
            joiningIp = st.nextToken();
            joiningPort = st.nextToken();
        } catch (NoSuchElementException ex) {
            isValid = false;
        }

        if (joiningIp != null && joiningPort != null && !isNodeExists(joiningIp, joiningPort) && isValid) {
            node.getNeighbours().add(new NodeData(joiningIp, joiningPort));
            JoinOK joinOK = new JoinOK(0);
            sendResponse(joinOK, new NodeData(joiningIp, joiningPort));
        } else {
            JoinOK joinOK = new JoinOK(9999);
            sendResponse(joinOK, new NodeData(joiningIp, joiningPort));
        }

    }

    //this checks whether a node already exists
    private boolean isNodeExists(String Ip, String port) {
        for (NodeData node : node.getNeighbours()) {
            if (node.getIp().equals(Ip) && node.getRecvPort().equals(port)) {
                return true;
            }
        }
        return false;
    }
}
