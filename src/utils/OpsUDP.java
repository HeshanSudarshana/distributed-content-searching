package utils;

import node.NodeData;
import request.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class OpsUDP {

    String sendPort, receivePort;

    public OpsUDP(String sendPort, String receivePort) {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
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
            //nodes = new com.distributed.app.Node[(parts.length - 3)/2];
            for (int i = 3; i < parts.length; i += 2) {
                //nodes[(i - 3)/2] = new com.distributed.app.Node(parts[i], Integer.valueOf(parts[i + 1]));
                nodes.add(new NodeData(parts[i], parts[i + 1]));
            }
        }
        return nodes;
    }

    public void processMessage(String msg) {

    }
}
