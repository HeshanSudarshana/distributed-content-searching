package utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer implements Runnable{

    private ServerSocket ftp_server_sock;
    private Socket ftp_client_sock;

    private String username;

    public FTPServer(int ftp_port, String username) throws Exception {
        // create socket
        ftp_server_sock = new ServerSocket(ftp_port);
        this.username = username;
    }

    public int getPort(){
        return ftp_server_sock.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ftp_client_sock = ftp_server_sock.accept();
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("Error: Unknown request from a client.");
            }
            Thread t_server = new Thread(new SendingData(ftp_client_sock, username));
            t_server.start();
        }
    }

}
