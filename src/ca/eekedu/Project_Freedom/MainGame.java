package ca.eekedu.Project_Freedom;

import static ca.eekedu.Project_Freedom.DrawingFrame.*;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.*;

public class MainGame extends JFrame{
	
	private static final long serialVersionUID = -2787039850560314750L;
	
	public static HashMap<Integer, Integer> keysPressed = new HashMap<Integer, Integer>();
	static Timer update = new Timer(0, null);
	
	static int RESOLUTION_WIDTH = 1080;
	static int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	static int SYSTEM_MAXDRAW_WIDTH = 0;
	static int SYSTEM_MAXDRAW_HEIGHT = 0;
	
	public static MainGame mainGame = null;
	public static GraphicsGame graphics = new GraphicsGame();
	
	public enum GAMEMODE { Menu, Game, Draw }
	public enum DRAWMODE { Line, EmptyRect, FilledRect, Oval, FilledOval;
		private static DRAWMODE[] vals = values();
		public DRAWMODE next(){
			if (this.ordinal() == vals.length - 1){
				return vals[0];
			}
			return vals[(this.ordinal()) + 1];
		}
		public DRAWMODE previous(){
			if (this.ordinal() == 0){
				return vals[vals.length - 1];
			}
			return vals[(this.ordinal()) - 1];
		}
	}
	
	public static KeyBinds keybinds = new KeyBinds();
	public static Drawings drawingsList = new Drawings();
	
	public static GAMEMODE mode = GAMEMODE.Game;
	public static DRAWMODE drawMode = DRAWMODE.Line;
	public static DrawingFrame draw = null;
	public static DrawHelperFrame dHelper = null;
	public static Color drawColor = Color.RED;
	public static Thread drawThread;
	public static int drawingCount = 0;
	
	MainGame() throws AWTException{
		
		setTitle("Project Freedom");
		setUndecorated(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT){
					if (keysPressed.containsKey(e.getKeyCode())){
						keysPressed.remove(e.getKeyCode(), 0);
					}
				} else if (e.getKeyCode() == keybinds.get("CHAR_UP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
						e.getKeyCode() == keybinds.get("CHAR_LEFT") || e.getKeyCode() == keybinds.get("CHAR_RIGHT")){
					if (keysPressed.containsKey(e.getKeyCode())){
						keysPressed.remove(e.getKeyCode(), 0);
					}
				}
			}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT){
					keysPressed.put(e.getKeyCode(), 0);
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					update.stop();
					dispose();
				}else if ( keysPressed.containsKey(new Integer(KeyEvent.VK_SHIFT))){
					if (e.getKeyCode() == keybinds.get("SIZE_UP")){
						RESOLUTION_WIDTH = 1280;
						RESOLUTION_HEIGHT = 800;
						positionWindowAndSize();
						graphics.scale();
					} else if (e.getKeyCode() == keybinds.get("SIZE_DOWN")){
						RESOLUTION_WIDTH = 1080;
						RESOLUTION_HEIGHT = 720;
						positionWindowAndSize();
						graphics.scale();
					}
				}
				if (e.getKeyCode() == keybinds.get("CHAR_UP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
						e.getKeyCode() == keybinds.get("CHAR_LEFT") || e.getKeyCode() == keybinds.get("CHAR_RIGHT")){
					keysPressed.put(e.getKeyCode(), 0);
				} else if (e.getKeyCode() == keybinds.get("DO_DRAW")){
					try {
						dHelper = new DrawHelperFrame();
						draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT);
						mode = GAMEMODE.Draw;
						getBackColor();
						drawThread = new Thread(draw);
						drawThread.start();
					} catch (Exception e1) {
						System.out.println("Ooops Something went wrong!");
						
					}
				} else if (e.getKeyCode() == keybinds.get("INVENT_B")){
					if (graphics.inventory == null){
						graphics.createInventory();
					} else {
						graphics.remove(graphics.inventory);
						graphics.inventory = null;
						revalidate();
						repaint();
					}
				}
			}
		});
		add(graphics);
		setVisible(true);
		positionWindowAndSize();
		
	}
	
	public void positionWindowAndSize(){
		setSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
		
		int posX = (SYSTEM_RES_WIDTH / 2) - (RESOLUTION_WIDTH / 2);
		int posY = (SYSTEM_RES_HEIGHT / 2) - (RESOLUTION_HEIGHT / 2);
		setLocation(posX, posY);
	}
	
	public static void main(String[] args) throws AWTException {
		Dimension system_resolution = Toolkit.getDefaultToolkit().getScreenSize();
		SYSTEM_RES_WIDTH = system_resolution.width;
		SYSTEM_RES_HEIGHT = system_resolution.height;
		Rectangle window = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		SYSTEM_MAXDRAW_WIDTH = window.width;
		SYSTEM_MAXDRAW_HEIGHT = window.height;
		mainGame = new MainGame();
		
		ActionListener updateTimer = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphics.update();
				if (draw != null){
					if (!draw.isVisible()){
						dHelper = null;
						draw.stop();
						draw = null;
					}
				}
				checkControls();
			}
		};
		
		update = new Timer(5, updateTimer); //Smooth update of graphics, reduced lag
		update.start();
		
		while (update.isRunning() || draw != null || dHelper != null){
		}
		
	}
	
	public static void checkControls(){
		for (Integer key: keysPressed.keySet()){
			if (key == keybinds.get("CHAR_UP")) 
				if (mode == GAMEMODE.Game) graphics.y--; 
				else if (pressed) mouseRobot.mouseMove(mouseX, mouseY - 1); mousePos();
			if (key == keybinds.get("CHAR_DOWN")) 
				if (mode == GAMEMODE.Game) graphics.y++; 
				else if (pressed) mouseRobot.mouseMove(mouseX, mouseY + 1); mousePos();
			if (key == keybinds.get("CHAR_LEFT"))
				if (mode == GAMEMODE.Game) graphics.x--; 
				else if (pressed) mouseRobot.mouseMove(mouseX - 1, mouseY); mousePos();
			if (key == keybinds.get("CHAR_RIGHT"))
				if (mode == GAMEMODE.Game) graphics.x++; 
				else if (pressed) mouseRobot.mouseMove(mouseX + 1, mouseY); mousePos();
		}
	}

}
