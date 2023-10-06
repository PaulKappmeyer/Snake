package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

public class Snake {

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

		private Bodypart(int x, int y) {
			this.currentX = x;
			this.currentY = y;
			drawX = x * bodysize;
			drawY = y * bodysize;
		}

		public void draw(Graphics graphics) {
			graphics.setColor(Color.WHITE);
			graphics.fillRect((int) drawX, (int) drawY, bodysize, bodysize);
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.drawRect((int) drawX, (int) drawY, bodysize, bodysize);
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
		head = new Bodypart(1, 1);
		direction = Direction.WEST;
		head.targetX = head.currentX + 1;
		head.targetY = head.currentY;
		body.add(head);
	}

	public void draw(Graphics graphics) {
		// draw body
		body.forEach(part -> part.draw(graphics));

		// draw head
		graphics.setColor(Color.GREEN);
		graphics.fillOval((int) head.drawX, (int) head.drawY, bodysize, bodysize);
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
			Bodypart last = body.peekLast();
			Bodypart newPart = new Bodypart(last.currentX, last.currentY);
			newPart.targetX = last.currentX;
			newPart.targetY = last.currentY;
			body.add(newPart);

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
