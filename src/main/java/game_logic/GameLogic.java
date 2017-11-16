package game_logic;

import model.Card;
import model.Game;
import model.Move;
import model.Player;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameLogic {

    private static final Logger LOGGER = Logger.getLogger(GameLogic.class.getName());

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
        LOGGER.info("Entering isValidMove");

        //Plus4 and PickColor can be played anytime
        if (cardPlayed.getCardType() == Card.CardType.PLUS4 || cardPlayed.getCardType() == Card.CardType.PICK_COLOR)
            return true;

        switch (topCard.getCardType()) {
            case NORMAL:
                //Workaround for Integer null values
                if (cardPlayed.getValue() == null) {
                    if (cardPlayed.getColor() != topCard.getColor()) return false;
                } else if (!cardPlayed.getValue().equals(topCard.getValue()) && cardPlayed.getColor() != topCard.getColor())
                    return false;
                return true;
            //If CardType or Color is equal to previous card, move is valid
            case PLUS2:
            case SKIP:
            case REVERSE:
                if (cardPlayed.getCardType() == topCard.getCardType() || cardPlayed.getColor() == topCard.getColor())
                    return true;
                return false;
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
        LOGGER.info("Entering gameUpdate in GAME LOGIC");

        if (move.getCard() != null) {
            //Adds move to the game's moveList
            game.addMove(move);

            //Remove card from player's hand
            game.removeCardFromPlayerHand(move);

            //Push the card to the bottom of the deck
            game.addCardToDeckBottom(move);

            switch (move.getCard().getCardType()) {
                //Change direction
                case REVERSE:
                    if(game.getGameSize() == 2){
                        game.setCurrentPlayer(game.getNextPlayer(2));

                    } else{
                    game.setClockwise(!game.isClockwise());
                    game.setCurrentPlayer(game.getNextPlayer(1));}
                    break;
                //Next player draws 2 cards and skips turn
                case PLUS2:
                    game.drawCards(game.getNextPlayer(1), 2);
                    game.setCurrentPlayer(game.getNextPlayer(2));
                    break;
                //Next player draws 4 cards and skips turn
                case PLUS4:
                    game.drawCards(game.getNextPlayer(1), 4);
                    game.setCurrentPlayer(game.getNextPlayer(2));
                    break;
                //Skips next player
                case SKIP:
                    game.setCurrentPlayer(game.getNextPlayer(2));
                    break;
                //Sets next player
                case NORMAL:
                case PICK_COLOR:
                    game.setCurrentPlayer(game.getNextPlayer(1));
                    break;
            }
            return null;
        } else {

            //Draw a card for a player and add the card to his hand
            Card drawnCard = game.drawCardForPlayer(move.getPlayer());
            move.setDrawnCard(drawnCard);

            //Add to game history
            game.addMove(move);

            // Set turn to next player
            game.setCurrentPlayer(game.getNextPlayer(1));

            LOGGER.log(Level.INFO, "Drawn card for player, card = {0}, player = {1}",
                    new Object[]{drawnCard, move.getPlayer()});

            return drawnCard;
        }
    }

    /**
     * Calculates winning players score based on remaining cards in other players hands
     * @param game
     * @return score
     */
    public int calculateScore(Game game){
        int score = 0;

        List<Card> remainingCards =
                game.getPlayerList().stream()
                        .flatMap(e->e.getHand().stream())
                        .collect(Collectors.toList());

        for(Card card : remainingCards){
            switch (card.getCardType()) {
                case NORMAL:
                    score += card.getValue();
                    break;
                case PICK_COLOR:
                case PLUS4:
                    score +=50;
                    break;
                default: score += 20;
            }
        }
        return score;
    }
}
