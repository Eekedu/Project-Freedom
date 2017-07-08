package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing.DrawObject;

import javax.swing.*;
import java.awt.*;

import static ca.eekedu.Project_Freedom.DrawingFrame.drawObjects;
import static ca.eekedu.Project_Freedom.MainGame.SYSTEM_MAXDRAW_HEIGHT;
import static ca.eekedu.Project_Freedom.MainGame.SYSTEM_MAXDRAW_WIDTH;

public class GraphicsDrawing extends JPanel {
	
	private static final long serialVersionUID = -3576483361126717271L;

	GraphicsDrawing() {
		setOpaque(false);
		setBackground(new Color(255, 255, 255, 0));
	}
	
	public void update(){
		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawObjects(g);
	}

	public void drawObjects(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(255, 0, 0));
		g2.drawRect(0, 0, SYSTEM_MAXDRAW_WIDTH - 1, SYSTEM_MAXDRAW_HEIGHT - 1);
		for(DrawObject object: drawObjects.values()){
			g2.setColor(object.color);
			switch (object.type) {
				case Line: g2.drawLine(object.position.x, object.position.y, object.endPoints.x, object.endPoints.y); break;
				case EmptyRect: Draw(g2, 'R', object.position, object.endPoints, false); break;
				case FilledRect: Draw(g2, 'R', object.position, object.endPoints, true); break;
				case Oval: Draw(g2, 'O', object.position, object.endPoints, false); break;
				case FilledOval: Draw(g2, 'O', object.position, object.endPoints, true); break;
			}
		}
	}

	public void Draw(Graphics2D g2, char type, Point start, Point end, boolean filled) {
		int startX = start.x; int startY = start.y;
		int mouseX = end.x; int mouseY = end.y;
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
