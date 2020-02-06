package Main;

import node.BootstrapServer;
import node.Node;
import node.NodeData;

import java.io.IOException;
import java.net.BindException;

/**
 * Created by Oshada on 2020-02-05.
 */

public class Main {

    public static void main(String[] args) {
        String DEFAULT_BS_IP = "127.0.0.1";
        String DEFAULT_BS_PORT = "55555";
        String DEFAULT_NODE_IP = "127.0.0.1";
        String DEFAULT_SEND_PORT = "9002";
        String DEFAULT_RECV_PORT = "10002";
        String DEFAULT_NODE_NAME = "crystal";

        String boostrapIP, boostrapPort, nodeIP, sendPort, recvPort, nodeName;

        if (args.length == 6)
        {

            boostrapIP = args[0];
            boostrapPort = args[1];
            nodeIP = args[2];
            sendPort = args[3];
            recvPort = args[4];
            nodeName = args[5];
        }
        //TODO if provided more than one argument, handle invalid number of arguments
        else
        {
            //if no arguments provided use the default values
            boostrapIP = DEFAULT_BS_IP;
            boostrapPort = DEFAULT_BS_PORT;
            nodeIP = DEFAULT_NODE_IP;
            sendPort = DEFAULT_SEND_PORT;
            recvPort = DEFAULT_RECV_PORT;
            nodeName = DEFAULT_NODE_NAME;
        }

        BootstrapServer bs = new BootstrapServer(boostrapIP, boostrapPort);
        NodeData nodeData = new NodeData(nodeName, nodeIP, sendPort, recvPort);
        try {
            Node node = new Node(bs, nodeData);
            node.start();
        } catch (BindException e) {
            System.out.println("specified port is used by another process, please use a different port");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
