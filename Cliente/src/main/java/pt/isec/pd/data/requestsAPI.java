package pt.isec.pd.data;
import pt.isec.pd.threads.ServerHandler;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.*;

public class requestsAPI{

    private static String ServerAddr;
    private static int ServerPort = 0;
    private static requestsAPI instance;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
    private Socket socket;
    private PropertyChangeSupport pcs;
    private requestsAPI() {

    }

    public static requestsAPI getInstance() {
        if (instance == null) {
            instance = new requestsAPI();
        }
        return instance;
    }



    public void registerValues(int port, String addr) throws IOException {
        ServerPort = port;
        ServerAddr = addr;
    }

    public boolean connect() throws IOException {

        if(ServerAddr == null || ServerPort == 0)
            return false;

        try{
            socket = new Socket(ServerAddr, ServerPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            pcs = new PropertyChangeSupport(this);
            System.out.println("Conectado ao servidor em " + ServerAddr + ":" + ServerPort);
            return true;

        }catch(Exception exp){

            System.err.println("[SERVER] Not running state!");
            return false;

        }

    }

    public boolean getConnection(){
        return socket.isConnected();
    }

    // <User Sender>
    public boolean send(User.types_msg MSG, String username, String password) throws IOException {

        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }

        try {
            User userObject = new User(MSG, username, password);

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
    public boolean send(Event.type_event EVT, int code) throws IOException {

        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }

        try {
            Event eventObject = new Event(EVT,code);

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

    public void receive(ObjectInputStream receive) {

        while(this.getConnection()){

            try{

                Object receiveObject = receive.readObject();

                if(receiveObject instanceof InfoStatus infoStatus){

                    switch (infoStatus.getStatus()){
                        case LOGIN_MADE_USER -> {
                            pcs.firePropertyChange("LOGIN_MADE_USER",null,null);
                            System.out.println("[SERVER] Login Made (normal client)!");
                        }
                        case LOGIN_MADE_ADMIN -> {
                            System.out.println("[SERVER] Login Made (admin client)!");
                        }
                        case LOGIN_FAIL -> {
                            System.out.println("[SERVER] Login Fail!");
                        }
                        case REGISTER_MADE -> {
                            System.out.println("[SERVER] Register Made!");
                        }
                        case REGISTER_FAIL -> {
                            System.out.println("[SERVER] Register Fail!");
                        }
                        case CHANGES_MADE -> {
                            System.out.println("[SERVER] Changes Made!");
                        }
                        case CHAGES_FAIL -> {
                            System.out.println("[SERVER] Changes Fail!");
                        }
                        case CODE_SEND_MADE -> {
                            pcs.firePropertyChange("CODE_SEND_MADE",null,null);
                            System.out.println("[SERVER] Code SEND!");
                        }
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

    public Socket getSocket() {
        return this.socket;
    }
    public void addPropertyChangeListener(String property,PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }
}