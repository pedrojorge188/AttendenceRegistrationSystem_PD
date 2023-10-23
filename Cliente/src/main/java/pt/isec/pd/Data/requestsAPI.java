package pt.isec.pd.Data;
import java.io.*;
import java.net.*;

public class requestsAPI {

    private static String ServerAddr;
    private static int ServerPort = 0;
    private static requestsAPI instance;
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
        this.ServerPort = port;
        this.ServerAddr = addr;
    }

    public boolean connect() throws IOException {

        if(ServerAddr == null || ServerPort == 0)
            return false;

        try{
            socket = new Socket(ServerAddr, ServerPort);
            System.out.println("Conectado ao servidor em " + ServerAddr + ":" + ServerPort);

            return true;

        }catch(Exception exp){
            exp.printStackTrace();
            return false;
        }

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
                socket.close();
                System.out.println("Desconectado do servidor");
            } catch (IOException e) {
                System.err.println("Erro ao desconectar: " + e.getMessage());
            }
        }
    }
}