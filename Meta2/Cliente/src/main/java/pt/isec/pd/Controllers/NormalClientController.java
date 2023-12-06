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
import pt.isec.pd.data.requestsAPI;
import pt.isec.pd.ClientApplication;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static pt.isec.pd.data.Event.type_event.GET_ATTENDANCE_HISTORY;
import static pt.isec.pd.data.Event.type_event.LIST_CREATED_EVENTS;
import static pt.isec.pd.data.InfoStatus.types_status.*;

public class NormalClientController {

    private Event eventToSend;

    private static requestsAPI client = requestsAPI.getInstance();
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
        requestsAPI.getInstance().addPropertyChangeListener(CODE_SEND_MADE.toString(),evt->{
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

        requestsAPI.getInstance().addPropertyChangeListener(REQUEST_CSV_EVENT.toString(),evt->{
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


        requestsAPI.getInstance().addPropertyChangeListener(CODE_SEND_FAIL.toString(),evt->{
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
        requestsAPI.getInstance().addPropertyChangeListener(CHANGES_MADE.toString(),evt -> {
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
        requestsAPI.getInstance().addPropertyChangeListener(CHANGES_FAIL.toString(),evt -> {
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
        loadView("ClientViews/attendances-list-view.fxml");
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
        if(!client.send(Event.type_event.CODE_EVENT, Integer.parseInt(code))){
            infoLabel.setText("Campos obrigatórios em branco!");
            infoLabel.setTextFill(Color.RED);
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
            client.disconnect();
            Platform.exit();
        }
    }

    public void confirmChangeDataAction(ActionEvent actionEvent) throws IOException {
        // verificações
        if(!Objects.equals(passwordField.getText(), passwordFieldConfirm.getText())
                || passwordField.getText().isEmpty() || passwordFieldConfirm.getText().isEmpty()) {
            infoLabel.setText("Passwords não correspondem");
            infoLabel.setTextFill(Color.RED);
            return;
        }else if(!usernameField.getText().isEmpty() && !usernameField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            infoLabel.setText("Email inválido!");
            infoLabel.setTextFill(Color.RED);
            return;
        }

        if(!client.send(User.types_msg.CHANGES, 0, "", usernameField.getText(), passwordField.getText())){
            infoLabel.setText("Erro ao realizar a operação. Por favor, verifique os dados.");
            infoLabel.setTextFill(Color.RED);
        }
    }

    public void retButton(ActionEvent actionEvent) throws IOException {
        BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("ClientViews/normal-client-view.fxml")));
        pane.getChildren().clear();
        box.getChildren().clear();
        box.getChildren().add(pane);
    }

    public void listAttendances(ActionEvent actionEvent) {

            eventToSend.setAttend_code(-1);
            eventToSend.setType(GET_ATTENDANCE_HISTORY);
            eventToSend.setEvent_date(null);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(null);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            eventToSend.setUser_email(requestsAPI.getInstance().getMyUser());

            if(!client.send(eventToSend)) {
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }

            initUserAttendanceTable();

    }

    public void csvReceive(ActionEvent actionEvent) {

        JFileChooser directoryChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int directoryReturnValue = directoryChooser.showOpenDialog(null);

        if (directoryReturnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = directoryChooser.getSelectedFile();

            String fileName = JOptionPane.showInputDialog("Digite o nome do arquivo (sem extensão):");
            if (fileName == null || fileName.trim().isEmpty()) {
                infoLabel.setText("Nome do arquivo inválido.");
                infoLabel.setTextFill(Color.RED);
                return;
            }

            String selectedDirectoryPath = selectedDirectory.getAbsolutePath();
            String filePath = selectedDirectoryPath + File.separator + fileName + ".csv";

            requestsAPI.getInstance().setFileName(filePath);

            String userEmail = client.getMyUser();

            if (userEmail.isEmpty()) {
                infoLabel.setText("Por favor, preencha todos os campos");
                infoLabel.setTextFill(Color.RED);
            } else {
                eventToSend.setEvent_name("");
                eventToSend.setEvent_start_time(null);
                eventToSend.setEvent_end_time(null);
                eventToSend.setType(Event.type_event.REQUEST_CSV_EVENT);
                eventToSend.setCsv_msg("UserEvents");
                eventToSend.setCsv_dir(selectedDirectoryPath);
                eventToSend.setUser_email(userEmail);
                eventToSend.setAttend_code(-1);
                if (!client.send(eventToSend)) {
                    infoLabel.setText("Ocorreu um erro.");
                    infoLabel.setTextFill(Color.RED);
                }
            }

        }

    }

    public void initUserAttendanceTable(){
        TableView<ObservableList<String>> tableView = new TableView<>();
        VBox vbox = new VBox();
        vbox.setSpacing(16);
        Label label = new Label("Listagem de Presenças");
        label.setStyle("-fx-font-size: 35px;");

        for (String attendanceString : requestsAPI.getInstance().getUserAttendanceRecords()) {
            String[] parts = attendanceString.split("\t");
            if (parts.length == 4) {
                ObservableList<String> row = FXCollections.observableArrayList(parts);
                tableView.getItems().add(row);
            }
        }
        for (int i = 0; i < 4; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>();
            final int columnIndex = i;
            column.setCellValueFactory(param -> {
                return new SimpleStringProperty(param.getValue().get(columnIndex));
            });
            column.setText(getColumnNameUserAttendance(i));
            column.setStyle("-fx-background-color: #fff; -fx-text-fill: #000; -fx-font-weight: bold; -fx-border-color: #444; -fx-border-width: 0.5px; -fx-text-decoration: none; -fx-alignment: center;");

            column.getStyleClass().add("custom-header");

            tableView.getColumns().add(column);
        }

        tableView.setMaxHeight(200);
        tableView.setStyle("-fx-background-color: #ffffff;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        vbox.getChildren().addAll(label, tableView);
        box.getChildren().clear();
        box.getChildren().add(vbox);
    }

    private String getColumnNameUserAttendance(int i) {
        switch (i) {
            case 0:
                return "Nome Evento";
            case 1:
                return "Hora inicio";
            case 2:
                return "Hora fim";
            case 3:
                return "Data";
            default:
                return "-";
        }
    }


}