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
import pt.isec.pd.data.SendAndReceive;

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
    private static SendAndReceive client = SendAndReceive.getInstance();
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


        SendAndReceive.getInstance().addPropertyChangeListener(CREATE_EVENT_MADE.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento criado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });
        SendAndReceive.getInstance().addPropertyChangeListener(CREATE_EVENT_FAIL.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Ocorreu um erro ao criar o evento");
                    eventToSend = new Event(null,-1);
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });
        SendAndReceive.getInstance().addPropertyChangeListener(DELETE_EVENT_MADE.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento eliminado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });
        SendAndReceive.getInstance().addPropertyChangeListener(DELETE_EVENT_FAIL.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Ocorreu um erro a eliminar o evento");
                    infoLabel.setTextFill(Color.RED);
                }
            });
        });
        SendAndReceive.getInstance().addPropertyChangeListener(LIST_REGISTERED_ATTENDANCE.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    initAttendanceTable();
                }
            });
        });

        SendAndReceive.getInstance().addPropertyChangeListener(GENERATE_CODE_MADE.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Código gerado com sucesso: " + client.getEventCode());
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        SendAndReceive.getInstance().addPropertyChangeListener(GENERATE_CODE_FAIL.toString(), evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Erro ao gerar o código");
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
            //POST: localhost:8080/event/create/name={name}/location={location}/date={date}/start_time={start_time}/end_time={end_time}
        }
    }


    public void deleteEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            //Delete: localhost:8080/event/delete/name={name}
        }
    }



    public void generateEventCode(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String codeTime = this.codeTime.getText();

        if(eventName.isEmpty() || codeTime.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            // POST: localhost:8080/code/generate/name=eventName/time=time
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
        //GET: localhost:8080/list?
    }

    // search attendence in an event
    public void searchAttendence(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            // Get: localhost:8080/code/search/?
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

        /*
        for (String attendanceString : SendAndReceive.getInstance().getAttendanceRecords()) {
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
        */
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
