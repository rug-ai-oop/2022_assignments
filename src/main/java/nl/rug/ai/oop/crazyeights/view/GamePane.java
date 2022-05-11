package nl.rug.ai.oop.crazyeights.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * GamePane is a visual class meant to show cards on a single row.
 * Spacing between cards is automatically adjusted for the number
 * of cards and the available width, and the height of cards is
 * scaled to the available height.
 *
 * When the mouse moves over a card, the corresponding card is
 * shown higher than the other cards.
 */
public class GamePane extends JPanel {
	public static final Map<String, BufferedImage> cardImages = new HashMap(4);
	public static final int DEFAULT_SELECTION_DELTA = -40;
	public static final String DEFAULT_ACTION_COMMAND = "CardSelected";

	private String[] hand = {};
	private int selectedCard = -1;
	private Dimension cardSize = new Dimension(120, 150);
	private int[] xCardPosition = {};
	private int yCardPosition = 0;
	private String actionCommand = DEFAULT_ACTION_COMMAND;
	private int selectionDelta = DEFAULT_SELECTION_DELTA;
	private List<ActionListener> listeners = new ArrayList();


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, (int)cardSize.getHeight()+Math.abs(selectionDelta));
	}

	/**
	 * Sets up a new panel and mouse interactions.
	 */
	public GamePane() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectedCard = getSelectedCard(e.getX());
				if (selectedCard >= 0) {
					notifyListeners();
					revalidate();
					repaint();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				selectedCard = getSelectedCard(e.getX());
				repaint();
			}
		});
	}

	/**
	 * Sets the action command sent along with every call of actionPerformed
	 * @param actionCommand String that identifies the action command
	 */
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	private void notifyListeners() {
		ActionEvent event = new ActionEvent(this, selectedCard, actionCommand);
		for (ActionListener listener : listeners) {
			listener.actionPerformed(event);
		}
	}

	/**
	 * Adds a listener for clicked cards
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a previously added listener
	 * @param listener
	 */
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	private int getSelectedCard(int x) {
		for (int i = hand.length - 1; i >= 0; i--) {
			if (x > xCardPosition[i] && x < xCardPosition[i] + cardSize.getWidth()) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		yCardPosition = (int)(getHeight() - cardSize.getHeight() - Math.abs(selectionDelta))/2;
		selectedCard = -1;
		int xDelta = (int)Math.floor(Math.min(cardSize.getWidth() * 1.1, (getWidth() - cardSize.getWidth())/(hand.length - 1)));
		int n = hand.length;
		xCardPosition = new int[n];
		int xPos = (int) ((getWidth() - cardSize.getWidth() - (n - 1)*xDelta) / 2);
		for (int i = 0; i < n; i++) {
			xCardPosition[i] = xPos;
			xPos += xDelta;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		for (int i = 0; i < hand.length; i++) {
			g2d.drawImage(cardImages.get(hand[i]),
					xCardPosition[i], yCardPosition + (selectedCard == i ? Math.max(selectionDelta, 0) : Math.max(-selectionDelta, 0)),
					(int)cardSize.getWidth(), (int)cardSize.getHeight(), null);
		}
		g2d.dispose();
	}

	/**
	 * Sets the hand of cards and resets the view to match.
	 * @param hand hand of cards, where every card is a character "C", "S", "D", or "H"
	 *             followed by a number between 1 and 13, or 00 for a face-down card
	 */
	public void setHand(String[] hand) {
		this.hand = hand;
		revalidate();
		repaint();
	}

	/**
	 * Retrieves the vertical difference for selected cards
	 * @return
	 */
	public int getSelectionDelta() {
		return selectionDelta;
	}

	/**
	 * Sets the difference in y coordinate of selected and unselected cards
	 * @param selectionDelta
	 */
	public void setSelectionDelta(int selectionDelta) {
		this.selectionDelta = selectionDelta;
	}

}