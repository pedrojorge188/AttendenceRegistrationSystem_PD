package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import pt.isec.pd.data.User;
import pt.isec.pd.data.requestsAPI;
import pt.isec.pd.attendence_registration_system.ClientApplication;

import java.io.IOException;
import java.util.Objects;

import static pt.isec.pd.data.InfoStatus.types_status.*;

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

    public void initialize(){
        requestsAPI.getInstance().addPropertyChangeListener("SERVER_CLOSE",evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.err.println("[SERVER] SERVER ERROR");
                    client.disconnect();
                    System.exit(1);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(LOGIN_MADE_USER.name(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadView("ClientViews/normal-client-view.fxml");
                    System.out.println("[SERVER] Welcome - "+client.getInstance().getMyUser());
                }
            });

        });


        requestsAPI.getInstance().addPropertyChangeListener(LOGIN_MADE_ADMIN.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadView("AdminViews/admin-view.fxml");
                }
            });

        });

        requestsAPI.getInstance().addPropertyChangeListener(REGISTER_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadView("ClientViews/normal-client-view.fxml");
                }
            });

        });
    }

    @FXML
    protected void authAction() throws IOException {

        String username = usernameField.getText();
        String password = passwordField.getText();

        // verificações
        if(username.isEmpty() || password.isEmpty() || !username.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            errorLabel.setText("Dados introduzidos inválidos");
            return;
        }

        if(client.send(User.types_msg.LOGIN,"",username,password)){

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

        if(client.send(User.types_msg.REGISTER,name,username,password)){

        }else{
            errorLabelReg.setText("Dados introduzidos inválidos");
        }
    }

    private void loadView(String fxmlPath)  {
        try {
            BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource(fxmlPath)));
            box.getChildren().clear();
            box.getChildren().add(pane);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void createNewAccount() throws IOException {
        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("ClientViews/register-acc-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    public void retButton(ActionEvent actionEvent) throws IOException {
        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("main-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }
}
