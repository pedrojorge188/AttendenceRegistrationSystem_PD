package pt.isec.pd.attendence_registration_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.pd.data.requestsAPI;
import pt.isec.pd.threads.ServerHandler;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 750);
        stage.setTitle("Attendance Registration System");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Uso: java MainClient <IP do Servidor> <Porta do Servidor>");
            System.exit(1);
        }
        ServerHandler svHandler = new ServerHandler();
        try{
            requestsAPI client = requestsAPI.getInstance();
            client.registerValues(Integer.parseInt(args[1]), args[0]);
            client.connect();
            svHandler.start();
            launch();


        }catch (Exception exp){
            System.out.println("[SERVER] Not running state!");
            System.exit(1);
        }
    }
}