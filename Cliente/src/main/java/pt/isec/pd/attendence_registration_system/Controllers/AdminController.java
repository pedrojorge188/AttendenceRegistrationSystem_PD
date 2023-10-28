package pt.isec.pd.attendence_registration_system.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.w3c.dom.Text;
import pt.isec.pd.attendence_registration_system.ClientApplication;
import pt.isec.pd.data.Event;
import pt.isec.pd.data.requestsAPI;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AdminController {
    //Singleton que serve para comunicar com o servidor
    private static requestsAPI client = requestsAPI.getInstance();
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
        String eventDate;
        if(this.eventDate.getValue()!=null)
            eventDate = this.eventDate.getValue().toString();
        String eventLocal = this.eventLocal.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(eventName.isEmpty()  || eventLocal.isEmpty() || eventStartHour.isEmpty() || eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            // create event
        }
    }

    public void editEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();
        String eventDate;
        if(this.eventDate.getValue()!=null)
            eventDate = this.eventDate.getValue().toString();
        String eventLocal = this.eventLocal.getText();
        String eventStartHour = this.eventStartHour.getText();
        String eventEndHour = this.eventEndHour.getText();

        if(eventName.isEmpty() || eventLocal.isEmpty() || eventStartHour.isEmpty() || eventEndHour.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            // edit event
        }
    }

    public void deleteEvent(ActionEvent actionEvent) {
        String eventName = this.eventName.getText();

        if(eventName.isEmpty()){
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        }else{
            // delete event
        }
    }

    // show all events of a specific user
    public void showUserEvents(ActionEvent actionEvent) {
        String userEmail = this.userEmail.getText();

        if (userEmail.isEmpty()) {
            infoLabel.setText("Por favor preencha todos os campos");
            infoLabel.setTextFill(Color.RED);
        } else {
            // show all events of a specific user
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
            // generate event code
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
            // insert attendence in a event
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
            // delete attendence in a event
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
            // search event
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
            // search attendence
        }
    }

    // csv file with all attendees of the specific event
    public void receiveCSVEvent(ActionEvent actionEvent) {
    }

    // csv file with all attendance at events for a specific user
    public void receiveCsvUserEvent(ActionEvent actionEvent) {
    }
}
