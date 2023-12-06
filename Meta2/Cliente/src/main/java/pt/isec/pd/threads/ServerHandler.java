package pt.isec.pd.threads;
import pt.isec.pd.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerHandler extends Thread{

    @Override
    public void run() {
        super.run();
        ObjectInputStream receive;

        try {
            receive = new ObjectInputStream(requestsAPI.getInstance().getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        requestsAPI.getInstance().receive(receive);
    }
}
