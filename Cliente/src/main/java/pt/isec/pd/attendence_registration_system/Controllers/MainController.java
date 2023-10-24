package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
    public Button backBtn;
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

        if(username.isEmpty() || password.isEmpty())
            return;

        if(client.send(username,password)){
            mode_path = "normal-client-view.fxml";
            loadView(mode_path);
        }

    }

    @FXML
    public void registerAction() throws IOException {

        String username = usernameRegField.getText();
        String password = passwordRegField.getText();
        String passwordConfirmation = passwordConfirmRegField.getText();

        if(username.isEmpty() || password.isEmpty())
            return;

        if(client.send(username,password,passwordConfirmation)){
            /*ChangePage*/
        }
    }

    private void loadView(String fxmlPath) throws IOException {
        BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource(fxmlPath)));
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    @FXML
    public void retButton() throws IOException {
        mode_path = "main-view.fxml";
        loadView(mode_path);
    }
    @FXML
    protected void createNewAccount() throws IOException {

        VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("register-acc-view.fxml")));
        box.getChildren().clear();
        box.getChildren().add(pane);

    }
}
