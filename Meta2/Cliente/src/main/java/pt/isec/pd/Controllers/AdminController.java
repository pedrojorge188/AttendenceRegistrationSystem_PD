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
import pt.isec.pd.ClientApplication;
import pt.isec.pd.data.Event;
import pt.isec.pd.data.requestsAPI;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static pt.isec.pd.data.Event.type_event.*;
import static pt.isec.pd.data.Event.type_event.LIST_CREATED_EVENTS;
import static pt.isec.pd.data.Event.type_event.LIST_REGISTERED_ATTENDANCE;
import static pt.isec.pd.data.InfoStatus.types_status.*;

public class AdminController {
    private static requestsAPI client = requestsAPI.getInstance();
    private Event eventToSend;
    @FXML
    public TextField eventNameId, eventNameAssoc, userNameAssoc;
    @FXML
    private VBox box;
    @FXML
    private TextField userEmail, eventName;
    @FXML
    private DatePicker eventDate;
    @FXML
    private TextField eventLocal,eventStartHour, eventEndHour,codeTime;
    @FXML
    private Label infoLabel;
    @FXML
    public TextField userEmailCsv;

    public void initialize(){

        requestsAPI.getInstance().addPropertyChangeListener(EDIT_EVENT_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento editado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(EDIT_EVENT_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento solicitado não existe");
                    eventToSend = new Event(null,-1);
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(ASSOC_USER_EVENT_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Utilizador associado ao evento com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(ASSOC_USER_EVENT_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Ocorreu um erro a associar o utilizador ao evento");
                    eventToSend = new Event(null,-1);
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(CREATE_EVENT_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento criado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(CREATE_EVENT_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Ocorreu um erro ao criar o evento");
                    eventToSend = new Event(null,-1);
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(DELETE_EVENT_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento eliminado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(DELETE_EVENT_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Ocorreu um erro a eliminar o evento");
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(LIST_REGISTERED_ATTENDANCE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    initAttendanceTable();
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(GET_HISTORY.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    initUserAttendanceTable();
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(GENERATE_CODE_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Código gerado com sucesso: " + client.getEventCode());
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(GENERATE_CODE_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Erro ao gerar o código");
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(INSERT_ATTENDANCE_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Presença inserida com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(INSERT_ATTENDANCE_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Erro ao inserir presença");
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(Event.type_event.REQUEST_CSV_EVENT.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Ficheiro csv criado com sucesso ");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(DELETE_ATTENDANCE_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Presença eliminada com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(DELETE_ATTENDANCE_FAIL.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Erro ao eliminar presença");
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });

        eventToSend = new Event(null,-1);
        eventToSend.setEvent_name("");
        eventToSend.setEvent_date("");
        eventToSend.setEvent_start_time("");
        eventToSend.setEvent_end_time("");
        eventToSend.setEvent_location("");
        eventToSend.setUser_email(client.getMyUser());
    }

    public void retButton(ActionEvent actionEvent) {
        try {
            BorderPane pane = FXMLLoader.load(Objects.requireNonNull(ClientApplication.class.getResource("AdminViews/admin-view.fxml")));
            box.getChildren().add(pane);
            box.getChildren().clear();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void createEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String eventDate = null;
        if(this.eventDate.getValue()!=null)
            eventDate = this.eventDate.getValue().toString();
        String eventLocal = this.eventLocal.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(eventName.isEmpty()  || eventLocal.isEmpty() || eventStartHour.isEmpty() || eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setType(CREATE_EVENT);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_date(eventDate);
            eventToSend.setEvent_start_time(eventStartHour);
            eventToSend.setEvent_end_time(eventEndHour);
            eventToSend.setEvent_location(eventLocal);
            eventToSend.setAttend_code(-1);
            eventToSend.setUser_email(client.getMyUser());
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    public void editEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String eventDate = null;
        if(this.eventDate.getValue()!=null)
            eventDate = this.eventDate.getValue().toString();
        String event_identify = this.eventNameId.getText();
        String eventLocal = this.eventLocal.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(event_identify.isEmpty() || eventName.isEmpty() || eventLocal.isEmpty() || eventStartHour.isEmpty() || eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(EDIT_EVENT);
            eventToSend.setEvent_date(eventDate);
            eventToSend.setEvent_location(eventLocal);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_identify(event_identify);
            eventToSend.setEvent_start_time(eventStartHour);
            eventToSend.setEvent_end_time(eventEndHour);
            eventToSend.setUser_email(client.getMyUser());
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    public void deleteEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(DELETE_EVENT);
            eventToSend.setEvent_date(null);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            eventToSend.setUser_email(null);
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    // show all events of a specific user attended
    public void showUserEvents(ActionEvent actionEvent) {
        String userEmail = this.userEmailCsv.getText();

        if (userEmail.isEmpty()) {
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        } else {
            eventToSend.setAttend_code(-1);
            eventToSend.setType(GET_ATTENDANCE_HISTORY);
            eventToSend.setEvent_date(null);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(null);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            eventToSend.setUser_email(userEmail);
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    // generate event code
    public void generateEventCode(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String codeTime = this.codeTime.getText();

        if(eventName.isEmpty() || codeTime.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(GENERATE_CODE);
            eventToSend.setEvent_date(null);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(codeTime);
            eventToSend.setUser_email(null);

            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    // insert attendence in a event
    public void insertAttendence(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String userEmail = this.userEmail.getText();

        if(eventName.isEmpty() || userEmail.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(INSERT_ATTENDANCE);
            eventToSend.setEvent_date(null);
            eventToSend.setUser_email(userEmail);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    // delete attendence in a event
    public void deleteAttendence(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String userEmail = this.userEmail.getText();

        if(eventName.isEmpty() || userEmail.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(DELETE_ATTENDANCE);
            eventToSend.setEvent_date(null);
            eventToSend.setUser_email(userEmail);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    // search event with filters
    public void searchEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

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

        eventToSend.setEvent_name(eventName);
        eventToSend.setEvent_start_time(eventStartHour);
        eventToSend.setEvent_end_time(eventEndHour);
        eventToSend.setEvent_date(null);
        eventToSend.setType(LIST_CREATED_EVENTS);
        eventToSend.setAttend_code(-1);
        eventToSend.setEvent_location(null);
        if(!client.send(eventToSend)){
            infoLabel.setText("Aconteceu algo de errado");
            infoLabel.setTextFill(Color.RED);
        }

    }

    // search attendence in an event
    public void searchAttendence(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            eventToSend.setType(LIST_REGISTERED_ATTENDANCE);
            eventToSend.setEvent_location(null);
            eventToSend.setAttend_code(-1);
            if(!client.send(eventToSend)) {
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    //associate user to a event
    public void assocUserEvent(ActionEvent actionEvent) {
        String eventName = this.eventNameAssoc.getText();
       String userName = this.userNameAssoc.getText();

        if(eventName.isEmpty() || userName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(ASSOC_USER_EVENT);
            eventToSend.setEvent_date(null);
            eventToSend.setEvent_location(null);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_identify(userName);
            eventToSend.setEvent_start_time(null);
            eventToSend.setEvent_end_time(null);
            eventToSend.setUser_email(null);
            if(!client.send(eventToSend)){
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }
    public void receiveCSVEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

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


            if (eventName.isEmpty()) {
                infoLabel.setText("Por favor, preencha todos os campos");
                infoLabel.setTextFill(Color.RED);
            } else {
                eventToSend.setEvent_name(eventName);
                eventToSend.setEvent_start_time(null);
                eventToSend.setEvent_end_time(null);
                eventToSend.setType(Event.type_event.REQUEST_CSV_EVENT);
                eventToSend.setCsv_msg("EventAttend");
                eventToSend.setCsv_dir(selectedDirectoryPath);
                eventToSend.setUser_email("");
                eventToSend.setAttend_code(-1);
                if (!client.send(eventToSend)) {
                    infoLabel.setText("Ocorreu um erro.");
                    infoLabel.setTextFill(Color.RED);
                }
            }

        }
    }

    public void receiveCsvUserEvent(ActionEvent actionEvent) {
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

            String userEmail = this.userEmailCsv.getText();

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
    private String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Nome";
            case 1:
                return "Email";
            default:
                return "-";
        }
    }

    public void initAttendanceTable(){
        TableView<ObservableList<String>> tableView = new TableView<>();
        VBox vbox = new VBox();
        vbox.setSpacing(16);
        Label label = new Label("Listagem de Presenças");
        label.setStyle("-fx-font-size: 35px;");

        for (String attendanceString : requestsAPI.getInstance().getAttendanceRecords()) {
            String[] parts = attendanceString.split("\t");
            if (parts.length == 2) {
                ObservableList<String> row = FXCollections.observableArrayList(parts);
                tableView.getItems().add(row);
            }
        }
        for (int i = 0; i < 2; i++) {
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

        tableView.setMaxHeight(200);
        tableView.setStyle("-fx-background-color: #ffffff;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        vbox.getChildren().addAll(label, tableView);
        box.getChildren().clear();
        box.getChildren().add(vbox);
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
