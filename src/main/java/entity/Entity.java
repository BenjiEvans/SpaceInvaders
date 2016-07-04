package entity;

import java.awt.Graphics2D;

public abstract class Entity {
	protected static boolean slow = false;
	protected double x, y;
	protected boolean finished = false;
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public boolean isFinished(){
		return finished;
	}
	public double getx(){ return x;}
	public double gety(){ return y;}
	protected void setx(double x){ this.x = x;}
	protected void sety(double y){ this.y = y;}
	public static boolean isSlowed(){
		return slow;
	}
	public static void setSlow(boolean b) {
		slow = b;		
	}
}
