package pt.isec.pd;

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
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 800);
            stage.setTitle("Attendance Registration System");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void stop() throws Exception {
        requestsAPI.getInstance().disconnect();
        System.exit(0);
    }
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java MainClient <IP do Servidor> <Porta do Servidor>");
            System.exit(1);
        }
        if(requestsAPI.getInstance().connect(args[0],Integer.parseInt(args[1]))){
            ServerHandler svHandler = new ServerHandler();
            svHandler.start();
            launch();
        }
    }
}