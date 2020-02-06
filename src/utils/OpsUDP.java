package utils;

import node.Node;
import node.NodeData;
import request.JoinReq;
import request.LeaveReq;
import request.Request;
import request.SearchReq;
import response.JoinOK;
import response.LeaveOK;
import response.Response;
import response.SearchOK;

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
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(receivePort));
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
    }

    public ArrayList<NodeData> prosessRegOK(String msg) throws IOException {
        StringTokenizer st = new StringTokenizer(msg, " ");
        String length = st.nextToken();
        st.nextToken();
        String noNodes = st.nextToken();
        if (noNodes.equals("0")) {
            System.out.println("received RegOK from bootstrap server without Nodes");
            return new ArrayList<NodeData>();
        } else if (noNodes.equals("1")) {
            ArrayList<NodeData> nodes = new ArrayList<>();
            NodeData node1 = new NodeData(st.nextToken(), st.nextToken());
            nodes.add(node1);
            System.out.println("received RegOK from bootstrap server and Nodes " + node1.getIp() + ":" + node1.getRecvPort());
            return nodes;
        } else if (noNodes.equals("2")) {
            ArrayList<NodeData> nodes = new ArrayList<>();
            NodeData node1 = new NodeData(st.nextToken(), st.nextToken());
            nodes.add(node1);
            NodeData node2 = new NodeData(st.nextToken(), st.nextToken());
            nodes.add(node2);
            System.out.println("received RegOK from bootstrap server and Nodes " + node1.getIp() + ":" + node1.getRecvPort() + " " + node2.getIp() + ":" + node2.getRecvPort());
            return nodes;
        } else if (noNodes.equals("9999")) {
            System.out.println("Error in the registration command");
            return null;
        } else if (noNodes.equals("9998")) {
            System.out.println("Registering Failed: This node is already registered, unregistering...");
            return null;
        } else if (noNodes.equals("9997")) {
            System.out.println("Registering Failed: Already registered to another user, try different IP & Port");
            return null;
        } else {
            System.out.println("Registration Failed: Can't register any new nodes, BS is full");
            return null;
        }
    }

    public void unregisterNode(Request request, String receiversIP, String receivingPort) throws IOException {
        System.out.println("Sent UNREG request to boostrap server");
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
                StringTokenizer st = new StringTokenizer(response, " ");
                st.nextToken();
                if (st.nextToken().equals("UNROK")) {
                    System.out.println("UNROK message received from boostrap");
                    processUNROK(st);
                } else {
                    System.out.println("invalid response: " + response);
                }
                socket.close();
            } else {
                System.out.println("Incorrect response from boostrap server...");
                socket.close();
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("No response from boostrap server...");
            socket.close();
        } catch (IOException e) {
            socket.close();
            e.printStackTrace();
        }

    }


    public void processMessage(String msg, InetAddress incomingIP, int port) throws IOException {
        //TODO use this method to processMessages from other Nodes
        StringTokenizer st = new StringTokenizer(msg, " ");
        st.nextToken();
        String command = st.nextToken();
        synchronized (node.getNeighbours()) {
            if (command.equals("JOIN")) {
                System.out.println("JOIN message received " + msg);
                processJOIN(st);
            } else if (command.equals("JOINOK")) {
                System.out.println("JOINOK message received " + msg);
                processJOINOK(st, incomingIP, port);
            } else if (command.equals("SER")) {
                System.out.println("SER message received " + msg);
                processSearch(st);
            } else if (command.equals("SEROK")) {
                System.out.println("SEARCHOK message received " + msg + " Timestamp:" + System.currentTimeMillis());
                //add the code here to display the result
            } else if (command.equals("LEAVE")) {
                System.out.println("LEAVE message received " + msg);
                processLeave(st);
            } else if (command.equals("LEAVEOK")) {
                System.out.println("LEAVEOK message received " + msg);

            }
        }

    }


    private void processUNROK(StringTokenizer st) {
        String value = st.nextToken();
        if (value.equals("0")) {
            System.out.println("unregistered successfully");
        } else {
            System.out.println("Failed Unregistering the node");
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
        String res = response.getResponse();
        byte[] buffer = res.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(receiver.getRecvPort()));
        socket.send(packet);
        socket.close();
    }

    // this executes when we receive a JOIN request from another Node
    private void processJOIN(StringTokenizer st) throws IOException {
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

    private void processSearch(StringTokenizer st) throws IOException {
        String searchersIP = st.nextToken();
        String searchersPort = st.nextToken();
        String query = "";
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            Character lastChar = str.charAt(str.length()-1);
            if (lastChar.equals('"')) {
                str = str.substring(0, str.length()-1);
                query += str;
                break;
            } else {
                query += str + " ";
            }
        }
        int hops = Integer.parseInt(st.nextToken());
        String searchQuery = query.substring(1, query.length());
        SearchQuery sQuery = new SearchQuery(searchQuery, searchersIP, searchersPort);
        if (!this.node.checkQueryPassed(sQuery)) {
            this.node.addQueryToHistory(sQuery);
            if (this.node.isFileExist(searchQuery)) {
                //Send SEROK to searching node
                ArrayList<DFile> matchingFiles = this.node.getFileList(searchQuery);
                SearchOK searchOK = new SearchOK(matchingFiles, this.node.getNodeData(), hops + 1);
                sendResponse(searchOK, new NodeData(searchersIP, searchersPort));
            } else {
                sendSearchRequestToNeighbours(searchQuery, new NodeData(searchersIP, searchersPort), hops + 1);
            }
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

    private boolean processJOINOK(StringTokenizer st, InetAddress incomingIP, int port) {
        if (st.nextToken().equals("0")) {
            System.out.println("join ok from " + incomingIP.toString() + " " + Integer.toString(port));
            return true;
        } else {
            System.out.println("Joining Failed");
            return false;
        }
    }

    public boolean joinToNode(NodeData nodeData, JoinReq request) throws IOException {
        System.out.println("Sent a " + request.getType() + " request to " + nodeData.getIp() + " on " + nodeData.getRecvPort());
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(receivePort));
        InetAddress receivingNodeAddress = InetAddress.getByName(nodeData.getIp());
        byte[] buffer = request.getRequest().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(nodeData.getRecvPort()));
        socket.send(packet);
        buffer = new byte[65536];
        String response;
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(2000);
        try {
            socket.receive(incoming);
            byte[] data = incoming.getData();
            response = new String(data, 0, incoming.getLength());
            socket.close();
            try {
                StringTokenizer st = new StringTokenizer(response, " ");
                st.nextToken();
                if (st.nextToken().equals("JOINOK")) {
                    if (st.nextToken().equals("0")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("No response from " + nodeData.getIp() + ":" + nodeData.getIp());
            socket.close();
            return false;
        }
    }

    private void sendSearchRequestToNeighbours(String query, NodeData searchersNodeData, int hopCount) throws IOException {
        for (NodeData ngbNodeData : this.node.getNeighbours()) {
            SearchReq searchReq = new SearchReq(query, searchersNodeData, hopCount);
            sendRequest(searchReq, ngbNodeData);
        }
    }

    private void processLeave(StringTokenizer st) throws IOException {
        boolean isSuccess = true;
        NodeData nodeData = null;
        try {
            nodeData = new NodeData(st.nextToken(), st.nextToken());
        } catch (Exception e) {
            isSuccess = false;
        }
        if (nodeData != null) {
            if (removeNeighbour(nodeData)) {
                //send leave ok with success
                LeaveOK leaveOK = new LeaveOK(0);
                sendResponse(leaveOK, nodeData);
            } else {
                //send leave ok with failed
                LeaveOK leaveOK = new LeaveOK(9999);
                sendResponse(leaveOK, nodeData);
            }
        }
    }

    private boolean removeNeighbour(NodeData data) {
        boolean isSuccess = false;
        ArrayList<NodeData> temp = new ArrayList<>();
        for (NodeData nodeData : this.node.getNeighbours()) {
            if (nodeData.isEqual(data)) {
                isSuccess = true;
            } else {
                temp.add(nodeData);
            }
        }
        this.node.setNeighbours(temp);
        return isSuccess;
    }

    public void leaveNode(LeaveReq leaveReq, NodeData nodeData) throws IOException {
        System.out.println("Sent LEAVE request to " + nodeData.getIp() + ":" + nodeData.getRecvPort());
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(sendPort));
        InetAddress receivingNodeAddress = InetAddress.getByName(nodeData.getIp());
        byte[] buffer = leaveReq.getRequest().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(nodeData.getRecvPort()));
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
                StringTokenizer st = new StringTokenizer(response, " ");
                st.nextToken();
                if (st.nextToken().equals("LEAVEOK")) {
                    System.out.println("LEAVEOK message received from " + nodeData.getIp() + ":" + nodeData.getRecvPort());
                    removeNeighbour(nodeData);
                } else {
                    System.out.println("invalid response: " + response);
                }
                socket.close();
            } else {
                System.out.println("Incorrect response from " + nodeData.getIp() + ":" + nodeData.getRecvPort());
                socket.close();
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("No response from " + nodeData.getIp() + ":" + nodeData.getRecvPort());
            socket.close();
        } catch (IOException e) {
            socket.close();
            e.printStackTrace();
        }
    }
}
