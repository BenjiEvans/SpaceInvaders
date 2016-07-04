package entity;
import java.awt.*;
import main.GamePanel;

public class Vehicle extends MovableEntity{
		
		//movement
		private boolean right;
		private boolean left;
		private boolean up;
		private boolean down;		
		private boolean playerIn = false;
		
		
		//color 
		private Color color1 = new Color(0,255,0,128);
		//private Color activated = new Color(255,255,255,100);
		private Color activated = new Color(0,255,0,100);
		private Color hurt = new Color(255,0,0,255);
		private Color shell = new Color (26,58,21,128);
		private Color sick;
		
		//health 
		private int shield;
		//private boolean dead;
		private boolean hit;
		private long hitTimer;
		private boolean infected = false;
		private boolean draining = false;
		private int fade = 128;
		
		//infected timers 
		private long infectedTimer;
		private long infectedTimerDiff;
		private long infectedLength = 3000;
		
		//spawning
		private boolean free;
		
		
	public Vehicle(){
		
		//initial possition is only temparary 
		
		free = true;
		
		
		x =  (int)(GamePanel.WIDTH*GamePanel.SCALE *Math.random());
		y = GamePanel.HEIGHT*GamePanel.SCALE ;
		
		int randomx = (int) (Math.random()*4+1);
		
		if(randomx == 1) dx = 1;
		else if(randomx == 2)dx =-1;
		else if(randomx == 3) dx = 2;
		else if(randomx == 4) dx = -2;
		
		int randomy = (int) (Math.random()*2+1);
		
		if(randomy == 1) dy = -1;
		else if(randomy == 2)dy =-2;
		
		
		
		radius = 20;
		
		speed =5;
			
		shield= 3;
		hit = false;
		hitTimer = 0;
		//dead = false;
		
		
		
		
	}
	
	/******************************************Functions **************************/
	
	
	
	
	public void setUp(boolean b){up =b;}
	public void setDown(boolean b){down =b;}
	public void setLeft(boolean b){left =b;}
	public void setRight(boolean b){right =b;}
	public void setFade(int i){ fade = i;}
	public int getFade(){return fade;}
	
	public long getInfectedTimer(){ return infectedTimer;	}
	
	public void setInfectedTimer(long time){ infectedTimer = time;}
	
	
	public void isBeingDrained(boolean b){draining = b;}
	
	public boolean isBeingDrained(){ return draining;}
	
	
	
	public void isInfected(boolean b){infected = b;}
	
	public boolean isInfected(){return infected;}
	
//	public void isDead(boolean b){ dead = b;	}
	
	
	
	public void playerIn(boolean b){playerIn = b; if(b) free = false;}
	
	public void isFree(boolean b ){
		free = b;
		//this.dx = -1;
		//this.dy = -3;
		}
	
	public boolean isFree(){ return free; }
	
	public boolean playerIn(){ return playerIn;}
	
	public void setSpeed(int newSpeed){ speed = newSpeed;}
	
	
	
	/*public void loseSheild(){ shield--;}
	
	public int getSheild(){return shield;}*/
	
	public void hit(/*int playerLevel*/ int dir){

		//y-=10;
		
		/*if(playerLevel < 2)
		{	
			if (dir ==0)x += 10;
			else if (dir == 90)y+= 10;
			else if (dir == 180)x -=10;
			else if (dir == 270)y-=10;
		}
		else if(playerLevel < 4)
		{
			if (dir ==0)x += 15;
			else if (dir == 90)y+= 15;
			else if (dir == 180)x -=15;
			else if (dir == 270)y-=15;
		}
		else {*/
			
			if (dir ==0)x += 20;
			else if (dir == 90)y+= 20;
			else if (dir == 180)x -=20;
			else if (dir == 270)y-=20;
			
		//}
		hit();
		
	}
	
	public void hit(){
		shield--;
		if(shield <= 0) {
			//dead = true;
			finished =true;
			GamePanel.player.isDriving(false);
			GamePanel.player.setInVehicle(false);
			
			
			double randomDrop = Math.random();

			if (randomDrop < .01) {
				GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.HEALTH, x, y));
			} else if (randomDrop < .2)
				GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.POWER2, x,y));
			else if (randomDrop < .2)
				GamePanel.entities.get("PowerUp").add(new PowerUp(PowerType.POWER, x,y));

			GamePanel.entities.get("Explosion").add(new Explosion(x, y,radius, radius+ 15));			
			
		}else{
			hit = true;
			hitTimer = System.nanoTime();
			System.out.print("got hit ");
		}
	}
	
	
	//public boolean isDead(){return dead;}
	
	
	/*** TODO :remove these--**/
	public void setdx(double num){dx = num;}
	public void setdy(double num){dy = num;}
	
	public double getdx(){ return dx;}
	public double getdy(){ return dy;}
	
	public void setx(double input){ x += (int) input;}
	
	public void setx(int increment){ x += increment;}
	
	
	public void sety(double input){ y = (int) (this.y +input);}
	
	public void sety(int increment){ y += increment;}
	
	
	
	public void update(){		
	if(free)
	{
		
		if(/*GamePanel.slowDownTimer != 0*/slow){
			
			x += dx*.3;
			y += dy*.3;
			
		}else{		
			x += dx;
			y += dy;
		}
		
		
		//keeps vehicle on screen
		
		if(x < radius && dx < 0) dx =-dx;
		
		if(y < radius && dy < 0) dy = -dy;
		
		if(x > GamePanel.WIDTH* GamePanel.SCALE - radius && dx >0) dx = -dx;
		
		if( y > GamePanel.HEIGHT*GamePanel.SCALE -radius  && dy >0 )dy =-dy;
		
		
	}else if(GamePanel.player.isDriving()){
		if(left){
			dx = -speed;
		}if(right){
			dx = speed;
		}if(up){
			dy = -speed ;
		}if(down){
			dy = speed ;
		}	
		x +=dx;
		y +=dy;
		
		
		
		
		dx = 0;
		dy = 0;
		
		if(x < radius )x = radius;
		if(y < radius) y = radius;
		if(x >(GamePanel.WIDTH*GamePanel.SCALE)- radius ) x = (GamePanel.WIDTH*GamePanel.SCALE)- radius;
		if(y >(GamePanel.HEIGHT*GamePanel.SCALE)- radius ) y = (GamePanel.HEIGHT*GamePanel.SCALE)- radius;
		
		
		
		if(GamePanel.player.isDriving() && playerIn())
		{
			GamePanel.player.setx(getx());
			GamePanel.player.sety(gety());
		}
		
		
	}
		
		
		
		
		
		if(hit)
		{
			long elapsed = (System.nanoTime()- hitTimer)/1000000;
			if(elapsed > 50)
			{
				hit = false;
				hitTimer = 0;
				
			}
			
		}
		
		if(infected)
		{
			if(infectedTimer != 0){
				infectedTimerDiff = (System.nanoTime() - infectedTimer)/1000000;
				if(infectedTimerDiff > infectedLength){
					infectedTimer = 0;
					this.hit();
				}
			}
			
		}
		
		
		
		
		
	}
	
	//infected color 
	
	
	
	
	
		

	public void draw(Graphics2D g){
	
	if(infected){
		
		if(fade ==0){
		
			if(this.playerIn)
			{
				GamePanel.player.isDriving(false);
			    GamePanel.player.setInVehicle(false);
			}  
			
			
			for (int i = 0; i < GamePanel.entities.get("Enemy").size(); i++){
				
				Enemy e = (Enemy) GamePanel.entities.get("Enemy").get(i);
				/*if( e.getType() == 3){
					e.grow(5);
				}*/
				
			}
			
			
			
		}else if(draining){
			sick = new Color(55,25,12,fade);
			fade--;
			g.setColor(sick);
			g.fillOval((int)x-radius, (int)y-radius, 2*radius, 2*radius);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(sick.darker());
			g.drawOval((int)x-radius , (int)y-radius, 2*radius , 2*radius);
			//fade--;
		}	
		else if(!hit){
		
		
		sick = new Color(55,25,12,fade);
		//fade--;
		g.setColor(sick);
		g.fillOval((int)x-radius, (int)y-radius, 2*radius, 2*radius);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(sick.darker());
		g.drawOval((int)x-radius ,(int) y-radius, 2*radius , 2*radius);
		//fade--;
		}
		else if(hit){
			sick = new Color(55,25,12,255);
			//fade--;
			g.setColor(sick);
			g.fillOval((int)x-radius,(int) y-radius, 2*radius, 2*radius);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(sick.darker());
			g.drawOval((int)x-radius ,(int) y-radius, 2*radius , 2*radius);
			//fade--;
		}
		
	}
	
	else if(playerIn){
		
		if(hit){
			g.setColor(hurt);
			g.fillOval((int)x-radius, (int)y-radius, 2*radius, 2*radius);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(hurt.darker());
			g.drawOval((int)x-radius ,(int) y-radius, 2*radius , 2*radius);
		}else{
		g.setColor(activated);
		g.fillOval((int)x-radius, (int)y-radius, 2*radius, 2*radius);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(activated.darker());
		g.drawOval((int) x-radius ,(int) y-radius, 2*radius , 2*radius);
		}
	}else if(!playerIn && free){
		
		if(hit){
			g.setColor(hurt);
			g.fillOval((int) x-radius, (int)y-radius, 2*radius, 2*radius);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(hurt.darker());
			g.drawOval((int) x-radius , (int) y-radius, 2*radius , 2*radius);
		}else{
			g.setColor(color1);
			g.fillOval((int)x-radius,(int) y-radius, 2*radius, 2*radius);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.drawOval((int) x-radius ,(int) y-radius, 2*radius , 2*radius);
			
			
			
		}
	}else if(!playerIn && !free){
			
			if(hit){
				g.setColor(hurt);
				g.fillOval((int) x-radius, (int) y-radius, 2*radius, 2*radius);
				
				g.setStroke(new BasicStroke(3));
				g.setColor(hurt.darker());
				g.drawOval((int) x-radius ,(int) y-radius, 2*radius , 2*radius);
			}else{
		
				g.setColor(shell);
				g.fillOval((int) x-radius, (int) y-radius, 2*radius, 2*radius);
				
				g.setStroke(new BasicStroke(3));
				g.setColor(shell.darker());
				g.drawOval((int) x-radius , (int)y-radius, 2*radius , 2*radius);
			
			
			}
		
		
	}
		
	}

}
