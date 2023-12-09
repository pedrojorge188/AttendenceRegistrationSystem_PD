package pt.isec.pd.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pt.isec.pd.ClientApplication;
import pt.isec.pd.data.Event;
import pt.isec.pd.data.SendAndReceive;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static pt.isec.pd.data.InfoStatus.types_status.LIST_CREATED_EVENTS;

public class MainAdminController {
    //Singleton que serve para comunicar com o servidor
    private static SendAndReceive client = SendAndReceive.getInstance();

    @FXML
    private VBox main_box;
    @FXML
    private Label title;
    public void initialize(){
        tableInit(true);
        SendAndReceive.getInstance().addPropertyChangeListener(LIST_CREATED_EVENTS.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tableInit(false);
                }

            });
        });
    }
    private String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Nome";
            case 1:
                return "Local";
            case 2:
                return "Data";
            case 3:
                return "Hora de Início";
            case 4:
                return "Hora de Fim";
            default:
                return "-";
        }
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

    //load views changes functions
    public void createEventAction(ActionEvent actionEvent) {
        loadView("AdminViews/create-event-view.fxml");
    }

    public void showEventsAction(ActionEvent actionEvent) {
        loadView("AdminViews/search-event-view.fxml");
    }

    public void deleteEventsAction(ActionEvent actionEvent) {
        loadView("AdminViews/delete-event-view.fxml");
    }

    public void showAttendenceAdminAction(ActionEvent actionEvent) {loadView("AdminViews/search-attendence-view.fxml");}

    public void generateCodeAction(ActionEvent actionEvent) {
        loadView("AdminViews/generate-code-view.fxml");
    }

    public void accountLogout(ActionEvent actionEvent) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Logout");
        confirmationDialog.setHeaderText("Tem certeza que deseja fazer logout?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void tableInit(boolean flag) {

        TableView<ObservableList<String>> tableView = new TableView<>();
        VBox vbox = new VBox();
        vbox.setSpacing(16);
        Label label = new Label("Listagem de Eventos");
        label.setStyle("-fx-font-size: 35px;");

        /*
        for (String eventString : SendAndReceive.getInstance().getEventsName()) {
            String[] parts = eventString.split("\t");
            if (parts.length == 5) {
                ObservableList<String> row = FXCollections.observableArrayList(parts);
                tableView.getItems().add(row);
            }
        }

        for (int i = 0; i < 5; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>();
            final int columnIndex = i;
            column.setCellValueFactory(param -> {
                return new SimpleStringProperty(param.getValue().get(columnIndex));
            });
            column.setText(getColumnName(i));
            column.setStyle("-fx-background-color: #fff; -fx-text-fill: #000; -fx-font-weight: bold; -fx-border-color: #444; -fx-border-width: 0.5px; -fx-text-decoration: none; -fx-alignment: center;");

            column.getStyleClass().add("custom-header");

            tableView.getColumns().add(column);
        }
        */

        tableView.setMaxHeight(200);
        tableView.setStyle("-fx-background-color: #ffffff;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        vbox.getChildren().addAll(label, tableView);
        main_box.getChildren().clear();
        main_box.getChildren().add(vbox);
    }

}
