package model;

import java.io.Serializable;

public class Move implements Serializable {
    private Player player;
    private Card card;
    private boolean takesFromDeck;

    public Move(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }


    public boolean isTakesFromDeck() {
        return takesFromDeck;
    }
}
