import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Enemy extends Rectangle {

	public int speed, side, counter, wasDamaged;
	private int type, hp, idx, loop;
	public static final int[] SIZES = {30,40,50};
	private static final int[] DAMAGE = {1,2,3};
	private static final int[] SPEED = {4,2,1};
	private ArrayList<Image> spriteArr, hpArr;
	private Image curr, dead;

	// constructor creates the obstacle and sets initial falling speed
	// diameter will vary depending on which kind of titan is spawned (10 for fast,
	// 20 for normal, 40 for slow)
	public Enemy(int x, int eType) {
		super(x, SIZES[eType] * -1, SIZES[eType],SIZES[eType]);
		type = eType;
		counter = 0;
		side = SIZES[type];
		speed = SPEED[type];
		spriteArr = new ArrayList<>();
		hpArr = new ArrayList<>();
		Image tmp;
		BufferedImage bi;
		try {
			for(int i = 1; i<=4;i++) {
				bi = ImageIO.read(new File("Assets/enemy ("+i+").png"));
				tmp = bi.getScaledInstance(side,side, Image.SCALE_SMOOTH);
				
				spriteArr.add(tmp); 
			}
			for(int i = 1; i<=6;i++) {
				bi = ImageIO.read(new File("Assets/hp ("+i+").png"));
				tmp = bi.getScaledInstance(side,side/5, Image.SCALE_SMOOTH);
				
				hpArr.add(tmp); 
			}
			
			curr = spriteArr.get(0);
			bi = ImageIO.read(new File("Assets/enemydead.png"));
			dead = bi.getScaledInstance(side,side, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		hp = 5;
		wasDamaged = 0;
		idx=0;
	}

	// update the position of the obstacle (called from GamePanel frequently)
	public void move() {
		if(hp>0) {
			y = y + speed;
		}
	}
	
	

	// draw the obstacle to the screen (called from GamePanel frequently)
	public void draw(Graphics g) {
		
		if(hp<=0) {
			curr=dead;
		}
		else {
			loop++; //increment time counter, every 15 times draw is called (which is 15 frames) the current image being displayed will change
			if(loop==15) {
				changeFrame();
				loop=0;
			}
		}
		g.drawImage(curr, x,y,null); //draws image
		g.drawImage(hpArr.get(Math.max(hp, 0)),x,y+side+3,null);
		
	}

	public void changeFrame() {
		idx++;//increment index counter, and changes curr image to the next image in the array
		if(idx==spriteArr.size()) {
			idx=0;
		}
		curr = spriteArr.get(idx);
		
	}
	
	public int getDamage() {
		return DAMAGE[type];
	}

	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
}