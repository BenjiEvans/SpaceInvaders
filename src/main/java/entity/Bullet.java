package entity;

import java.awt.*;

import main.GamePanel;

public class Bullet extends MovableEntity {
	// fields
	private int direction;
	//private double rad;
	
	Bullet(int angle, double x, double y) {
		this.x = x;
		this.y = y;
		radius = 2;
		speed = 15;
		double rad = Math.toRadians(angle);
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		direction = angle;
	}

	public void update() {
		if (finished)
			return;
		x += dx;
		y += dy;
		if (x < -radius || x > (GamePanel.WIDTH * GamePanel.SCALE) + radius || y < -radius
				|| y > (GamePanel.HEIGHT * GamePanel.SCALE) + radius) {
			finished = true;
		}
	}

	public void draw(Graphics2D g) {
		if (finished)
			return;
		g.setColor(Color.YELLOW);
		g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
	}

	public void setDirection(int dir) {
		direction = dir;
	}

	public int getDirection() {
		return direction;
	}

	@Override
	public void hit() {
		// TODO Auto-generated method stub
		finished = true; 
	}

	@Override
	public void hit(int angle) {
		// TODO Auto-generated method stub
		hit();
	}
}