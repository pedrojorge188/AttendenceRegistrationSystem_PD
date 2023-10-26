package pt.isec.pd.Threads;

import pt.isec.pd.data.Event;
import pt.isec.pd.data.InfoStatus;
import pt.isec.pd.data.User;
import pt.isec.pd.helpers.EventManager;
import pt.isec.pd.helpers.UserManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {

    private User user = null;
    private final Socket clientSocket;
    private Boolean isLogged;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        isLogged = false;
        objectInputStream = new  ObjectInputStream(clientSocket.getInputStream());
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public ObjectInputStream getInStream() {
        return objectInputStream;
    }

    @Override
    public void run() {
        try {
            System.out.println("[Client " + this.getName() + "-] Connected (ip: " + clientSocket.getInetAddress() + " | port: " + clientSocket.getPort() + ")");

            while (clientSocket.isConnected()) {
                Object receivedObject = objectInputStream.readObject();

                if (receivedObject instanceof User receivedUser) {

                    UserManager.manage(receivedUser,clientSocket,objectInputStream,objectOutputStream,this.user);

                }else if(receivedObject instanceof Event event){

                    EventManager.manage(event,clientSocket,objectInputStream,objectOutputStream,this.user);
                }
            }
        } catch (IOException | ClassNotFoundException e) {

            try {
                this.isLogged = false;
                clientSocket.close();
                System.out.println("[CLIENT " + this.user.getUsername_email()  + "] Disconnected! ");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }
}