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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

import static client.Main.sceneFactory;

public class CreateGameController {
    private static final Logger LOGGER = Logger.getLogger(CreateGameController.class.getName());

    @FXML
    private BorderPane createGameBorderPane;

    @FXML
    private ChoiceBox<Integer> numberOfPlayers;

    @FXML
    private TextField gameName;


    @FXML
    public void initialize() {

        BackgroundImage myBI= new BackgroundImage(new Image("background/CreateGame-Screen-Background.gif",sceneFactory.getWIDTH(),sceneFactory.getHEIGHT()*1.02,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        createGameBorderPane.setBackground(new Background(myBI));

        //Set choicebox values
        ObservableList<Integer> availableChoices = FXCollections.observableArrayList(2, 3, 4);
        numberOfPlayers.setItems(availableChoices);
        numberOfPlayers.setValue(2);
    }

    /**
     * Creates a new game, based on name and amount of players, with a click on the button. Calls switchToGameLobbyScene
     * @param event
     */
    @FXML
    public void createGame(ActionEvent event) {

        int playerCount = numberOfPlayers.getSelectionModel().getSelectedItem();
        String name = gameName.getText();

        CreateGameService createGameService = new CreateGameService(name, playerCount, Main.currentPlayer);

        createGameService.setOnSucceeded(event1 -> {
            String gameId = (String) event1.getSource().getValue();

            LOGGER.log(Level.INFO, "Game Id = {0}",gameId);


            if (gameId != null) {
                String msg = "Created game with success";
                LOGGER.log(Level.INFO, msg);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchToGameLobbyScene(stage, new Game(gameId, name,playerCount,Main.currentPlayer));
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

    private void switchToGameLobbyScene(Stage stage, Game game) {
        LOGGER.log(Level.INFO, "switching To GameLobbyScene");

        stage.setScene(Main.sceneFactory.getGameLobbyScene(game));

        LOGGER.log(Level.INFO, "switched To GameLobbyScene");
    }

}
