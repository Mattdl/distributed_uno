package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@DatabaseTable(tableName = "Player")
public class Player implements Serializable {
    @DatabaseField(id = true)
    private String name;
    @DatabaseField(canBeNull = false)
    private String hash;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private LinkedList<Card> hand;
    private int handSize; //used on server for lightweight Player object
    //private String token;


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
        return hand;
    }

    public boolean hasHand(){
        return !hand.isEmpty();
    }

    public void setHand(List<Card> hand) {
        this.hand = (LinkedList<Card>) hand;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean equals(Player player) {
        return this.name.equals(player.name);
    }

    public void removeCard(Card card){
        hand.remove(card);
    }

    public int handListSize(){
        return hand.size();
    }

    /**
     * Method used for to get handsize of lightweight Player object returned by Server
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
}
