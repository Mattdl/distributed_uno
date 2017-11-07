package client.controller;

import client.Main;
import client.service.game_lobby.GameLobbyService;
import client.service.game_lobby.LeaveGameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Game;
import model.Player;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLobbyController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(GameLobbyController.class.getName());

    private Game currentGame;

    private GameLobbyService gameLobbyService;

    @FXML
    private VBox currentPlayersVBox;

    @FXML
    private Button leaveGameButton;

    @FXML
    private Text gameNameText;

    @FXML
    private Text numberOfPlayersText;

    public GameLobbyController(Game game) {
        this.currentGame = game;
    }


    @FXML
    public void initialize() {
        LOGGER.log(Level.INFO, "Initializing GameLobbyController, Currentgame={0}", currentGame);
        currentGame.addObserver(this);
        gameLobbyService = new GameLobbyService(currentGame);
        gameLobbyService.start();

        gameNameText.setText(currentGame.getGameName());
    }

    /**
     * Called by button in GameLobby view
     */
    @FXML
    public void leaveGame() {
        LOGGER.log(Level.INFO, "Called leaveGame method in GameLobbyController");

        LeaveGameService leaveGameService = new LeaveGameService(currentGame.getGameName());
        leaveGameService.setOnSucceeded(event -> {

            String failMsg = (String) event.getSource().getValue();
            LOGGER.log(Level.INFO, failMsg);

            if (failMsg == null) {

                gameLobbyService.setInGameLobby(false);
                Stage stage = (Stage) leaveGameButton.getScene().getWindow();
                switchToLobbyScene(stage, null);

            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("UNO");
                        alert.setHeaderText("Failed to leave the game");
                        alert.setContentText(failMsg);
                        alert.showAndWait();
                    }
                });
            }
        });
        leaveGameService.start();
    }

    @Override
    public void update(Observable o, Object arg) {

        //Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                //Set Playerlist
                currentPlayersVBox.getChildren().clear();
                for (Player p : currentGame.getPlayerList()) {
                    currentPlayersVBox.getChildren().add(new Text(p.getName()));
                }

                //Set playercount
                numberOfPlayersText.setText(currentGame.getPlayerList().size() + " of " + currentGame.getGameSize());

                if (currentGame.isStartable()) {
                    //Go to the game scene
                    //TODO countdown before starting
                    LOGGER.info("Game is startable");
                    Stage stage = (Stage) leaveGameButton.getScene().getWindow();
                    currentGame.deleteObservers(); //VERY IMPORTANT!!!
                    switchToGameScene(stage, currentGame);

                }
            }
        });

    }

    private void switchToGameScene(Stage stage, Game game) {
        LOGGER.log(Level.INFO, "switching To GameScene");

        stage.setScene(Main.sceneFactory.getGameScene(game));

        LOGGER.log(Level.INFO, "switched To GameScene");
    }

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }
}
