package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The player object.
 */
@DatabaseTable
public class Player implements Serializable {

    @DatabaseField(id = true)
    private String name;

    /*
    //May be serializable, because we won't persist Cards by themself
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    */
    @ForeignCollectionField(eager = true)
    private Collection<Card> hand = new LinkedList<Card>();

    @DatabaseField
    private int score; //The total score over all games of the player


    private int handSize; //used on server for lightweight Player object

    // ORMLITE: Returning fields for foreign keys
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Game game;


    public Player() {
    }

    public Player(String name) {
        this.name = name;
        this.hand = new LinkedList<>();
    }

    public Player(String name, String hash) {
        this.name = name;
        this.hand = new LinkedList<>();
    }

    /**
     * Lightweight constructor used at server side
     *
     * @param name
     * @param handSize
     */
    public Player(String name, int handSize) {
        this.name = name;
        this.handSize = handSize;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return (List<Card>) hand;
    }

    public boolean hasHand() {
        return !hand.isEmpty();
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public boolean equals(Player player) {
        return this.name.equals(player.name);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Checks are for bugfix
     * Zie Game.removeCardFromPlayerHand()
     *
     * @param card
     * @return
     */
    public boolean removeCard(Card card) {
        boolean successfull = hand.remove(card);
        if (!successfull && (card.getCardType() == Card.CardType.PLUS4 || card.getCardType() == Card.CardType.PICK_COLOR)) {
            successfull = hand.remove(new Card(null, card.getCardType(), null, card.getValue()));
        }
        return successfull;
    }

    public int handListSize() {
        return hand.size();
    }

    /**
     * Method used for to get handsize of lightweight Player object returned by Server
     *
     * @return
     */
    public int getHandSize() {
        return handSize;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", hand=" + hand +
                ", handSize=" + handSize +
                '}';
    }

    public void addCards(List<Card> cards) {
        hand.addAll(cards);
    }

    public Collection<Card> getHandCollection() {
        return hand;
    }
}
