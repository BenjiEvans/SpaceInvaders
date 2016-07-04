package entity;

import java.awt.*;
import java.util.ArrayList;

import main.EventTimer;
import main.GameEvents;
import main.GamePanel;

public class Player extends MovableEntity implements GameEvents {

	private int lives, score;
	private EventTimer recoveryTimer = new EventTimer(2000,PLAYER_RECOVERY,this);
	
	private boolean isRecovering;
	/*private long recoveryTimer;*/
	
	// movement
	private boolean right, left, up, down;
	private boolean trapped = false;
	// rotory movement
	private boolean rotRight,rotLeft,rotUp,rotDown;
	private int shootingAngle;

	// player state
	private Color safe;
	private Color damage;
	private Color ghost = new Color(255, 255, 255, 128);

	// missle launch variables
	private boolean firing;
	private long firingTimer, firingDelay;
	//private EventTimer firingTimer = new EventTimer(200,PLAYER_SHOOT,this);

	// vehicle
	private boolean inVehicle = false;
	private boolean driving = false;
	private boolean isDead = false;
	//private boolean isShooting = false;
	// power up fields
	private int powerLevel, power;
	//private int power;
	private int[] requiredPower = { 1, 2, 3, 4, 5 };
	//private EventTimer slow = new EventTimer(6000,GameEvents.,this);

	public Player() {
		// initial position
		x = 300;
		y = 300;
		radius = 5;
		// initials speeds
		dx = 0;
		dy = 0;
		// initial shooting angle
		shootingAngle = 270;
		speed = 5;
		lives = 3;
		ghost = new Color(255, 255, 255, 50);
		safe = Color.WHITE;
		damage = Color.RED;
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 200;
		isRecovering = false;
	//	recoveryTimer = 0;
		score = 0;
	}

	// functions
	public void setFiring(boolean b) {
		firing = b;
	}

	public int getScore() {
		return score;
	}

	public boolean getInVehicle() {
		return inVehicle;
	}

	public void setInVehicle(boolean b) {
		inVehicle = b;
	}

	public boolean isDriving() {
		return driving;
	}

	public void isDriving(boolean b) {
		driving = b;
	}

	public boolean isDead() {
		return isDead;
	}

	public int addScore(int i) {
		return score += i;
	}

	public void gainLife() {
		lives++;
	}

	public void increasePower(int j) {

		power += j;
		if (powerLevel == 4) {
			if (power > requiredPower[powerLevel]) {
				power = requiredPower[powerLevel];

			}
			return;
		}
		if (power >= requiredPower[powerLevel]) {
			power -= requiredPower[powerLevel];
			powerLevel++;

		}

	}

	public void slowTime(boolean b) {

		if (power > 0 && b) {
			power--;
			slow = true;
			//GamePanel.slowDownTimer = System.nanoTime();
			//Enemy.setSlow(true);
			//slowTimer.start();
			GamePanel.entities.get("Text").add(new Text(x, y, "Slow Down", 2000));

		}else{
			if(!b)slow=false;
		}

	}

	public void setVisible(boolean b) {

		if (power > 0 && !b) {
			power--;
			//isGhost(true);
			isVisible =false;
			//GamePanel.invisibleTimer = System.nanoTime();

			/*for (int i = 0; i < GamePanel.entities.get("Enemy").size(); i++)
				( (Enemy) GamePanel.entities.get("Enemy").get(i)).setChase(false);*/

			GamePanel.entities.get("Text").add(new Text(GamePanel.player.getx(),
					GamePanel.player.gety(), "Invisible", 2000));

		}else{
			isVisible = true;
		}
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	public int getPower() {
		return power;
	}

	public int getRequiredPower() {
		return requiredPower[powerLevel];
	}

	public boolean isTrapped() {
		return trapped;
	}

	public void isTrapped(boolean b) {
		trapped = b;
	}

	public void update() {
		if(finished)return;
		
		if(isDead){
			y+=dy;
			
			if(y + radius >= GamePanel.HEIGHT * GamePanel.SCALE)
			{
				finished = true;
				GamePanel.entities.get("Explosion").add(new Explosion(x, y, radius, radius + 15));
			}
			return;
		}
	
		if (!trapped && !driving) {
				if (left) {
					dx = -speed;
				}
				if (right) {
					dx = speed;
				}
				if (up) {
					dy = -speed;
				}
				if (down) {
					dy = speed;
				}
				x += dx;
				y += dy;

				dx = 0;
				dy = 0;
			}
			if (rotRight) {
				shootingAngle = 0;

			}
			if (rotLeft) {
				shootingAngle = 180;

			}
			if (rotUp) {
				shootingAngle = 270;

			}
			if (rotDown) {

				shootingAngle = 90;

			}

			if (trapped) {
				int gox;
				int goy;

				for (int i = 0; i < GamePanel.entities.get("Enemy").size(); i++) {

					Enemy e = (Enemy) GamePanel.entities.get("Enemy").get(i);

					if (e.isTrapping()) {
						gox = (int) e.getx();
						goy = (int) e.gety();

						if (x != gox) {
							if (gox > x) {
								x += 2;
							} else
								x -= 2;
						}
						if (y != goy) {
							if (goy > y) {
								y += 2;
							} else
								y -= 2;
						}

					}

					// isTrapped(false);

				}

			}

			if (!driving && inVehicle) {
				double gox;
				double goy;

				for (int i = 0; i < GamePanel.entities.get("Vehicle").size(); i++) {

					Vehicle e = (Vehicle) GamePanel.entities.get("Vehicle").get(i);

					if (e.playerIn()) {
						gox = e.getx();
						goy = e.gety();

						if (x != gox) {
							if (gox > x) {
								x += 1;
							} else
								x -= 1;
						}
						if (y != goy) {
							if (goy > y) {
								y += 1;
							} else
								y -= 1;
						}

						if (x == gox && y == goy) {
							isDriving(true);

						}
						break;
					}

				}

			}

			if (x < radius)
				x = radius;
			if (y < radius)
				y = radius;
			if (x > (GamePanel.WIDTH * GamePanel.SCALE) - radius)
				x = (GamePanel.WIDTH * GamePanel.SCALE) - radius;
			if (y > (GamePanel.HEIGHT * GamePanel.SCALE) - radius)
				y = (GamePanel.HEIGHT * GamePanel.SCALE) - radius;
			
			//if(firing)shoot();

			if (firing && !driving) {

				long elapsed = (System.nanoTime() - firingTimer) / 1000000;

				if (elapsed > firingDelay) {

					firingTimer = System.nanoTime();

					if (powerLevel < 2) {
						shootBullets(new Bullet(shootingAngle, x, y));
					} else if (powerLevel < 4) {
						if (shootingAngle == 0 || shootingAngle == 180) {
							
							shootBullets(new Bullet(shootingAngle, x,y + 5),new Bullet(shootingAngle, x,y - 5));
							
						} else {
							shootBullets(new Bullet(shootingAngle,x + 5, y),new Bullet(shootingAngle,x - 5, y));
						}
					} else {
						if (shootingAngle == 0) {
							shootBullets(new Bullet(shootingAngle, x,y),new Bullet(shootingAngle + 5,x, y + 5),new Bullet(shootingAngle - 5,x, y - 5));

						} else if (shootingAngle == 90) {
							shootBullets(new Bullet(shootingAngle, x,y),new Bullet(shootingAngle - 5,x + 5, y),new Bullet(shootingAngle + 5,x - 5, y));

						} else if (shootingAngle == 270) {
							shootBullets(new Bullet(shootingAngle, x,y),new Bullet(shootingAngle + 5,x + 5, y),new Bullet(shootingAngle - 5,x - 5, y));
						} else {
							shootBullets(new Bullet(shootingAngle, x,y),new Bullet(shootingAngle - 5,x, y + 5),new Bullet(shootingAngle + 5,x, y - 5));	
						}
					}

				}

			}
	}

	// more functions
	
	private void shootBullets(Bullet... bullets){
		GamePanel.shots += bullets.length;
		ArrayList<Entity> list = GamePanel.entities.get("Bullet");
		for(Bullet b: bullets){
			list.add(b);
		}
	}	
	
	public void setLeft(boolean b) {
		left = b;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void setDown(boolean b) {
		down = b;
	}

	public void setRotRight(boolean b) {
		rotRight = b;
	}

	public void setRotLeft(boolean b) {
		rotLeft = b;
	}

	public void setRotUp(boolean b) {
		rotUp = b;
	}

	public void setRotDown(boolean b) {
		rotDown = b;
	}

	public int getLives() {
		return lives;
	}

	public boolean isRecoverting() {
		return isRecovering;
	}

	public void hit() {
		if(isRecovering)return;
		lives--;
		if(lives <= 0){
			isDead =true;
			dy = 5;
		}
		/*else{
			recovering = true;
			recoveryTimer = System.nanoTime();
		}*/
		isRecovering = true;
		recoveryTimer.start();
	}

	public void draw(Graphics2D g) {
		if(finished)return;
		
		if (!isVisible) {

			g.setColor(ghost);
			g.fillOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);

			g.setStroke(new BasicStroke(3));
			g.setColor(ghost.darker());
			g.drawOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);

		} else if (isRecovering || isDead) {

			g.setColor(damage);
			g.fillOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);

			g.setStroke(new BasicStroke(3));
			g.setColor(damage.darker());
			g.drawOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);

		} else {
			g.setColor(safe);
			g.fillOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);
			g.setStroke(new BasicStroke(3));
			g.setColor(safe.darker());
			g.drawOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);
		}
	}

	@Override
	public void hit(int angle) {
		hit();		
	}

	@Override
	public void startWave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void slowGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hidePlayer() {
		// TODO Auto-generated method stub
		
	}
	
	public void finishRecovery(){
		isRecovering = false;
		recoveryTimer.reset();
	}
	
	/*private void shoot(){
		if(!firingTimer.hasStarted()){
			firingTimer.start();
		}
	}*/
}
