import java.io.*;
import java.net.Socket;

class ClientThread extends Thread {
    public Socket clientSocket;

    public DataInputStream dataIn;
    public DataOutputStream dataOut;

    public FileInputStream fileIn;
    public FileOutputStream fileOut;

    public BufferedReader br;
    public String inputFromUser = "";

    public File file;

    public ClientThread(Socket c) {
        try {
            clientSocket = c;
            dataIn = new DataInputStream(c.getInputStream());
            dataOut = new DataOutputStream(c.getOutputStream());

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        String fileName;
        while(true) {
            try {
                String input = dataIn.readUTF();

                if(input.equals("FILE_SENT_FROM_CLIENT")) {
                    fileName = dataIn.readUTF();
                    if (fileName.length() > 0) {
                        fileOut = new FileOutputStream("../servidor/Files/"+fileName);
                        int fileContentLength = dataIn.readInt();
                        if (fileContentLength > 0) {
                            byte[] fileContentBytes = new byte[fileContentLength];
                            dataIn.readFully(fileContentBytes, 0, fileContentBytes.length);
                            fileOut.write(fileContentBytes);
                        }

                        fileOut.close();
                    }
                } else if(input.equals("DOWNLOAD_FILE")) {
                    fileName = dataIn.readUTF();
                    File file = new File("../servidor/Files/"+fileName);
                    fileIn = new FileInputStream(file.getAbsolutePath());
                    byte[] fileBytes = new byte[(int)file.length()];

                    fileIn.read(fileBytes);
                    fileIn.close();

                    dataOut.writeUTF(fileName);

                    dataOut.writeInt(fileBytes.length);
                    dataOut.write(fileBytes);
                } else if(input.equals("EXIT")) {
                    clientSocket.close();
                    System.exit(0);
                } else {
                    System.out.println("Error at server");
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
