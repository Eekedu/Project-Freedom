package ca.eekedu.Project_Freedom;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class DrawingFrame extends JFrame{
	
	private static final long serialVersionUID = 1644654621927813840L;
	
	public static int mouseX = 0; static int mouseY = 0;
	public static boolean pressed = false;
	
	public GraphicsDrawing draw = new GraphicsDrawing();
	
	DrawingFrame (int width, int height){
		setTitle("Drawing Frame");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setUndecorated(true);
		setSize(width, height);
		setLocation(0, 0);
		setBackground(new Color(255, 255, 255, 0));
		add(draw);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	MainGame.mode = MainGame.GAMEMODE.Game;
		    	dispose();
		    }
		});
		
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					MainGame.mode = MainGame.GAMEMODE.Game;
					dispose();
				}
			}
		});
		
		addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				pressed = false;
			}
			public void mousePressed(MouseEvent e) {
				pressed = true;
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				mousePos();
			}
			public void mouseDragged(MouseEvent e) {
				mousePos();
			}
		});
		mousePos();
	}
	
	public void mousePos(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		mouseX = p.x;
		mouseY = p.y;
	}

}
