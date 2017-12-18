package client.controller;


import client.Main;
import client.service.game.*;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import game_logic.GameLogic;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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

    private Boolean isGameFinished;

    @FXML
    private ChoiceBox<Card.CardColor> colorChoiceBox;

    @FXML
    private ListView<Card> handListView;

    @FXML
    private ImageView lastCardPlayed;

    @FXML
    private Text lastPlayedCardText;

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

    private FetchPlayersInfoService fetchPlayersInfoService;

    private FetchCurrentPlayerAndCardService currentPlayerAndCardService;

    private boolean successfulGameStart;

    private FetchPlusCardsService fetchPlusCardsService;

    public GameController(Game game) {
        this.game = game;
        this.gameLogic = new GameLogic();
        this.game.addObserver(this);
        this.isGameFinished = false;
        this.successfulGameStart = false;
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



        //Set choicebox values
        ObservableList<Card.CardColor> availableChoices = FXCollections.observableArrayList(Card.CardColor.BLUE, Card.CardColor.GREEN, Card.CardColor.RED, Card.CardColor.YELLOW);
        colorChoiceBox.setItems(availableChoices);
        colorChoiceBox.setValue(Card.CardColor.YELLOW);
        colorChoiceBox.setVisible(false);
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
                    successfulGameStart = (boolean) event.getSource().getValue();

                    if (successfulGameStart) {
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

        //TODO Can pass this boolean object for finishing game?
        fetchPlayersInfoService = new FetchPlayersInfoService(game, false, false);
        fetchPlayersInfoService.start();

        currentPlayerAndCardService = new FetchCurrentPlayerAndCardService(game, false,false);
        currentPlayerAndCardService.start();

        fetchPlusCardsService = new FetchPlusCardsService(game, false);
        fetchPlusCardsService.start();
    }

    /**
     * Method called when a player draws a card.
     *
     * @param event
     */
    @FXML
    public void drawCard(ActionEvent event) {
        LOGGER.info("Entering drawCard");
        if (game.getCurrentPlayer().equals(Main.currentPlayer)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("Running drawCard thread");
                    PlayMoveService playMoveService = new PlayMoveService(game, new Move(Main.currentPlayer, null));
                    playMoveService.setOnSucceeded(event -> {

                        LOGGER.info("Draw card successfully passed to server!");
                        serverInfoText.setText("Draw card is passed to server, enjoy!");
                    });
                    playMoveService.start();

                    LOGGER.info("Ended drawCard thread");
                }
            });
        }
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

                    Card playedCard = handListView.getSelectionModel().getSelectedItem();

                    if (click.getClickCount() % 2 == 0) {
                        if ((playedCard.getCardType() == Card.CardType.PICK_COLOR || playedCard.getCardType() == Card.CardType.PLUS4) && !colorChoiceBox.isVisible())
                            colorChoiceBox.setVisible(true);
                        else {

                            Card lastPlayedCard = game.getLastPlayedCard();

                            boolean isValidMove = gameLogic.isValidMove(playedCard, lastPlayedCard);

                            LOGGER.log(Level.INFO, "Trying to play: " + playedCard.toString() + " on " + lastPlayedCard.toString());
                            LOGGER.log(Level.INFO, "Move is valid: " + isValidMove);

                            if (isValidMove) {
                                if (playedCard.getCardType() == Card.CardType.PLUS4 || playedCard.getCardType() == Card.CardType.PICK_COLOR) {
                                    playedCard.setColor(colorChoiceBox.getSelectionModel().getSelectedItem());
                                }

                                PlayMoveService playMoveService = new PlayMoveService(game, new Move(Main.currentPlayer, playedCard));
                                playMoveService.setOnSucceeded(event -> {

                                    game.removeCardFromPlayerHand(new Move(Main.currentPlayer, playedCard));

                                    LOGGER.info("Move successfully passed to server!");
                                    serverInfoText.setText("Move is passed to server, enjoy!");
                                });

                                colorChoiceBox.setVisible(false);

                                playMoveService.start();

                            } else {
                                LOGGER.info("Unvalid move");
                                serverInfoText.setText("Move is not valid. Please pick another card.");
                            }
                        }
                    }
                }
            });
        } else
        {
            serverInfoText.setText("It is not your turn...");
        }
    }

    public void gameFinished() {
        EndGameService endGameService = new EndGameService(game);
        endGameService.setOnSucceeded(event -> {

            List<String> results = endGameService.getValue();

            LOGGER.log(Level.INFO, "Game finished");
            fetchPlayersInfoService.setGameFinished(true);
            currentPlayerAndCardService.setGameFinished(true);
            fetchPlusCardsService.setGameFinished(true);

            Stage stage = (Stage) handListView.getScene().getWindow();
            switchToWinnerScene(stage, null, results.get(0), results.get(1));
            game.deleteObservers();

        });
        endGameService.start();
    }

    private void switchToWinnerScene(Stage stage, String msg, String winner, String score) {
        LOGGER.log(Level.INFO, "switching To WinnerScene");

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setResizable(false);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setTitle("Winner Screen");


        popup.setScene(Main.sceneFactory.getWinnerScene(stage, game, winner, score));
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
                LOGGER.log(Level.INFO, "updating UI");
                List<Player> playerList = game.getPlayerList();


                if (!isGameFinished) {

                    //Check if game hasn't ended
                    if(successfulGameStart) {
                        for (Player player : playerList) {
                            LOGGER.log(Level.INFO, "Player " + player.getName() + " has " + player.getHandSize() + " cards");
                            if (player.getHandSize() == 0) {
                                LOGGER.log(Level.WARNING, "EMPTY HAND FOUND");
                                isGameFinished = true;
                                gameFinished();
                            }
                        }
                    }

                    if (game.getCurrentPlayer() != null) {
                        if (game.getCurrentPlayer().equals(Main.currentPlayer)) {
                            currentPlayerText.setText("It is your turn, play a card!");
                        } else {
                            currentPlayerText.setText("It's the turn of player " + game.getCurrentPlayer().getName());
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
                else{
                }
            }
        });
    }
}
