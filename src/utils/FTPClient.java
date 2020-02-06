package utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPClient {

    public FTPClient(String ip, int ftp_port, String filename){
        try{
            Socket ftp_ser_sock = new Socket(ip, ftp_port);
            Thread t_client = new Thread(new ReceiveData(ftp_ser_sock, filename));
            t_client.start();
        } catch (UnknownHostException e) {
            System.out.println("Given node ("+ip+":"+ftp_port+") not exist in the network.");
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("File '"+filename+"' not exist in the node ("+ip+":"+ftp_port+").");
        }
    }
}
