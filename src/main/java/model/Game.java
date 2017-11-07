package model;

import app_server.DeckBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//@DatabaseTable(tableName = "game")
public class Game extends Observable implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    //@DatabaseField(generatedId = true)
    //private int id;

    //@DatabaseField
    private State state;

    //@DatabaseField
    private boolean clockwise;

    //@DatabaseField(foreign = true)
    private List<Player> playerList;

    //@DatabaseField(foreign = true)
    private LinkedList<Card> deck;

    //@DatabaseField(foreign = true)
    private List<Move> moves;

    //@DatabaseField
    private String gameName;

    //@DatabaseField
    private int gameSize;

    private int joinedPlayers;

    private int version;

    private Player currentPlayer;

    //private String password;

    public Game(String gameName, int gameSize, Player initialPlayer) {
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        this.version = 0;
        this.moves = new LinkedList<>();
        playerList.add(initialPlayer);
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

            if (playerList.get(i).equals(player)) {
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
            if (playerList.get(i).equals(player)) {
                return playerList.get(i);
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
            Move move = moves.get(i);
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
            Card card = deck.pollFirst();
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
        Card firstCard = deck.pollFirst();
        moves.add(new Move(null, firstCard));
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public void removeCardFromPlayerHand(Move move) {
        move.getPlayer().removeCard(move.getCard());

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
            player.addCard(deck.pollFirst());
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
        Card ret = deck.pollFirst();
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
            return playerList.get((playerList.indexOf(currentPlayer) + amount) % playerList.size());
        else {
            int newIndex = playerList.indexOf(currentPlayer) - amount;
            if (newIndex < 0) {
                newIndex += playerList.size();
            }
            return playerList.get(newIndex % playerList.size());
        }
    }

    /**
     * Puts a card on the bottom of the deck
     *
     * @param move
     */
    public void addCardToDeckBottom(Move move) {
        deck.push(move.getCard());
    }

    public Move getLastMove() {
        return moves.get(moves.size() - 1);
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
        return playerList;
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
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = new DeckBuilder().makeDeck();
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

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


    @Override
    public String toString() {
        return "Game{" +
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

