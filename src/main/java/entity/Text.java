package entity;
import java.awt.*;
public class Text extends Entity {
	private String s;
	private long duration;
	private long start;	
	public Text(double x, double y, String s, long duration){
		this.x = x;
		this.y = y;
		this.s = s;
		this.duration = duration;
		start = System.nanoTime();		
	}
	
	public void update(){	
		if(finished)return;
		long elapsed = (System.nanoTime() - start)/1000000;
		if(elapsed > duration){
			finished = true;	
		}		
	}
	
	public void draw(Graphics2D g){
		if(finished)return;
		g.setFont(new Font("Century Gothic", Font.PLAIN,12));
		long elapsed = (System.nanoTime() - start)/1000000;
		int alpha = (int)(255*Math.sin(3.14 * elapsed/duration));
		if(alpha > 255) alpha = 255;
		g.setColor( new Color (255, 255, 255, alpha));
		int length = (int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s, (int)(x - (length/2)), (int)y);
	}	
}
