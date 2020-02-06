package utils;

import java.io.*;
import java.net.Socket;

public class ReceiveData implements Runnable {

    private Socket ftp_serv_sock;
    private BufferedReader input = null;
    private String filename;

    public ReceiveData(Socket ftp_serv_sock, String filename)
    {
        this.ftp_serv_sock = ftp_serv_sock;
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(ftp_serv_sock.getInputStream()));
            DataOutputStream output_data = new DataOutputStream(ftp_serv_sock.getOutputStream());
            output_data.writeUTF(filename);
            output_data.flush();
            receive_file();
            input.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot receive requested file data.");
        }
    }

    public void receive_file()
    {
        try {
            int bytesRead;
            DataInputStream serverData = new DataInputStream(ftp_serv_sock.getInputStream());
            String fileName = serverData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = serverData.readLong();
            byte[] buffer = new byte[1024];

            while (size > 0 && (bytesRead = serverData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();

            serverData.close();

            System.out.println("File " + fileName + " successfully downloaded.");

        } catch (IOException ex) {
            System.err.println("Error: FTP Server error. Connection closed.");

        }
    }
}
