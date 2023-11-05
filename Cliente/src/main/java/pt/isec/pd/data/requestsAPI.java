package pt.isec.pd.data;
import pt.isec.pd.threads.ServerHandler;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.*;
import java.util.Objects;

import static pt.isec.pd.data.InfoStatus.types_status.*;

public class requestsAPI{

    private static String ServerAddr;
    private static int ServerPort = 0;
    private static requestsAPI instance;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
    private Socket socket;
    private String myUser;
    private PropertyChangeSupport pcs;
    private requestsAPI() {

    }

    public static requestsAPI getInstance() {
        if (instance == null) {
            instance = new requestsAPI();
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
            System.out.println("Sent Event object to the server.");
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

                if(receiveObject instanceof InfoStatus infoStatus){
                    pcs.firePropertyChange(infoStatus.getStatus().toString(),null,null);
                    switch (infoStatus.getStatus()){
                        case LOGIN_MADE_USER -> {
                            this.myUser = infoStatus.getMsg_log();
                            System.out.println("[SERVER] Login Made (normal client)!");
                        }
                        case LOGIN_MADE_ADMIN -> {
                            this.myUser = infoStatus.getMsg_log();
                            System.out.println("[SERVER] Login Made (admin client)!");
                        }
                        case LOGIN_FAIL -> {
                            this.disconnect();
                            System.out.println("[SERVER] Login Fail!");
                            System.exit(1);
                        }
                        case REGISTER_MADE -> {
                            this.myUser = infoStatus.getMsg_log();
                            System.out.println("[SERVER] Register Made!");
                        }
                        case REGISTER_FAIL -> {
                            System.out.println("[SERVER] Register Fail!");
                        }
                        case CHANGES_MADE -> {
                            this.myUser = infoStatus.getMsg_log();
                            System.out.println("[SERVER] Changes Made!");
                        }
                        case CHANGES_FAIL -> {
                            System.out.println("[SERVER] Changes Fail!");
                        }
                        case CODE_SEND_MADE -> {
                            System.out.println("[SERVER] Code SEND!");
                        }
                        case CODE_SEND_FAIL -> {
                            System.out.println("[SERVER] Code SEND Fail!");
                        }
                        case EDIT_EVENT_MADE ->{
                            System.out.println("[SERVER] Event edited");
                        }
                        case EDIT_EVENT_FAIL -> {
                            System.out.println("[SERVER] Event edit failed");
                        }
                        case CREATE_EVENT_MADE -> {
                            System.out.println("[SERVER] Event Created");
                        }
                        case CREATE_EVENT_FAIL -> {
                            System.out.println("[SERVER] Event Creation Failed");
                        }
                        case DELETE_EVENT_MADE -> {
                            System.out.println("[SERVER] Event Deleted");
                        }
                        case DELETE_EVENT_FAIL -> {
                            System.out.println("[SERVER] Event Deleted fail");
                        }
                        case LIST_REGISTERED_ATTENDANCE -> {
                            System.out.println("[SERVER] Attendances listed");
                        }
                        case LIST_REGISTERED_ATTENDANCE_FAIL -> {
                            System.out.println("[SERVER] Attendances list fail");
                        }
                        case GENERATE_CODE_MADE -> {
                            System.out.println("[SERVER] Code generated");
                        }
                        case GENERATE_CODE_FAIL -> {
                            System.out.println("[SERVER] Code generate fail");
                        }
                        case REQUEST_CSV_EVENT -> {
                            System.out.println("[SERVER] REQUEST_CSV_EVENT");
                        }
                        case GET_HISTORY -> {
                            System.out.println("[SERVER] GET HISTORY");
                        }
                        case GET_HISTORY_FAIL -> {
                            System.out.println("[SERVER] GET HISTORY FAIL");
                        }
                        case LIST_CREATED_EVENTS -> {
                            System.out.println("[SERVER] LIST_CREATED_EVENTS");
                        }
                        case LIST_CREATED_EVENTS_FAIL -> {
                            System.out.println("[SERVER] LIST_CREATED_EVENTS_FAIL");
                        }
                        case DELETE_ATTENDANCE_MADE -> {
                            System.out.println("[SERVER] DELETE_ATTENDANCE_MADE");
                        }
                        case DELETE_ATTENDANCE_FAIL -> {
                            System.out.println("[SERVER] DELETE_ATTENDANCE_FAIL");
                        }
                        case INSERT_ATTENDANCE_MADE -> {
                            System.out.println("[SERVER] INSERT_ATTENDANCE_MADE");
                        }
                        case INSERT_ATTENDANCE_FAIL -> {
                            System.out.println("[SERVER] INSERT_ATTENDANCE_FAIL");
                        }
                        case MSG_STACK -> System.out.println("[LOG] Server:"+infoStatus.getMsg_log());

                        default -> System.out.println("Implementa no requestApi comunicacao assincrona");
                    }
                }

            }catch (Exception e){
                pcs.firePropertyChange("SERVER_CLOSE",null,null);
            }

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

    public String getMyUser() {
        return myUser;
    }

    public Socket getSocket() {
        return this.socket;
    }
    public void addPropertyChangeListener(String property,PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }
    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }
}