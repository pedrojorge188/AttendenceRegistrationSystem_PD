package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pd.data.User;
import pt.isec.pd.data.requestsAPI;
import pt.isec.pd.attendence_registration_system.ClientApplication;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    //Singleton que serve para comunicar com o servidor
    private static requestsAPI client = requestsAPI.getInstance();

    private static String mode_path = null;
    @FXML
    public TextField usernameRegField;
    @FXML
    public TextField passwordRegField;
    @FXML
    public TextField passwordConfirmRegField;
    @FXML
    public TextField studentNumberRegField;
    @FXML
    public TextField nameRegField;
    @FXML
    public Label errorLabel;
    @FXML
    public Label errorLabelReg;
    @FXML
    private VBox box;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;


    @FXML
    protected void authAction() throws IOException {

        String username = usernameField.getText();
        String password = passwordField.getText();

        // verificações
        if(username.isEmpty() || password.isEmpty() || !username.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            errorLabel.setText("Dados introduzidos inválidos");
            return;
        }

        if(client.send(User.types_msg.LOGIN, username,password)){
            mode_path = "normal-client-view.fxml";
            loadView(mode_path);
        }else{
            errorLabelReg.setText("Ocorreu um erro!");
        }

    }

    @FXML
    public void registerAction() throws IOException {
        String name = nameRegField.getText();
        String studentNumber = studentNumberRegField.getText();
        String username = usernameRegField.getText();
        String password = passwordRegField.getText();
        String passwordConfirmation = passwordConfirmRegField.getText();

        // verificações
        if(username.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()
        || studentNumber.isEmpty() || name.isEmpty()){
            errorLabelReg.setText("Campos obrigatórios em branco!");
            return;
        }else if(!username.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            errorLabelReg.setText("Email inválido!");
            return;
        }else if( !password.equals(passwordConfirmation)) {
            errorLabelReg.setText("As passwords devem coincidir!");
            return;
        }

        if(client.send(User.types_msg.REGISTER,username,password)){
            loadView("normal-client-view.fxml");
        }else{
            errorLabelReg.setText("Dados introduzidos inválidos");
        }
    }

    private void loadView(String fxmlPath) throws IOException {
        BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource(fxmlPath)));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    @FXML
    protected void createNewAccount() throws IOException {
        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("register-acc-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    public void retButton(ActionEvent actionEvent) throws IOException {
        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("main-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }
}
