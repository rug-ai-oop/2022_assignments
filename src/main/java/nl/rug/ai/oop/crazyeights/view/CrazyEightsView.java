package nl.rug.ai.oop.crazyeights.view;

import nl.rug.ai.oop.crazyeights.controller.CrazyEightsController;
import nl.rug.ai.oop.crazyeights.model.Card;
import nl.rug.ai.oop.crazyeights.model.CrazyEights;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Main frame of the Crazy Eights game
 */
public class CrazyEightsView extends JFrame {
	GamePane selfPane, otherPane, drawPane;
	CrazyEightsController controller;
	JMenu restartOption;

	/**
	 * Creates a new Crazy Eights window
	 */
	public CrazyEightsView() {
		super("Crazy Eights");
		init();
	}

	/**
	 * The play area consists of three GamePanes
	 * 1) The opponent's hand
	 *    no effect on click, not highlighted when selected, card backs only
	 * 2) The deck and discard pile
	 * 3) The player's hand
	 */
	private void init() {
		selfPane = new GamePane();
		selfPane.setActionCommand("playCard");
		add(selfPane, BorderLayout.SOUTH);
		otherPane = new GamePane();
		otherPane.setSelectionDelta(0);
		add(otherPane, BorderLayout.NORTH);
		drawPane = new GamePane();
		drawPane.setActionCommand("drawCard");
		drawPane.setSelectionDelta(0);
		add(drawPane, BorderLayout.CENTER);
		JMenuBar menuBar = new JMenuBar();
		restartOption = new JMenu("Restart");
		setJMenuBar(menuBar);
		menuBar.add(restartOption);
	}

	/**
	 * Collapses a List of Cards into an array of String
	 * @param cards List of Cards to collapse
	 * @return collapsed array of String
	 */
	private static String[] collapseHand(List<Card> cards) {
		String output[] = new String[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			output[i] = cards.get(i).toString();
		}
		return output;
	}

	/**
	 * Creates an array of "00" Strings of the given size
	 * @param size number of "00" entries
	 * @return
	 */
	private static String[] getEmptyHand(int size) {
		String output[] = new String[size];
		for (int i = 0; i < size; i++) {
			output[i] = "00";
		}
		return output;
	}

	/**
	 * Sets up the view to show the given game,
	 * creates a Controller to control the given game, and
	 * starts the game
	 * @param game
	 */
	public void setup(CrazyEights game) {
		createController(game);
		setupListener(game);
	}

	private void setupListener(CrazyEights game) {
		game.addPropertyChangeListener(evt -> {
			switch (evt.getPropertyName()) {
				case "gameComplete":
					if (game.getHand(controller).size() == 0) {
						JOptionPane.showMessageDialog(this,
								"Congratulations, you win the game!",
								"Congratulations",
								JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this,
								"Unfortunately, you have lost the game.",
								"Sorry",
								JOptionPane.PLAIN_MESSAGE);
					}
					break;
				case "handSize":
					List<Card> hand = game.getHand(controller);
					selfPane.setHand(collapseHand(hand));
					int[] sizes = game.getHandSizes(controller);
					otherPane.setHand(getEmptyHand(sizes[1]));
					break;
				case "topCardOnDiscardPile":
					drawPane.setHand(new String[]{"00", evt.getNewValue().toString()});
					break;
			}
		});
	}

	private void createController(CrazyEights game) {
		controller = new CrazyEightsController(game, this);
		restartOption.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				game.start();
			}
		});
		drawPane.addActionListener(controller);
		selfPane.addActionListener(controller);
		game.addPlayer(controller);
	}
}
