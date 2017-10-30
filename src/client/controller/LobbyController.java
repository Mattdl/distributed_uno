package client.controller;

import client.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
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

    private void switchToCreateGameScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To CreateGameScene");

        stage.setScene(Main.sceneFactory.getCreateGameScene(msg));

        LOGGER.log(Level.INFO, "switched To CreateGameScene");
    }

    private void switchToGameScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To GameScene");

        stage.setScene(Main.sceneFactory.getCreateGameScene(msg));

        LOGGER.log(Level.INFO, "switched To GameScene");
    }
}
