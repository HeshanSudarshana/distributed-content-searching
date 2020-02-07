package utils;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.io.File;
import java.util.Scanner;

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

            String file_content = getFileContent(fileName);
            System.out.println("Encode SHA Value (Client): "+getSHA256(file_content));
            System.out.println("----- Requested file " + fileName + " received from server. ----- \n");

        } catch (IOException ex) {
            System.err.println("Error: FTP Server error. Connection closed.");

        }
    }

    public String getFileContent(String downloaded_filename) throws IOException {
        File file = new File(downloaded_filename);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null)
            System.out.println(st);

//        File file = new File(downloaded_filename);
        Scanner sc = new Scanner(file);
        String file_content = "";
        while (sc.hasNextLine()) {
            file_content+= sc.nextLine();
        }

        return file_content;
    }

    public String getSHA256(String data){
        StringBuffer sb = new StringBuffer();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
