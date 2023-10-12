package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gameengine.GameBase;
import gamelogic.Snake.Direction;

public class Main extends GameBase implements KeyListener {

	// size of the window (in pixel)
	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 900;

	public static final int BODYSIZE = 50;
	
	public static final int NUM_COLS = SCREEN_WIDTH/BODYSIZE;
	public static final int NUM_ROWS = SCREEN_HEIGHT/BODYSIZE;

	
	public static Snake snake;
	public static final int NUM_FOOD = 5;
	public static Food[] foods;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.start("Snake", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void init() {
		snake = new Snake(BODYSIZE);
		foods = new Food[NUM_FOOD];
		for (int i = 0; i < NUM_FOOD; i++) {
			foods[i] = new Food(BODYSIZE);
			foods[i].randomLocation();
		}
		
		// Adding inputManagers to window
		window.addKeyListener(this);
	}

	@Override
	public void update(double tslf) {
		snake.update(tslf);
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		for (Food f : foods) {
			f.draw(graphics);
		}
		
		snake.draw(graphics);
		
		graphics.setColor(Color.BLACK);
		graphics.drawString("Länge: " + snake.getLength(), 10, 10);
		graphics.drawString("Highscore: " + snake.getHighscore(), 100, 10);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			snake.move(Direction.NORTH);
			break;

		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			snake.move(Direction.SOUTH);
			break;

		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			snake.move(Direction.WEST);
			break;

		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			snake.move(Direction.EAST);
			break;
			
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
