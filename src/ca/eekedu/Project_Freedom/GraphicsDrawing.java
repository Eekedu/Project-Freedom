package ca.eekedu.Project_Freedom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GraphicsDrawing extends JPanel{
	
	private static final long serialVersionUID = -3576483361126717271L;

	GraphicsDrawing(){
		setBackground(new Color(255, 255, 255, 0));
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(255, 0, 0));
		g2.drawRect(0, 0, MainGame.SYSTEM_MAXDRAW_WIDTH - 1, MainGame.SYSTEM_MAXDRAW_HEIGHT - 1);
	}

}
