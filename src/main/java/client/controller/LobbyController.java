package client.controller;

import client.Main;
import client.service.lobby.JoinGameService;
import client.service.lobby.LobbyService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Game;
import model.Lobby;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LobbyController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(LobbyController.class.getName());

    private String loginMsg;

    private Lobby lobby;

    private LobbyService lobbyService;

    //@FXML
    // private VBox vboxEntryList;

    @FXML
    private TilePane centerContainer;

    public LobbyController() {
        this.lobby = new Lobby(-1);
        lobby.addObserver(this);
    }

    @FXML
    public void initialize() {

        //Don't show if succesfully logged in or registered

        if (loginMsg != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome to UNO");
            alert.setHeaderText("Join the lobby!");
            alert.setContentText(loginMsg);
            alert.show();
        }

        lobbyService = new LobbyService(lobby);
        lobbyService.start();
    }


    public void update(Observable o, Object arg) {
        LOGGER.info("Model is updated, updating view");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                VBox vboxEntryList = new VBox();
                vboxEntryList.getChildren().add(new Text("Lobby"));
                LOGGER.info("VBOX initiated");

                for (Game game : lobby.getGameList()) {
                    vboxEntryList.getChildren().add(createGameEntry(game));
                }
                LOGGER.info("game entries initiated");

                //Set the list to the container view
                centerContainer.getChildren().clear();
                LOGGER.info("Children cleared");

                centerContainer.getChildren().add(vboxEntryList);
                LOGGER.info("Set to container view");


                LOGGER.info("View updated!");
            }
        });
    }

    /**
     * Creates a list entry for the VBOX
     *
     * @param game
     * @return
     */
    public HBox createGameEntry(Game game) {
        HBox entry = new HBox();
        entry.getChildren().add(new Text(game.getGameName()));
        entry.getChildren().add(new Text("Players: " + game.getPlayerList().size() + " of " + game.getGameSize()));

        Button button = new Button(game.getGameName());
        button.setId(game.getGameId());
        button.setOnAction(e -> joinGame(e));
        entry.getChildren().add(button);

        return entry;
    }

    public void setLoginMsg(String msg) {
        loginMsg = msg;
    }


    private void switchToCreateGameScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To CreateGameScene");

        lobbyService.setInLobby(false);
        stage.setScene(Main.sceneFactory.getCreateGameScene(msg));

        LOGGER.log(Level.INFO, "switched To CreateGameScene");
    }

    private void switchToGameLobbyScene(Stage stage, Game game) {
        LOGGER.log(Level.INFO, "switching To GameLobbyScene, with currentGame = {0}", game);

        stage.setScene(Main.sceneFactory.getGameLobbyScene(game));

        LOGGER.log(Level.INFO, "switched To GameLobbyScene, with currentGame = {0}", game);
    }

    @FXML
    public void createNewGame(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switchToCreateGameScene(stage, null);
    }

    public void joinGame(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String gameName = clickedButton.getId();

        JoinGameService joinGameService = new JoinGameService(gameName);
        joinGameService.setOnSucceeded(event1 -> {
            String failMsg = (String) event1.getSource().getValue();
            if (failMsg == null) {
                LOGGER.log(Level.INFO, "CLIENT RECEIVED ACK FROM SERVER: COULD JOIN GAME");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Game game = lobby.findGame(gameName);

                LOGGER.log(Level.INFO, "CLIENT found game in lobby = {}", game);

                switchToGameLobbyScene(stage, game);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("UNO");
                alert.setHeaderText("Joining is not possible");
                alert.setContentText(failMsg);
                alert.showAndWait();
            }
        });
        joinGameService.start();
    }
}
