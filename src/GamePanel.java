/*
 * Colleen Chang & Emily Liu
 * June 21, 2022
 * GamePanel continuously runs the program, calling necessary methods and classes 
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{

	public static final int GAME_WIDTH = 400;
	public static final int GAME_HEIGHT = 600;

	public Thread gameThread;
	public Image image;
	public Graphics graphics;
	public WallHP health;
	public Player player;
	public DrawImage menu;

	public ArrayList<Enemy>[] enemies;

	public int timer;
	public boolean attacked = false;
	public String state = "menu"; // possible states: menu, game, cutscene, lose

	public int count =0;
	public int gCount =0;
	public int mX, mY;
	
	public GamePanel() {
		player = new Player(0, 0);
		int[] numOfTitans = { 1, 2, 3 };
		enemies = new ArrayList[3];
		for (int i = 0; i < 3; i++) {
			enemies[i] = new ArrayList<Enemy>();
			for (int j = 0; j < numOfTitans[i]; j++) {
				enemies[i].add(new Enemy((int) (Math.random() * (GAME_WIDTH - Enemy.SIZES[i])), i));
			}
		}
//		menu = new DrawImage(GAME_WIDTH, GAME_HEIGHT, "bg.png");

		health = new WallHP(GAME_WIDTH);
		state = "menu";

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// ball.mousePressed(e);
				mX = e.getX();
				mY = e.getY();
				if (!player.isMidair && state.equals("game")) {
					player.shoot(mX, mY);
				}

			}
		});

		this.setFocusable(true); // make everything in this class appear on the screen
		this.addKeyListener(this);
		this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		gameThread = new Thread(this);
		gameThread.start();
	}

	//overrides paint method
	public void paint(Graphics g) {
		image = createImage(GAME_WIDTH, GAME_HEIGHT); // draw off screen
		graphics = image.getGraphics();
		draw(graphics); // update the positions of everything on the screen
		g.drawImage(image, 0, 0, this); // redraw everything on the screen

	}

	// calls the requested draw methods depending on the program's "state" (public string)
	public void draw(Graphics g) {
		if (state.equals("menu")) {
			drawMenu(g);
		} else if (state.equals("game")) {
			drawGame(g);
		} else if (state.equals("cutscene")) {
			drawCutscene(g);
		}
		
	}

	// calls draw methods from relevant classes to draw game entities and update their positions
	public void drawGame(Graphics g) {
		// Draws wall at the bottom of the screen
		g.setColor(Color.gray);
		g.fillRect(0, GAME_HEIGHT - 15, GAME_WIDTH, 15);
		
		
		if (gCount < 300) {
			g.setColor(Color.WHITE);
			g.drawString("tap to make youre player move across the screen", 100, 60);
		} else {
			// Draws all enemies in "Enemy" array list
			for (int i = 2; i >= 0; i--) {
				ArrayList<Enemy> arr = enemies[i];
				for (Enemy e : arr) {
					e.draw(g);
				}
			}
		}
		
		health.draw(g);
		player.draw(g);
	}
	
	public void drawMenu(Graphics g) {
		// draws red, hollow box surrounding text (temp)
		g.setColor(Color.RED);
		g.drawRect(70, 40, 100, 30);
		
		// draws menu options
		g.setColor(Color.WHITE);
		g.drawString("menu", 100, 60);
		g.drawString("controls", 100,120);
		
		//hard-coded buttons (if the program detects that the user clicks
		//within a certain area around a string, the "state" string is updated)
		if ( 60-10 <= mX && mX <= 150  && 60-10 <= mY && mY <= 60 +10) {
			state = "cutscene";
		}
		
		//bg photo
		//name
		
	}
	
	public void drawCutscene(Graphics g) {
		//timer: counter goes up every 1/60 of a second when GamePanel loops
		count++;
		g.setColor(Color.WHITE);
		
		//cutscene image placeholder (copy multiple times)
		if (0 <= count  && count <= 180) {
			g.drawString("cutscene", 100, 60);
		}else if (181 <= count && count <= 360) {
			g.drawString("ending", 100, 60);
		}else { // once the cutscenes have all played, "state" updates and the game starts
			state = "game";
		}
	
	}

	//calls move methods to let entities move across the screen
	public void move() {
		//calls method for all enemies in "Enemy" array list
		if (state.equals("game") && gCount == 60*5){
			for (ArrayList<Enemy> arr : enemies) {
				for (Enemy e : arr) {
					e.move();
				}
			}
			player.move();
		}
	}

	// detects whether certain entities collide with each other
	public void checkCollision() {
		// Collides the enemy with the wall
		for (int i = 0; i < 3; i++) {// loop through enemies array
			ArrayList<Enemy> currArr = enemies[i];
			for (Enemy e : currArr) {
				if (e.y >= GAME_HEIGHT - 15 - Enemy.SIZES[i]) {
					e.speed = 0;
					e.counter++;
				}
			}
		}

		// Collides player with screen
		if (player.x < 0 || player.x > GAME_WIDTH - player.PLAYER_SIZE) {
			player.setVX(0);
			player.setVY(0);
			if (player.x < 0) {
				player.x = 0;
			}
			if (player.x > GAME_WIDTH - player.PLAYER_SIZE) {
				player.x = GAME_WIDTH - player.PLAYER_SIZE;
			}
			player.isMidair = false;
		}
		if (player.y < 0 || player.y > GAME_HEIGHT - player.PLAYER_SIZE - 15) {
			player.setVX(0);
			player.setVY(0);
			if (player.y < 0) {
				player.y = 0;
			}
			if (player.y > GAME_HEIGHT - player.PLAYER_SIZE - 15) {
				player.y = GAME_HEIGHT - player.PLAYER_SIZE - 15;
			}
			player.isMidair = false;
		}

	}

	// updates enemies' health points when player collides with their hitbox
	public void checkHitTitan() {
		// loops through enemies array and checks for player and enemy hitbox intersection
		for (int i = 0; i < 3; i++) {
			ArrayList<Enemy> currArr = enemies[i];
			for (Enemy e : currArr) {
				if (e.wasDamaged == 0) {
					int tX1 = e.x, tX2 = e.x + e.side, tY1 = e.y, tY2 = e.y + e.side;

					// Titans hitbox
					Line2D d1 = new Line2D.Double(tX1, tY1, tX2, tY2);
					Line2D d2 = new Line2D.Double(tX2, tY1, tX1, tY2);
					Line2D path = player.getLastMovement();
					if (path.intersectsLine(d1) || path.intersectsLine(d2)) {
						e.setHp(e.getHp() - 1);
						e.wasDamaged++;
					}
				} else {
					e.wasDamaged = 0;
				}
			}
		}
	}

	// updates wall's health points when enemies collide with it
	public void damageWall() {
		for (int i = 0; i < 3; i++) {// for each enemy type
			ArrayList<Enemy> currArr = enemies[i];
			for (Enemy e : currArr) { // for each enemy of each type
				if (e.getHp() <= 0) {
					continue;
				}
				if (e.counter == 60 && health.hp > 0) { //enemy damage is subtracted from wall health every second
					health.damage(e);
					e.counter = 0;
				} else if (health.hp <= 0) { //prevents wall hp from becoming negative
					health.hp = 0;
				}
			}
		}

	}

	// let's the program run until the player exits the game
	public void run() {
		// the CPU runs our game code too quickly - we need to slow it down! The
		// following lines of code "force" the computer to get stuck in a loop for short
		// intervals between calling other methods to update the screen.
		long lastTime = System.nanoTime();
		double amountOfTicks = 60;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long now;

		while (true) { // stops running when wall has 0 HP
			now = System.nanoTime();
			delta = delta + (now - lastTime) / ns;
			lastTime = now;

			// only move objects around and update screen if enough time has passed
			if (delta >= 1) {
				damageWall();
				move();
				checkCollision();
				if (player.isMidair) {
					checkHitTitan();
				}
				repaint();
				delta--;
			}
		}
	}

	// starts game after 'd' is pressed (for testing)
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'd') {
			state = "game";
		} else if (e.getKeyChar() == 'p') {
			state = "cutscene";
		}
		
		//pressing 's' skips the cutscene
		if (state.equals("cutscene")) {
			if (e.getKeyChar() == 's') {
				state = "game";
			}
		}
	}
	
	// required for KeyListener
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}

}