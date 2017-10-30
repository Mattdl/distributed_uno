package client.controller;

import client.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGameController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());


    @FXML
    private ChoiceBox<Integer> numberOfPlayers;

    @FXML
    private PasswordField password;

    @FXML
    private TextField gameName;


    @FXML
    public void initialize() {

        //Set choicebox values
        ObservableList<Integer> availableChoices = FXCollections.observableArrayList(2,3,4);
        numberOfPlayers.setItems(availableChoices);
    }

    @FXML
    public void createGame(ActionEvent event){

        int playerCount = numberOfPlayers.getSelectionModel().getSelectedItem();

        String msg = "Created game with succes";
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switchToLobbyScene(stage, msg);

    }

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }

}
