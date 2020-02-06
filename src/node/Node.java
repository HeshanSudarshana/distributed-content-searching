package node;

import request.JoinReq;
import request.RegReq;
import utils.Listener;
import utils.OpsUDP;

import java.io.*;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Node {
    private static String FILE_LIST_PATH = "./resources/FileNames.txt";
    private static String QUERY_LIST_PATH = "./resources/Queries.txt";

    private BootstrapServer bootstrapServer;
    private NodeData nodeData;
    private ArrayList<NodeData> neighbours;
    private ArrayList<String> files;
    private ArrayList<String> queries;
    private OpsUDP opsUDP;
    private boolean isRegistered, isRunning;
    private Scanner fileScanner;

    public Node(BootstrapServer bootstrapServer, NodeData nodeData) {
        this.bootstrapServer = bootstrapServer;
        this.nodeData = nodeData;
        generateFileList();
        generateQueryList();
        opsUDP = new OpsUDP(nodeData.getSendPort(), nodeData.getRecvPort(), this);
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

    public void generateFileList() {
        // reading the file and adding it to files
        files = new ArrayList<>();
        ArrayList <String> tempFiles = new ArrayList<>();
        try {
            fileScanner = new Scanner(new File(FILE_LIST_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        while (fileScanner.hasNext()) {
            tempFiles.add(fileScanner.nextLine());
        }
        fileScanner.close();

        // select random files from if (3-5)
        Random randomGen = new Random();
        int num = randomGen.nextInt(3) + 3;
        int i=0;
        while (i<num) {
            int index = randomGen.nextInt(tempFiles.size());
            if (!files.contains(tempFiles.get(index))) {
                files.add(tempFiles.get(index));
                i++;
            }
        }
    }

    public void generateQueryList() {
        queries = new ArrayList<>();
        try {
            fileScanner = new Scanner(new File(QUERY_LIST_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        while (fileScanner.hasNext()) {
            queries.add(fileScanner.nextLine());
        }
        fileScanner.close();
    }

    //registers the current Node in Boostrep Server
    private void regToBS() throws IOException {
        RegReq registerRequest = new RegReq(nodeData.getIp(), nodeData.getRecvPort(), nodeData.getNodeName());
        neighbours = opsUDP.RegisterNode(registerRequest, bootstrapServer.getIp(), bootstrapServer.getPort());
    }

    //starts the node functionality
    public void start() throws IOException, InterruptedException {
        isRunning = true;
        printFileList();
        regToBS();
        if (neighbours != null) {
            joinNetwork();
            printNeighbors();
            startListening();
            readUserCommands();
        }


    }

    //Node will start to listen for the incoming messages
    private void startListening() throws SocketException {
        DatagramSocket receivingSocket = new DatagramSocket(Integer.parseInt(nodeData.getRecvPort()));
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

    private void joinNetwork() throws IOException {
        ArrayList<NodeData> tempNeighbors = new ArrayList<NodeData>();
        for (NodeData neighborData : neighbours) {
            JoinReq joinReq = new JoinReq(nodeData);
            if (opsUDP.joinToNode(neighborData, joinReq)) {
                tempNeighbors.add(neighborData);
                System.out.println("received JoinOK from " + neighborData.getIp() + ":" + neighborData.getRecvPort());
            } else {
                System.out.println("Join failed to " + neighborData.getIp() + ":" + neighborData.getRecvPort());
            }
        }
        this.neighbours = tempNeighbors;
    }

    private void readUserCommands() throws IOException {
        while (isRunning) {
            System.out.println(">");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String command = reader.readLine();
            StringTokenizer tokens = new StringTokenizer(command, " ");
            int tokenCount = tokens.countTokens();
            if (tokenCount > 0) {
                String firstParam = tokens.nextToken();
                if (firstParam.equals("neighbours")) {
                    printNeighbors();
                } else if (firstParam.equals("search")) {
                    if (tokens.hasMoreTokens()) {
                        String searchQuery = "";
                        while (tokens.hasMoreTokens()) {
                            searchQuery += tokens.nextToken() + " ";
                        }
                        System.out.println("started a search for " + searchQuery);
                    } else {
                        System.out.println("enter command with the filename");
                    }
                }
            } else {
                System.out.println("invalid command");
            }
        }

    }

    private void printFileList() {
        System.out.println("Files available on this node");
        for (String file : files) {
            System.out.println(">" + file);
        }
    }
}
