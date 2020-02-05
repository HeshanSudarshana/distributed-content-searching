package utils;

import node.Node;
import request.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class OpsUDP {

    String sendPort, receivePort;

    public OpsUDP(String sendPort, String receivePort) {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
    }

    public ArrayList<Node> RegisterNode(Request request, String receiversIP, String receivingPort) throws IOException {
        System.out.println("Sent a " + request.getType() + " request to " + receiversIP + " on " + receivingPort);
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(sendPort));
        InetAddress receivingNodeAddress = InetAddress.getByName(receiversIP);
        byte[] buffer = request.getRequest().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(receivingPort));
        socket.send(packet);
        buffer = new byte[65536];
        String s;
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        socket.receive(incoming);
        byte[] data = incoming.getData();
        s = new String(data, 0, incoming.getLength());
        //TODO read the ack and act upon
        socket.close();
        return null;
    }

    public void prosessMessage(String msg) {
        System.out.println("received : " + msg);
    }
}
