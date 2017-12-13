package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * The Move object represents the played Move. The Player (client) that Played the card and the Card of the Move.
 * If the hasDrawnCard attribute is true, this means that the card attribute represents the Card that is drawn from the
 * deck by the player.
 */
@DatabaseTable
public class Move implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField( foreign = true)
    private Player player;

    @DatabaseField( foreign = true)
    private Card card;

    @DatabaseField
    private boolean hasDrawnCard;

    // ORMLITE: Returning fields for foreign keys
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Game game;

    public Move() {
    }

    public Move(Player player, Card card) {
        this.player = player;
        this.card = card;
        this.hasDrawnCard = false;
    }

    public Move(Player player, Card card, boolean hasDrawnCard) {
        this.player = player;
        this.card = card;
        this.hasDrawnCard = hasDrawnCard;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public boolean isHasDrawnCard() {
        return hasDrawnCard;
    }

    public void setHasDrawnCard(boolean hasDrawnCard) {
        this.hasDrawnCard = hasDrawnCard;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Called when a Move object get's a drawn Card assigned.
     *
     * @param drawnCard
     */
    public void setDrawnCard(Card drawnCard) {
        this.card = drawnCard;
        hasDrawnCard = true;
    }

    @Override
    public String toString() {
        return "Move{" +
                "player=" + player +
                ", card=" + card +
                ", hasDrawnCard=" + hasDrawnCard +
                '}';
    }
}
