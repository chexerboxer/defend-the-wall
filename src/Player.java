import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Player extends Rectangle {
	private int yVelocity;
	private int xVelocity;
	public static final int PLAYER_SIZE = 20;
	public boolean isMidair;
	private ArrayList<Point> path;
	private static final int SPEED = 60;
	public BufferedImage tit;
	public Image tmp;
	
	public Player(int x, int y) {
		super(x, y, PLAYER_SIZE, PLAYER_SIZE);
		path = new ArrayList<>();
		yVelocity = 0;
		xVelocity = 0;
		isMidair = false;
		path.add(new Point(x, y));
		try {
			tit = ImageIO.read(new File("Assets/levi.png"));
			tmp = tit.getScaledInstance(PLAYER_SIZE,PLAYER_SIZE, Image.SCALE_SMOOTH);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void move() {
		x = x + xVelocity;
		y = y + yVelocity;
		if (isMidair) {
			path.add(new Point(x, y));
		}
	}

	public void shoot(int mX, int mY) {
		double dX = mX - x;
		double dY = mY - y;
		if (dY == 0) {
			dY = 0.000001;
		}
		double m = dX / dY;
		yVelocity = (int) Math.abs(SPEED / (Math.sqrt(m * m + 1)));
		xVelocity = (int) Math.abs(m * yVelocity);
		if (yVelocity == 0) {
			xVelocity = SPEED;
		}

		if (mX < x) {
			xVelocity *= -1;
		}
		if (mY < y) {
			yVelocity *= -1;
		}
		int s = (int) (Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity));
//		System.out.println("dX=" + dX + " dY=" + dY + " m=" + m + " yV=" + yVelocity + " yX=" + xVelocity + " s=" + s);

		if (!isMidair) {
			isMidair = true;
			path.clear();
			path.add(new Point(x, y));
		}
	}

	public int getVY() {
		return yVelocity;
	}

	public void setVY(int v) {
		yVelocity = v;
	}

	public int getVX() {
		return xVelocity;
	}

	public void setVX(int v) {
		xVelocity = v;
	}

	// called frequently from the GamePanel class
	// draws the current location of the ball to the screen
	public void draw(Graphics g) {
		g.setColor(Color.PINK);
//		g.fillOval(x, y, HOOK_SIZE, HOOK_SIZE);
		g.drawImage(tmp, x,y,null);
		g.setColor(Color.WHITE);
		for (int i = 1; i < path.size(); i++) {
			Point p0 = path.get(i - 1);
			Point p = path.get(i);
			g.drawLine(p0.x, p0.y, p.x, p.y);
		}

		g.setColor(Color.RED);
		for (int i = 0; i < path.size(); i++) {
			Point p = path.get(i);
			g.drawOval(p.x, p.y, 2, 2);
			
		}
	}

	public Line2D getLastMovement() {
		Point p0 = path.get(path.size() - 2);
		Point p1 = path.get(path.size() - 1);
		return new Line2D.Double(p0.x, p0.y, p1.x, p1.y);
	}
}
