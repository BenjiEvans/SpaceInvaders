package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import main.GamePanel;
public class ChaseEnemy extends Enemy {
	
	//public static int type = 2;
	//private static Color color = new Color(255, 0, 0, 128);
	//private double maxChaseSpeed;
	//private boolean chase = false;
	protected double targetLocation;
	protected boolean targetReached;
	protected boolean chase = false;
	private char chaseAxis;	
	
	ChaseEnemy(int rank, double xlocation, double ylocation){
		this(rank);
		x = xlocation;
		y = ylocation;
	}
	
	public ChaseEnemy(int rank) {
		super(2,rank);
		color1 = new Color(255, 0, 0, 128);
		//color1 = 
		if (rank == 1) {
			speed = 3;
			maxChaseSpeed = 5.5;
			radius = 5;
			health = 4;
		}
		if (rank == 2) {
			speed = 2;
			maxChaseSpeed = 5;
			radius = 10;
			health = 2;
		}
		if (rank == 3) {
			speed = 2;
			maxChaseSpeed = 3.5;
			radius = 20;
			health = 2;
		}
		
		x = Math.random() * GamePanel.WIDTH * GamePanel.SCALE / 2
				+ GamePanel.WIDTH * GamePanel.SCALE / 4;
		y = -radius;

		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);

		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;

		ready = false;
		hit = false;
		hitTimer = 0;
	}
	
	public void update(){
		if(slow && !chase){
			x += dx * .3;
			y += dy * .3;
			
			
		}
		if(!chase && !slow)
		{
			
			//updates position 
			x+= dx;
			y+= dy;
		}
		if(GamePanel.player.isDead() || !GamePanel.player.isVisible())chase = false;
		int xlocation = (int) GamePanel.player.x;
		int ylocation = (int) GamePanel.player.y;		
		
		if(!GamePanel.player.isDead()){
			if((!chase && !chaseVehicle && GamePanel.player.isVisible()))
			{
				int xpos = (int) this.getx();
				int ypos = (int) this.gety();
				
				double dist = getDistanceFromPlayer(xlocation, ylocation);
				double distv = getClosestVehicle();
				targetVehicle = findTargetVehicle(distv);
				if(distv < 150 && !GamePanel.player.isDriving() && validTarget(targetVehicle) && !((Vehicle)GamePanel.entities.get("Vehicle").get(targetVehicle)).isFree() /*&& type != 3*/){
					setChaseVehicle(true);
				}
				if(dist < 150 && !GamePanel.player.isDriving())setChase(true);
				
				
				
				if( ypos >= ylocation-5 && ypos <= ylocation+5 && !chaseVehicle /*&& type != 3*/)
				{
					
					double chaseSpeed = getChaseSpeed(xlocation,ylocation,xpos,ypos);
					
					if(chaseSpeed == 0 || GamePanel.player.isDriving())setChase(false);
					else 
					{
						setChase(true, 'x');
						
						if (xlocation > xpos)
						{
							if (slow)x+= chaseSpeed*.3;
							else x+= chaseSpeed;
						}
						
						if (xlocation < xpos)
						{
							if(slow)x-= chaseSpeed*.3;
							else x-= chaseSpeed;
						}
						
					}
					
					
				}
				
				if( xpos >= xlocation-5 && xpos <= xlocation+5 && !chaseVehicle /*&& type != 3*/)
				{
					double chaseSpeed = getChaseSpeed(xlocation,ylocation,xpos,ypos);
					if(chaseSpeed == 0 || GamePanel.player.isDriving())setChase(false);
					else
					{
						setChase(true, 'y');
					
						
						if (ylocation > ypos)
						{
							if(slow)y+= chaseSpeed*.3;
							else y+= chaseSpeed;
						}
						if (ylocation < ypos)
						{
							if(slow)y-= chaseSpeed;
							else y-= chaseSpeed;
						}
					}
					
				
				}	
			}
			
			//Chasing Algorithim
			if(chase && type == 2)
			{
				
				
				int xpos = (int) this.getx();
				int ypos = (int) this.gety();
				
				
				if(inSight(xpos, ypos, xlocation, ylocation, chaseAxis))
				{
					if (chaseAxis == 'x')
					{
						//double chaseSpeed = getChaseSpeed(xlocation,ylocation);
						
						double chaseSpeed = getChaseSpeed(xlocation,ylocation,xpos,ypos);
						
						if(chaseSpeed == 0)
						{
							setChase(false);
							
							double angle = Math.random()*140 + 20;
							rad = Math.toRadians(angle);
							
							if(slow){
								dx = Math.cos(rad)*speed*.3;
								dy = Math.sin(rad)*speed*.3;
							}
							else {
								dx = Math.cos(rad)*speed;
								dy = Math.sin(rad)*speed;
							}
						}
						
						 if (xlocation > xpos)
						{
							if(slow)x+= chaseSpeed*.3;
							else x+= chaseSpeed;
						}
						
						 if (xlocation < xpos)
						{
							if(slow)x-= chaseSpeed*.3;
							else x-= chaseSpeed;
						}
						
					}
					else if(chaseAxis == 'y')
					{
						//double chaseSpeed = getChaseSpeed(xlocation, ylocation);
						
						double chaseSpeed = getChaseSpeed(xlocation,ylocation,xpos,ypos);
						
						if(chaseSpeed == 0)
						{
							setChase(false);
							double angle = Math.random()*140 + 20;
							rad = Math.toRadians(angle);
							
							if(slow){
								dx = Math.cos(rad)*speed*.3;
								dy = Math.sin(rad)*speed*.3;
							}
							else{
								dx = Math.cos(rad)*speed;
								dy = Math.sin(rad)*speed;
								
							}
						
							
						}
						
						
						if (ylocation > ypos)
						{
							if(slow) y+=chaseSpeed*.3;
							else y+= chaseSpeed;
						}
						
						if (ylocation < ypos)
						{
							if(slow) y-= chaseSpeed*.3;
							else y-= chaseSpeed;
						}
						
						
					}
					
					
					
				}
				else
				{
					if (chaseAxis == 'x')
					{
						setTargetLocation(xlocation);
						setReached(false);
						
						
					}
					
				if (chaseAxis == 'y')
					{
						setTargetLocation(ylocation);
						setReached(false);
						
						
					}
					
					
					
					if(!targetReached)
					{
						if(chaseAxis == 'x')
						{
							//double chaseSpeed = getChaseSpeed(xlocation,ylocation);
							
							double chaseSpeed = getChaseSpeed(xlocation,ylocation,xpos,ypos);
							
							if(chaseSpeed == 0)
							{
								setChase(false);
								
								double angle = Math.random()*140 + 20;
								rad = Math.toRadians(angle);
								
								if(slow){
									dx = Math.cos(rad)*speed*.3;
									dy = Math.sin(rad)*speed*.3;
								}
								else {
									dx = Math.cos(rad)*speed;
									dy = Math.sin(rad)*speed;
								}
								
								
							}
							
							if (targetLocation > xpos)
							{
								
								if(slow)x+= chaseSpeed*.3;
								else x+= chaseSpeed;
							}
							
							if (targetLocation < xpos)
							{
								if(slow)x-= chaseSpeed*.3;
								else x-= chaseSpeed;
							}
							xpos = (int) getx();
							
							if( xpos >= targetLocation-5 && xpos <= targetLocation+5 )
							{
								setReached(true);
								setChaseAxis('y');
								
							}
							
						}
						else if (chaseAxis == 'y')
						{
							//double chaseSpeed = getChaseSpeed(xlocation, ylocation);
							
							
							double chaseSpeed = getChaseSpeed(xlocation,ylocation,xpos,ypos);
							
							if(chaseSpeed == 0)
							{
								setChase(false);
								
								double angle = Math.random()*140 + 20;
								rad = Math.toRadians(angle);
								if(slow){
									dx = Math.cos(rad)*speed*.3;
									dy = Math.sin(rad)*speed*.3;
								}
								else 
								{
									dx = Math.cos(rad)*speed;
									dy = Math.sin(rad)*speed;
								}
								
							}
							 
							
							
							if (targetLocation > ypos)
							{
								if(slow)y+= chaseSpeed*.3; 
								else y+= chaseSpeed;
							}
							
							if (targetLocation < ypos)
							{
								if(slow) y-= chaseSpeed*.3;
								else y-= chaseSpeed;
							}
							
							ypos = (int) gety();
							
							if( ypos >= targetLocation-5 && ypos <= targetLocation+5 )
							{
								setReached(true);
								setChaseAxis('x');
								
							}
							
						}
					}
					
					
				}
				
			}		
			
			checkSelfDestruct();			
			checkBounds();
			
			if(hit)
			{
				long elapsed = (System.nanoTime()- hitTimer)/1000000;
				if(elapsed > 50)
				{
					hit = false;
					hitTimer = 0;
					
				}
				
			}
		}		
	}
	
	public void checkSelfDestruct() {

		double dist = getDistanceFromPlayer(GamePanel.player.getx(),
				GamePanel.player.gety());

		if (dist <= 100 && rank == 3 && type == 2
				&& !GamePanel.player.isDriving() && GamePanel.player.isVisible()
				&& !GamePanel.player.isDead()){
			explode();

		}else if (dist <= 50 && rank == 2 && type == 2
				&& !GamePanel.player.isDriving() && GamePanel.player.isVisible()
				&& !GamePanel.player.isDead()){
			explode();
		}else if (dist <= 5 && rank == 1 && type == 2
				&& !GamePanel.player.isDriving() && GamePanel.player.isVisible()
				&& !GamePanel.player.isDead()){
			explode();
		}

	}
	
	
	
	
	public void setChase(boolean chase, char input) {
		this.chase = chase;
		this.chaseAxis = input;
		if (this.chase)
			chaseVehicle = false;
	}

	public void setChase(boolean chase) {
		this.chase = chase;

		double random = Math.random();

		if (random >= .5)
			chaseAxis = 'x';
		else
			chaseAxis = 'y';

	}
	
	public int getChaseAxis() {
		return chaseAxis;
	}

	public void setChaseAxis(char axis) {
		chaseAxis = axis;
	}
	
	public void setChaseVehicle(boolean chase) {

		chaseVehicle = chase;
		if (chase)
			this.chase = false;

	}	
	protected void createLesserEnemy(){
		ChaseEnemy e = new ChaseEnemy(rank - 1,	x, y);
		e.x += dx;
		e.y += dy;
		GamePanel.entities.get("Enemy").add(e);
	}	
	
	public double getChaseSpeed(double px, double py, double xpos, double ypos) {

		double edx = px - xpos;
		double edy = py - ypos;
		double dist = Math.sqrt(edx * edx + edy * edy);
		if (dist >= 150)
			return 0;
		else
			return maxChaseSpeed;
	}
	
	public boolean inSight(double xpos, double ypos, double xlocation,
			double ylocation, char axis) {
		if (axis == 'x') {
			if (ypos >= ylocation - 5 && ypos <= ylocation + 5)
				return true;
			else
				return false;
		} else {
			if (xpos >= xlocation - 5 && xpos <= xlocation + 5)
				return true;
			else
				return false;
		}

	}
	
	public void setTargetLocation(double PlayerLocation) {
		targetLocation = PlayerLocation;
	}

	public void setReached(boolean reached) {
		targetReached = reached;
	}
	public double getDistanceFromPlayer(double px, double py) {

		double edx = px - getx();
		double edy = py - gety();
		double dist = Math.sqrt(edx * edx + edy * edy);

		return dist;

	}
	public double getChaseAngle(int px, int py) {

		double edx = px - getx();
		double edy = py - gety();
		double angle = Math.tanh(edy / edx);
		return angle;

	}
	public double getClosestVehicle() {
		double min = 0;

		for (int i = 0; i < GamePanel.entities.get("Vehicle").size(); i++) {

			double edx = GamePanel.entities.get("Vehicle").get(i).getx() - getx();
			double edy = GamePanel.entities.get("Vehicle").get(i).gety() - gety();

			double dist = Math.sqrt(edx * edx + edy * edy);
			if (i == 0)
				min = dist;
			if (dist < min)
				min = dist;

		}

		return min;

	}
	public int findTargetVehicle(double distance) {

		for (int i = 0; i < GamePanel.entities.get("Vehicle").size(); i++) {

			double edx = GamePanel.entities.get("Vehicle").get(i).getx() - getx();
			double edy = GamePanel.entities.get("Vehicle").get(i).gety() - gety();

			double dist = Math.sqrt(edx * edx + edy * edy);
			if (dist == distance)
				return i;

		}

		return -15;

	}
	public boolean validTarget(int number) {
		if (number >= 0)
			return true;
		else
			return false;
	}
	
}
