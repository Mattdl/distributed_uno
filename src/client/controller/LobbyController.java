package client.controller;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.awt.Button;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LobbyController implements Observer {

    private static final Logger LOGGER = Logger.getLogger( LobbyController.class.getName() );


    //private Model model = new Model();

    /* LOGIN SCREEN */
    @FXML
    private Button loginButton;

    @Override
    public void update(Observable o, Object arg) {

    }

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
