package client.controller;


import client.Main;
import client.service.ImgFetchService;
import client.service.game.*;
import game_logic.GameLogic;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import model.Card;
import model.Game;
import model.Move;
import model.Player;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.Main.sceneFactory;


public class GameController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private int currentPlayerIndex;

    private Game game;

    private GameLogic gameLogic;

    private Boolean isGameFinished;

    private boolean haveAllPlayersJoined;

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

    @FXML
    private BorderPane gameBorderPane;

    private List<VBox> otherPlayers;

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

        BackgroundImage myBI = new BackgroundImage(new Image("background/Game-Screen-Background.jpg", sceneFactory.getWIDTH(), sceneFactory.getHEIGHT() * 1.02, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        gameBorderPane.setBackground(new Background(myBI));

        initOtherPlayers();

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

        initServices();

        //Set choicebox values
        ObservableList<Card.CardColor> availableChoices = FXCollections.observableArrayList(Card.CardColor.BLUE, Card.CardColor.GREEN, Card.CardColor.RED, Card.CardColor.YELLOW);
        colorChoiceBox.setItems(availableChoices);
        colorChoiceBox.setValue(Card.CardColor.YELLOW);
        colorChoiceBox.setVisible(false);
    }

    //TODO tries to load background immediately, but first needs to wait on images to be loaded.
    private void initOtherPlayers() {
        otherPlayers = new ArrayList<>();

        Card backCard = new Card(Card.CardType.BACK, null, -1);

        VBox otherPlayer;
        ImageView imageView;
        Text text;

        // Don't need to include yourself
        for (int i = 0; i < game.getGameSize() - 1; i++) {

            otherPlayer = new VBox();
            otherPlayer.setAlignment(Pos.CENTER);

            imageView = new ImageView(ImgFetchService.imageMap.get(backCard));
            text = new Text();
            text.setStyle("-fx-text-fill: white;");

            otherPlayer.getChildren().add(imageView);
            otherPlayer.getChildren().add(text);

            otherPlayers.add(otherPlayer);
        }

        if (game.getGameSize() == 2) {

            VBox vboxOtherPlayer = otherPlayers.get(0);
            gameBorderPane.setTop(vboxOtherPlayer);

        } else if (game.getGameSize() == 3) {

            VBox vboxOtherPlayer = otherPlayers.get(0);
            gameBorderPane.setLeft(vboxOtherPlayer);

            vboxOtherPlayer = otherPlayers.get(1);
            gameBorderPane.setRight(vboxOtherPlayer);

        } else if (game.getGameSize() == 4) {

            VBox vboxOtherPlayer = otherPlayers.get(0);
            gameBorderPane.setLeft(vboxOtherPlayer);

            vboxOtherPlayer = otherPlayers.get(1);
            gameBorderPane.setTop(vboxOtherPlayer);

            vboxOtherPlayer = otherPlayers.get(2);
            gameBorderPane.setRight(vboxOtherPlayer);
        }
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

                        haveAllPlayersJoined = true;
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

        fetchPlayersInfoService = new FetchPlayersInfoService(game, false, false);
        fetchPlayersInfoService.start();

        currentPlayerAndCardService = new FetchCurrentPlayerAndCardService(game, false, false);
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

        if (ImgFetchService.hasFetchedAllCards) {
            if (haveAllPlayersJoined) {
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
            } else {
                serverInfoText.setText("Still waiting on other players to start...");
            }
        } else {
            serverInfoText.setText("Not all card images are fetched!");
        }
    }

    /**
     * Method called when player plays a card from his hand.
     *
     * @param click
     */
    @FXML
    public void playCard(MouseEvent click) {
        if (ImgFetchService.hasFetchedAllCards) {

            if (haveAllPlayersJoined) {

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
                } else {
                    serverInfoText.setText("It is not your turn...");
                }
            } else {
                serverInfoText.setText("Still waiting on other players to start...");
            }
        } else {
            serverInfoText.setText("Not all card images are fetched!");
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
                    if (successfulGameStart) {
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

                    if (Main.currentPlayer.hasHand()) {
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
                                    LOGGER.log(Level.INFO, "SETTING IMAGE FOR CARD = {0}", card);
                                    imageView.setImage(ImgFetchService.imageMap.get(card));
                                    setGraphic(imageView);
                                }
                            }
                        });
                    }


                    /*
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
                    */

                    //Update last played card image
                    if (game.hasPlayedCards()) {
                        lastPlayedCardText.setText(game.getLastPlayedCard().toString());
                        lastCardPlayed.setImage(ImgFetchService.imageMap.get(game.getLastPlayedCard()));
                    }


                    //Set other players hand size
                    for (int i = 0; i < otherPlayers.size(); i++) {
                        VBox otherPlayer = otherPlayers.get(i);
                        Text text = (Text) otherPlayer.getChildren().get(1);

                        int playerOffset = i + 1;

                        text.setText(playerList.get((currentPlayerIndex + playerOffset) % playerList.size()).getName() + " has " + playerList.get((currentPlayerIndex + playerOffset) % playerList.size()).getHandSize() + " cards");
                    }

                }
            }
        });
    }
}
