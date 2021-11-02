import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.text.*;

public class Client {
    public Socket clientSocket;
    private int port = 5050;

    public DataInputStream dataIn;
    public DataOutputStream dataOut;

    public FileInputStream fileIn;
    public FileOutputStream fileOut;

    public BufferedReader bufReader;
    public String inputFromUser = "";
    final File[] fileToSend = new File[1];

    public static void main(String[]args) {
        Client client = new Client();
        client.connect();
    }

    public void connect() {
        try {
            InputStreamReader inStream = new InputStreamReader(System.in);
            bufReader = new BufferedReader(inStream);
            clientSocket = new Socket("localhost", port);

            dataIn = new DataInputStream(clientSocket.getInputStream());
            dataOut = new DataOutputStream(clientSocket.getOutputStream());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: unable to connect to server\n");
        }

        while(true) {
            try {
                System.out.println(
                        "Options:\n \n\t1. Upload file to server \n\t2. Download file from server \n\t3. List files in Client \n\t4. List files in Server\n\t5. Quit \n\t\n> Type the number of your choice: "
                );

                inputFromUser = bufReader.readLine();
                int s = Integer.parseInt(inputFromUser);

                switch(s) {
                    case 1:
                        chooseFile();
                        uploadFile();
                        break;
                    case 2:
                        downloadFile();
                        break;
                    case 3:
                        listFilesClient();
                        break;
                    case 4:
                        listFilesServer();
                        break;
                    case 5:
                        quit();
                        break;
                    default:
                        System.out.println("\n[-]Invalid option!\n");
                }

            } catch (Exception ex) {
                System.out.println("Error: check your inputs");
                ex.printStackTrace();
            }
        }
    }

    /*
     * uploadFile() eh responsavel por fazer o upload de um arquivo do cliente para
     * o servidor
     *
     */
    public void uploadFile() {
        try {
            if (fileToSend[0] != null) {
                fileIn = new FileInputStream(fileToSend[0].getAbsolutePath());
                String fileName = fileToSend[0].getName();
                byte[] fileBytes = new byte[(int)fileToSend[0].length()];

                fileIn.read(fileBytes);
                fileIn.close();

                dataOut.writeUTF("FILE_SENT_FROM_CLIENT");
                dataOut.writeUTF(fileName);
                dataOut.writeInt(fileBytes.length);
                dataOut.write(fileBytes);

                System.out.println("\n[+] File upload successfully!\n");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /*
     * downloadFile() eh responsavel por fazer o download de um arquivo do servidor
     * para o cliente
     */
    public void downloadFile() {
        try {
            String fileName = "";
            String fileData = "";
            System.out.println("Enter the file name: ");
            fileName = bufReader.readLine();
			
            dataOut.writeUTF("DOWNLOAD_FILE");
            dataOut.writeUTF(fileName);
			
            fileData = dataIn.readUTF();

            if (fileData.equals("")) {
                System.out.println("\n[-]No such file\n");
            }
            else {
                fileOut = new FileOutputStream("../cliente/Files/"+fileName);
                int fileContentLength = dataIn.readInt();

                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    dataIn.readFully(fileContentBytes, 0, fileContentBytes.length);
                    fileOut.write(fileContentBytes);
                }

                fileOut.close();
                System.out.println("\n[+] File download successfully!\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void listFilesClient() {
        File dir = new File("../cliente/Files");
        File[] listOfFiles = dir.listFiles();

        System.out.println("-------------------------------------");
        System.out.println("File list from Client:\n");

        for(File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("\t" + file.getName() + "\t" + file.length() + " bytes");
            }
        }

        System.out.println("\n-------------------------------------");
    }

    public void listFilesServer() {
        String resp = "No files found";

        try {
            dataOut.writeUTF("LIST_SERVER_FILES");
            resp = dataIn.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        System.out.println("-------------------------------------");
        System.out.println("File list from Server:\n");
        System.out.println(resp);
        System.out.println("\n-------------------------------------");
    }

    public void quit() throws IOException {
        dataOut.writeUTF("EXIT");
        clientSocket.close();
        System.exit(0);
    }
    
    public void chooseFile(){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Choose a file to send.");
        if (jFileChooser.showOpenDialog(null)  == JFileChooser.APPROVE_OPTION) {
            fileToSend[0] = jFileChooser.getSelectedFile();
        }
    }
    
    public static String humanReadableByteCountBin(long bytes) {
	long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        
        if (absB < 1024) {
            return bytes + " B";
        }

        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }

        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
