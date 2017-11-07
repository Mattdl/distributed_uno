package game_logic;

import model.Card;
import model.Game;
import model.Move;

public class GameLogic {

    public GameLogic() {
    }

    /**
     * Used for client and serverside
     *
     * @param cardPlayed (card that players wants to play)
     * @param topCard    (last card played)
     * @return
     */
    public boolean isValidMove(Card cardPlayed, Card topCard) {
        //Plus4 and PickColor can be played anytime
        if (cardPlayed.getCardType() == Card.CardType.PLUS4 || cardPlayed.getCardType() == Card.CardType.PICK_COLOR)
            return true;

        switch (topCard.getCardType()) {
            //Only unvalid if value AND color is different when previous card is a normal card
            case NORMAL:
                if (cardPlayed.getValue() != topCard.getValue() && cardPlayed.getColor() != topCard.getColor())
                    return false;
                return true;
            //If CardType or Color is equal to previous card, move is valid
            case PLUS2:
            case SKIP:
            case REVERSE:
                if (cardPlayed.getCardType() == topCard.getCardType() || cardPlayed.getColor() == topCard.getColor())
                    return true;
                return false;
            //TODO: implement colorchanging function of PLUS4 and PICK_COLOR cards!
            case PICK_COLOR:
            case PLUS4:
                if (cardPlayed.getColor() == topCard.getColor()) return true;
                return false;
        }

        return false;
    }

    /**
     * Processing played move, used by server to update the Game object.
     *
     * @param game
     * @param move
     */
    public Card gameUpdate(Game game, Move move) {

        //Adds move to the game's moveList
        game.addMove(move);

        //Remove card from player's hand
        game.removeCardFromPlayerHand(move);


        switch (move.getCard().getCardType()) {
            //Change direction
            case REVERSE:
                game.setClockwise(!game.isClockwise());
                game.setCurrentPlayer(game.getNextPlayer());
                break;
            //Next player draws 2 cards and skips turn
            case PLUS2:
                game.drawCards(game.getNextPlayer(), 2);
                game.setCurrentPlayer(game.getNextPlayer(2));
                break;
            //Next player draws 4 cards and skips turn
            case PLUS4:
                game.drawCards(game.getNextPlayer(), 4);
                game.setCurrentPlayer(game.getNextPlayer(2));
                break;
            //Skips next player
            case SKIP:
                game.setCurrentPlayer(game.getNextPlayer(2));
                break;
            //Sets next player
            case NORMAL:
            case PICK_COLOR:
                game.setCurrentPlayer(game.getNextPlayer());
                break;
        }
    }
}
