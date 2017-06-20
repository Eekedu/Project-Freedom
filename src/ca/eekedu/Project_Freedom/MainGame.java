package ca.eekedu.Project_Freedom;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;


import javax.swing.JFrame;

public class MainGame extends JFrame{
	
	private static final long serialVersionUID = -2787039850560314750L;

	public static MainGame mainGame = null;
	public static GraphicsGame graphics = new GraphicsGame();
	public boolean enabled = true;
	
	Map<Integer, Integer> keysPressed = new HashMap<Integer, Integer>();
	
	static int RESOLUTION_WIDTH = 1080;
	static int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	
	MainGame(){
		positionWindowAndSize();
		
		setTitle("Project Freedom");
		setUndecorated(true);
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
					dispose();
					enabled = false;
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
				}  else if (e.getKeyCode() == KeyEvent.VK_UP){
					graphics.y-=10;
				}  else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					graphics.y+=10;
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT){
					graphics.x-=10;
				}  else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
					graphics.x+=10;
				}
			}
		});
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(new Color(255,255,255,0));
		add(graphics);
		setVisible(true);
		
		ActionListener firstTim = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				remove(graphics);
			}
		};
		
		ActionListener secondTim = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				add(graphics);
				
			}
		};
		
		
		while (enabled){
			graphics.update();
		}
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
		mainGame = new MainGame();

	}
	

}
