package pt.isec.pd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pd.data.SendAndReceive;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 800);
            stage.setTitle("Attendance Registration System");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java MainClient <IP do Servidor> <Porta do Servidor>");
            System.exit(1);
        }
        if(SendAndReceive.getInstance().connect(args[0],Integer.parseInt(args[1]))){
            launch();
        }else{
            System.out.println("[*] Error connecting to a server");
            System.exit(-1);
        }

    }
}