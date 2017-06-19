import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JWindow;

public class Project_Freedom extends JWindow{
	
	static Project_Freedom mainGame = null;
	
	int RESOLUTION_WIDTH = 1080;
	int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	
	Project_Freedom(){
		positionWindowAndSize();
		
		setName("Project Freedom");
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
		mainGame = new Project_Freedom();

	}

}
