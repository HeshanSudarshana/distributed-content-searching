package node;

import request.*;
import utils.*;

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
    protected FTPServer ftp_server;
    private BootstrapServer bootstrapServer;
    private NodeData nodeData;
    private ArrayList<NodeData> neighbours;
    private ArrayList<DFile> files;
    private ArrayList<String> queries;
    private OpsUDP opsUDP;
    private boolean isRegistered, isRunning;
    private Scanner fileScanner;
    private ArrayList<SearchQuery> queryHistory;
    private Listener listener;
    private DatagramSocket receivingSocket;

    public Node(BootstrapServer bootstrapServer, NodeData nodeData) throws Exception {
        this.bootstrapServer = bootstrapServer;
        this.nodeData = nodeData;
        generateFileList();
        generateQueryList();
        opsUDP = new OpsUDP(nodeData.getSendPort(), nodeData.getRecvPort(), this);
        isRegistered = false;
        this.queryHistory = new ArrayList<>();

//        this.ftp_server = new FTPServer(
//                Integer.parseInt(nodeData.getRecvPort()) + Constants.FTP_PORT_OFFSET,
//                nodeData.getNodeName()
//        );

        this.ftp_server = new FTPServer(
                Integer.parseInt(nodeData.getRecvPort()) + Constants.FTP_PORT_OFFSET,
                nodeData.getNodeName(),
                files
        );

        Thread t = new Thread(ftp_server);
        t.start();
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

    public ArrayList<DFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<DFile> files) {
        this.files = files;
    }

    public void generateFileList() {
        // reading the file and adding it to files
        files = new ArrayList<>();
        ArrayList<DFile> tempFiles = new ArrayList<>();
        try {
            fileScanner = new Scanner(new File(FILE_LIST_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        while (fileScanner.hasNext()) {
            DFile file = new DFile(fileScanner.nextLine());
            tempFiles.add(file);
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
            System.out.println("DFile not found!");
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
            startListening();
            printNeighbors();
            readUserCommands();
        }
    }

    //Node will start to listen for the incoming messages
    private void startListening() throws SocketException {
        System.out.println(nodeData.getNodeName() + " started listening on " + nodeData.getIp() + ":" + nodeData.getRecvPort());
        receivingSocket = new DatagramSocket(Integer.parseInt(nodeData.getRecvPort()));
        receivingSocket.setSoTimeout(5000);
        this.listener = new Listener(isRunning, receivingSocket, opsUDP);
        Thread listenerThread = new Thread(listener);
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

                        System.out.println("started a search for " + searchQuery + ", Timestamp:" + System.currentTimeMillis());
                        searchFile(searchQuery.trim());
                    } else {
                        System.out.println("enter command with the filename");
                    }
                } else if (firstParam.equals("download"))
                {
                    int num_of_parameters = 0;
                    String ip = "";
                    String port = "";
                    String filename = "";

                    try {
                        filename = tokens.nextToken();
                        num_of_parameters++;
                    }
                    catch(Exception e)
                    {

                    }

                    try {
                        ip = tokens.nextToken();
                        num_of_parameters++;
                    }
                    catch(Exception e)
                    {

                    }

                    try {
                        port = tokens.nextToken();
                        num_of_parameters++;
                    }
                    catch(Exception e)
                    {

                    }



                    if (num_of_parameters == 3)
                    {
                        getFile(ip, Integer.parseInt(port), filename);
                    }

                    //TODO handle when only file name provided
                } else if (firstParam.equals("leave")) {
                    leaveNetwork();
                }
            } else {
                System.out.println("invalid command");
            }
        }

    }

    private void printFileList() {
        System.out.println("Files available on this node");
        for (DFile file : files) {
            System.out.println(">" + file.getFileName());
        }
    }

    public boolean isFileExist(String query) {
        for (DFile file : files) {
            if (file.isMatch(query)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<DFile> getFileList(String query) {
        ArrayList<DFile> temp = new ArrayList<>();
        for (DFile file : files) {
            if (file.isMatch(query)) {
                temp.add(file);
            }
        }
        return temp;
    }

    private void searchFile(String query) throws IOException {
        SearchQuery sQuery = new SearchQuery(query, this.nodeData.getIp(), this.nodeData.getRecvPort());
        queryHistory.add(sQuery);
        if (isFileExist(query)) {
            System.out.println("File exists on current Node");
            ArrayList<DFile> searchResult = getFileList(query);
        } else {
            System.out.println("File cannot be found on current node, sending SER request to network..");
            sendSearchRequestToNeighbours(query);
        }

    }

    private void sendSearchRequestToNeighbours(String query) throws IOException {
        for (NodeData ngbNodeData : neighbours) {
            SearchReq searchReq = new SearchReq(query, this.nodeData, 0);
            opsUDP.sendRequest(searchReq, ngbNodeData);
        }
    }

    public boolean checkQueryPassed(SearchQuery searchQuery) {
        refreshHistory();
        for (SearchQuery query : queryHistory) {
            if (query.isEqual(searchQuery) && (searchQuery.getParsedTime() - query.getParsedTime()) < 5000) {
                return true;
            }
        }
        return false;
    }

    private void refreshHistory() {
        ArrayList<SearchQuery> temp = new ArrayList<>();
        for (SearchQuery query : queryHistory) {
            if (System.currentTimeMillis() - query.getParsedTime() < 5000) {
                temp.add(query);
            }
        }
        queryHistory = temp;
    }

    public void addQueryToHistory(SearchQuery query) {
        this.queryHistory.add(query);
    }

    public void getFile(String ip, int port, String filename) {

        try {

            FTPClient ftpClient = new FTPClient(
                    ip,
                    port+Constants.FTP_PORT_OFFSET,
                    filename
            );

            System.out.println("Waiting for file download");
            Thread.sleep(Constants.FILE_DOWNLOAD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveNetwork() throws IOException {
        notifyLeaving();
        UnregReq unregReq = new UnregReq(this.nodeData.getIp(), this.getNodeData().getRecvPort(), this.getNodeData().getNodeName());
        opsUDP.unregisterNode(unregReq, this.bootstrapServer.getIp(), this.bootstrapServer.getPort());
        System.exit(0);
    }

    private void notifyLeaving() throws IOException {
        for (NodeData node : this.neighbours) {
            LeaveReq leaveReq = new LeaveReq(this.nodeData);
            opsUDP.sendRequest(leaveReq, node);
        }
    }

}
