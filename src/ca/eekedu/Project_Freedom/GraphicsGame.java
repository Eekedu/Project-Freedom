package ca.eekedu.Project_Freedom;
import static ca.eekedu.Project_Freedom.MainGame.*;
import static ca.eekedu.Project_Freedom.DrawingFrame.*;

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
		g2.setColor(new Color(45, 45, 45));
		g2.fillRoundRect(x, y, 100, 100, 25, 25);
		g2.setColor(new Color(0, 0, 0));
		g2.fillRect(0, 0, getWidth(), 23);
		g2.setColor(new Color(255, 255, 255));
		g2.drawString("Mode: " + mode.toString(), 1, 18);
		if (mode == GAMEMODE.Draw){
			try {
				drawtabString(g2, "\tDraw Mode: " + drawMode.toString() + "\t" + 
					"Mouse: (" + mouseX + "," + mouseY + ")\t" +
					"Color(RGB): " + drawColor.getRed() + ", " 
						+ drawColor.getGreen() + ", " 
						+ drawColor.getBlue()
					, -50, 18);
			} catch (NullPointerException e){}
		}
		
	}
	
	private void drawtabString(Graphics2D g2, String text, int x, int y) {
        for (String line : text.split("\t")) {
            g2.drawString(line, x, y);
            x += g2.getFontMetrics().getHeight() * 10.5;
        }
    }

}
