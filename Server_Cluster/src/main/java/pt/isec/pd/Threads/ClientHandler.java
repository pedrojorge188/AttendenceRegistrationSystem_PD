package pt.isec.pd.Threads;

import pt.isec.pd.data.InfoStatus;
import pt.isec.pd.data.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {

    private String name;
    private final Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
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

                if (receivedObject instanceof User user) {

                    System.out.println("[Client " + this.getName() + "-] SEND A USER NOTIFICATION! (" + user.getUsername_email() +")");

                    this.name = user.getUsername_email();
                    switch (user.getType()) {
                        case LOGIN -> {

                            try{

                                InfoStatus response = new InfoStatus(InfoStatus.types_status.LOGIN_MADE_USER);
                                objectOutputStream.writeObject(response);
                                objectOutputStream.flush();

                            }catch (Exception exception){
                                exception.printStackTrace();
                            }

                        }
                        case REGISTER -> {

                            try{

                                InfoStatus response = new InfoStatus(InfoStatus.types_status.REGISTER_MADE);
                                objectOutputStream.writeObject(response);
                                objectOutputStream.flush();

                            }catch (Exception exception){
                                exception.printStackTrace();
                            }

                        }
                        case CHANGES -> {

                            try{

                                InfoStatus response = new InfoStatus(InfoStatus.types_status.CHANGES_MADE);
                                objectOutputStream.writeObject(response);
                                objectOutputStream.flush();

                            }catch (Exception exception){
                                exception.printStackTrace();
                            }

                        }
                    }

                }
            }
        } catch (IOException | ClassNotFoundException e) {

            try {
                clientSocket.close();
                System.out.println("[CLIENT " + this.name + "] Disconnected! ");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }
}