package model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Player implements Serializable {
    private String name;
    private String password;
    private List<Card> hand;
    private int handSize; //used on server for lightweight Player object
    //private String token;


    public Player(String name) {
        this.name = name;
        this.hand = new LinkedList<>();
    }

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
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
        this.hand = hand;
    }

    public String getPassword() {
        return password;
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
                ", password='" + password + '\'' +
                ", hand=" + hand +
                ", handSize=" + handSize +
                '}';
    }
}
