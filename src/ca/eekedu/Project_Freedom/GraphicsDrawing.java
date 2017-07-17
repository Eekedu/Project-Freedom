package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing.DrawObject;

import javax.swing.*;
import java.awt.*;

import static ca.eekedu.Project_Freedom.DrawingFrame.drawObjects;
import static ca.eekedu.Project_Freedom.MainGame.SYSTEM_MAXDRAW_HEIGHT;
import static ca.eekedu.Project_Freedom.MainGame.SYSTEM_MAXDRAW_WIDTH;

public class GraphicsDrawing extends JPanel {

	GraphicsDrawing() {
		setOpaque(false);
		setBackground(new Color(255, 255, 255, 1));
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
				case Line:
				case FreeDraw:
					g2.drawLine(object.position.x, object.position.y, object.size.x, object.size.y);
					break;
				case EmptyRect:
					Draw(g2, 'R', object.position, object.size, false);
					break;
				case FilledRect:
					Draw(g2, 'R', object.position, object.size, true);
					break;
				case Oval:
					Draw(g2, 'O', object.position, object.size, false);
					break;
				case FilledOval:
					Draw(g2, 'O', object.position, object.size, true);
					break;
			}
		}
	}

	public void Draw(Graphics2D g2, char type, Point start, Point size, boolean filled) {
		switch (type){
			case 'R': {
				if (filled) g2.fillRect(start.x, start.y, size.x, size.y);
				else g2.drawRect(start.x, start.y, size.x, size.y);
				break;
			}
			case 'O':{
				if (filled) g2.fillOval(start.x, start.y, size.x, size.y);
				else g2.drawOval(start.x, start.y, size.x, size.y);
				break;
			}
		}
	}

}
