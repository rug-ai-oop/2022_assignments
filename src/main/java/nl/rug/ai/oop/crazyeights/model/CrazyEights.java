package nl.rug.ai.oop.crazyeights.model;

import java.util.*;


public class CrazyEights {
	/**
	 * The initial hand size of players
	 */
	public static int INITIAL_HAND_SIZE = 5;

	/**
	 * Tracks the state of the game. Useful for determining
	 * what actions are legal to perform
	 */
	enum GameState {
		INACTIVE, PLAYING, CHOOSING_SUIT
	}
	private List<CrazyEightsPlayer> players = new ArrayList();
	private Map<CrazyEightsPlayer, List<Card>> hands = new HashMap();
		// Note that there is no outside access to card hands,
		// ensuring that players cannot see the cards of others
	private List<Card> deck = new ArrayList();
	private List<Card> discardPile = new ArrayList();
	private Card topCardOnDiscardPile;
	private CrazyEightsPlayer currentPlayer;
	private GameState state = GameState.INACTIVE;

	/**
	 * Builds a deck of cards, implementing the special rules
	 * for special cards.
	 */
	private void buildDeck() {
		deck.clear();
		discardPile.clear();
		for (CrazyEightsPlayer player : players) {
			hands.get(player).clear();
		}
		for (Card.Suit suit : Card.Suit.values()) {
			deck.add(new Card(suit, 1){
				/**
				 * An Ace changes reverses the order of players.
				 */
				@Override
				protected void execute() {
					Collections.reverse(players);
					moveToNextPlayer();
				}
			});
			deck.add(new Card(suit, 2){
				/**
				 * A two causes the next player to draw 2 cards
				 */
				@Override
				protected void execute() {
					moveToNextPlayer();
					drawCards(2);
				}
			});
			deck.add(new Card(suit, 12){
				/**
				 * A Queen causes the next player to skip their turn
				 */
				@Override
				protected void execute() {
					moveToNextPlayer();
					moveToNextPlayer();
				}
			});
			deck.add(new Card(suit, 8){
				/**
				 * An eight changes its suit to match the player's choice
				 */
				@Override
				protected void execute() {
					selectSuit();
					moveToNextPlayer();
				}

				/**
				 * An eight is wild, and can be played on top of any card
				 */
				@Override
				protected boolean isPlayableOn(Card other) {
					return true;
				}
			});
			for (int i: new int[]{3,4,5,6,7,9,10,12}) {
				deck.add(new Card(suit, i){
					/**
					 * Regular cards do nothing but
					 * pass the turn to the next player
					 */
					@Override
					protected void execute() {
						moveToNextPlayer();
					}
				});
			}
		}
	}

	/**
	 * Allows the current player to select a suit. To simulate this,
	 * a "virtual card" of the chosen suit and the original value
	 * is placed on top of the discard pile.
	 */
	private void selectSuit() {
		Card.Suit suit = currentPlayer.chooseSuit(this);
		topCardOnDiscardPile = new Card(suit, topCardOnDiscardPile.getValue()) {
			@Override
			protected void execute() {	}
		};
		System.out.println("Player "+players.indexOf(currentPlayer)+" changes color to "+suit);

	}

	/**
	 * Moves a given card to the top of the discard pile
	 * @param card Card to be discarded
	 */
	private void discard(Card card) {
		topCardOnDiscardPile = card;
		discardPile.add(card);
	}

	/**
	 * Passes the turn to the next player
	 */
	private void moveToNextPlayer() {
		int currentPlayerId = players.indexOf(currentPlayer);
		currentPlayer = players.get((currentPlayerId + 1) % players.size());
	}

	/**
	 * Causes the current player to draw cards
	 * @param n the number of cards to draw
	 */
	private void drawCards(int n) {
		for (int i = 0; i < n; i++) {
			drawCard();
		}
	}

	/**
	 * Causes the current player to draw a card. If the deck has
	 * run out, the discard pile is shuffled into the deck.
	 * @return Card that was drawn by the player
	 */
	private Card drawCard() {
		if (deck.size() <= 1) {
			while (discardPile.size() > 1) {
				deck.add(discardPile.remove(1));
			}
			Collections.shuffle(deck);
		}
		Card cardDrawn = deck.remove(0);
		hands.get(currentPlayer).add(cardDrawn);
		System.out.println("Player "+players.indexOf(currentPlayer)+" draws "+cardDrawn);
		return cardDrawn;
	}

	/**
	 * Adds a player to the game. Can only be performed when the game
	 * is not currently being played.
	 * @param player CardGamePlayer to add to the game
	 */
	public void addPlayer(CrazyEightsPlayer player) {
		if (!isGameActive()) {
			players.add(player);
			hands.put(player, new ArrayList());
		}
	}

	/**
	 * Removes a player from the game. Can only be performed when the game
	 * is not currently being played.
	 * @param player CardGamePlayer to remove from the game
	 */
	public void removePlayer(CrazyEightsPlayer player) {
		if (!isGameActive()) {
			players.remove(player);
			hands.remove(player).clear();
		}
	}

	/**
	 * Determines whether the game is currently being played
	 * @return true iff a game is in progress
	 */
	public boolean isGameActive() {
		return (state != GameState.INACTIVE);
	}

	/**
	 * Starts a new game
	 */
	public void start() {
		if (!isGameActive()) {
			state = GameState.PLAYING;
			buildDeck();
			Collections.shuffle(deck);
			for (int i = 0; i < players.size(); i++) {
				List<Card> hand = hands.get(players.get(i));
				for (int j = 0; j < INITIAL_HAND_SIZE; j++) {
					hand.add(deck.remove(0));
				}
				System.out.println(i + ":\t" + hand);
			}
			discard(deck.remove(0));
			System.out.println("Top card is " + topCardOnDiscardPile);
			currentPlayer = players.get(0);
			playRound();
		}
	}

	/**
	 * Plays a single round of the game by
	 * 1) Asking a player to choose an action
	 * 2) Executing the action
	 * 3) Ending the game if appropriate
	 */
	private void playRound() {
		while (isGameActive()) {
			List<Card> hand = hands.get(currentPlayer);
			List<Card> cards = new ArrayList();
			cards.addAll(hand);
				// By handing the player a copy of their hand,
				// players are unable to change the cards in
				// their actual hand
			Card cardPlayed;
			do {
				cardPlayed = currentPlayer.takeTurn(cards, this);
			} while (!(cardPlayed == null || isPlayable(cardPlayed)));
				// The while loop above ensures that the current player
				// can actually play the card they have chosen
			if (cardPlayed == null) {
				drawCard();
				moveToNextPlayer();
			} else {
				System.out.println("Player "+players.indexOf(currentPlayer)+" plays "+cardPlayed);
				hand.remove(cardPlayed);
				discard(cardPlayed);
				cardPlayed.execute();
			}
			checkEndGame();
		}
	}

	/**
	 * Determines whether the game has ended because some player
	 * no longer has any cards
	 */
	private void checkEndGame() {
		for (List<Card> hand : hands.values()) {
			if (hand.size() < 1) {
				state = GameState.INACTIVE;
				return;
			}
		}
	}

	/**
	 * Retrieves the current card on top of the discard pile.
	 * Since Cards are immutable, players are unable to change
	 * the top card of the discard pile
	 * @return Card on top of the discard pile
	 */
	public Card getTopCardOnDiscardPile() {
		return topCardOnDiscardPile;
	}

	/**
	 * Determines whether a card is playable in the current situation.
	 * @param card Card held by the current player
	 * @return true iff the current player can play the given Card
	 */
	public boolean isPlayable(Card card) {
		if (hands.get(currentPlayer).contains(card)) {
			return card.isPlayableOn(topCardOnDiscardPile);
		}
		return false;
	}

	/**
	 * Plays a test game of Crazy Eights
	 * @param args
	 */
	public static void main(String[] args) {
		CrazyEights game = new CrazyEights();
		game.addPlayer(new RandomCrazyEightsPlayer());
		game.addPlayer(new RandomCrazyEightsPlayer());
		game.addPlayer(new RandomCrazyEightsPlayer());
		System.out.println(game.players.size());
		game.start();
	}

}
