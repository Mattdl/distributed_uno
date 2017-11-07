package model;

import java.io.Serializable;

/**
 * The Move object represents the played Move. The Player (client) that Played the card and the Card of the Move.
 * If the hasDrawnCard attribute is true, this means that the card attribute represents the Card that is drawn from the
 * deck by the player.
 */
public class Move implements Serializable {
    private Player player;
    private Card card;
    private boolean hasDrawnCard;

    public Move(Player player, Card card) {
        this.player = player;
        this.card = card;
        this.hasDrawnCard = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

    public boolean isHasDrawnCard() {
        return hasDrawnCard;
    }

    public void setHasDrawnCard(boolean hasDrawnCard) {
        this.hasDrawnCard = hasDrawnCard;
    }
}
