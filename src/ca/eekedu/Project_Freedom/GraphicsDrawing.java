package ca.eekedu.Project_Freedom;
import static ca.eekedu.Project_Freedom.MainGame.*;
import static ca.eekedu.Project_Freedom.DrawingFrame.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GraphicsDrawing extends JPanel{
	
	private static final long serialVersionUID = -3576483361126717271L;
	GraphicsDrawing(){
		setOpaque(false);
		setBackground(new Color(255, 255, 255, 0));
	}
	
	public void update(){
		repaint();
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(255, 0, 0));
		g2.drawRect(0, 0, SYSTEM_MAXDRAW_WIDTH - 1, SYSTEM_MAXDRAW_HEIGHT - 1);
		if (doDraw){
			g2.setColor(drawColor);
			switch (drawMode) {
				case Line: g2.drawLine(startX, startY, mouseX, mouseY); break;
				case EmptyRect: Draw(g2, 'R', false); break;
				case FilledRect: Draw(g2, 'R', true); break;
				case Oval: Draw(g2, 'O', false); break;
				case FilledOval: Draw(g2, 'O', true); break;
			}
			dir = DIRECTION.None;
			doDraw = false;
			startX = 0; startY = 0;
		}
	}
	
	public void Draw(Graphics2D g2, char type, boolean filled){
		int x = (mouseX < startX)? mouseX : startX;
		int y = (mouseY < startY)? mouseY : startY;
		int width = (mouseX > startX)? mouseX - startX: startX - mouseX;
		int height = (mouseY > startY)? mouseY - startY: startY - mouseY;
		switch (type){
			case 'R': {
				if (filled) g2.fillRect(x, y, width, height); else g2.drawRect(x, y, width, height); break;
			}
			case 'O':{ 
				if (filled) g2.fillOval(x, y, width, height); else g2.drawOval(x, y, width, height); break;
			}
		}
	}

}
