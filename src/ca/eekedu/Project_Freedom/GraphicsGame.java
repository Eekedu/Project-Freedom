package ca.eekedu.Project_Freedom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GraphicsGame extends JPanel {
	int x = 0; int y = 0;
	GraphicsGame(){
		setOpaque(false);
	}
	
	private static final long serialVersionUID = -5342794367022521148L;

	public void update(){
		repaint();
	}
	

	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
		g2.setColor(new Color(255, 255, 255, 55));
		g2.drawRect(0, 0, MainGame.RESOLUTION_WIDTH - 1, MainGame.RESOLUTION_HEIGHT - 1);
		//g.translate(x, y);
		g2.fillRoundRect(x, y, 100, 100, 25, 25);
		
	}

}
