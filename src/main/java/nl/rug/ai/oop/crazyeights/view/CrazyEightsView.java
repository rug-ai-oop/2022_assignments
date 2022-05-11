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

	public CrazyEightsView() {
		super("Crazy Eights");
	}

	public void init() {
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

	private String[] collapseHand(List<Card> cards) {
		String output[] = new String[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			output[i] = cards.get(i).toString();
		}
		return output;
	}

	private String[] getEmptyHand(int size) {
		String output[] = new String[size];
		for (int i = 0; i < size; i++) {
			output[i] = "00";
		}
		return output;
	}

	public void setup(CrazyEights game) {
		controller = new CrazyEightsController(game, this);
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
					System.out.println(hand);
					selfPane.setHand(collapseHand(hand));
					int[] n = game.getHandSizes(controller);
					System.out.println(Arrays.toString(n));
					otherPane.setHand(getEmptyHand(n[1]));
					break;
				case "topCardOnDiscardPile":
					drawPane.setHand(new String[]{"00", evt.getNewValue().toString()});
					break;
			}
		});
		restartOption.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				game.start();
			}
		});
		drawPane.addActionListener(controller);
		selfPane.addActionListener(controller);
		game.addPlayer(controller);
		game.start();
		drawPane.setHand(new String[]{"00", game.getTopCardOnDiscardPile().toString()});
	}
}
