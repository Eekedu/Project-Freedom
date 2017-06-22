package ca.eekedu.Project_Freedom;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GraphicsGame extends JPanel {
	int x = 0; int y = 0;
	GraphicsGame(){
		setForeground(new Color(0, 0, 0));
	}
	
	private static final long serialVersionUID = -5342794367022521148L;

	public void update(){
		repaint();
	}
	

	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Serif", Font.PLAIN, 18);
		g2.setFont(font);
		g2.setColor(new Color(0, 0, 45));
		g2.fillRoundRect(x, y, 100, 100, 25, 25);
		g2.setColor(new Color(0, 0, 0));
		g2.drawString("Mode: " + ((MainGame.mode == MainGame.GAMEMODE.Game)? "Game" : "Draw"), 1, 19);
		if (MainGame.mode == MainGame.GAMEMODE.Draw){
			try {
				g2.drawString("Mouse: (" + MainGame.draw.mouseX + "," + MainGame.draw.mouseY + ")", 200, 19);
			} catch (NullPointerException e){}
		}
		
	}

}
