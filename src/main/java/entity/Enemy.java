package entity;

import java.awt.*;

import main.GamePanel;

public class Enemy extends MovableEntity {

	protected double rad;
	protected double maxChaseSpeed;
	protected int health;
	protected int type;
	protected int rank;
	// for type 3 & 4 only
	private boolean ghost = false;
	private long ghostTimer;
	private int fade;
	// Chase variables s
	protected boolean chase = false;
	//private char chaseAxis;
	protected double targetLocation;
	protected boolean targetReached;
	protected boolean chaseVehicle = false;
	protected int targetVehicle;
	// type 1
	private boolean trapping = false;
	// enemy colors
	protected Color color1;// safe
	public static Color color2 = Color.RED; // damaged
	protected boolean hit;
	protected long hitTimer;
	protected boolean ready;

	/*
	 * 
	 * type 1 = normal(blue) type 2 = chaser(red) type 3 = phantom (uses chase);
	 */

	/*************************************************** Enemy Constructors below ************************************************/
	// initial enemy constructors (wave enemies)
	public Enemy(/*int type,*/ int rank) {
		this(1,rank);
	}
	protected Enemy(int type, int rank) {

		this.type = type;
		this.rank = rank;

		// default enemy

		if (type == 1) {
			// color1 = Color.BLUE;
			color1 = new Color(0, 0, 255, 128);

			if (rank == 1) {
				speed = 3.5;
				radius = 5;
				health = 1;
			}
			if (rank == 2) {
				speed = 3;
				radius = 10;
				health = 2;
			}
			if (rank == 3) {
				speed = 2;
				radius = 20;
				health = 3;
			}
			if (rank == 4) {
				speed = 1.5;
				radius = 40;
				health = 4;
			}
		}
		
		if (type == 3) {
			if (rank == 1) {
				speed = 2;
				maxChaseSpeed = 2;
				radius = 10;
				health = 40;
				fade = 255;
				//chase = true;
			}

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

	// Constructor used for exploded enemy chasers
	public Enemy(int type, int rank, double xlocation, double ylocation) {

		this.type = type;
		this.rank = rank;

		// Enemy atributes
		if (type == 1) {
			// color1 = Color.BLUE;
			color1 = new Color(0, 0, 255, 128);

			if (rank == 1) {
				speed = 3.5;
				radius = 5;
				health = 1;
			}
			if (rank == 2) {
				speed = 3;
				radius = 10;
				health = 2;
			}
			if (rank == 3) {
				speed = 2;
				radius = 20;
				health = 3;
			}
			if (rank == 4) {
				speed = 1.5;
				radius = 40;
				health = 4;
			}
		}
		if (type == 2) {
			color1 = new Color(255, 0, 0, 128);
			if (rank == 1) {
				speed = 4;
				maxChaseSpeed = 5.5;
				radius = 5;
				health = 4;
			}
			if (rank == 2) {
				speed = 3;
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

		}
		if (type == 3) {

			// color1 = new Color(200,191,231,fade);
			if (rank == 1) {
				speed = 2;
				maxChaseSpeed = 2;
				radius = 5;
				health = 40;
				fade = 255;
				//chase = true;
			}

		}

		x = xlocation;
		y = ylocation;

		// movement

		double angle = Math.random() * 360 + 20;
		rad = Math.toRadians(angle);
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;

		ready = false;
		hit = false;
		hitTimer = 0;
	}

	/************************************************************* Functions are bellow ************************************************************/

	/*-------------------------------------- Getters and Setters -------------------------------*/

	// getters
	/*public int getType() {
		return type;
	}*/

	public int getRank() {
		return rank;
	}

	public boolean getGhost() {
		return ghost;
	}

	/*public boolean getChase() {
		return chase;
	}*/

	public int getFade() {
		return fade;
	}

	// this method is used to get the actual test speed
	public double getChaseSpeed(double px, double py, double xpos, double ypos) {

		double edx = px - xpos;
		double edy = py - ypos;
		double dist = Math.sqrt(edx * edx + edy * edy);
		if (dist >= 150)
			return 0;
		else
			return maxChaseSpeed;
	}

	// TODO what the heck is this?
	public void grow(int num) {
		radius += num;
		health += num;
	}

	/*public void checkSelfDestruct() {

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

	}*/

	// this method is used to see if the poublelayer is within the enmeies chase
	// radius
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

	public void setRad(double newRad) {
		rad = newRad;
	}

	/*public void setChaseVehicle(boolean chase) {

		chaseVehicle = chase;
		if (chase)
			this.chase = false;

	}*/

	/*public void setChase(boolean chase, char input) {
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

	}*/

	/*----------- Other Functions ----------------------------------------*/
	// Chase logic functions ..........................................
	/*public int getChaseAxis() {
		return chaseAxis;
	}

	public void setChaseAxis(char axis) {
		chaseAxis = axis;
	}*/

	public void setTargetLocation(double PlayerLocation) {
		targetLocation = PlayerLocation;
	}

	public void setReached(boolean reached) {
		targetReached = reached;
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

	public boolean isTrapping() {
		return trapping;
	}

	public void isTrapping(boolean b) {
		trapping = b;
	}
	
	public void hit(/*int playerLevel,*/ int dir) {
		//health--;
		if (/*playerLevel < 2 &&*/ type != 3) {
			if (dir == 0)
				x += 10;
			else if (dir == 90)
				y += 10;
			else if (dir == 180)
				x -= 10;
			else if (dir == 270)
				y -= 10;
		} else if (/*playerLevel < 4 &&*/ type != 3) {
			if (dir == 0)
				x += 15;
			else if (dir == 90)
				y += 15;
			else if (dir == 180)
				x -= 15;
			else if (dir == 270)
				y -= 15;
		} else if (/*playerLevel >= 4 &&*/ type != 3) {

			if (dir == 0)
				x += 20;
			else if (dir == 90)
				y += 20;
			else if (dir == 180)
				x -= 20;
			else if (dir == 270)
				y -= 20;

		}
		hit();
	}
	
	protected void explode(){
		finished = true;
		double randomDrop = Math.random();
		
		if (randomDrop < .001) {
			GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.HEALTH, x, y));
		} else if (randomDrop < .02)
			GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.POWER2, x, y));
		else if (randomDrop < .1)
			GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.POWER, x, y));
		
		//this.explode(this);
		GamePanel.entities.get("Explosion").add(new Explosion(x, y, radius, radius+15));

		GamePanel.player.addScore(type+ rank);
		if(isTrapping())GamePanel.player.isTrapped(false);
		
		if (rank > 1) {

				for (int i = 0; i < 2; i++) {
					createLesserEnemy();;
				}

		}
		
		
	}
	
	protected void createLesserEnemy(){
		Enemy e = new Enemy(type, rank - 1,	x, y);
		e.x += dx;
		e.y += dy;
		GamePanel.entities.get("Enemy").add(e);
	}

	public void hit() {
		if(getGhost())return;
		GamePanel.enemyHits++;
		health--;
		if (health <= 0){
			explode();
			finished = true;
			double randomDrop = Math.random();
			
			if (randomDrop < .001) {
				GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.HEALTH, x, y));
			} else if (randomDrop < .02)
				GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.POWER2, x, y));
			else if (randomDrop < .1)
				GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.POWER, x, y));
			
			//this.explode(this);
			GamePanel.entities.get("Explosion").add(new Explosion(x, y, radius, radius+15));

			GamePanel.player.addScore(type+ rank);
			if(isTrapping())GamePanel.player.isTrapped(false);	
					
		}else{
		
		hit = true;
		hitTimer = System.nanoTime();
		}
	}

	/***************** Update and Draw Functions Below ****************************************/

	public void update()
	{
		//enemy movement
		int xlocation = (int) GamePanel.player.x;
		int ylocation = (int) GamePanel.player.y;		
		
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
		
if(!GamePanel.player.isDead()){
		//looking for player
		/*if((type == 2 && !chase && !chaseVehicle && GamePanel.player.isVisible()) || type == 3)
		{
			int xpos = (int) this.getx();
			int ypos = (int) this.gety();
			
			double dist = getDistanceFromPlayer(xlocation, ylocation);
			double distv = getClosestVehicle();
			targetVehicle = findTargetVehicle(distv);
			if(distv < 150 && !GamePanel.player.isDriving() && validTarget(targetVehicle) && !((Vehicle)GamePanel.entities.get("Vehicle").get(targetVehicle)).isFree() && type != 3){
				setChaseVehicle(true);
			}
			if(dist < 150 && !GamePanel.player.isDriving())setChase(true);
			
			
			
			if( ypos >= ylocation-5 && ypos <= ylocation+5 && !chaseVehicle && type != 3)
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
			
			if( xpos >= xlocation-5 && xpos <= xlocation+5 && !chaseVehicle && type != 3 )
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
		}*/
		
		
		//Chasing Algorithim
		/*if(chase && type == 2)
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
			
		}*/
	
}	
		//chase Algorithim for vehicle 
			
		
		//Chasing Algorithim for ghost 
				if(type == 3){
					
					
					if (GamePanel.entities.get("Vehicle").size() == 0 && GamePanel.player.isVisible() && !GamePanel.player.isDead()){		
					
					
						if (getx() != xlocation){
							
							if (xlocation > getx())
							{
								if(slow)x+= maxChaseSpeed*.3;
								else x+= maxChaseSpeed;
							}
							if (xlocation < getx())
							{
								if(slow)x-= maxChaseSpeed*.3;
								else x-= maxChaseSpeed;
							}
							
							
						}
						
						if (gety() != ylocation){
							
							if (ylocation > gety())
							{
								if(slow)y+= maxChaseSpeed*.3;
								else y+= maxChaseSpeed;
							}
							if (ylocation < gety())
							{
								if(slow)y-= maxChaseSpeed*.3;
								else y-= maxChaseSpeed;
							}
							
							
						}
					
				}else if(GamePanel.entities.get("Vehicle").size() > 0 ){
					
					int vx = (int) GamePanel.entities.get("Vehicle").get(0).getx();
					int vy = (int) GamePanel.entities.get("Vehicle").get(0).gety();
					
					if (getx() != vx){
						
						if (vx > getx())
						{
							if(slow)x+= maxChaseSpeed;
							else x+= maxChaseSpeed;
						}
						if (vx < getx())
						{
							if(slow)x-= maxChaseSpeed*.3;
							else x-= maxChaseSpeed;
						}
						
						
					}
					
					if (gety() != vy){
						
						if (vy > gety())
						{
							if(slow)y+= maxChaseSpeed*.3;
							else y+= maxChaseSpeed;
						}
						if (vy < gety())
						{
							if(slow)y-= maxChaseSpeed*.3;
							else y-= maxChaseSpeed;
						}
						
						
					}
					
					if( gety() >= vy-5 && gety() <= vy+5 && getx() >= vx-5 && getx() <= vx+5){
						( (Vehicle)GamePanel.entities.get("Vehicle").get(0)).isBeingDrained(true);
					} else ((Vehicle)GamePanel.entities.get("Vehicle").get(0)).isBeingDrained(false);
					
					
					
				}else{
					x+=dx;
					y+=dy;
				}
					
					
					
					
					
					
				}
		
		
		
		
		
		//checkSelfDestruct();

		if(!ready)
		{
			
			if(x > radius && x< GamePanel.WIDTH* GamePanel.SCALE - radius && y > radius && y < GamePanel.HEIGHT*GamePanel.SCALE-radius)
				ready = true;  
		
		}
		
		//keeps enemy on screen
		checkBounds();
		/*if(x < radius && dx < 0 && type != 3) //left boundary
		{dx =-dx;}
		if(y < radius && dy < 0 && type !=3) //top of the screen 
		{dy = -dy;}
		if(x > GamePanel.WIDTH* GamePanel.SCALE - radius && dx >0 && type != 3)//right boundary
		{dx = -dx;}
		if( y > GamePanel.HEIGHT*GamePanel.SCALE -radius  && dy >0 && type !=3 )//bottom boundary
		{dy =-dy;}*/
		
		
		//enemy hit update
		if(hit)
		{
			long elapsed = (System.nanoTime()- hitTimer)/1000000;
			if(elapsed > 50)
			{
				hit = false;
				hitTimer = 0;
				
			}
			
		}
		
		//ghost fade
		
			
	if(type == 3)			
	{
		if (fade == 0)
		{
			fade = 1;
			ghost = true;
			ghostTimer = System.nanoTime();
			
		}
		if(ghost)
		{
			long elapsed = (System.nanoTime() - ghostTimer)/1000000;
			System.out.println("GhostTimer: "+ elapsed);
			if (elapsed > 2000)
			{
				ghost = false;
				ghostTimer = 0;
				fade = 255;
				
				
			}else fade+=1;
			
		}
	}
		
	}
	
	protected void checkBounds(){
		if(x < radius && dx < 0 && type != 3) //left boundary
		{dx =-dx;}
		if(y < radius && dy < 0 && type !=3) //top of the screen 
		{dy = -dy;}
		if(x > GamePanel.WIDTH* GamePanel.SCALE - radius && dx >0 && type != 3)//right boundary
		{dx = -dx;}
		if( y > GamePanel.HEIGHT*GamePanel.SCALE -radius  && dy >0 && type !=3 )//bottom boundary
		{dy =-dy;}		
	}
	
	

	public void draw(Graphics2D g) {
		fade -= 1;
		if (hit && !ghost) {
			g.setColor(color2);
			g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);

			g.setStroke(new BasicStroke(3));
			g.setColor(color2.darker());
			g.drawOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
			g.setStroke(new BasicStroke(1));
		} else if (type == 3) {

			g.setColor(new Color(200, 191, 231, fade));
			g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);

			g.setStroke(new BasicStroke(3));
			g.setColor(new Color(171, 157, 219, fade));
			g.drawOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
			g.setStroke(new BasicStroke(1));

		} else

		{
			g.setColor(color1);
			g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);

			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.drawOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
			g.setStroke(new BasicStroke(1));
		}

	}

	public void attackPlayer(Player player) {
		if (type == 2) {
			explode();
		}else if (type == 1 && rank > 2) {
			if (!player.isTrapped()) {
				isTrapping(true);
				player.isTrapped(true);
			}
		}
		
	}

}
