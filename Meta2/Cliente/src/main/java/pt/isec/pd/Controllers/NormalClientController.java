package pt.isec.pd.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pt.isec.pd.data.Event;
import pt.isec.pd.data.User;
import pt.isec.pd.data.SendAndReceive;
import pt.isec.pd.ClientApplication;

import javax.json.JsonArray;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static pt.isec.pd.data.Event.type_event.GET_ATTENDANCE_HISTORY;
import static pt.isec.pd.data.InfoStatus.types_status.*;

public class NormalClientController {
    @FXML
    public TextField eventName;
    @FXML
    public TextField eventStartHour;
    @FXML
    public TextField eventEndHour;

    private Event eventToSend;

    private static SendAndReceive client = SendAndReceive.getInstance();
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;

    @FXML
    public TextField codeField;

    @FXML
    public Label title;

    @FXML
    public Label infoLabelCode;

    @FXML
    public PasswordField passwordFieldConfirm;
    @FXML
    private VBox main_box;
    @FXML
    private VBox box;
    @FXML
    private Label infoLabel;


    public void initialize(){

        SendAndReceive.getInstance().addPropertyChangeListener(CODE_SEND_MADE.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (infoLabelCode != null) {
                        infoLabelCode.setText("Código submetido");
                        infoLabelCode.setTextFill(Color.GREEN);
                    }
                }
            });

        });

        SendAndReceive.getInstance().addPropertyChangeListener(REQUEST_CSV_EVENT.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (infoLabelCode != null) {
                        infoLabelCode.setText("Ficheiro recebido com sucesso");
                        infoLabelCode.setTextFill(Color.GREEN);
                    }
                }
            });

        });


        SendAndReceive.getInstance().addPropertyChangeListener(CODE_SEND_FAIL.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (infoLabelCode != null) {
                        infoLabelCode.setText("Código não existe");
                        infoLabelCode.setTextFill(Color.RED);
                    }
                }
            });

        });
        SendAndReceive.getInstance().addPropertyChangeListener(CHANGES_MADE.toString(), evt -> {
            Platform.runLater(new Runnable() {
                                  @Override
                                  public void run() {
                                      if(infoLabel!=null) {
                                          infoLabel.setText("Operação concluída com sucesso.");
                                          infoLabel.setTextFill(Color.GREEN);
                                      }
                                  }
                              }
            );
        });
        SendAndReceive.getInstance().addPropertyChangeListener(CHANGES_FAIL.toString(), evt -> {
            Platform.runLater(new Runnable() {
                                  @Override
                                  public void run() {
                                      if(infoLabel!=null) {
                                          infoLabel.setText("Operação concluída com erro.");
                                          infoLabel.setTextFill(Color.GREEN);
                                      }
                                  }
                              }
            );
        });

        eventToSend = new Event(null, -1);
    }

    @FXML
    public void showAttendenceAction() {
        loadView("ClientViews/search-event-view.fxml");
    }

    @FXML
    public void receiveCSVAction() {
        System.out.println("receber csv");
    }

    @FXML
    public void submitCodeAction() throws IOException {
        loadView("ClientViews/send-code-view.fxml");
    }

    @FXML
    public void accountAction() {
        loadView("ClientViews/change-acc-view.fxml");
    }

    @FXML
    public void sendCode() throws IOException {
        String code = codeField.getText();
        // POST: localhost:8080/code/send/your_code
        try{
            int response_code =    client.sendCode(Integer.parseInt(code));
            if(response_code != 200){
                infoLabelCode.setText("Occorreu um Erro ["+response_code+"]");
            }
        }catch (IOException e) {
            infoLabelCode.setText("Ocorreu um erro com o Servidor ");
            System.out.println("[SERVER ERROR] " + e);
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

    public void accountLogout(ActionEvent actionEvent) throws IOException {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Logout");
        confirmationDialog.setHeaderText("Tem certeza que deseja fazer logout?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void retButton(ActionEvent actionEvent) throws IOException {
        BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("ClientViews/normal-client-view.fxml")));
        pane.getChildren().clear();
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    public void listAttendances(ActionEvent actionEvent) {
            // Get: localhost:8080/code/search/?
        String eventName = this.eventName.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(eventName == null)
            eventName = "";

        if (!eventStartHour.isEmpty()){
            if(eventEndHour.isEmpty()){
                infoLabel.setText("Preencha a hora de fim ");
                infoLabel.setTextFill(Color.RED);
                return;
            }
        }else if(!eventEndHour.isEmpty()){
            if(eventStartHour.isEmpty()){
                infoLabel.setText("Preencha a hora de Inicio ");
                infoLabel.setTextFill(Color.RED);
                return;
            }
        }

        try{
            JsonArray response =  client.searchEventAttendances(eventName,eventStartHour,eventEndHour);
            if(response != null)
                initEventsTable(response);
            else
                infoLabel.setText("Nenhuma informação encontrada");


        }catch (IOException e) {
            infoLabel.setText("Ocorreu um erro com o Servidor ");
            System.out.println("[SERVER ERROR] " + e);
        }
    }



    public void initEventsTable(JsonArray jsonArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        VBox vbox = new VBox();
        vbox.setSpacing(16);

        for (int i = 0; i < jsonArray.size(); i++) {
            String[] parts = jsonArray.getString(i).split("\t");
            if (parts.length == 4) {
                ObservableList<String> row = FXCollections.observableArrayList(parts);
                tableView.getItems().add(row);
            }
        }

        for (int i = 0; i < 4; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>();
            final int columnIndex = i;
            column.setCellValueFactory(param -> {
                return new javafx.beans.property.SimpleStringProperty(param.getValue().get(columnIndex));
            });
            column.setStyle("-fx-background-color: #fff; -fx-text-fill: #000; -fx-font-weight: bold; -fx-border-color: #444; -fx-border-width: 0.5px; -fx-text-decoration: none; -fx-alignment: center;");
            column.getStyleClass().add("custom-header");
            column.setText(getColumnName(i));
            tableView.getColumns().add(column);
        }

        tableView.setMaxHeight(200);
        tableView.setStyle("-fx-background-color: #ffffff;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        box.getChildren().clear();
        box.getChildren().add(tableView);
    }


    private String getColumnName(int i) {
        switch (i) {
            case 0:
                return "Nome Evento";
            case 1:
                return "Hora Inicio";
            case 2:
                return "Hora Fim";
            case 3:
                return "Data";
            default:
                return "-";
        }
    }


}