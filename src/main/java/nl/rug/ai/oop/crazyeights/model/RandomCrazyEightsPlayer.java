package nl.rug.ai.oop.crazyeights.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Player that plays Crazy Eights by randomly choosing
 * legal actions to play
 */
public class RandomCrazyEightsPlayer implements CrazyEightsPlayer {
	/**
	 * Shuffles their hand and picks the first playable Card to
	 * play. If no such Card exists, draws a card
	 * @param hand List of Cards in the player's hand
	 * @param game CardGame being played
	 */
	@Override
	public void takeTurn(List<Card> hand, CrazyEights game) {
		Collections.shuffle(hand);
		for (Card card : hand) {
			if (game.isPlayable(card)) {
				game.playCard(this, card);
			}
		}
		game.playCard(this, null);
	}

	/**
	 * Chooses a random suit.
	 * @param game CardGame being played
	 */
	@Override
	public void chooseSuit(CrazyEights game) {
		game.selectSuit(this, Card.Suit.values()[new Random().nextInt(Card.Suit.values().length)]);
	}
}
