import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class WallHP extends Rectangle{

	public static int GAME_WIDTH;
	public int hp = 100;
	
	//constructor
	public WallHP(int GAME_WIDTH){
		WallHP.GAME_WIDTH = GAME_WIDTH;
	}
	
	public void damage(Enemy e) {
		hp-=e.getDamage();
	}
	
	//draws needed strings and entities onto the screen
	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Consolas",Font.PLAIN,60));
		
		//draws 2 two-digit score counters for players 1 and 2
		g.drawString(String.valueOf(hp), 100, 60);

	}
}