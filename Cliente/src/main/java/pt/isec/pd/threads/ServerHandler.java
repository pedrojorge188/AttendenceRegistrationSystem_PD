package pt.isec.pd.threads;
import pt.isec.pd.data.*;

public class ServerHandler extends Thread{

    public ServerHandler() {

    }

    @Override
    public void run() {
        super.run();
        int i = 0;
        while(requestsAPI.getInstance().getConnection()){

            try {
                sleep(1000);
                System.out.println(i++);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
