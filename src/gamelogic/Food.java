package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Random;

import gamelogic.Snake.Bodypart;

public class Food {

	private static final Random random = new Random();

	private int x;
	private int y;
	private int size;

	public Food(int size) {
		this.size = size;
	}

	public void randomLocation(LinkedList<Bodypart> snake) {
		do {
			x = random.nextInt(0, Main.SCREEN_WIDTH/size);
			y = random.nextInt(0, Main.SCREEN_HEIGHT/size);

			for (Bodypart part : snake) {
				if (x == part.getX() && y == part.getY()) {
					continue;
				}
			}
			break;
		} while(false);
	}

	public void draw(Graphics graphics) {
		graphics.setColor(Color.RED);
		graphics.fillRect(x * size, y * size, size, size);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(x * size, y * size, size, size);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
