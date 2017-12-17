package client.controller;

import client.Main;
import client.service.game_lobby.LeaveGameService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Card;
import model.Game;
import model.Player;

import javax.xml.soap.Text;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WinnerController {

    private static final Logger LOGGER = Logger.getLogger(WinnerController.class.getName());

    private Game game;
    private Stage mainStage;
    private String winner, score;

    @FXML
    private Label winnerText;

    @FXML
    private Label scoreText;

    //mainStage needed for popup window
    public WinnerController(Stage mainStage, Game game, String winner, String score){
        this.mainStage = mainStage;
        this.game = game;
        this.winner = winner;
        this.score = score;
    }

    @FXML
    public void initialize() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                winnerText.setText(winner);
                scoreText.setText(score);
            }
        });

    }

    @FXML
    private Button returnToLobbyButton;


    @FXML
    public void leaveGame() {
        LOGGER.log(Level.INFO, "Called leaveGame method in GameController");

        LeaveGameService leaveGameService = new LeaveGameService(game.getGameId());
        leaveGameService.setOnSucceeded(event -> {

            String failMsg = (String) event.getSource().getValue();
            LOGGER.log(Level.INFO, failMsg);

            if (failMsg == null) {

                //TODO: remove player from game and delete game if last player left
                Stage stage = (Stage) returnToLobbyButton.getScene().getWindow();
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

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        mainStage.setScene(Main.sceneFactory.getLobbyScene(msg));
        stage.close();

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }
}
