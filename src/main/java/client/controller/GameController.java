package client.controller;


import client.Main;
import client.service.game.CheckPlayersService;
import client.service.game.FetchCurrentPlayerAndCardService;
import client.service.game.FetchPlayersInfoService;
import client.service.game.InitService;
import client.service.game.PlayMoveService;
import game_logic.GameLogic;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Card;
import model.Game;
import model.Move;
import model.Player;

import java.util.Observable;
import java.util.Observer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private int currentPlayerIndex;

    private Game game;

    private GameLogic gameLogic;

    @FXML
    private ListView<Card> handListView;

    @FXML
    private ImageView lastCardPlayed;

    @FXML
    private Text lastPlayedCardText;

    @FXML
    private Button endGameButton;

    @FXML
    private Text player2info;

    @FXML
    private Text player3info;

    @FXML
    private Text player4info;

    @FXML
    private Button drawCardButton;

    @FXML
    private Text serverInfoText;

    @FXML
    private Text currentPlayerText;

    public GameController(Game game) {
        this.game = game;
        this.gameLogic = new GameLogic();
        this.game.addObserver(this);
    }

    @FXML
    public void initialize() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Player player : game.getPlayerList())
                    if (player.equals(Main.currentPlayer))
                        currentPlayerIndex = game.getPlayerList().indexOf(player);
                if (game.getGameSize() == 2) {
                    player3info.setVisible(false);
                    player4info.setVisible(false);
                }
                if (game.getGameSize() == 3)
                    player4info.setVisible(false);
            }
        });

        LOGGER.log(Level.INFO, "Everybody's waiting to start");

        initServices();

        //Used to create ListView with images of cards in hand (UNTESTED)


/*        ObservableList<Card> observableList = FXCollections.observableList(Main.currentPlayer.getHand());
        handListView.setItems(observableList);
        handListView.setCellFactory(listView -> new ListCell<Card>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(Card card, boolean empty) {
                super.updateItem(card, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    imageView.setImage(card.getImage());
                    setGraphic(imageView);
                }
            }
        });*/
    }

    /**
     * Calls init services, when everything is returned, the CheckPlayersService is called to wait on other players to start
     * the game.
     */
    private void initServices() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Performing initServices.");

                CheckPlayersService checkPlayersService = new CheckPlayersService(game);
                checkPlayersService.setOnSucceeded(event -> {
                    boolean successful = (boolean) event.getSource().getValue();

                    if (successful) {
                        LOGGER.info("Successful initialization");

                        serverInfoText.setText("Game successfully initialized, all players ready to start!");
                        runGame();

                    } else {
                        LOGGER.info("Failed initialization");

                        serverInfoText.setText("Game failed initialization...");
                    }
                });

                InitService initService = new InitService(game);
                initService.setOnSucceeded(event -> checkPlayersService.start());
                initService.start();

                LOGGER.info("All init services started");

            }
        });
    }

    /**
     * Method for when the game is actually playing after it is initialized
     */
    private void runGame() {
        LOGGER.info("Running game!");

        //TODO run game
        FetchPlayersInfoService fetchPlayersInfoService = new FetchPlayersInfoService(game, false);
        fetchPlayersInfoService.start();

        FetchCurrentPlayerAndCardService currentPlayerAndCardService = new FetchCurrentPlayerAndCardService(game, false);
        currentPlayerAndCardService.start();
    }

    /**
     * Method called when a player draws a card.
     *
     * @param event
     */
    @FXML
    public void drawCard(ActionEvent event) {

    }

    /**
     * Method called when player plays a card from his hand.
     *
     * @param click
     */
    @FXML
    public void playCard(MouseEvent click) {
        if (game.getCurrentPlayer().equals(Main.currentPlayer)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    if (click.getClickCount() == 2) {
                        Card playedCard = handListView.getSelectionModel().getSelectedItem();
                        Card lastPlayedCard = game.getLastPlayedCard();

                        boolean isValidMove = gameLogic.isValidMove(playedCard, lastPlayedCard);

                        LOGGER.log(Level.INFO, "Trying to play: " + playedCard.toString() + " on " + lastPlayedCard.toString());
                        LOGGER.log(Level.INFO, "Move is valid: " + isValidMove);

                        if (isValidMove) {
                            PlayMoveService playMoveService = new PlayMoveService(game, new Move(Main.currentPlayer, playedCard));
                            playMoveService.setOnSucceeded(event -> {

                                serverInfoText.setText("It is your turn, play a card!");

                            });
                            playMoveService.start();
                        } else {
                            serverInfoText.setText("Move is not valid. Please pick another card.");
                        }
                    }
                }
            });
        } else {
            serverInfoText.setText("It is not your turn...");
        }
    }

    //TODO: implementeren zodat automatisch gebeurt wanneer spel gedaan is
    public void eindeSpel() {
        Stage stage = (Stage) endGameButton.getScene().getWindow();
        switchToWinnerScene(stage, null);
    }

    private void switchToWinnerScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To WinnerScene");

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setResizable(false);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setTitle("Winner Screen");


        popup.setScene(Main.sceneFactory.getWinnerScene(stage, game));
        popup.show();

        //stage.setScene(Main.sceneFactory.getWinnerScene(game));

        LOGGER.log(Level.INFO, "switched To WinnerScene");
    }

    @Override
    public void update(Observable o, Object arg) {
        //Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if (game.getCurrentPlayer() != null) {
                    if (game.getCurrentPlayer().equals(Main.currentPlayer)) {
                        currentPlayerText.setText("It is your turn, play a card!");
                    } else {
                        currentPlayerText.setText("It's the turn of player " + game.getCurrentPlayer());
                    }
                }


                //LOAD IMAGES FOR CARDS (SHOULD WORK WHEN IMAGES ARE FIXED)
/*
                ObservableList<Card> observableList = FXCollections.observableList(Main.currentPlayer.getHand());
                handListView.setItems(observableList);
                handListView.setCellFactory(listView -> new ListCell<Card>() {
                private ImageView imageView = new ImageView();

                    @Override
                    public void updateItem(Card card, boolean empty) {
                        super.updateItem(card, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            LOGGER.log(Level.INFO,"cardImage: "+card.getImage());
                            imageView.setImage(card.getImage());
                            setGraphic(imageView);
                        }
                    }
                });*/


                //Show cards as text
                if (Main.currentPlayer.hasHand()) {
                    ObservableList<Card> observableList = FXCollections.observableList(Main.currentPlayer.getHand());
                    handListView.setItems(observableList);
                    handListView.setCellFactory(listView -> new ListCell<Card>() {
                        @Override
                        public void updateItem(Card card, boolean empty) {
                            super.updateItem(card, empty);
                            if (card == null) {
                                setText(null);
                            } else {
                                setText(card.toString());
                            }
                        }
                    });
                }

                List<Player> playerList = game.getPlayerList();

                //Update last played card image
                //lastCardPlayed.setImage(game.getLastPlayedCard().getImage());
                if (game.hasPlayedCards()) {
                    lastPlayedCardText.setText(game.getLastPlayedCard().toString());
                }


                //Set other players hand size
                player2info.setText(playerList.get((currentPlayerIndex + 1) % playerList.size()).getName() + " has " + playerList.get((currentPlayerIndex + 1) % playerList.size()).getHandSize() + " cards");
                player3info.setText(playerList.get((currentPlayerIndex + 2) % playerList.size()).getName() + " has " + playerList.get((currentPlayerIndex + 2) % playerList.size()).getHandSize() + " cards");
                player4info.setText(playerList.get((currentPlayerIndex + 3) % playerList.size()).getName() + " has " + playerList.get((currentPlayerIndex + 3) % playerList.size()).getHandSize() + " cards");


            }
        });
    }
}
