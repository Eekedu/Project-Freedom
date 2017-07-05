package ca.eekedu.Project_Freedom;
import static ca.eekedu.Project_Freedom.MainGame.*;
import static ca.eekedu.Project_Freedom.DrawingFrame.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
public class GraphicsGame extends JPanel {
	int x = 0; int y = 0;
	float scale;
	JScrollPane inventory;
	JPanel inventoryPanel = new JPanel(); 
	GraphicsGame(){
		setForeground(new Color(0, 0, 0));
		scale();
	}
	
	private static final long serialVersionUID = -5342794367022521148L;

	public void update(){
		repaint();
	}
	
	public void createInventory(){
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(null);
		int buttonX = 0; int buttonY = 0;
		for (Drawing drawing: drawingsList.values()){
			if (buttonX >  399){
				buttonY += 100;
				buttonX = 0;
			}
			JButton button = new JButton();
			button.setIcon(new ImageIcon(drawing.screenshot.getScaledInstance(200, 100, RenderingHints.KEY_ANTIALIASING.hashCode())));
			button.setToolTipText(drawing.drawingName);
			button.setContentAreaFilled(false);
			button.setBounds(buttonX, buttonY, 200, 100);
			button.setFocusable(false);
			inventoryPanel.add(button);
			buttonX += 200;
		}
		inventory = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inventory.setViewportView(inventoryPanel);
		inventory.setBounds(RESOLUTION_WIDTH - 400, 24, 400, RESOLUTION_HEIGHT - 24);
		inventory.setFocusable(false);
		setLayout(null);
		add(inventory);
		inventory.requestFocus();
		revalidate();
		repaint();
	}
	
	public void scale(){
		scale = RESOLUTION_WIDTH / RESOLUTION_HEIGHT;
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(scale, scale);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Serif", Font.PLAIN, 18);
		g2.setFont(font);
		g2.setColor(new Color(45, 45, 45));
		g2.fillRoundRect(x, y, 100, 100, 25, 25);
		g2.setColor(new Color(0, 0, 0));
		g2.fillRect(0, 0, getWidth(), 23);
		g2.setColor(new Color(255, 255, 255));
		g2.drawString("Mode: " + mode.toString(), 1, 18);
		if (mode == GAMEMODE.Draw){
			try {
				Point p = new Point(0, 0);
				drawtabString(g2, "\tDraw Mode: " + drawMode.toString() + "\t" + 
					"Mouse: (" + mouseX + "," + mouseY + ")\t" +
					"Color: " + drawColor.getRed() + ", " 
						+ drawColor.getGreen() + ", " 
						+ drawColor.getBlue() + "\t"
						+ "Size: " + ((drawMode == DRAWMODE.Line)? 
								(float)p.distance(dHelper.getWidth(), dHelper.getHeight()): 
									dHelper.getWidth() + ", " + dHelper.getHeight())
					, -50, 18);
			} catch (NullPointerException e){}
		} else {
			if (!drawingsList.isEmpty()){
				drawtabString(g2, "\t# of Drawings: " + drawingsList.size(), -50, 18);
			}
		}
		
	}
	
	private void drawtabString(Graphics2D g2, String text, int x, int y) {
        for (String line : text.split("\t")) {
            g2.drawString(line, x, y);
            x += g2.getFontMetrics().getHeight() * 10.5;
        }
    }

}
