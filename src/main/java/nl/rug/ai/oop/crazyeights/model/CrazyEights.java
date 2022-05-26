package nl.rug.ai.oop.crazyeights.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;


/**
 * Game controller class for Crazy Eights, with the following rules:
 *  Ace  - Reverses play order
 *   2   - Draw 2 cards
 *   8   - Can be played on any card, changes suit
 * Queen - Skip turn
 */
public class CrazyEights {
	/**
	 * The initial hand size of players
	 */
	public static int INITIAL_HAND_SIZE = 5;

	/**
	 * Tracks the state of the game. Used to prevent players from
	 * executing cards.
	 */
	enum GameState {
		INACTIVE, PLAYING, WAITING_FOR_PLAYER_CARD, WAITING_FOR_PLAYER_SUIT
	}
	private List<CrazyEightsPlayer> players = new ArrayList();
	private Map<CrazyEightsPlayer, List<Card>> hands = new HashMap();
		// Note that there is no outside access to card hands,
		// ensuring that players cannot see the cards of others
		// However, players can also not see how many cards other
		// players have. You may want to change this for a
		// GUI implementation.
	private List<Card> deck = new ArrayList();
	private List<Card> discardPile = new ArrayList();
	private Card topCardOnDiscardPile;
	private CrazyEightsPlayer currentPlayer;
	private GameState state = GameState.INACTIVE;
	private List<PropertyChangeListener> listeners = new ArrayList();

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
					if (state == GameState.PLAYING) {
						Collections.reverse(players);
						moveToNextPlayer();
					}
				}
			});
			deck.add(new Card(suit, 2){
				/**
				 * A two causes the next player to draw 2 cards
				 */
				@Override
				protected void execute() {
					if (state == GameState.PLAYING) {
						moveToNextPlayer();
						drawCards(2);
					}
				}
			});
			deck.add(new Card(suit, 12){
				/**
				 * A Queen causes the next player to skip their turn
				 */
				@Override
				protected void execute() {
					if (state == GameState.PLAYING) {
						moveToNextPlayer();
						moveToNextPlayer();
					}
				}
			});
			deck.add(new Card(suit, 8){
				/**
				 * An eight changes its suit to match the player's choice
				 */
				@Override
				protected void execute() {
					if (state == GameState.PLAYING) {
						selectSuit();
					}
				}

				/**
				 * An eight is wild, and can be played on top of any card
				 */
				@Override
				protected boolean isPlayableOn(Card other) {
					return true;
				}
			});
			for (int i: new int[]{3,4,5,6,7,9,10,11,13}) {
				deck.add(new Card(suit, i){
					/**
					 * Regular cards do nothing but
					 * pass the turn to the next player
					 */
					@Override
					protected void execute() {
						if (state == GameState.PLAYING) {
							moveToNextPlayer();
						}
					}
				});
			}
		}
	}

	/**
	 * Notifies the current player to choose a suit.
	 */
	private void selectSuit() {
		if (state == GameState.PLAYING) {
			state = GameState.WAITING_FOR_PLAYER_SUIT;
			currentPlayer.chooseSuit(this);
		}
	}

	/**
	 * Notifies the game that the given player has selected the given suit.
	 * To simulate this, a "virtual card" of the chosen suit and the original
	 * value is placed on top of the discard pile.
	 *
	 * Throws an IllegalStateException when the given player is not allowed
	 * to select a suit at this point in the game.
	 *
	 * @param player Player that has selected a suit
	 * @param suit Suit that has been selected
	 */
	public void selectSuit(CrazyEightsPlayer player, Card.Suit suit) {
		if (state == GameState.WAITING_FOR_PLAYER_SUIT && currentPlayer == player) {
			state = GameState.PLAYING;
			topCardOnDiscardPile = new Card(suit, topCardOnDiscardPile.getValue());
			notifyListenersOfTopCard();
			moveToNextPlayer();
		} else {
			throw new IllegalStateException("Player is not allowed to select a suit at this time.");
		}
	}

	/**
	 * Moves a given card to the top of the discard pile
	 * @param card Card to be discarded
	 */
	private void discard(Card card) {
		if (state == GameState.PLAYING) {
			topCardOnDiscardPile = card;
			discardPile.add(card);
			notifyListenersOfTopCard();
		}
	}

	/**
	 * Passes the turn to the next player
	 */
	private void moveToNextPlayer() {
		if (state == GameState.PLAYING) {
			int currentPlayerId = players.indexOf(currentPlayer);
			currentPlayer = players.get((currentPlayerId + 1) % players.size());
		}
	}

	/**
	 * Causes the current player to draw cards
	 * @param n the number of cards to draw
	 */
	private void drawCards(int n) {
		if (state == GameState.PLAYING) {
			for (int i = 0; i < n; i++) {
				drawCard();
			}
		}
	}

	private void shuffleDiscardPileIntoDeck() {
		while (discardPile.size() > 1) {
			deck.add(discardPile.remove(1));
		}
		Collections.shuffle(deck);
	}

	/**
	 * Causes the current player to draw a card. If the deck has
	 * run out, the discard pile is shuffled into the deck.
	 * @return Card that was drawn by the player
	 */
	private Card drawCard() {
		if (state == GameState.PLAYING) {
			if (deck.size() <= 1) {
				shuffleDiscardPileIntoDeck();
			}
			Card cardDrawn = deck.remove(0);
			hands.get(currentPlayer).add(cardDrawn);
			notifyListenersOfHandSize();
			return cardDrawn;
		}
		return null;
	}

	/**
	 * Adds a player to the game.
	 *
	 * Throws an IllegalStateException when
	 * the game is already being played.
	 *
	 * @param player CardGamePlayer to add to the game
	 */
	public void addPlayer(CrazyEightsPlayer player) {
		if (!isGameActive()) {
			players.add(player);
			hands.put(player, new ArrayList());
		} else {
			throw new IllegalStateException("Cannot add players to a game in progress.");
		}
	}

	/**
	 * Removes a player from the game.
	 *
	 * Throws an IllegalStateException when
	 * the game is already being played.
	 *
	 * @param player CardGamePlayer to remove from the game
	 */
	public void removePlayer(CrazyEightsPlayer player) {
		if (!isGameActive()) {
			players.remove(player);
			hands.remove(player).clear();
		} else {
			throw new IllegalStateException("Cannot remove players from a game in progress.");
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
		state = GameState.PLAYING;
		buildDeck();
		Collections.shuffle(deck);
		for (CrazyEightsPlayer player : players) {
			hands.get(player).clear();
			currentPlayer = player;
			drawCards(INITIAL_HAND_SIZE);
		}
		discard(deck.remove(0));
		currentPlayer = players.get(0);
		playRound();
	}

	/**
	 * Plays a single round of the game by asking a player to choose an action.
	 */
	private void playRound() {
		if (isGameActive()) {
			List<Card> hand = hands.get(currentPlayer);
			List<Card> cards = new ArrayList();
			cards.addAll(hand);
				// By handing the player a copy of their hand,
				// players are unable to change the cards in
				// their actual hand
			state = GameState.WAITING_FOR_PLAYER_CARD;
			currentPlayer.takeTurn(cards, this);
		}
	}

	/**
	 * Returns the number of cards in the hands of players,
	 * starting with the given player and continuing in order
	 * of play.
	 * @param player Player to use as a reference
	 * @return array of ints
	 */
	public int[] getHandSizes(CrazyEightsPlayer player) {
		if (isGameActive()) {
			int[] handSizes = new int[players.size()];
			int currentPlayerID = players.indexOf(player);
			for (int i = 0; i < players.size(); i++) {
				handSizes[i] = hands.get(players.get((currentPlayerID + i) % players.size())).size();
			}
			return handSizes;
		}
		return null;
	}

	/**
	 * Returns the cards in the hand of the given player
	 * @param player Player to return the hand of
	 * @return list of cards on the player's hand
	 */
	public List<Card> getHand(CrazyEightsPlayer player) {
		List<Card> hand = hands.get(player);
		List<Card> cards = new ArrayList();
		cards.addAll(hand);
		return cards;
	}

	/**
	 * Notifies the game that the given player has decided to draw a card.
	 * @param player Player to play the card
	 * @return true iff a card was successfully drawn
	 */
	public boolean drawCard(CrazyEightsPlayer player) {
		return playCard(player, null);
	}

	/**
	 * Notifies the game that the given player has decided to play the given card,
	 * or draw a card when the given card is null.
	 *
	 * @param player Player to play the card
	 * @param card Card to be played
	 * @return true iff the card was played successfully
	 */
	public boolean playCard(CrazyEightsPlayer player, Card card) {
		if (state == GameState.WAITING_FOR_PLAYER_CARD && player == currentPlayer) {
			state = GameState.PLAYING;
			List<Card> hand = hands.get(currentPlayer);
			if (card == null) {
				drawCard();
				moveToNextPlayer();
			} else if (hand.contains(card) && isPlayable(card)) {
				hand.remove(card);
				discard(card);
				notifyListenersOfHandSize();
				card.execute();
			} else {
				state = GameState.WAITING_FOR_PLAYER_CARD;
				return false;
			}
			if (!checkEndGame()) {
				playRound();
			}
		}
		return false;
	}

	/**
	 * Adds an object to be notified of changes in hand sizes, discard pile, and game end
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a previously added listener
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	private void notifyListenersOfTopCard() {
		PropertyChangeEvent event = new PropertyChangeEvent(this, "topCardOnDiscardPile", null, getTopCardOnDiscardPile());
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	private void notifyListenersOfHandSize() {
		PropertyChangeEvent event = new PropertyChangeEvent(this, "handSize", null, null);
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	private void notifyListenersOfGameEnd() {
		PropertyChangeEvent event = new PropertyChangeEvent(this, "gameComplete", false, true);
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	/**
	 * Determines whether the game has ended because some player
	 * no longer has any cards
	 * @return true iff the game has ended
	 */
	private boolean checkEndGame() {
		for (int i = 0; i < players.size(); i++) {
			List<Card> hand = hands.get(players.get(i));
			if (hand.size() < 1) {
				state = GameState.INACTIVE;
				notifyListenersOfGameEnd();
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves the current card on top of the discard pile.
	 * Since Cards are immutable, players are unable to change
	 * the top card of the discard pile
	 * @return Card on top of the discard pile
	 */
	public Card getTopCardOnDiscardPile() {
		return new Card(topCardOnDiscardPile);
	}

	/**
	 * Determines whether a card is playable in the current situation.
	 * @param card Card held by the current player
	 * @return true iff the current player can play the given Card
	 */
	public boolean isPlayable(Card card) {
		return card.isPlayableOn(topCardOnDiscardPile);
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
		game.start();
	}

}
