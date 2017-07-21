package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
import ca.eekedu.Project_Freedom.MainGame.*;
import org.dyn4j.geometry.Transform;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;

import static ca.eekedu.Project_Freedom.DrawingFrame.mouseX;
import static ca.eekedu.Project_Freedom.DrawingFrame.mouseY;
import static ca.eekedu.Project_Freedom.MainGame.*;

/**
 * Graphics Panel for the main game
 */
public class GraphicsGame extends JPanel {

	/**
	 * Variables
	 */
	float scale;
	JScrollPane inventory;
	JPanel inventoryPanel = new JPanel();
	double worldX = 0, worldY = 0;
	BufferedImage backdrop, ground, wheel;

	/**
	 * Main constructor
	 * Read the image for the background
	 */
	GraphicsGame(){
		setForeground(new Color(0, 0, 0));
		try {
			backdrop = ImageIO.read(new File("images/background/city.png"));
			ground = ImageIO.read(new File("images/background/ground.png"));
			wheel = ImageIO.read(new File("images/sprites/wheel.png"));
		} catch (Exception e){
		}
	}

	/**
	 * Update graphics
	 */
	public void update(){
		repaint();
	}

	/**
	 * Create Inventory and fill it with the list of Drawings if any are created
	 */
	public void createInventory(){
		inventoryPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.scale(scale, scale);
				Font font = new Font("Serif", Font.PLAIN, 18);
				g2.setFont(font);
				g2.fillRect(0, 0, 419, 22);
				if (drawingsList.isEmpty()) {
					g2.drawLine(1, 23, getWidth() - 2, getHeight() - 2);
					g2.drawLine(getWidth() - 2, 23, 1, getHeight() - 2);
				}
				g2.setColor(Color.WHITE);
				g2.drawString("Inventory:", 10, 16);
			}
		};
		inventoryPanel.setLayout(null);
		if (!drawingsList.isEmpty()) {
			inventoryPanel.setPreferredSize(new Dimension(420, ((drawingsList.size() + 1) * 50) + 23));
			int buttonX = 1;
			int buttonY = 23;
			Iterator<Integer> keyIt = drawingsList.keySet().iterator();
			for (Drawing drawing : drawingsList.values()) {
				if (buttonX > 399) {
					buttonY += 100;
					buttonX = 1;
				}
				int id = keyIt.next();

				DrawingInvent panel = new DrawingInvent(drawing.screenshot.getScaledInstance(200, 100, RenderingHints.KEY_ANTIALIASING.hashCode()));
				panel.setToolTipText(drawing.drawingName);
				panel.setBounds(buttonX, buttonY, 200, 100);
				panel.setLayout(new BorderLayout());

				JButton buttonE = new JButton("EDIT");
				buttonE.setFocusable(false);
				buttonE.addActionListener(new ButtonClick(id, 'e'));
				buttonE.setBackground(Color.WHITE);

				JButton buttonD = new JButton("DELETE");
				buttonD.setFocusable(false);
				buttonD.addActionListener(new ButtonClick(id, 'd'));
				buttonD.setBackground(Color.WHITE);

				JPanel holder = new JPanel();
				holder.add(buttonE);
				holder.add(buttonD);
				holder.setBackground(new Color(0, 0, 0, 0));
				panel.addMouseListener(new myAdapter(panel, holder));

				inventoryPanel.add(panel);
				inventoryPanel.addMouseListener(new myAdapter(panel, holder, 1));
				buttonX += 200;
			}
		}
		inventory = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inventory.setViewportView(inventoryPanel);
		inventory.setBounds(RESOLUTION_WIDTH - 420, 24, 420, RESOLUTION_HEIGHT - 24);
		inventory.setFocusable(false);
		inventory.setBackground(new Color(1F, 1F, 1F, 0.5F));
		inventoryPanel.setBackground(new Color(1F, 1F, 1F, 0.5F));
		setLayout(null);
		add(inventory);
		inventory.revalidate();
		inventory.repaint();
		revalidate();
		repaint();
	}

	/**
	 * Scale the graphics context by the resolution
	 */
	public void scale(){
		if (RESOLUTION_HEIGHT != 0) {
			scale = (RESOLUTION_WIDTH / RESOLUTION_HEIGHT);
		} else {
			scale = 1;
		}
	}

	/**
	 *  Method to render current looped-into World body
	 * @param g the Graphics context
	 * @param body the Body to render
	 */
	protected void render(Graphics2D g, SimulationBody body) {
		// draw the object
		body.render(g, scale, body.color);
	}

	/**
	 * Overridden paintComponent method in JPanel class
	 * @param g the graphics context
	 */
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(scale, scale);
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Serif", Font.PLAIN, 18);
		g2.setFont(font);
		AffineTransform t = g2.getTransform();
		double transY = 0;
		Transform charBodyTrans = charBody.getTransform();
		Transform floorTrans = floor.getTransform();

		worldX = charBodyTrans.getTranslationX() - floorTrans.getTranslationX();
		worldY = (charBodyTrans.getTranslationY() - floorTrans.getTranslationY()) - 25;

		if (charBodyTrans.getTranslationY() < RESOLUTION_HEIGHT / 2) {
			transY = -charBodyTrans.getTranslationY() + (RESOLUTION_HEIGHT / 2);
		}
		g2.translate(-charBodyTrans.getTranslationX() + (RESOLUTION_WIDTH / 2), transY);
		if (ground != null) {
			drawBackground(g2, backdrop, 40.0, 0.50);
			drawBackground(g2, backdrop, 10.0, 0.75);
			drawBackground(g2, ground, -72.0, 0.0);
		}
		for (int i = 0; i < world.getBodyCount(); i++) {
			SimulationBody body = (SimulationBody) world.getBody(i);
			render(g2, body);
		}
		g2.setTransform(t);

		g2.setColor(new Color(0, 0, 0));
		g2.fillRect(0, 0, getWidth(), 23);
		g2.setColor(new Color(255, 255, 255));
		g2.drawString("Mode: " + mode.toString(), 1, 18);
		if (mode.equals(GAMEMODE.Draw)) {
			try {
				Point p = new Point(0, 0);
				drawtabString(g2, "\tDraw Mode: " + drawMode.toString() + "\t" +
								"Mouse: (" + mouseX + "," + mouseY + ")\t" +
								"Color: " + drawColor.getRed() + ", "
								+ drawColor.getGreen() + ", "
								+ drawColor.getBlue() + "\t"
								+ "Size: " + ((drawMode.equals(DRAWMODE.Line)) ?
								(float)p.distance(dHelper.getWidth(), dHelper.getHeight()):
								(!drawMode.equals(DRAWMODE.FreeDraw)) ? dHelper.getWidth() + ", " + dHelper.getHeight() : "")
						, -50, 18);
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
		} else {
			DecimalFormat df = new DecimalFormat("#0.0");
			String wX = df.format(worldX / 10);
			String wY = df.format((-(worldY + 100) / 10 < 0.0) ? 0.0 : -(worldY + 100) / 10);
			drawtabString(g2, "\t# of Drawings: " + drawingsList.size() + "\t" +
					"Position(x,y): (" + wX + "," + wY + ")", -50, 18);
		}

	}

	/**
	 * Draw a never ending background
	 * parallax scrolling supported with speedDec
	 *
	 * @param g2       The Graphics2D context
	 * @param image    The image to draw
	 * @param pos      Position from the bottom
	 * @param speedDec The deceleration from the characters point of view 0.0 to 1.0
	 */
	public void drawBackground(Graphics2D g2, BufferedImage image, double pos, double speedDec) {
		AffineTransform before = g2.getTransform();
		g2.translate(worldX * speedDec, 0.0);
		double w = image.getWidth(), h = image.getHeight(), hW = -w / 2.0;
		double hH = ((double) RESOLUTION_HEIGHT - h) - pos;

		AffineTransform xT = new AffineTransform();
		xT.translate(hW + (Math.round((worldX - (worldX * speedDec)) / w) * w) - w, hH);

		g2.drawImage(image, xT, null);
		xT.translate(w, 0.0);
		g2.drawImage(image, xT, null);
		xT.translate(w, 0.0);
		g2.drawImage(image, xT, null);
		g2.setTransform(before);
	}


	private void drawtabString(Graphics2D g2, String text, int x, int y) {
		for (String line : text.split("\t")) {
			g2.drawString(line, x, y);
			x += g2.getFontMetrics().getHeight() * 10.5;
		}
	}

	/**
	 * Custom class for the InventoryPanel
	 * Holds the Snapshot of the drawing within each instance
	 */
	public class DrawingInvent extends JPanel {
		Image myImage;

		DrawingInvent(Image image) {
			myImage = image;
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(myImage, 0, 0, getWidth(), getHeight(), this);
			g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	/**
	 * Custom MouseAdapter for InventoryPanel
	 * Is able to add and remove buttons depending on if mouse is hovering over it
	 */
	public class myAdapter extends MouseAdapter {
		JPanel panel;
		JPanel holder;
		int type;

		myAdapter(JPanel panel, JPanel holder) {
			this(panel, holder, 0);
		}

		myAdapter(JPanel panel, JPanel holder, int type) {
			super();
			this.panel = panel;
			this.holder = holder;
			this.type = type;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (type == 0) {
				panel.add(holder, BorderLayout.SOUTH);
			} else {
				panel.remove(holder);
			}
			revalidate();
			repaint();
		}
	}

	/**
	 * Custom ActionListener class for Edit and Delete Buttons
	 */
	public class ButtonClick implements ActionListener {
		int pos;
		char type;

		ButtonClick(int pos, char type) {
			super();
			this.pos = pos;
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				switch (type) {
					case 'e': {
						remove(inventory);
						revalidate();
						repaint();
						doDraw(pos);
						break;
					}
					case 'd': {
						drawingsList.remove(pos);
						remove(inventory);
						inventory = null;
						revalidate();
						repaint();
						createInventory();
						break;
					}
				}

			} catch (Exception e1) {
				System.out.println("Something went wrong!");
			}
		}
	}

}
