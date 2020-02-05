package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Oshada on 2020-02-05.
 */
public class Listener implements Runnable {
    boolean isRunning;
    DatagramSocket recvSocket;
    OpsUDP opsUDP;

    public Listener(boolean isRunning, DatagramSocket receiveSocket, OpsUDP opsUDP) {
        this.isRunning = isRunning;
        this.recvSocket = receiveSocket;
        this.opsUDP = opsUDP;
    }

    @Override
    public void run() {
        while (isRunning) {
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try {
                recvSocket.receive(incoming);
                byte[] data = incoming.getData();
                String s = new String(data, 0, incoming.getLength());
                opsUDP.prosessMessage(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        recvSocket.close();
    }
}
