package pt.isec.pd.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
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
import java.util.ArrayList;
import java.util.List;
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
            try{
                int response_code = client.createEvent(eventName,eventLocal,eventDate,eventStartHour,eventEndHour);
                if(response_code != 200){
                    infoLabel.setText("Occorreu um Erro ["+response_code+"]");
                }else{
                    infoLabel.setText("Evento Criado com sucesso!");
                }
            }catch (IOException e) {
                infoLabel.setText("Ocorreu um erro com o Servidor ");
                System.out.println("[SERVER ERROR] " + e);
            }
        }
    }

    public void deleteEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            //Delete: localhost:8080/event/delete/name={name}
            try{
                int response_code = client.deleteEvent(eventName);
                if(response_code != 200){
                    infoLabel.setText("Occorreu um Erro ["+response_code+"]");
                }else{
                    infoLabel.setText("Evento eliminado com sucesso!");
                }
            }catch (IOException e) {
                infoLabel.setText("Ocorreu um erro com o Servidor ");
                System.out.println("[SERVER ERROR] " + e);
            }
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
            try{
                int response_code = client.generateEventCode(eventName, codeTime);
                if(response_code != 200){
                    infoLabel.setText("Occorreu um Erro ["+response_code+"]");
                }else{
                    infoLabel.setText("Código gerado com sucesso! " + client.getEventCode());
                }
            }catch (IOException e) {
                infoLabel.setText("Ocorreu um erro com o Servidor ");
                System.out.println("[SERVER ERROR] " + e);
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

        try{
            JsonArray response =  client.searchEvent(eventName,eventStartHour,eventEndHour);
            if(response != null)
                initEventsTable(response);

        }catch (IOException e) {
            infoLabel.setText("Ocorreu um erro com o Servidor ");
            System.out.println("[SERVER ERROR] " + e);
        }

    }
    // search attendence in an event
    public void searchAttendence(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            try{
                JsonArray response =  client.searchEventByAttendances(eventName);

                if(response != null)
                    initAttendancesTable(response);

            }catch (IOException e) {
                infoLabel.setText("Ocorreu um erro com o Servidor ");
                System.out.println("[SERVER ERROR] " + e);
            }

        }
    }
    public void initAttendancesTable(JsonArray jsonArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        VBox vbox = new VBox();
        vbox.setSpacing(16);

        for (int i = 0; i < jsonArray.size(); i++) {
            String[] parts = jsonArray.getString(i).split("\t");
            if (parts.length == 2) {
                ObservableList<String> row = FXCollections.observableArrayList(parts);
                tableView.getItems().add(row);
            }
        }


        for (int i = 0; i < 2; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>();
            final int columnIndex = i;
            column.setCellValueFactory(param -> {
                return new javafx.beans.property.SimpleStringProperty(param.getValue().get(columnIndex));
            });
            column.setStyle("-fx-background-color: #fff; -fx-text-fill: #000; -fx-font-weight: bold; -fx-border-color: #444; -fx-border-width: 0.5px; -fx-text-decoration: none; -fx-alignment: center;");
            column.getStyleClass().add("custom-header");
            column.setText(getColumnName2(i));
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
                return "Local";
            case 2:
                return "Data";
            case 3:
                return "Hora Inicio";
            default:
                return "-";
        }
    }

    public void initEventsTable(JsonArray jsonArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        VBox vbox = new VBox();
        vbox.setSpacing(16);

        for (int i = 0; i < jsonArray.size(); i++) {
            String[] parts = jsonArray.getString(i).split("\t");
            if (parts.length == 5) {
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

    private String getColumnName2(int i) {
        switch (i) {
            case 0:
                return "Nome do utilizador";
            case 1:
                return "Email do utilizador";
            default:
                return "-";
        }
    }

}
