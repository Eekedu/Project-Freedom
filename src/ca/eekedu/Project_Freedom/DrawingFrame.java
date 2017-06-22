package ca.eekedu.Project_Freedom;

import java.awt.Color;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class DrawingFrame extends JFrame{
	
	private static final long serialVersionUID = 1644654621927813840L;

	DrawingFrame (int width, int height){
		setTitle("Drawing Frame");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		setSize(width, height);
		setLocation(0, 0);
		setBackground(new Color(255, 255, 255, 0));
		GraphicsDrawing draw = new GraphicsDrawing();
		add(draw);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	MainGame.mode = MainGame.GAMEMODE.Game;
		    	dispose();
		    }
		});
	}

}
