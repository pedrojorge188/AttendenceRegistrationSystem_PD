package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.attendence_registration_system.ClientApplication;
import pt.isec.pd.data.requestsAPI;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class MainAdminController {
    //Singleton que serve para comunicar com o servidor
    private static requestsAPI client = requestsAPI.getInstance();

    @FXML
    private VBox main_box;
    private void loadView(String fxmlPath) {
        try {
            VBox pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource(fxmlPath)));
            main_box.getChildren().clear();
            main_box.getChildren().add(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //load views changes functions
    public void createEventAction(ActionEvent actionEvent) {
        loadView("AdminViews/create-event-view.fxml");
    }

    public void showEventsAction(ActionEvent actionEvent) {
        loadView("AdminViews/search-event-view.fxml");
    }

    public void editEventsAction(ActionEvent actionEvent) {
        loadView("AdminViews/edit-event-view.fxml");
    }

    public void deleteEventsAction(ActionEvent actionEvent) {
        loadView("AdminViews/delete-event-view.fxml");
    }

    public void showUserEventsAction(ActionEvent actionEvent) {
        loadView("AdminViews/search-user-events-view.fxml");
    }

    public void showAttendenceAdminAction(ActionEvent actionEvent) {
        loadView("AdminViews/search-attendence-view.fxml");
    }

    public void generateCodeAction(ActionEvent actionEvent) {
        loadView("AdminViews/generate-code-view.fxml");
    }

    public void deleteAttendenceAction(ActionEvent actionEvent) {
        loadView("AdminViews/delete-attendence-view.fxml");
    }

    public void insertAttendenceAction(ActionEvent actionEvent) {
        loadView("AdminViews/insert-attendence-view.fxml");
    }

    public void receiveCsvAdminAction(ActionEvent actionEvent) {
        loadView("AdminViews/receivecsv-view.fxml");
    }

    public void accountLogout(ActionEvent actionEvent) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Logout");
        confirmationDialog.setHeaderText("Tem certeza que deseja fazer logout?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            client.disconnect();
            Platform.exit();
        }
    }


}
