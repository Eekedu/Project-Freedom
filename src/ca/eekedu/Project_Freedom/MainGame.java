package ca.eekedu.Project_Freedom;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class MainGame extends JFrame{
	
	private static final long serialVersionUID = -2787039850560314750L;

	public static MainGame mainGame = null;
	public static GraphicsGame graphics = new GraphicsGame();
	
	Map<Integer, Integer> keysPressed = new HashMap<Integer, Integer>();
	static Timer update = new Timer(0, null);
	
	static int RESOLUTION_WIDTH = 1080;
	static int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	static int SYSTEM_MAXDRAW_WIDTH = 0;
	static int SYSTEM_MAXDRAW_HEIGHT = 0;
	
	public enum GAMEMODE { Game, Draw }
	public static GAMEMODE mode = GAMEMODE.Game;
	public static DrawingFrame draw = null;
	MainGame(){
		
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
					if (e.getKeyCode() == KeyEvent.VK_UP){
						RESOLUTION_WIDTH = 1280;
						RESOLUTION_HEIGHT = 800;
						positionWindowAndSize();
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN){
						RESOLUTION_WIDTH = 1080;
						RESOLUTION_HEIGHT = 720;
						positionWindowAndSize();
					}
				}  else if (e.getKeyCode() == KeyEvent.VK_W){
					graphics.y-=10;
				}  else if (e.getKeyCode() == KeyEvent.VK_S){
					graphics.y+=10;
				}
				if (e.getKeyCode() == KeyEvent.VK_A){
					graphics.x-=10;
				}  else if (e.getKeyCode() == KeyEvent.VK_D){
					graphics.x+=10;
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE){
					draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT);
					mode = GAMEMODE.Draw;
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
	

	public static void main(String[] args) {
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
						draw = null;
					}
				}
			}
		};
		
		update = new Timer(5, updateTimer); //Smooth update of graphics, reduced lag
		update.start();
		
		while (update.isRunning() || draw != null){
		}
		
	}
	

}
