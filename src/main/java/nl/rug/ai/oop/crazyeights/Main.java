package nl.rug.ai.oop.crazyeights;

import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		JFrame gameFrame = new JFrame("Crazy Eights");
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(800, 600);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setVisible(true);
	}
}
