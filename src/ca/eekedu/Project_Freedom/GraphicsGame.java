package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
import ca.eekedu.Project_Freedom.MainGame.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import static ca.eekedu.Project_Freedom.DrawingFrame.*;
import static ca.eekedu.Project_Freedom.MainGame.*;

public class GraphicsGame extends JPanel {
	private static final long serialVersionUID = -5342794367022521148L;
	int x = 0;
	int y = 0;
	float scale;
	JScrollPane inventory;
	JPanel inventoryPanel = new JPanel();

	GraphicsGame(){
		setForeground(new Color(0, 0, 0));
		scale();
	}

	public void update(){
		repaint();
	}
	
	public void createInventory(){
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(null);
		int buttonX = 0; int buttonY = 0;
		Iterator<Integer> keyIt = drawingsList.keySet().iterator();
		for (Drawing drawing: drawingsList.values()){
			if (buttonX >  399){
				buttonY += 100;
				buttonX = 0;
			}
			DrawingInvent panel = new DrawingInvent(drawing.screenshot.getScaledInstance(200, 100, RenderingHints.KEY_ANTIALIASING.hashCode()));
			panel.setToolTipText(drawing.drawingName);
			panel.setBounds(buttonX, buttonY, 200, 100);
			panel.setLayout(new BorderLayout());
			JButton button = new JButton();
			button.setContentAreaFilled(false);
			button.setText("EDIT");
			button.setFocusable(false);
			ButtonClick btnCL = new ButtonClick();
			btnCL.drawing = drawing;
			btnCL.pos = keyIt.next();
			button.addActionListener(btnCL);

			panel.addMouseListener(new myAdapter(panel, button));

			inventoryPanel.add(panel);
			buttonX += 200;
		}
		inventory = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
		if (RESOLUTION_HEIGHT != 0) {
			scale = RESOLUTION_WIDTH / RESOLUTION_HEIGHT;
		} else {
			scale = 1;
		}
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

	public class DrawingInvent extends JPanel {
		Image myImage;

		DrawingInvent(Image image) {
			myImage = image;
		}

		protected void paintComponent(Graphics g) {
			g.drawImage(myImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	public class myAdapter extends MouseAdapter {
		JButton button;
		JPanel panel;

		myAdapter(JPanel panel, JButton button) {
			super();
			this.panel = panel;
			this.button = button;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			panel.add(button, BorderLayout.SOUTH);
			revalidate();
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			panel.remove(button);
			revalidate();
			repaint();
		}
	}

	public class ButtonClick implements ActionListener{
		Drawing drawing;
		int pos;
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				remove(inventory);
				inventory = null;
				mainGame.revalidate();
				mainGame.repaint();
				dHelper = new DrawHelperFrame();
				MainGame.draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT, drawing.objects);
				MainGame.draw.curDrawingIndex = pos;
				drawing = null;
				mode = GAMEMODE.Draw;
				getBackColor();
				drawThread = new Thread(MainGame.draw);
				drawThread.start();
				
			} catch (Exception e1) {
				System.out.println("Something went wrong!");
			}
		}
	}

}
