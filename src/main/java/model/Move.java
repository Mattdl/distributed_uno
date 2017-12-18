package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import static model.Move.MoveType.DRAW_CARD;
import static model.Move.MoveType.NORMAL;
import static model.Move.MoveType.PLUS_CARD;

/**
 * The Move object represents the played Move. The Player (client) that Played the card and the Card of the Move.
 * If the hasDrawnCard attribute is true, this means that the card attribute represents the Card that is drawn from the
 * deck by the player.
 */
@DatabaseTable
public class Move implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Player player;

    @DatabaseField(foreign = true)
    private Card card;

    @DatabaseField
    private MoveType moveType;


    // ORMLITE: Returning fields for foreign keys
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Game game;

    public Move() {
    }

    public Move(Player player, Card card) {
        this.player = player;
        this.card = card;
        this.moveType = NORMAL;
    }

    public Move(Player player, Card card, MoveType moveType) {
        this.player = player;
        this.card = card;
        this.moveType = moveType;
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    /**
     * Called when a Move object get's a drawn Card assigned.
     *
     * @param drawnCard
     */
    public void setDrawnCard(Card drawnCard) {
        this.card = drawnCard;
        moveType = DRAW_CARD;
    }

    public void setPlusCard(Card plusCard) {
        this.card = plusCard;
        moveType = PLUS_CARD;
    }

    @Override
    public String toString() {
        return "Move{" +
                "player=" + player +
                ", card=" + card +
                ", moveType=" + moveType +
                ", game=" + game +
                '}';
    }

    public enum MoveType {
        NORMAL, DRAW_CARD, PLUS_CARD, SKIP
    }
}
