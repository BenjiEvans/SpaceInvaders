package entity;

public abstract class MovableEntity extends Entity{
	int radius;
	double speed, dx, dy;
	boolean isVisible = true;

	public int getRadius() {
		return radius;
	}

	public double getdx() {
		return dx;
	}

	public double getdy() {
		return dy;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void reflect(){
		dx = -dx;
		dy = -dy;
	}
	
	public abstract void hit();
	public abstract void hit(int angle);
}
