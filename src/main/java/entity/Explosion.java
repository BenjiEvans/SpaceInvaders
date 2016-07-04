package entity;

import java.awt.*;

public class Explosion extends Entity {

	private int radius;// radius
	private double maxRadius;
	private int expFade = 128;// transparency

	public Explosion(double x, double y, int r, double max) {
		this.x = x;
		this.y = y;
		this.radius = r;
		maxRadius = max;
	}

	public void update() {
		if (finished)
			return;
		radius += 2;
		if (radius > maxRadius)
			finished = true;
		else {
			if (expFade < 0)
				expFade = 0;
			else
				expFade -= 16;
		}
	}

	public void draw(Graphics2D g) {
		if (finished)
			return;
		g.setColor(new Color(255, 255, 255, expFade));
		g.setStroke(new BasicStroke(3));
		g.drawOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
		g.setStroke(new BasicStroke(1));
	}
}