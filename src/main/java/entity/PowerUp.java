package entity;

import java.awt.*;

import main.GamePanel;

public class PowerUp extends MovableEntity {
	private PowerType type;
	private Color color;
	private String message;

	public PowerUp(PowerType type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
		switch (type) {
		case HEALTH:
			color = Color.PINK;
			radius = 3;
			message = "+1 Life";
			break;
		case POWER:
			color = Color.YELLOW;
			radius = 3;
			message = "+1 Power";
			break;
		case POWER2:
			color = Color.YELLOW;
			radius = 5;
			message = "+2 Power";
			break;
		case SLOW_TIME:
			color = Color.WHITE;
			radius = 3;
			message = "Slow";
			break;
		}
	}

	public void update() {
		if (finished)
			return;
		if (slow/*GamePanel.slowDownTimer != 0*/)
			y += 2 * .3;
		else
			y += 2;
		if (y > GamePanel.HEIGHT * GamePanel.SCALE)
			finished = true;
	}

	public void draw(Graphics2D g) {
		if (finished)
			return;
		g.setColor(color);
		g.fillRect((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);

		g.setStroke(new BasicStroke(3));
		g.setColor(color.darker());
		g.drawRect((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
		g.setStroke(new BasicStroke(1));

	}

	public PowerType getType() {
		return type;
	}

	@Override
	public void hit() {		
		GamePanel.entities.get("Text").add(new Text(x,y, message, 2000));
		finished = true;
	}

	@Override
	public void hit(int angle) {
		hit();
		
	}
}