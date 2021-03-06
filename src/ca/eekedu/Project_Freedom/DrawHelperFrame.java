package ca.eekedu.Project_Freedom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

import static ca.eekedu.Project_Freedom.DrawingFrame.*;
import static ca.eekedu.Project_Freedom.MainGame.drawColor;
import static ca.eekedu.Project_Freedom.MainGame.drawMode;

public class DrawHelperFrame extends JFrame implements Runnable {
	
	DrawPanel drawPanel = new DrawPanel();

	public DrawHelperFrame() {
		add(drawPanel);
        setTitle("I'm the helper window :3");
        setFocusable(false);
		setUndecorated(true);
		setOpacity(0.5F);
		setSize(1, 1);
		setLocation(1, 1);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	dispose();
		    }
		});
		
	}

    public void run() {
    }

	public class DrawPanel extends JPanel {

		private static final long serialVersionUID = 6689874781906933342L;

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(drawColor);
			if (pressed){
				int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
				if (dir.equals(DIRECTION.NE) ||
						dir.equals(DIRECTION.SW)) {
					x1 = 0; y1 = getHeight();
					x2 = getWidth(); y2 = 0;
				} else if (dir.equals(DIRECTION.NW) ||
						dir.equals(DIRECTION.SE)) {
					x1 = 0; y1 = 0;
					x2 = getWidth(); y2 = getHeight();
				}
                switch (drawMode) {
                    case Line: g2.drawLine(x1, y1, x2, y2); break;
					case EmptyRect: g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1); break;
					case FilledRect: g2.fillRect(0, 0, getWidth(), getHeight()); break;
					case Oval: g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1); break;
					case FilledOval: g2.fillOval(0,0 , getWidth() - 1, getHeight() - 1);
				}
			}
		}

    }

}
