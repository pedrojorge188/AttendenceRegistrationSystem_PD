package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pt.isec.pd.data.requestsAPI;
import pt.isec.pd.attendence_registration_system.ClientApplication;

import java.io.IOException;
import java.util.Objects;

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
    private VBox main_box;

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

    public void accountLogout(ActionEvent actionEvent) {
        System.out.printf("fazer logout");
    }

    public void confirmChangeDataAction(ActionEvent actionEvent) {
        System.out.printf("alterar dados");
    }
}
