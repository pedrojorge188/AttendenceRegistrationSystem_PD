package pt.isec.pd.Controllers;

import javafx.application.Platform;
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

import java.io.IOException;
import java.util.Objects;

import static pt.isec.pd.data.Event.type_event.*;
import static pt.isec.pd.data.Event.type_event.LIST_CREATED_EVENTS;
import static pt.isec.pd.data.Event.type_event.LIST_REGISTERED_ATTENDANCE;
import static pt.isec.pd.data.InfoStatus.types_status.*;

public class AdminController {
    //Singleton que serve para comunicar com o servidor
    private static requestsAPI client = requestsAPI.getInstance();
    private Event eventToSend;
    @FXML
    private VBox box;
    @FXML
    private TextField userEmail;
    @FXML
    private TextField eventName;
    @FXML
    private DatePicker eventDate;
    @FXML
    private TextField eventLocal;
    @FXML
    private TextField eventStartHour;
    @FXML
    private TextField eventEndHour;
    @FXML
    private TextField codeTime;
    @FXML
    private Label infoLabel;
    public void initialize(){
        //register handlers
        requestsAPI.getInstance().addPropertyChangeListener(EDIT_EVENT_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento editado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                    System.out.println("Evento editado com sucesso");
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
        requestsAPI.getInstance().addPropertyChangeListener(DELETE_EVENT_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Evento eliminado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });
        requestsAPI.getInstance().addPropertyChangeListener(LIST_REGISTERED_ATTENDANCE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Listagem de presencas com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(LIST_CREATED_EVENTS.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Listagem de presencas com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(GENERATE_CODE_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Código gerado com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        requestsAPI.getInstance().addPropertyChangeListener(LIST_REGISTERED_ATTENDANCE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Presenças Listadas com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
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

        requestsAPI.getInstance().addPropertyChangeListener(DELETE_ATTENDANCE_MADE.toString(),evt->{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    infoLabel.setText("Presença eliminada com sucesso");
                    infoLabel.setTextFill(Color.GREEN);
                }
            });
        });

        eventToSend = new Event(null,-1);
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
            eventToSend.setUser_email(null);
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
        String eventLocal = this.eventLocal.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(eventName.isEmpty() || eventLocal.isEmpty() || eventStartHour.isEmpty() || eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setAttend_code(-1);
            eventToSend.setType(EDIT_EVENT);
            eventToSend.setEvent_date(eventDate);
            eventToSend.setEvent_location(eventLocal);
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(eventStartHour);
            eventToSend.setEvent_end_time(eventEndHour);
            eventToSend.setUser_email(null);
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

    // show all events of a specific user
    public void showUserEvents(ActionEvent actionEvent) {
        String userEmail = this.userEmail.getText();

        if (userEmail.isEmpty()) {
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        } else {
            eventToSend.setAttend_code(-1);
            eventToSend.setType(LIST_CREATED_EVENTS_BY_USER);
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

        if(eventName.isEmpty() && eventStartHour.isEmpty() && eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha um dos campos");
            infoLabel.setTextFill(Color.RED);
        }else{
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
    }

    // search attendence in a event
    public void searchAttendence(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(eventName.isEmpty() || eventStartHour.isEmpty() || eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            eventToSend.setEvent_name(eventName);
            eventToSend.setEvent_start_time(eventStartHour);
            eventToSend.setEvent_end_time(eventEndHour);
            eventToSend.setType(LIST_REGISTERED_ATTENDANCE);
            eventToSend.setEvent_location(null);
            eventToSend.setAttend_code(-1);
            if(!client.send(eventToSend)) {
                infoLabel.setText("Aconteceu algo de errado");
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    // csv file with all attendees of the specific event
    public void receiveCSVEvent(ActionEvent actionEvent) {
    }

    // csv file with all attendance at events for a specific user
    public void receiveCsvUserEvent(ActionEvent actionEvent) {
    }
}