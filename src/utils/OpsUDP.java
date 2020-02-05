package utils;

import request.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class OpsUDP {

    String sendPort, receivePort;

    public OpsUDP(String sendPort, String receivePort) {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
    }

    public void sendRequest(Request request, String receiversIP, String receivingPort) throws IOException {
        System.out.println("Sent a " + request.getType() + " request to " + receiversIP + " on " + receivingPort);
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(sendPort));
        InetAddress receivingNodeAddress = InetAddress.getByName(receiversIP);
        byte[] buffer = request.getRequest().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receivingNodeAddress, Integer.parseInt(receivingPort));
        socket.send(packet);
    }
}
