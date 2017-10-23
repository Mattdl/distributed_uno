package client.controller;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.awt.Button;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController{

    private static final Logger LOGGER = Logger.getLogger( LoginController.class.getName() );

    @FXML
    private Button loginButton;

    /* LOGIN SCREEN */
    @FXML
    public void tryLogin(ActionEvent event) {
        LOGGER.log(Level.INFO,"Trying Login");

        //TODO delete dummy
        boolean isSuccesful = true;

        //TODO server call

        if (isSuccesful) {
            LOGGER.log(Level.INFO,"Login successful");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchToLobbyScene(stage);
        }
    }

    @FXML
    public void tryRegister() {
        //TODO
    }

    private void switchToLobbyScene(Stage stage) {
        LOGGER.log(Level.INFO,"switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene());

        LOGGER.log(Level.INFO,"switched To LobbyScene");
    }
}
