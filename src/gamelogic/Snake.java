package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Snake {

	private static final Random random = new Random();
	
	private int bodysize;
	private LinkedList<Bodypart> body;
	private Bodypart head;
	private Direction direction;

	// time since last move
	private double tslm;
	private final double MOVE_TIME = 0.125;

	public class Bodypart {
		private int currentX;
		private int currentY;
		private int targetX;
		private int targetY;
		private double drawX;
		private double drawY;
		private Color color;

		private Bodypart(int currentX, int currentY, Color color) {
			this.currentX = currentX;
			this.currentY = currentY;
			this.drawX = currentX * bodysize;
			this.drawY = currentY * bodysize;
			this.color = color;
		}
		
		private Bodypart(Bodypart inFront) {
			this(inFront.currentX, inFront.currentY, modifyColor(inFront.color));
			this.targetX = currentX;
			this.targetY = currentY;
		}

		private static Color modifyColor(Color color) {
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			return new Color(clamp(r + random.nextInt(-25, 25), 0, 255), clamp(g + random.nextInt(-25, 25), 0, 255), clamp(b + random.nextInt(-25, 25), 0, 255));
		}
		
		private static int clamp(int val, int min, int max) {
		    return Math.max(min, Math.min(max, val));
		}
		
		public void draw(Graphics graphics) {
			// draw body
			graphics.setColor(color);
			graphics.fillOval((int) drawX, (int) drawY, bodysize, bodysize);
			// draw outline
			graphics.setColor(Color.BLACK);
			graphics.drawOval((int) drawX, (int) drawY, bodysize, bodysize);
		}

		public int getX() {
			return currentX;
		}

		public int getY() {
			return currentY;
		}
	}

	public enum Direction {
		NORTH,
		SOUTH,
		WEST,
		EAST;

		public boolean isOpposite(Direction dir) {
			switch(this) {
			case NORTH:
				return dir == SOUTH;

			case SOUTH:
				return dir == NORTH;

			case WEST:
				return dir == EAST;

			case EAST:
				return dir == WEST;
			}
			return false;
		}
	}

	public Snake(int bodysize) {
		this.bodysize = bodysize;
		body = new LinkedList<>();
		head = new Bodypart(1, 1, new Color(25, 255, 80));
		direction = Direction.WEST;
		head.targetX = head.currentX + 1;
		head.targetY = head.currentY;
		body.add(head);
		int initalLength = 2;
		for (int i = 0; i < initalLength; i++) {
			body.add(new Bodypart(body.peekLast()));
		}
	}

	public void draw(Graphics graphics) {
		// left border loop
		graphics.translate(-Main.SCREEN_WIDTH, 0);
		drawSnake(graphics);
		graphics.translate(Main.SCREEN_WIDTH, 0);
		
		// right border loop
		graphics.translate(Main.SCREEN_WIDTH, 0);
		drawSnake(graphics);
		graphics.translate(-Main.SCREEN_WIDTH, 0);

		// top border loop
		graphics.translate(0, -Main.SCREEN_HEIGHT);
		drawSnake(graphics);
		graphics.translate(0, Main.SCREEN_HEIGHT);
		
		// top border looping
		graphics.translate(0, Main.SCREEN_HEIGHT);
		drawSnake(graphics);
		graphics.translate(0, -Main.SCREEN_HEIGHT);
		
		// normal drawing in vies
		drawSnake(graphics);
	}
	
	private void drawSnake(Graphics graphics) {
		// draw body
		Iterator<Bodypart> it = body.descendingIterator();
		it.forEachRemaining(part -> part.draw(graphics));
		
		// draw head: eyes
		graphics.setColor(new Color(255, 255, 255, 200));
		graphics.fillOval((int) (head.drawX +  5./24 * bodysize), (int) head.drawY + bodysize/4, bodysize/4, bodysize/4);
		graphics.fillOval((int) (head.drawX + 13./24 * bodysize), (int) head.drawY + bodysize/4, bodysize/4, bodysize/4);
	}

	public void update(double tslf) {
		// update body: draw position
		for (Bodypart part : body) {
			part.drawX = (part.currentX + tslm/MOVE_TIME * (part.targetX - part.currentX)) * bodysize;
			part.drawY = (part.currentY + tslm/MOVE_TIME * (part.targetY - part.currentY)) * bodysize;
		}

		tslm += tslf;
		if (tslm < MOVE_TIME) {
			return;
		}
		tslm -= MOVE_TIME;

		// update tail: grid position
		Iterator<Bodypart> it = body.descendingIterator();
		Bodypart current = it.hasNext() ? it.next() : null;
		while(current != null && it.hasNext()){ 
			Bodypart next = it.next();
			current.currentX = next.currentX;
			current.currentY = next.currentY;
			current.targetX = next.targetX;
			current.targetY = next.targetY;

			current = next;
		} 

		// update head: grid position
		head.currentX = head.targetX;
		head.currentY = head.targetY;

		// check collision
		int collision_index;
		int size = body.size();
		for (collision_index = 1; collision_index < size; collision_index++) {
			Bodypart part = body.get(collision_index);
			if (part == head) {
				continue;
			}

			if (part.currentX == head.currentX && part.currentY == head.currentY) {
				break;
			}
		}
		for (int i = collision_index; i < size; i++) {
			body.removeLast();
		}

		// check food
		if (head.currentX == Main.food.getX() && head.currentY == Main.food.getY()) {
			body.add(new Bodypart(body.peekLast()));

			Main.food.randomLocation(body);
		}

		// move
		switch(direction) {
		case NORTH:
			head.targetY--;

			if (head.targetY < 0) {
				head.currentY += Main.NUM_ROWS;
				head.targetY += Main.NUM_ROWS;
			}
			break;

		case SOUTH:
			head.targetY++;

			if (head.targetY >= Main.NUM_ROWS) {
				head.currentY -= Main.NUM_ROWS;
				head.targetY -= Main.NUM_ROWS;
			}
			break;

		case WEST:
			head.targetX++;

			if (head.targetX >= Main.NUM_COLS) {
				head.currentX -= Main.NUM_COLS;
				head.targetX -= Main.NUM_COLS;
			}
			break;

		case EAST:
			head.targetX--;

			if (head.targetX < 0) {
				head.currentX += Main.NUM_COLS;
				head.targetX += Main.NUM_COLS;
			}
			break;
		}
	}

	public void move(Direction desiredDirection) {
		if (direction.isOpposite(desiredDirection)) {
			return;
		}
		direction = desiredDirection;
	}

	public int getLength() {
		return body.size();
	}

	public LinkedList<Bodypart> getBody(){
		return body;
	}
}
