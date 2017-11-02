package client.controller;

import client.Main;
import client.service.lobby.CreateGameService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGameController {
    private static final Logger LOGGER = Logger.getLogger(CreateGameController.class.getName());


    @FXML
    private ChoiceBox<Integer> numberOfPlayers;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField gameName;


    @FXML
    public void initialize() {

        //Set choicebox values
        ObservableList<Integer> availableChoices = FXCollections.observableArrayList(2, 3, 4);
        numberOfPlayers.setItems(availableChoices);
        numberOfPlayers.setValue(4);
    }

    @FXML
    public void createGame(ActionEvent event) {

        int playerCount = numberOfPlayers.getSelectionModel().getSelectedItem();
        String name = gameName.getText();
        String password = passwordField.getText(); //TODO encryption

        CreateGameService createGameService = new CreateGameService(name, playerCount, Main.currentPlayer, password);

        createGameService.setOnSucceeded(event1 -> {
            boolean successful = (boolean) event1.getSource().getValue();
            String msg1 = "Boolean: " + successful;
            LOGGER.log(Level.INFO, msg1);


            if (successful) {
                String msg = "Created game with success";
                LOGGER.log(Level.INFO, msg);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchToGameLobbyScene(stage, new Game(name,playerCount,Main.currentPlayer));
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                LOGGER.log(Level.INFO, "Could not create game");

                alert.setTitle("UNO");
                alert.setHeaderText("Could not create game");
                alert.setContentText("Try a different name.");
                alert.showAndWait();

                //TODO: switch back to lobby if failed? switchToLobbyScene(stage, msg);
            }
        });
        createGameService.start();
    }

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }

    private void switchToGameLobbyScene(Stage stage, Game game) {
        LOGGER.log(Level.INFO, "switching To GameLobbyScene");

        stage.setScene(Main.sceneFactory.getGameLobbyScene(game));

        LOGGER.log(Level.INFO, "switched To GameLobbyScene");
    }

}
