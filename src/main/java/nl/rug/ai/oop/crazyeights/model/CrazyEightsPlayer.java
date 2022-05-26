package nl.rug.ai.oop.crazyeights.model;

import java.util.List;

/**
 * Player functions for the Crazy Eights game
 */
public interface CrazyEightsPlayer {
	/**
	 * Notifies the player that it is their turn to
	 * play or draw a card.
	 * @param hand List of Cards in the player's hand
	 * @param game CardGame being played
	 */
	void takeTurn(List<Card> hand, CrazyEights game);

	/**
	 * Notifies the player that they can select a suit from
	 * the four possibilities Clubs, Hearts, Spades, and Diamonds
	 * @param game CardGame being played
	 */
	void chooseSuit(CrazyEights game);

}
