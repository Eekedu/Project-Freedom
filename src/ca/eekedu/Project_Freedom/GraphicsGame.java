package ca.eekedu.Project_Freedom;

import javax.swing.JPanel;

public class GraphicsGame extends JPanel {
	
	private static final long serialVersionUID = -5342794367022521148L;

	protected void paintComponent(java.awt.Graphics g){
		super.paintComponent(g);
		g.drawLine(0, 0, 100, 100);
	}

}
