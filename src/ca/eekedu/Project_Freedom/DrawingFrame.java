package ca.eekedu.Project_Freedom;
import static ca.eekedu.Project_Freedom.MainGame.*;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JFrame;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
import ca.eekedu.Project_Freedom.Drawings.Drawing.DrawObject;

public class DrawingFrame extends JFrame implements Runnable{
	
	private static final long serialVersionUID = 1644654621927813840L;
	
	public static int startX = 0; static int startY = 0;
	public static int mouseX = 0; static int mouseY = 0;
	public static boolean pressed = false;
	public static boolean doDraw = false;
	public static boolean center = false;
	
	public enum DIRECTION { None, NE, NW, SE, SW }
	public static DIRECTION dir = DIRECTION.None;
	
	public volatile boolean running = true;
	
	public static GraphicsDrawing draw = new GraphicsDrawing();
	public static Robot mouseRobot = null;
	public Map<Integer, DrawObject> drawObjects = new HashMap<Integer, DrawObject>();
	public int drawingCount = 0;
	
	DrawingFrame (int width, int height) throws AWTException{
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
		
		mouseRobot = new Robot();
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	mode = GAMEMODE.Game;
		    	checkDrawings();
		    	dispose();
		    }
		});
		
		addWindowFocusListener(new WindowFocusListener() {
			
			public void windowLostFocus(WindowEvent e) {
				mainGame.toFront();
				if (dHelper != null){
					dHelper.toFront();
					toFront();
				}
			}
			public void windowGainedFocus(WindowEvent e) {}
		});
		
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == keybinds.get("CLICK_M")){
					int mouseMod = (keybinds.get("MOUSE_P") - 1) * 2;
					if (mouseMod == 0) mouseMod = 1;
					mouseRobot.mouseRelease(16 / mouseMod);
				} else if (e.getKeyCode() == keybinds.get("C_UP") || e.getKeyCode() == keybinds.get("C_DOWN") ||
						e.getKeyCode() == keybinds.get("C_LEFT") || e.getKeyCode() == keybinds.get("C_RIGHT")){
					if (keysPressed.containsKey(e.getKeyCode())){
						keysPressed.remove(e.getKeyCode(), 0);
					}
				} else if (e.getKeyCode() == keybinds.get("CENTER_B")){
					center = false;
					mouseRobot.mouseMove(mouseX, mouseY);
					mousePos();
				}
			}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					if (!pressed){
						dHelper.dispose();
						checkDrawings();
						mode = GAMEMODE.Game;
						dispose();
					} else {
						pressed = false;
					}
				} else if (e.getKeyCode() == keybinds.get("CLICK_M")){
					int mouseMod = (keybinds.get("MOUSE_P") - 1) * 2;
					if (mouseMod == 0) mouseMod = 1;
					mouseRobot.mousePress(16 / mouseMod);
				} else if (e.getKeyCode() == keybinds.get("COLOR_C")){
					Color prevColor = drawColor;
					drawColor = JColorChooser.showDialog(getParent(), "Choose drawing color", drawColor);
					if (drawColor == null){
						drawColor = prevColor;
					} else {
						getBackColor();
					}
				} else if (e.getKeyCode() == keybinds.get("COLOR_B")){
					drawColor = drawColor.brighter();
					getBackColor();
				}  else if (e.getKeyCode() == keybinds.get("COLOR_D")){
					drawColor = drawColor.darker();
					getBackColor();
				} else if (e.getKeyCode() == keybinds.get("C_UP") || e.getKeyCode() == keybinds.get("C_DOWN") ||
						e.getKeyCode() == keybinds.get("C_LEFT") || e.getKeyCode() == keybinds.get("C_RIGHT")){
					keysPressed.put(e.getKeyCode(), 0);
				} else if (e.getKeyCode() == keybinds.get("CENTER_B") && pressed && !center){
					center = true;
					int newMouseX = (dir == DIRECTION.NE || dir == DIRECTION.SE)? 
						mouseX - (dHelper.getWidth() /2):
						startX - (dHelper.getWidth() / 2);
					int newMouseY = (dir == DIRECTION.SW || dir == DIRECTION.SE)? 
						mouseY -  (dHelper.getHeight() /2): 
						startY -  (dHelper.getHeight() /2);
					mouseRobot.mouseMove(newMouseX, newMouseY);
					mousePos();
				}
			}
		});
		
		addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == keybinds.get("MOUSE_P")){
					Point endPos = new Point(dHelper.getLocation().x + dHelper.getWidth(), dHelper.getLocation().y + dHelper.getHeight());
					DrawObject drawObject = new DrawObject(drawMode, dHelper.getLocation(), endPos, drawColor);
					drawObjects.put(drawingCount, drawObject);
					drawingCount++;
					pressed = false;
					mousePos();
					dHelper.setLocation(1, 1);
					dHelper.setSize(1, 1);
					doDraw = true;
				}
			}
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == keybinds.get("MOUSE_P")){
					pressed = true;
					startX = mouseX; startY = mouseY;
					mousePos();
				}
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
		
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getPreciseWheelRotation() < 0){
					drawMode = drawMode.previous();
				} else if (e.getPreciseWheelRotation() > 0){
					drawMode = drawMode.next();
				}
			}
		});
		mousePos();
	}
	
	@SuppressWarnings("incomplete-switch")
	public static void mousePos(){
		Point p = MouseInfo.getPointerInfo().getLocation();
		if (center){
			startX = p.x - (dHelper.getWidth() / 2);
			startY = p.y - (dHelper.getHeight() / 2);
			mouseX = p.x + (dHelper.getWidth() / 2);
			mouseY = p.y + (dHelper.getWidth() / 2);
		} else {
			mouseX = p.x;
			mouseY = p.y;
		}
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
						dHelper.setLocation(startX, mouseY);
						dHelper.setSize(mouseX - startX, startY - mouseY);
						break;
					}
					case NW: {
						dHelper.setLocation(mouseX, mouseY);
						dHelper.setSize(startX - mouseX, startY - mouseY);
						break;
					}
					case SE: {
						dHelper.setLocation(startX, startY);
						dHelper.setSize(mouseX - startX, mouseY - startY);
						break;
					}
					case SW: {
						dHelper.setLocation(mouseX, startY);
						dHelper.setSize(startX - mouseX, mouseY - startY);
						break;
					}
				}
			}
			dHelper.repaint();
		}
	}

	public void run() {
		while (running){
			draw.update();
		}
	}
	
	public void stop() {
		running = false;
	}
	
	public static void getBackColor(){
		float hsv[] = new float[3];
		Color.RGBtoHSB(255 - drawColor.getRed(), 255 - drawColor.getGreen(), 255 - drawColor.getBlue(), hsv);
	    hsv[0] = (hsv[0] + 180) % 360;
	    Color newColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
	    dHelper.setBackground(newColor);
		dHelper.drawPanel.setBackground(newColor);
	}
	
	public void checkDrawings(){
		if (!drawObjects.isEmpty()){
			DrawObject[] objects = new DrawObject[drawObjects.size()];
			int at = 0;
			for (DrawObject object: drawObjects.values()){
				objects[at] = object;
				at++;
			}
			Drawing drawing = new Drawing("NULL", objects);
			drawingsList.put(0, drawing);
		}
	}

}
