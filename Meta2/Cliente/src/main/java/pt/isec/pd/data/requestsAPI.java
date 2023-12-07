package pt.isec.pd.data;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
    private List<String> attendanceRecords;
    private List<String> userAttendanceRecords;
    private PropertyChangeSupport pcs;
    private int event_code;

    private requestsAPI() {}

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
    public boolean send(User.types_msg MSG, int uid,  String name, String username, String password) throws IOException {

        if(Objects.equals(name, ""))
            name = this.myUser;

        if(Objects.equals(username, ""))
            username = this.myUser;


        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }

        try {
            User userObject = new User(MSG, uid, name, username, password);

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
                        case LIST_REGISTERED_ATTENDANCE -> {
                            attendanceRecords = new ArrayList<>();
                            attendanceRecords.addAll(infoStatus.getAttendanceRecords());
                        }
                        case GET_HISTORY -> {
                            userAttendanceRecords = new ArrayList<>();
                            userAttendanceRecords.addAll(infoStatus.getUserAttendanceRecords());
                        }
                        case REQUEST_CSV_EVENT ->
                            receiveCSVFile(this.getFileName());
                        case LOGIN_MADE_USER, LOGIN_MADE_ADMIN -> this.myUser = infoStatus.getMsg_log();
                        case LOGIN_FAIL -> System.exit(-1);
                        case GENERATE_CODE_MADE ->
                            setEventCode(Integer.parseInt(infoStatus.getMsg_log()));

                    }
                    System.out.println(infoStatus.getStatus().toString());
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

                do {
                    nbytes = in.read(fileChunk);

                    if (nbytes > -1) {
                        fileOutputStream.write(fileChunk, 0, nbytes);
                        totalBytes += nbytes;
                    }
                    break;
                } while (true);

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

    public void setEventCode(int code){
        this.event_code = code;
    }
    public int getEventCode() {
        return this.event_code;
    }
    public String getMyUser() {return myUser;}
    public String getFileName() {return myFile;}
    public String setFileName(String file) {return myFile = file;}
    public Socket getSocket() {return this.socket;}
    public List<String> getEventsName(){return eventsName;}
    public List<String> getUserAttendanceRecords() {
        return userAttendanceRecords;
    }
    public List<String> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void addPropertyChangeListener(String property,PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }

}