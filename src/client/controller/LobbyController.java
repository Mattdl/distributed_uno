package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

public class LobbyController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(LobbyController.class.getName());

    private String loginMsg;

    @FXML
    public void initialize() {

        //Show if succesfully logged in or registered
        if(loginMsg != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome to UNO");
            alert.setHeaderText("Join the lobby!");
            alert.setContentText(loginMsg);
            alert.showAndWait();
        }
    }


    @Override
    public void update(Observable o, Object arg) {

    }


    public void setLoginMsg(String msg) {
        loginMsg = msg;
    }
}
