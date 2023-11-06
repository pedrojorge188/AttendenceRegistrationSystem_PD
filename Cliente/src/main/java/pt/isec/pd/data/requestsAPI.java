package pt.isec.pd.data;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class requestsAPI{

    private static String ServerAddr;
    private static int ServerPort = 0;
    private static requestsAPI instance;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
    private Socket socket;
    private String myFile;
    private String myUser;
    private List<String> eventsName;
    private PropertyChangeSupport pcs;
    private requestsAPI() {

    }

    public static requestsAPI getInstance() {
        if (instance == null) {
            instance = new requestsAPI();
            instance.setFileName("csvFile");
        }
        return instance;
    }

    public boolean connect(String addr,int port){
        if(addr == null || port <= 0)
            return false;
        ServerPort = port;
        ServerAddr = addr;
        try{
            socket = new Socket(ServerAddr, ServerPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            pcs = new PropertyChangeSupport(this);
            eventsName = new ArrayList<>();
        }catch(Exception exp){
            return false;
        }
        return true;
    }

    public boolean isConnected(){
        return socket.isConnected();
    }

    // <User Sender>
    public boolean send(User.types_msg MSG, String name, String username, String password) throws IOException {

        if(Objects.equals(name, ""))
            name = this.myUser;

        if(Objects.equals(username, ""))
            username = this.myUser;


        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }

        try {
            User userObject = new User(MSG, name, username, password);

            objectOutputStream.writeObject(userObject);
            objectOutputStream.flush();

        } catch (IOException e) {

            System.err.println("Error sending User object: " + e.getMessage());
            pcs.firePropertyChange("SERVER_CLOSE",null,null);
            return false;

        }

        return true;
    }
    // <Event Sender>
    public boolean send(Event.type_event EVT, int code) {

        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }

        try {
            Event eventObject = new Event(EVT,code);
            eventObject.setUser_email(this.getMyUser());
            objectOutputStream.writeObject(eventObject);
            objectOutputStream.flush();
            System.out.println("Sent Event object to the server.");

        } catch (IOException e) {

            pcs.firePropertyChange("SERVER_CLOSE",null,null);
            System.err.println("Error sending Event object: " + e.getMessage());
            return false;

        }

        return true;

    }
    public boolean send(Event event){
        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }
        try {
            objectOutputStream.writeObject(event);
            objectOutputStream.flush();

        } catch (IOException e) {
            pcs.firePropertyChange("SERVER_CLOSE",null,null);
            System.err.println("Error sending Event object: " + e.getMessage());
            return false;
        }

        return true;
    }
    public void receive(ObjectInputStream receive) {
        while(isConnected()){

            try{
                Object receiveObject = receive.readObject();
                if(receiveObject instanceof InfoStatus infoStatus) {
                    switch (infoStatus.getStatus()){
                        case LIST_CREATED_EVENTS -> {
                            eventsName.clear();
                            eventsName.addAll(infoStatus.getEventsName());
                        }
                        case REQUEST_CSV_EVENT -> receiveCSVFile(this.getFileName());
                    }
                    pcs.firePropertyChange(infoStatus.getStatus().toString(), null, null);
                }
            }catch (Exception e){
                pcs.firePropertyChange("SERVER_CLOSE",null,null);
            }
        }
    }

    public void receiveCSVFile(String destinationPath) {
        byte[] fileChunk = new byte[5000];
        InputStream in = null;
        int nbytes;
        System.out.println();

        String requestedCanonicalFilePath = null;
        try {
            requestedCanonicalFilePath = new File(destinationPath).getCanonicalPath();

            try (OutputStream fileOutputStream = new FileOutputStream(requestedCanonicalFilePath)) {
                in = this.socket.getInputStream();

                int totalBytes = 0;
                int nChunks = 0;
                do {
                    nbytes = in.read(fileChunk);

                    if (nbytes > 0) {
                        fileOutputStream.write(fileChunk, 0, nbytes);
                        totalBytes += nbytes;
                        nChunks++;
                    }else {
                        break;
                    }

                } while (nbytes > 0);

                System.out.format("(CSV File Received)(%d bytes)\r\n", totalBytes);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void disconnect() {
        if (socket != null) {
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
                if (objectInputStream != null)
                    objectInputStream.close();
                socket.close();
                System.out.println("Desconectado do servidor");
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Erro ao desconectar: " + e.getMessage());
            }
        }
    }
    public String getMyUser() {return myUser;}
    public String getFileName() {return myFile;}
    public String setFileName(String file) {return myFile = file;}
    public Socket getSocket() {return this.socket;}
    public List<String> getEventsName(){return eventsName;}

    public void addPropertyChangeListener(String property,PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }
}