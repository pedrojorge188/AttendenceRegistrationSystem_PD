package pt.isec.pd.Controllers;

import javafx.application.Platform;
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

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import static pt.isec.pd.data.InfoStatus.types_status.*;

public class NormalClientController {

    //Singleton que serve para comunicar com o servidor
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

    }

    @FXML
    public void showAttendenceAction() {
        System.out.println("ver presencas");
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

        if(!client.send(User.types_msg.CHANGES, "", usernameField.getText(), passwordField.getText())){
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
}
