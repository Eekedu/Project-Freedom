package ca.eekedu.Project_Freedom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
		g2.setColor(new Color(0, 0, 0));
		g2.drawString("Mode: " + ((MainGame.mode == MainGame.GAMEMODE.Game)? "Game" : "Draw"), 0, 0);
		g2.fillRoundRect(x, y, 100, 100, 25, 25);
		
	}

}
