package ca.eekedu.Project_Freedom;
import static ca.eekedu.Project_Freedom.MainGame.*;
import static ca.eekedu.Project_Freedom.DrawingFrame.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawHelperFrame extends JFrame{
	
	private static final long serialVersionUID = -4898785221895216109L;

	public DrawHelperFrame() {
		add(new DrawPanel());
		setFocusable(false);
		setUndecorated(true);
		setSize(1, 1);
		setLocation(1, 1);
		setBackground(Color.BLACK);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	dispose();
		    }
		});
		
	}
	
	public class DrawPanel extends JPanel {

		private static final long serialVersionUID = 6689874781906933342L;
		
		DrawPanel(){
			setBackground(Color.BLACK);
		}
		
		@SuppressWarnings("incomplete-switch")
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(drawColor);
			if (pressed){
				int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
				if (dir == DIRECTION.NE ||
						dir == DIRECTION.SW){
					x1 = 0; y1 = getHeight();
					x2 = getWidth(); y2 = 0;
				} else if (dir == DIRECTION.NW ||
						dir == DIRECTION.SE){
					x1 = 0; y1 = 0;
					x2 = getWidth(); y2 = getHeight();
				}
				switch (d_mode){
					case Line: g2.drawLine(x1, y1, x2, y2); break;
				}
			}
		}
		
	}

}
