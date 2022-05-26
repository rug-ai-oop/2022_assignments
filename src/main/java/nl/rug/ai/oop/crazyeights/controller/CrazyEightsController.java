package nl.rug.ai.oop.crazyeights.controller;

import nl.rug.ai.oop.crazyeights.model.Card;
import nl.rug.ai.oop.crazyeights.model.CrazyEights;
import nl.rug.ai.oop.crazyeights.model.CrazyEightsPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controller for the Crazy Eights game.
 * Interprets player action to be used in the game, and handles changing suit.
 *
 * The Controller is the element that implements the CrazyEightsPlayer interface.
 * The View should not control the game directly, and therefore has no need to
 * implement this interface.
 */
public class CrazyEightsController implements CrazyEightsPlayer, ActionListener {
	private CrazyEights game;
	private JFrame view;

	/**
	 * Creates a new controller to go with a specific view and model
	 * @param game CrazyEights game model
	 * @param view CrazyEights view
	 */
	public CrazyEightsController(CrazyEights game, JFrame view) {
		this.game = game;
		this.view = view;
	}

	@Override
	public void takeTurn(List<Card> hand, CrazyEights game) {}

	/**
	 * Opens a dialog to get the player to choose a suit
	 * @param game CardGame being played
	 */
	@Override
	public void chooseSuit(CrazyEights game) {
		String[] options = {"Clubs","Diamonds","Hearts","Spades"};
		int n = JOptionPane.showOptionDialog(view,
				"What would you like the suit to change to?",
				"You playes a crazy eight!",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
		switch (n) {
			case 0:
				game.selectSuit(this, Card.Suit.CLUBS);
				break;
			case 1:
				game.selectSuit(this, Card.Suit.DIAMONDS);
				break;
			case 2:
				game.selectSuit(this, Card.Suit.HEARTS);
				break;
			case 3:
				game.selectSuit(this, Card.Suit.SPADES);
				break;
		}
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "drawCard":
				game.playCard(this, null);
				break;
			case "playCard":
				if (e.getID() >= 0) {
					Card card = game.getHand(this).get(e.getID());
					if (game.isPlayable(card)) {
						game.playCard(this, card);
					}
				}
				break;
		}
	}
}
