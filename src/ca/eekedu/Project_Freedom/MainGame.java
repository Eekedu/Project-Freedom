package ca.eekedu.Project_Freedom;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JWindow;

public class MainGame extends JWindow{
	
	private static final long serialVersionUID = -2787039850560314750L;

	static MainGame mainGame = null;
	static GraphicsGame graphics = new GraphicsGame();
	
	int RESOLUTION_WIDTH = 1080;
	int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	
	MainGame(){
		positionWindowAndSize();
		
		setName("Project Freedom");
		setOpacity(0.75F);
		addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyReleased(KeyEvent e) {
				
				
			}
			
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.out.println("YESH");
					setVisible(false);
				}
				
			}
		});
		add(graphics);
		setVisible(true);
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
