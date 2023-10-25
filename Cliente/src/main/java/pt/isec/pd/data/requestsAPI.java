package pt.isec.pd.data;
import java.io.*;
import java.net.*;

public class requestsAPI {

    private static String ServerAddr;
    private static int ServerPort = 0;
    private static requestsAPI instance;
    private static ObjectOutputStream objectOutputStream;
    private Socket socket;

    private requestsAPI() {

    }

    public static requestsAPI getInstance() {
        if (instance == null) {
            instance = new requestsAPI();
        }
        return instance;
    }

    public void registerValues(int port, String addr){
        ServerPort = port;
        ServerAddr = addr;
    }

    public boolean connect() throws IOException {

        if(ServerAddr == null || ServerPort == 0)
            return false;

        try{
            socket = new Socket(ServerAddr, ServerPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Conectado ao servidor em " + ServerAddr + ":" + ServerPort);

            return true;

        }catch(Exception exp){

            System.err.println("[SERVER] Not running state!");
            return false;

        }

    }

    // <Login Sender>
    public boolean send(User.types_msg MSG, String username, String password) {
        if (socket == null) {
            System.err.println("[CLIENT] Not connected!");
            return false;
        }

        try {
            User userObject = new User(MSG, username, password);

            objectOutputStream.writeObject(userObject);
            objectOutputStream.flush();
            System.out.println("Sent User object to the server.");

        } catch (IOException e) {

            System.err.println("Error sending User object: " + e.getMessage());
            return false;

        }

        return true;
    }

    public void send() throws IOException {
        /** send commands / objects */
    }

    public void receive() throws IOException {
        /** send commands / objects */
    }

    public void disconnect() {
        if (socket != null) {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                socket.close();
                System.out.println("Desconectado do servidor");
            } catch (IOException e) {
                System.err.println("Erro ao desconectar: " + e.getMessage());
            }
        }
    }
}