package utils;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class SendingData implements Runnable {

    private final Logger LOG = Logger.getLogger(SendingData.class.getName());
    private Socket client_socket;
    private BufferedReader input = null;

    private String file_separator = System.getProperty("file.separator");
    private String username;
    private String rootfolder;

    public SendingData(Socket client, String username) {
        this.client_socket = client;
        this.username = username;
        this.rootfolder = "." + file_separator + this.username;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(
                    new InputStreamReader(
                    client_socket.getInputStream()
                    )
            );

            DataInputStream dIn = new DataInputStream(client_socket.getInputStream());
            String filename = dIn.readUTF();

            if (filename != null) {
                System.out.println("Filename : "+filename);
                sendFile(filename);
            }

            input.close();

        } catch (IOException e) {
//            e.printStackTrace();
            LOG.severe("Error: Cannot send the requested file data.");
        }
    }


    public void sendFile(String filename) {
        try {
            //handle file read
//            String cwd = new File("").getAbsolutePath();
//            System.out.println("Filepath Absolute : "+cwd);
//            System.out.println("Filepath : "+this.rootfolder + file_separator + fileName);

            File req_file = new File(this.rootfolder + file_separator + filename);
            byte[] byte_array = new byte[(int) req_file.length()];

            FileInputStream file_input_stream = new FileInputStream(req_file);
            BufferedInputStream buffered_input_stream = new BufferedInputStream(file_input_stream);
            DataInputStream data_input_stream = new DataInputStream(buffered_input_stream);
            data_input_stream.readFully(byte_array, 0, byte_array.length);

            //file send over socket
            OutputStream os = client_socket.getOutputStream();

            //sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(req_file.getName());
            dos.writeLong(byte_array.length);
            dos.write(byte_array, 0, byte_array.length);

            dos.flush();
            file_input_stream.close();
            LOG.fine("Requested file " + req_file.getName() + " sent to client.");

        } catch (Exception e) {
            LOG.severe("Error: Requested file " + filename + " does not exist!");
//            e.printStackTrace();
        }
    }
}
