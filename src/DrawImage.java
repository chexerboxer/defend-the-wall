import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DrawImage {
	private Image curr;
	private int length, height;

	public DrawImage(int w,int h,String filePath) {
		BufferedImage bg;
		try {
			bg = ImageIO.read(new File(filePath));
			curr = bg.getScaledInstance(w,h, Image.SCALE_SMOOTH);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPos(int x, int y) {
		length = x;
		height = y;
	}
	
	public void draw(Graphics g) {
		g.drawImage(curr, length, height, null);
	}

}
