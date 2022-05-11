package nl.rug.ai.oop.crazyeights;

import nl.rug.ai.oop.crazyeights.model.CrazyEights;
import nl.rug.ai.oop.crazyeights.model.RandomCrazyEightsPlayer;
import nl.rug.ai.oop.crazyeights.view.CrazyEightsView;
import nl.rug.ai.oop.crazyeights.view.GamePane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
	public static int CARDS_PER_SUIT = 13;

	/**
	 * Preloads images
	 */
	private static void loadImages() {
		try {
			for (String suit : new String[]{"C", "S", "D", "H"}) {
				for (int i = 2; i <= 10; i++) {
					GamePane.cardImages.put(suit + "" + i,
							ImageIO.read(GamePane.class.getResource("/" + suit + i + ".png")));
				}
				GamePane.cardImages.put(suit + "A",
						ImageIO.read(GamePane.class.getResource("/" + suit + "1.png")));
				GamePane.cardImages.put(suit + "J",
						ImageIO.read(GamePane.class.getResource("/" + suit + "11.png")));
				GamePane.cardImages.put(suit + "Q",
						ImageIO.read(GamePane.class.getResource("/" + suit + "12.png")));
				GamePane.cardImages.put(suit + "K",
						ImageIO.read(GamePane.class.getResource("/" + suit + "13.png")));
			}
			GamePane.cardImages.put("00", ImageIO.read(GamePane.class.getResource("/00.png")));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		loadImages();
		CrazyEights game = new CrazyEights();
		game.addPlayer(new RandomCrazyEightsPlayer());

		CrazyEightsView gameFrame = new CrazyEightsView();
		gameFrame.init();
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(800, 600);
		gameFrame.setup(game);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setVisible(true);
	}

}
