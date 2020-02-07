package utils;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.Random;

public class SendingData implements Runnable {

    private final Logger LOG = Logger.getLogger(SendingData.class.getName());
    private Socket client_socket;
    private BufferedReader input = null;

    private String file_separator = System.getProperty("file.separator");
    private String username;
    private String rootfolder;

    private ArrayList<DFile> files;

    public SendingData(Socket client, String username) {
        this.client_socket = client;
        this.username = username;
        this.rootfolder = "." + file_separator + this.username;
    }

    public SendingData(Socket client, String username, ArrayList<DFile> files) {
        this.client_socket = client;
        this.username = username;
        this.rootfolder = "." + file_separator + this.username;
        this.files = files;
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
                if(checkFilesNameExistsInServer(filename)) {
                    sendFile(filename);
                }
                else
                {
                    System.err.println("Error: Server do not have the File: "+filename);
                }
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

            int filesize = getRandomNumber(2 , 10);
            String file_content_string = generateFileString(filesize);
            createDirectory(rootfolder + file_separator );
            generateFile(this.rootfolder + file_separator + filename, file_content_string);

            String encoded_sha_str = getSHA256(file_content_string);
            System.out.println("File Size: "+filesize);
            System.out.println("Encode SHA Value (Server): "+encoded_sha_str);

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
            System.out.println("----- Requested file " + req_file.getName() + " sent to client. ----- \n");

        } catch (Exception e) {
            LOG.severe("Error: Requested file " + filename + " does not exist!");
//            e.printStackTrace();
        }
    }

    public String generateFileString(int size)
    {
        System.out.println("Generating File String...");
//        int oneMBFileCharatcters = 1048576;
        int oneMBFileCharatcters = 10;

        Random random = new Random();
        int rand_temp ;
        String temp = "";
//        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        String[] alphabet = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        for (int index = 0; index < size*oneMBFileCharatcters; index++)
        {
            rand_temp =  random.nextInt(61);
            temp+=alphabet[rand_temp];
        }
//        System.out.println("Generated File String : "+ temp);
        System.out.println("File String Generation Completed!");
        return temp;
    }

    public int getRandomNumber(int min, int max)
    {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public void generateFile(String filepath, String file_content)
    {
        try {
            System.out.println("Generating The File...");
            File new_text_file = new File(filepath);
            FileWriter fw = new FileWriter(new_text_file);
            fw.write(file_content);
            fw.close();
            System.out.println("File Generation Completed!");
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }


    public void createDirectory(String directory_path) {
        //To create single directory/folder
        File file = new File(directory_path);

        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

//        File files = new File("D:\\Directory2\\Sub2\\Sub-Sub2");
//        if (!files.exists()) {
//            if (files.mkdirs()) {
//                System.out.println("Multiple directories are created!");
//            } else {
//                System.out.println("Failed to create multiple directories!");
//            }
//        }

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

    public Boolean checkFilesNameExistsInServer(String filename_given)
    {
        Boolean exists = false;

        for (DFile file : files) {
//            System.out.println("SEARCH : Current File : "+ file.getFileName()+", given query file : "+ filename_given);
            if( (file.getFileName().replace(" ", "_")).trim().equals(filename_given.trim()))
            {
//                System.out.println("FOUND : Current File : "+ file.getFileName()+", given query file : "+ filename_given);
                exists = true;
                break;
            }
        }
        return exists;
    }
}
