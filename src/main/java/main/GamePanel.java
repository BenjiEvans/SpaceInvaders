package main;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.util.*;

import entity.*;

public class GamePanel extends JPanel implements Runnable, KeyListener, GameEvents {

	// window attributes
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;

	// game attributes
	private Thread thread;
	private boolean running, pause, lose, win;

	// Graphcs attributes
	private BufferedImage image;
	private Graphics2D g;

	// control game speed
	private int FPS = 30; // frames per second
	private double averageFPS;

	// players
	public static Player player;
	public static double shots = 0;
	public static double enemyHits = 0;
	public static HashMap<String, ArrayList<Entity>> entities;
	private int waveNum;
	private EventTimer waveTimer = new EventTimer(2000,WAVE_START,this);
	private EventTimer slowTimer = new EventTimer(6000,SLOW,this);
	private EventTimer playerDeathTimer = new EventTimer(3500,END_GAME,this);
	private EventTimer invisibleTimer  = new EventTimer(2000,HIDE_PLAYER,this);

	GamePanel() {
		super();
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
		player = new Player();
		entities = new HashMap<String, ArrayList<Entity>>();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
	}

	public void run() {
		running = true;
		image = new BufferedImage(WIDTH * SCALE, HEIGHT * SCALE,
				BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		// modify graphics object
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		entities.put("Bullet", new ArrayList<Entity>());
		entities.put("Enemy", new ArrayList<Entity>());
		entities.put("PowerUp", new ArrayList<Entity>());
		entities.put("Explosion", new ArrayList<Entity>());
		entities.put("Vehicle", new ArrayList<Entity>());
		entities.put("Text", new ArrayList<Entity>());
		waveNum = 1;
		long startTime, URDTime, waitTime;
		long totalTime = 0;
		int frameCount = 0;
		int maxFrameCount = 30;
		long targetTime = 1000 / FPS;
		// Game loop
		while (running) {

			startTime = System.nanoTime();

			if (!pause) {
				gameUpdate();
				gameRender();
				gameDraw();
			}

			URDTime = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - URDTime;

			try {
				Thread.sleep(waitTime);

			} catch (Exception e) {

			}

			totalTime += System.nanoTime() - startTime;
			frameCount++;

			if (frameCount == maxFrameCount) {
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
				System.out.println(averageFPS);
				frameCount = 0;
				totalTime = 0;
			}

		}

		final int X_ALIGN = 150;
		final int Y_ALIGN = 100;
		final int INCR = 20;

		// draws background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
		g.setColor(Color.WHITE);

		// draws Game Over
		g.setFont(new Font("Century Gothic", Font.PLAIN, 16));
		String s = "Game Over";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH * SCALE - length) / 2, HEIGHT * SCALE / 5);

		// draw shots fired
		g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		g.drawString("Shots Fired: " + shots, X_ALIGN, Y_ALIGN + INCR);

		// draw shots hit
		g.drawString("Shots Hit: " + enemyHits, X_ALIGN, Y_ALIGN + 2 * INCR);

		// draw accuracy rating
		double accuracyRate = 0;
		if (shots > 0)
			accuracyRate = (enemyHits * 1.0 / shots * 1.0) * 100.0;

		g.drawString("Accuracy Rating: " + Math.round(accuracyRate) + " %",
				X_ALIGN, Y_ALIGN + 3 * INCR);

		// bonus points
		int bonus = (int) (enemyHits * accuracyRate);
		g.drawString("Bonus Points: " + bonus, X_ALIGN, Y_ALIGN + 4 * INCR);

		// dividing line
		g.drawString("_________________", X_ALIGN, Y_ALIGN + (4 * INCR + 5));

		// total score
		int totalScore = bonus + player.getScore();
		g.drawString("TotalScore: " + totalScore, X_ALIGN, Y_ALIGN + 5 * INCR);

		// draws players score
		g.setColor(Color.WHITE);
		g.drawString("Score: " + player.getScore(), X_ALIGN, Y_ALIGN);

		gameDraw();
	}

	private void gameUpdate() {
		// new wave
		if (!waveTimer.hasStarted() && entities.get("Enemy").size() == 0) {
			waveTimer.start();
		}
		// player update
		player.update();

		// update entities
		for (String key : entities.keySet()) {
			ArrayList<Entity> list = (ArrayList<Entity>) entities.get(key);
			for (int i = 0; i < list.size(); i++) {
				Entity e = list.get(i);
				e.update();
				if (e.isFinished()) {
					list.remove(i);
					i--;
				}
			}
		}

		// bullet collisions
		ArrayList<Entity> bullets = entities.get("Bullet");
		ArrayList<Entity> enemies = entities.get("Enemy");
		ArrayList<Entity> vehicles = entities.get("Vehicle");
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = (Bullet) bullets.get(i);
			boolean hit = false;
			hit = handleBulletCollision(b, enemies);
			if (hit)
				continue;
			hit = handleBulletCollision(b, vehicles);
		}

		ArrayList<Entity> powerups = entities.get("PowerUp");

		// checks to see if player is dead
		if (player.isFinished() && !playerDeathTimer.hasStarted()) {
			playerDeathTimer.start();
		}

		// player - enemy collision
		if (!player.isRecoverting() && !player.isDriving()
				&& player.isVisible()) {

			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = (Enemy) enemies.get(i);
				if (entitiesHaveCollided(player, e)) {
					player.hit();
					e.attackPlayer(player);
					break;
				}
			}

		}

		// vehicle <---> enemy collision

		/*for (int i = 0; i < vehicles.size(); i++) {
			Vehicle v = (Vehicle) vehicles.get(i);
		
			for (int j = 0; j < enemies.size(); j++) {

				Enemy e = (Enemy) enemies.get(j);
				
				if (entitiesHaveCollided(v, e)) {

					double enemieRadius = e.getRadius();
					double vehicleRadius = v.getRadius();

					if (!v.playerIn() && !v.isFree()) {

						if (e.getType() == 1) {
							e.reflect();
							v.hit();
						} else if (e.getType() == 2) {
							e.explode();
							v.hit();
						}

					} else {
						
						if (e.getType() == 3) {

							if (!v.isInfected()) {
								v.isInfected(true);
								v.setSpeed(3);
							}
						}else if (vehicleRadius > enemieRadius) {
							if(e.getType() != 3)e.hit();
						}

					}
				}
			}

		}*/

		// player - power up collision
		for (int i = 0; i < powerups.size(); i++) {
			PowerUp e = (PowerUp) powerups.get(i);
			if (entitiesHaveCollided(e, player)) {
				// collected power ups
				switch (e.getType()) {
				case HEALTH:
					player.gainLife();
					break;
				case POWER:
					player.increasePower(1);
					break;
				case POWER2:
					player.increasePower(2);
					break;

				}
				e.hit();
			}
		}

		// infected update
		
		/* if (vehicles.size() != 0) {
		  
		  for (int i = 0; i < vehicles.size(); i++) {
		  
		  Vehicle v = (Vehicle) vehicles.get(i);
		  
		  if (v.isInfected() && v.getInfectedTimer() == 0 &&
		  !v.isBeingDrained()) {
		  
		  v.setInfectedTimer(System.nanoTime()); } } }*/
		 

	}

	public boolean entitiesHaveCollided(MovableEntity e1, MovableEntity e2) {
		double dx = e1.getx() - e2.getx();
		double dy = e1.gety() - e2.gety();
		return Math.sqrt(dx * dx + dy * dy) <= e1.getRadius() + e2.getRadius();
	}

	private boolean handleBulletCollision(Bullet b, ArrayList<Entity> list) {

		for (int j = 0, size = list.size(); j < size; j++) {
			MovableEntity e = (MovableEntity) list.get(j);
			if (entitiesHaveCollided(b, e) && e.isVisible()) {// TODO make ghost
																// -> visible

				e.hit(b.getDirection());
				b.hit();

				return true;
			}
		}
		return false;
	}

	/*
	 * private double getDistance(double bx, double by, double ex, double ey) {
	 * double dx = bx - ex; double dy = by - ey; return Math.sqrt(dx * dx + dy *
	 * dy); }
	 */

	private void gameRender() {
		// draws on an offscreen canvas

		// FPS
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + averageFPS, 422, 350);

		// draw slowDown screen
		if (/*slowDownTimer != 0*/Entity.isSlowed()) {
			g.setColor(new Color(255, 255, 255, 64));
			g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

		}

		// draw player
		player.draw(g);

		// update entities
		for (String key : entities.keySet()) {
			ArrayList<Entity> list = (ArrayList<Entity>) entities.get(key);
			for (int i = 0, size = list.size(); i < size; i++) {
				list.get(i).draw(g);
			}
		}
		// draw wave number

		if (/*waveStartTimer != 0*/	waveTimer.hasStarted() && !waveTimer.isDone()) {
			g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
			String s = "- W A V E  " + waveNum + "  -";
			int length = (int) g.getFontMetrics().getStringBounds(s, g)
					.getWidth();
			int alpha = (int) (255 * Math.sin(3.14 * waveTimer.getTimePassed()
					/ waveTimer.getDuration()));
			/*int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff
					/ waveDelay));*/
			if (alpha > 255)alpha = 255;
			else if(alpha < 0)alpha = 0;
			System.out.println(alpha);
			g.setColor(new Color(255, 255, 255, alpha));
			g.drawString(s, ((WIDTH * SCALE) / 2) - (length / 2),
					(WIDTH * SCALE) / 3);

		}

		// draw player lives

		for (int i = 0; i < player.getLives(); i++) {

			g.setColor(Color.WHITE);
			g.fillOval(20 + (20 * i), 20, player.getRadius() * 2,
					player.getRadius() * 2);

			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.drawOval(20 + (20 * i), 20, player.getRadius() * 2,
					player.getRadius() * 2);
			g.setStroke(new BasicStroke(1));
		}

		// draw player power

		g.setColor(Color.YELLOW);
		g.fillRect(20, 40, player.getPower() * 8, 8);
		g.setColor(Color.YELLOW.darker());

		g.setStroke(new BasicStroke(2));

		for (int i = 0; i < player.getRequiredPower(); i++) {

			g.drawRect(20 + 8 * i, 40, 8, 8);

		}

		g.setStroke(new BasicStroke(1));

		// draw player's score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		g.drawString("Score: " + player.getScore(), 1, 350);

		// draw slowdown meter

		if (Entity.isSlowed()/*slowDownTimer != 0*/) {
			g.setColor(Color.WHITE);
			g.drawRect(20, 60, 100, 8);
			g.fillRect(20, 60, (int) (100 - 100.0 * slowTimer.getTimePassed()
					/ slowTimer.getDuration()), 8);
		}

		// draw invisible meter

		if (/*invisibleTimer != 0*/!player.isVisible()) {
			g.setColor(Color.WHITE);
			g.drawRect(20, 60, 100, 8);
			g.fillRect(20, 60, (int) (100 - 100.0 * invisibleTimer.getTimePassed()
					/ invisibleTimer.getDuration()), 8);

		}

		//

	}

	private void gameDraw() {
		// displays the offscreen drawing
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	private void createNewEnemies() {
		ArrayList<Entity> enemies = entities.get("Enemy");
		ArrayList<Entity> vehicles = entities.get("Vehicle");
		enemies.clear();

		if (waveNum == 1) {
			for (int i = 0; i < 8; i++)
				enemies.add(new Enemy(1));
		}
		if (waveNum == 2) {
			for (int i = 0; i < 8; i++)
				enemies.add(new Enemy(1));
			for (int i = 0; i < 4; i++)
				enemies.add(new Enemy(2));
		}
		if (waveNum == 3) {
			for (int i = 0; i < 2; i++)
				enemies.add(new Enemy(4));
			for (int i = 0; i < 4; i++)
				enemies.add(new Enemy(3));
		}

		if (waveNum == 4) {
			for (int i = 0; i < 5; i++)
				enemies.add(new ChaseEnemy(1));
		}

		if (waveNum == 5) {
			for (int i = 0; i < 4; i++)
				enemies.add(new ChaseEnemy(2));
		}

		if (waveNum == 6) {
			for (int i = 0; i < 2; i++)
				enemies.add(new ChaseEnemy(3));
		}

		if (waveNum == 7) {
			for (int i = 0; i < 4; i++)
				enemies.add(new Enemy(4));
			for (int i = 0; i < 3; i++)
				enemies.add(new ChaseEnemy(3));
			for (int i = 0; i < 3; i++)
				vehicles.add(new Vehicle());
		}
		if (waveNum == 8) {
			for (int i = 0; i < 30; i++)
				enemies.add(new Enemy(1));
			for (int i = 0; i < 5; i++)
				enemies.add(new Enemy(4));
			for (int i = 0; i < 3; i++)
				vehicles.add(new Vehicle());
		}
		if (waveNum == 9) {
			for (int i = 0; i < 5; i++)
				enemies.add(new Enemy(4));
			for (int i = 0; i < 1; i++)
				enemies.add(new GhostEnemy(1));
			for (int i = 0; i < 1; i++)
				enemies.add(new ChaseEnemy(3));
			for (int i = 0; i < 3; i++)
				vehicles.add(new Vehicle());

		}

		if (waveNum == 10) {
			win = true;
			running = false;
		}

		if (waveNum == 20) {
			for (int i = 0; i < 3; i++)
				vehicles.add(new Vehicle());
			/*for (int i = 0; i < 1; i++)
				enemies.add(new Enemy(3, 1));*/
		}

	}

	public void keyTyped(KeyEvent key) {

	}

	public void keyPressed(KeyEvent key) {
		int keyCode = key.getKeyCode();

		ArrayList<Entity> vehicles = entities.get("Vehicle");

		if (keyCode == KeyEvent.VK_LEFT) {
			if (!player.getInVehicle())
				player.setLeft(true);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setLeft(true);
						player.setLeft(true);
					}
				}

			}
		}
		if (keyCode == KeyEvent.VK_RIGHT) {

			if (!player.getInVehicle())
				player.setRight(true);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setRight(true);
						player.setRight(true);
					}
				}

			}
		}
		if (keyCode == KeyEvent.VK_UP) {
			if (!player.getInVehicle())
				player.setUp(true);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setUp(true);
						player.setUp(true);
					}
				}

			}
		}
		if (keyCode == KeyEvent.VK_DOWN) {

			if (!player.getInVehicle())
				player.setDown(true);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setDown(true);
						player.setDown(true);
					}
				}

			}

		}
		if (keyCode == 32) {
			player.setFiring(true);
			//player.shoot();
		}
		if (keyCode == KeyEvent.VK_D) {
			player.setRotRight(true);
		}
		if (keyCode == KeyEvent.VK_A) {
			player.setRotLeft(true);
		}
		if (keyCode == KeyEvent.VK_W) {
			player.setRotUp(true);
		}
		if (keyCode == KeyEvent.VK_S) {
			player.setRotDown(true);
		}
		if (keyCode == KeyEvent.VK_ENTER) {

			player.setUp(false);
			player.setDown(false);
			player.setRight(false);
			player.setLeft(false);

			if (!player.getInVehicle()) {
				int px = (int) player.getx();
				int py = (int) player.gety();
				int pr = player.getRadius();

				for (int i = 0; i < vehicles.size(); i++) {

					if (!player.getInVehicle()) {
						Vehicle e = (Vehicle) vehicles.get(i);

						double ex = e.getx();
						double ey = e.gety();
						int er = e.getRadius();

						double dx = px - ex;
						double dy = py - ey;

						double dist = Math.sqrt(dx * dx + dy * dy);

						if (dist <= er + pr) {
							player.setInVehicle(true);
							e.playerIn(true);
							e.isFree(false);
						}
					}

				}

			} else if (player.getInVehicle()) {
				player.isDriving(false);
				player.setInVehicle(false);

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn()) {
						v.playerIn(false);
						v.setDown(false);
						v.setUp(false);
						v.setLeft(false);
						v.setRight(false);

					}

				}

			}

		}
		if (keyCode == KeyEvent.VK_Q) {
			player.slowTime(true);
			if(Entity.isSlowed() && !slowTimer.hasStarted()){
				slowTimer.start();
			}
		}
		if (keyCode == KeyEvent.VK_E) {
			player.setVisible(false);
			if(!player.isVisible() && !invisibleTimer.hasStarted()){
				invisibleTimer.start();
			}
		}
		if (keyCode == KeyEvent.VK_P) {
			if (pause)
				pause = false;
			else if (!pause) {
				pause = true;
				g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
				String s = "- P A U S E D -";
				int length = (int) g.getFontMetrics().getStringBounds(s, g)
						.getWidth();
				// int alpha = (int)(255 * Math.sin(3.14 * waveStartTimerDiff /
				// waveDelay));
				// if(alpha > 255) alpha = 255;
				g.setColor(new Color(255, 255, 255, 255));
				g.drawString(s, ((WIDTH * SCALE) / 2) - (length / 2),
						(WIDTH * SCALE) / 3);
				gameDraw();

			}
		}
		if (keyCode == KeyEvent.VK_N) {
			//waveNum++; TODO 
			startWave();
		}

	}

	public void keyReleased(KeyEvent key) {
		int keyCode = key.getKeyCode();
		ArrayList<Entity> vehicles = entities.get("Vehicle");
		if (keyCode == KeyEvent.VK_LEFT) {

			if (!player.getInVehicle())
				player.setLeft(false);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setLeft(false);
						player.setLeft(false);
					}
				}

			}
		}
		if (keyCode == KeyEvent.VK_RIGHT) {

			if (!player.getInVehicle())
				player.setRight(false);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setRight(false);
						player.setRight(false);
					}
				}

			}

		}
		if (keyCode == KeyEvent.VK_UP) {
			if (!player.getInVehicle())
				player.setUp(false);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setUp(false);
						player.setUp(false);
					}
				}

			}
		}
		if (keyCode == KeyEvent.VK_DOWN) {

			if (!player.getInVehicle())
				player.setDown(false);
			else {

				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle v = (Vehicle) vehicles.get(i);
					if (v.playerIn() && player.isDriving()) {
						v.setDown(false);
						player.setDown(false);
					}
				}

			}

		}
		if (keyCode == 32) {
			player.setFiring(false);
			//player.shoot();
		}
		if (keyCode == KeyEvent.VK_D) {
			player.setRotRight(false);
		}
		if (keyCode == KeyEvent.VK_A) {
			player.setRotLeft(false);

		}
		if (keyCode == KeyEvent.VK_W) {
			player.setRotUp(false);
		}
		if (keyCode == KeyEvent.VK_S) {
			player.setRotDown(false);
		}
	}

	@Override
	public void startWave() {
		//waveStart = true;
		createNewEnemies();
		waveTimer.reset();
		waveNum++;
		
	}

	@Override
	public void endGame() {
		running = false;
		lose = true;		
	}

	@Override
	public void slowGame() {
		// TODO Auto-generated method stub
		Entity.setSlow(false);
		slowTimer.reset();
	}

	@Override
	public void hidePlayer() {
		invisibleTimer.reset();
		player.setVisible(true);
	}

	@Override
	public void finishRecovery() {
		// TODO Auto-generated method stub
		
	}

}
