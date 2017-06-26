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
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class DrawingFrame extends JFrame{
	
	private static final long serialVersionUID = 1644654621927813840L;
	
	public static int startX = 0; static int startY = 0;
	public static int mouseX = 0; static int mouseY = 0;
	public static boolean pressed = false;
	public static boolean doDraw = false;
	
	public enum DIRECTION { None, NE, NW, SE, SW }
	public static DIRECTION dir = DIRECTION.None;
	
	public static GraphicsDrawing draw = new GraphicsDrawing();
	
	DrawingFrame (int width, int height){
		setTitle("Drawing Frame");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setAutoRequestFocus(true);
		setUndecorated(true);
		setSize(width, height);
		setLocation(0, 0);
		setBackground(new Color(255, 255, 255, 0));
		add(draw);
		setVisible(true);
		toFront();
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	MainGame.mode = MainGame.GAMEMODE.Game;
		    	dispose();
		    }
		});
		
		addWindowFocusListener(new WindowFocusListener() {
			
			public void windowLostFocus(WindowEvent e) {
				MainGame.mainGame.toFront();
				if (MainGame.dHelper != null){
					MainGame.dHelper.toFront();
					toFront();
				}
			}
			public void windowGainedFocus(WindowEvent e) {}
		});
		
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT){
					pressed = false;
					mousePos();
					dir = DIRECTION.None;
					MainGame.dHelper.setLocation(1, 1);
					MainGame.dHelper.setSize(1, 1);
					doDraw = true;
				}
			}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					MainGame.dHelper.dispose();
					MainGame.mode = MainGame.GAMEMODE.Game;
					dispose();
				} else if (e.getKeyCode() == KeyEvent.VK_SHIFT){
					pressed = true;
					startX = mouseX; startY = mouseY;
					mousePos();
				}
			}
		});
		
		addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				pressed = false;
				mousePos();
				dir = DIRECTION.None;
				MainGame.dHelper.setLocation(1, 1);
				MainGame.dHelper.setSize(1, 1);
				doDraw = true;
			}
			public void mousePressed(MouseEvent e) {
				pressed = true;
				startX = mouseX; startY = mouseY;
				mousePos();
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
	
	@SuppressWarnings("incomplete-switch")
	public void mousePos(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		mouseX = p.x;
		mouseY = p.y;
		if (pressed){
			if (mouseY < startY){
				if (mouseX < startX){
					dir = DIRECTION.NW;
				} else {
					dir = DIRECTION.NE;
				}
			} else if (mouseY > startY){
				if (mouseX < startX){
					dir = DIRECTION.SW;
				} else {
					dir = DIRECTION.SE;
				}
			} else {
				dir = DIRECTION.None;
			}
			if (dir != DIRECTION.None){
				switch (dir){
					case NE: {
						MainGame.dHelper.setLocation(startX, mouseY);
						MainGame.dHelper.setSize(mouseX - startX, startY - mouseY);
						break;
					}
					case NW: {
						MainGame.dHelper.setLocation(mouseX, mouseY);
						MainGame.dHelper.setSize(startX - mouseX, startY - mouseY);
						break;
					}
					case SE: {
						MainGame.dHelper.setLocation(startX, startY);
						MainGame.dHelper.setSize(mouseX - startX, mouseY - startY);
						break;
					}
					case SW: {
						MainGame.dHelper.setLocation(mouseX, startY);
						MainGame.dHelper.setSize(startX - mouseX, mouseY - startY);
						break;
					}
				}
			}
			MainGame.dHelper.repaint();
		}
	}

}
