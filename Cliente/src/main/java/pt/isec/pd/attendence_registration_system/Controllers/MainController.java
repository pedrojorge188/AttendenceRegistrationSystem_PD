package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.attendence_registration_system.ClientApplication;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    private static String mode_path = null;
    @FXML
    public TextField usernameRegField;
    @FXML
    public TextField nifRegField;
    @FXML
    public TextField emailRegField;
    @FXML
    public TextField passwordRegField;
    @FXML
    public TextField passwordConfirmRegField;
    @FXML
    private VBox box;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    private Button authBtn;

    @FXML
    protected void authAction() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        /**
         * > Código a desenvolver na model - irá pedir ao servidor se existe este user caso sim mostra a Normal Client View s
         * e receber do server que é um admin então cria um Admin Client View
         *
         *
        if (authUser(username, password)) {


        }
        */

        mode_path = "normal-client-view.fxml";
        loadView(mode_path);

    }


    @FXML
    public void registerAction() {
        System.out.printf("registar");
    }

    private void loadView(String fxmlPath) throws IOException {
        BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource(fxmlPath)));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    @FXML
    public void retButton() throws IOException {
        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("main-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }
    @FXML
    protected void createNewAccount() throws IOException {

        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("register-acc-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);

    }
}
