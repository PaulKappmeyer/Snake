package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

public class Snake {
	
	private int bodysize;
	private LinkedList<Bodypart> body;
	private Bodypart head;
	private Direction direction = Direction.WEST;

	// time since last move
	private double tslm;
	private final double MOVE_TIME = 0.125;
	
	public class Bodypart {
		private int x;
		private int y;

		private Bodypart(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void draw(Graphics graphics) {
			graphics.setColor(Color.WHITE);
			graphics.fillRect(x * bodysize, y * bodysize, bodysize, bodysize);
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.drawRect(x * bodysize, y * bodysize, bodysize, bodysize);
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
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
		body.add(head);
	}

	public void draw(Graphics graphics) {
		// draw body
		body.forEach(part -> part.draw(graphics));
		
		// draw head
		graphics.setColor(Color.GREEN);
		graphics.fillOval(head.x * bodysize, head.y * bodysize, bodysize, bodysize);
	}

	public void update(double tslf) {
		tslm += tslf;
		if (tslm < MOVE_TIME) {
			return;
		}
		tslm -= MOVE_TIME;
		
		// update tail
		Iterator<Bodypart> it = body.descendingIterator();
		Bodypart current = it.hasNext() ? it.next() : null;
		while(current != null && it.hasNext()){ 
			Bodypart next = it.next();
			current.x = next.x;
			current.y = next.y;
			
			current = next;
        } 
		
		// update position
		switch(direction) {
		case NORTH:
			head.y--;
			
			if (head.y < 0) {
				head.y += Main.NUM_ROWS;
			}
			break;

		case SOUTH:
			head.y++;
			
			if (head.y >= Main.NUM_ROWS) {
				head.y -= Main.NUM_ROWS;
			}
			break;
		
		case WEST:
			head.x++;
			
			if (head.x >= Main.NUM_COLS) {
				head.x -= Main.NUM_COLS;
			}
			break;
			
		case EAST:
			head.x--;
			
			if (head.x < 0) {
				head.x += Main.NUM_COLS;
			}
			break;
		}
		
		// check collision
		int collision_index;
		int size = body.size();
		for (collision_index = 1; collision_index < size; collision_index++) {
			Bodypart part = body.get(collision_index);
			if (part == head) {
				continue;
			}
			
			if (part.x == head.x && part.y == head.y) {
				break;
			}
		}
		for (int i = collision_index; i < size; i++) {
			body.removeLast();
		}
		
		// check food
		if (head.x == Main.food.getX() && head.y == Main.food.getY()) {
			Bodypart last = body.peekLast();
			body.add(new Bodypart(last.x, last.y));
			
			Main.food.randomLocation(body);
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
