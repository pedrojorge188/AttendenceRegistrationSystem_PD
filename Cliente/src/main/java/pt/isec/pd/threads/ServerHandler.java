package pt.isec.pd.threads;
import pt.isec.pd.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerHandler extends Thread{

    public ServerHandler() {

    }

    @Override
    public void run() {
        super.run();
        ObjectInputStream receive;

        try {
            receive = new ObjectInputStream(requestsAPI.getInstance().getSocket().getInputStream());

            requestsAPI.getInstance().receive(receive);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
