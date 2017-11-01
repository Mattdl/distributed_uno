package model;

import java.util.List;

public class Player {
    private String name;
    private String password;
    private List<Card> hand;
    //private String token;


    public Player(String name) {
        this.name = name;
    }

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void addCard(Card card){
        hand.add(card);
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public String getPassword() {
        return password;
    }
}
