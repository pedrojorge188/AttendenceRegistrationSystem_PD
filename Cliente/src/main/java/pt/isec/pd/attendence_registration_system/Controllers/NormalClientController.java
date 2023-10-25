package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pt.isec.pd.data.User;
import pt.isec.pd.data.requestsAPI;
import pt.isec.pd.attendence_registration_system.ClientApplication;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class NormalClientController {

    //Singleton que serve para comunicar com o servidor
    private static requestsAPI client = requestsAPI.getInstance();

    @FXML
    public TextField usernameField;
    @FXML
    public TextField nifField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField passwordField;
    @FXML
    private BorderPane border;

    @FXML
    private VBox main_box;

    @FXML
    private VBox box;

    @FXML
    private RadioButton changeUsernameRadioButton;

    @FXML
    private RadioButton changePasswordRadioButton;

    @FXML
    private TextField newValueField;

    @FXML
    private Label infoLabel;
    @FXML
    private Button logoutButton;

    @FXML
    public void showAttendenceAction() {

        System.out.println("ver presencas");
    }

    @FXML
    public void receiveCSVAction() {

        System.out.printf("receber csv");
    }

    @FXML
    public void submitCodeAction() throws IOException {

        loadView("send-code-view.fxml");
    }

    @FXML
    public void accountAction() {

        loadView("change-acc-view.fxml");
    }

    @FXML
    public void sendCode() throws IOException {

        System.out.printf("send code");

    }

    private void loadView(String fxmlPath) {
        try {
            VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource(fxmlPath)));
            main_box.getChildren().clear();
            main_box.getChildren().add(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accountLogout(ActionEvent actionEvent) throws IOException {

        if(!client.getConnection()){
            client.connect();
        }

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Logout");
        confirmationDialog.setHeaderText("Tem certeza que deseja fazer logout?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            client.disconnect();
            loadView("main-view.fxml");
        }
    }

    public void confirmChangeDataAction(ActionEvent actionEvent) throws IOException {
        // verificações
        if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            infoLabel.setText("Campos obrigatórios em branco!");
            infoLabel.setTextFill(Color.RED);
            return;
        }else if(!usernameField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            infoLabel.setText("Email inválido!");
            infoLabel.setTextFill(Color.RED);
            return;
        }

        if(client.send(User.types_msg.CHANGES, usernameField.getText(), passwordField.getText())){
            infoLabel.setText("Operação concluída com sucesso.");
            infoLabel.setTextFill(Color.GREEN);
        }else{
            infoLabel.setText("Erro ao realizar a operação. Por favor, verifique os dados.");
            infoLabel.setTextFill(Color.RED);
        }
    }
    public void retButton(ActionEvent actionEvent) throws IOException {
        BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("normal-client-view.fxml")));
        pane.getChildren().clear();
        box.getChildren().clear();
        box.getChildren().add(pane);
    }
}
