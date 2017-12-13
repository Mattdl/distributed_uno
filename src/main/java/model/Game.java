package model;

import app_server.DeckBuilder;
import client.Main;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@DatabaseTable
public class Game extends Observable implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    @DatabaseField(id = true)
    private String gameNameId; // gamename with timestamp concatenated

    @DatabaseField
    private String gameName;

    @DatabaseField
    private State state;

    @DatabaseField
    private boolean clockwise;

    @ForeignCollectionField(eager = false)
    private Collection<Player> playerList;

    @ForeignCollectionField(eager = false)
    private Collection<Card> deck = new LinkedList<Card>();

    @ForeignCollectionField(eager = false)
    private Collection<Move> moves;

    @DatabaseField
    private int gameSize;

    @DatabaseField
    private int joinedPlayers;

    private int version;

    private Player currentPlayer;

    private boolean isInitialyPersisted;

    //private String password;


    public Game() {
    }

    public Game(String gameName, int gameSize, Player initialPlayer) {
        this.gameNameId = gameName + new Date().getTime();
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        this.version = 0;
        this.moves = new LinkedList<>();
        playerList.add(initialPlayer);
        isInitialyPersisted = false;
    }

    public Game(String gameName, int gameSize, Player initialPlayer, int version) {
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        this.version = version;
        this.moves = new LinkedList<>();
        playerList.add(initialPlayer);
        isInitialyPersisted = false;
    }

    /**
     * Adds a player to the game
     *
     * @param player
     */
    public synchronized void addPlayer(Player player) {
        playerList.add(player);
        setChanged();
        notifyObservers();
    }

    public boolean isJoinable() {
        return playerList.size() < gameSize;
    }

    public boolean isStartable() {
        return playerList.size() == gameSize;
    }

    public synchronized boolean removePlayer(Player player) {
        LOGGER.log(Level.INFO, "Removing player from game");

        int i = 0;

        while (i < playerList.size()) {
            //LOGGER.log(Level.INFO,"In the while");

            if (((List<Player>) playerList).get(i).equals(player)) {
                playerList.remove(i);
                LOGGER.log(Level.INFO, "Removed player: {0}", player);

                setChanged();
                notifyObservers();
                return true;
            }
            i++;
        }

        LOGGER.log(Level.INFO, "Did not find player = {0}", player);

        return false;
    }

    public void addJoinedPlayer() {
        joinedPlayers++;

        setChanged();
        notifyObservers();
    }

    public synchronized Player findPlayer(Player player) {
        int i = 0;

        while (i < playerList.size()) {
            if (((List<Player>) playerList).get(i).equals(player)) {
                return ((List<Player>) playerList).get(i);
            }
            i++;
        }
        return null;
    }

    public void makeCopy(Game serverSideGame) {
        this.gameName = serverSideGame.gameName;
        this.gameSize = serverSideGame.gameSize;
        this.playerList = serverSideGame.playerList;
        this.clockwise = serverSideGame.clockwise;
        this.state = serverSideGame.state;
        this.version = serverSideGame.version;
        //this.moves = serverSideGame.moves;

        setChanged();
        notifyObservers();
    }

    public void updateVersion() {
        this.version++;
    }

    /**
     * Finds the last played card that is not a Drawn Card move.
     *
     * @return
     */
    public Card getLastPlayedCard() {
        int i = moves.size() - 1;

        while (i >= 0) {
            Move move = ((List<Move>) moves).get(i);
            if (!move.isHasDrawnCard() && move.getCard() != null) {
                LOGGER.log(Level.INFO, "Last played card is on deck is {0}", move.getCard());
                return move.getCard();
            }
            i--;
        }

        LOGGER.log(Level.SEVERE, "No last played card found!!");

        return null;
    }

    public boolean hasPlayedCards() {
        return !moves.isEmpty();
    }

    /**
     * Method used by client to add the last played card to the game.
     *
     * @param card
     */
    public void setLastPlayedCard(Card card) {
        moves.add(new Move(null, card));
        setChanged();
        notifyObservers();
    }

    /**
     * Method called by client to draw a card
     *
     * @param drawnCard
     * @param player
     */
    public void setDrawnCardForPlayer(Card drawnCard, Player player) {
        player.getHand().add(drawnCard);

        setChanged();
        notifyObservers();
    }

    /**
     * Method called by FetchInitCardService of client
     *
     * @param cards
     * @param currentPlayer
     */
    public void setCurrentPlayerHand(List<Card> cards, Player currentPlayer) {
        currentPlayer.setHand(cards);

        setChanged();
        notifyObservers();
    }

    /**
     * Method used by server to make lightweight Player-objects to return over RMI to client
     *
     * @return
     */
    public List<Player> getLightPlayerList() {
        LOGGER.info("Entering getLightPlayerList");

        List<Player> ret = new LinkedList<>();

        for (Player p : playerList) {
            Player lightPlayer = new Player(p.getName(), p.getHand().size());

            LOGGER.log(Level.INFO, "Made light player object = {0}", lightPlayer);

            ret.add(lightPlayer);
        }

        LOGGER.info("Leaving getLightPlayerList");

        return ret;
    }

    /**
     * Method to draw the cards from the deck and give them initially to a player
     *
     * @param amountOfCards
     */
    public synchronized LinkedList<Card> givePlayerInitHand(int amountOfCards, Player player) {
        LOGGER.info("Entering givePlayerInitHand.");

        LinkedList<Card> drawnCards = new LinkedList<>();

        for (int i = 0; i < amountOfCards; i++) {
            Card card = ((LinkedList<Card>) deck).pollFirst();
            drawnCards.add(card);
        }

        LOGGER.log(Level.INFO, "Drawn cards for player hand={0}", drawnCards);
        LOGGER.log(Level.INFO, "Deck size : {0}", deck.size());

        player.setHand(drawnCards);

        LOGGER.info("Set hand to player. Leaving givePlayerInitHand.");

        return drawnCards;
    }

    /**
     * Method to draw first card from the deck.
     */
    public synchronized void drawFirstCard() {
        Card firstCard = ((LinkedList<Card>) deck).pollFirst();
        moves.add(new Move(null, firstCard));
    }

    public void addMove(Move move) {
        moves.add(move);
    }


    /**
     * The passed Move has a color, picked by the user. But in the hand on the server, the color is null.
     * This when you call the remove method (in removeCard), it wil not find the card, because it's not equal.
     *
     * @param move
     */
    public void removeCardFromPlayerHand(Move move) {
        LOGGER.log(Level.INFO, "removeCardFromPlayerHand Move = {0}", move);


        boolean foundCard = move.getPlayer().removeCard(move.getCard());

        LOGGER.log(Level.INFO, "AFTER removeCardFromPlayerHand Move = {0}", move);
        LOGGER.log(Level.INFO, "AFTER removeCardFromPlayerHand foundCard = {0}", foundCard);
        LOGGER.log(Level.INFO, "AFTER removeCardFromPlayerHand PLAYER HAND = {0}", move.getPlayer().getHand());
        LOGGER.log(Level.INFO, "AFTER removeCardFromPlayerHand TARGET CARD = {0}", move.getCard());

        setChanged();
        notifyObservers();
    }

    /**
     * Draw cards from the deck for the player. The card is added to the hand of the player.
     *
     * @param player
     * @param amount
     */
    public void drawCards(Player player, int amount) {
        for (int i = 0; i < amount; i++)
            player.addCard(((LinkedList<Card>) deck).pollFirst());
    }

    /**
     * Draw card from the deck for the player. The card is added to the hand of the player.
     *
     * @param player
     * @return the drawn card
     */
    public Card drawCardForPlayer(Player player) {
        LOGGER.log(Level.INFO, "Entering drawCardForPlayer, before: decksize = {0}, playerhand size = {1}",
                new Object[]{deck.size(), player.getHand().size()});
        Card ret = ((LinkedList<Card>) deck).pollFirst();
        player.addCard(ret);
        LOGGER.log(Level.INFO, "Entering drawCardForPlayer, after: decksize = {0}, playerhand size = {1}",
                new Object[]{deck.size(), player.getHand().size()});
        return ret;
    }

    /**
     * Returns next player with certain amount, used for skipping players
     *
     * @param amount
     * @return
     */
    public Player getNextPlayer(int amount) {
        if (clockwise)
            return ((List<Player>) playerList).get(
                    (((List<Player>) playerList).indexOf(currentPlayer) + amount) % playerList.size());
        else {
            int newIndex = ((List<Player>) playerList).indexOf(currentPlayer) - amount;
            if (newIndex < 0) {
                newIndex += playerList.size();
            }
            return ((List<Player>) playerList).get(newIndex % playerList.size());
        }
    }

    /**
     * Puts a card on the bottom of the deck
     *
     * @param move
     */
    public void addCardToDeckBottom(Move move) {
        deck.add(move.getCard());
    }

    /**
     * A Move may be a 'draw_card' or a 'played card'
     *
     * @return
     */
    public Move getLastMove() {
        return ((List<Move>) moves).get(moves.size() - 1);
    }

    /**
     * Finds the last played move that is not a Drawn Card move.
     *
     * @return
     */
    public Move getLastPlayedMove() {
        int i = moves.size() - 1;

        while (i >= 0) {
            Move move = ((List<Move>) moves).get(i);
            if (!move.isHasDrawnCard() && move.getCard() != null) {
                LOGGER.log(Level.INFO, "Last played move is on deck is {0}", move);
                return move;
            }
            i--;
        }

        LOGGER.log(Level.SEVERE, "No last played card found!!");

        return null;
    }


    public void addPlayerPlusCards(List<Card> cards) {

        LOGGER.log(Level.INFO, "Adding plus cards in CLIENT BACKEND, player = {0}, cards = {1}", new Object[]{currentPlayer, cards});

        Main.currentPlayer.addCards(cards);

        LOGGER.log(Level.INFO, "addPlayerPlusCards Finished");


        setChanged();
        notifyObservers();
    }

    /**
     * Checks if the currentPlayer is the turn after the last played player.
     * Can be used to assign plus-cards to the currentPlayer
     *
     * @param currentPlayer
     * @return
     */
    public boolean isPlayerAfterLastPlayer(Player currentPlayer) {

        int currentPlayerIndex = ((List<Player>) playerList).indexOf(currentPlayer);

        //Get last player of a move, NOT a drawcard
        Player lastPlayer = getLastPlayedMove().getPlayer();

        if (lastPlayer == null) {
            return false;
        }

        int lastPlayerIndex = ((List<Player>) playerList).indexOf(lastPlayer);

        boolean ret = currentPlayerIndex == lastPlayerIndex + 1 % gameSize;

        LOGGER.log(Level.INFO, "Plus cards for player = {0}, is = {1}", new Object[]{currentPlayer, ret});

        return ret;
    }


    public enum State {
        WAITING,
        INITALIZING,
        READY,
        ENDED
    }

    public void setDeck() {
        this.deck = new DeckBuilder().makeDeck();
    }

    //GETTERS & SETTERS
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public List<Player> getPlayerList() {
        return ((List<Player>) playerList);
    }

    /**
     * Method called by client to set the lighweight PlayerList
     *
     * @param playerList
     */
    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;

        setChanged();
        notifyObservers();
    }

    public List<Card> getDeck() {
        return ((List<Card>) deck);
    }

    public void setDeck(List<Card> deck) {
        this.deck = new DeckBuilder().makeDeck();
    }

    public List<Move> getMoves() {
        return ((List<Move>) moves);
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    //TODO check usages
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getGameSize() {
        return gameSize;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    public void setJoinedPlayers(int joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        setChanged();
        notifyObservers();
    }

    public boolean isInitialyPersisted() {
        return isInitialyPersisted;
    }

    public void setInitialyPersisted(boolean initialyPersisted) {
        isInitialyPersisted = initialyPersisted;
    }

    public String getGameNameId() {
        return gameNameId;
    }

    @Override
    public String toString() {
        return "game{" +
                "state=" + state +
                ", clockwise=" + clockwise +
                ", playerList=" + playerList +
                ", deck=" + deck +
                ", moves=" + moves +
                ", gameName='" + gameName + '\'' +
                ", gameSize=" + gameSize +
                ", joinedPlayers=" + joinedPlayers +
                '}';
    }
}

