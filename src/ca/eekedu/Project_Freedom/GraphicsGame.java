package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
import ca.eekedu.Project_Freedom.MainGame.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

import static ca.eekedu.Project_Freedom.DrawingFrame.mouseX;
import static ca.eekedu.Project_Freedom.DrawingFrame.mouseY;
import static ca.eekedu.Project_Freedom.MainGame.*;

public class GraphicsGame extends JPanel {

	float scale;
	JScrollPane inventory;
	JPanel inventoryPanel = new JPanel();

	GraphicsGame(){
		setForeground(new Color(0, 0, 0));
	}

	public void update(){
		repaint();
	}
	
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
	
	public void scale(){
		if (RESOLUTION_HEIGHT != 0) {
			scale = (RESOLUTION_WIDTH / RESOLUTION_HEIGHT);
		} else {
			scale = 1;
		}
	}

	protected void render(Graphics2D g, SimulationBody body) {
		// draw the object
		body.render(g, scale, body.color);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(scale, scale);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.PLAIN, 18);
		g2.setFont(font);

		AffineTransform t = g2.getTransform();
		g2.translate(-charBody.getTransform().getTranslationX() + (RESOLUTION_WIDTH / 2), 0);
		for (int i = 0; i < world.getBodyCount(); i++) {
			SimulationBody body = (SimulationBody) world.getBody(i);
			render(g2, body);
		}
		g2.setTransform(t);

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
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
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
            Graphics2D g2 = (Graphics2D) g;
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(myImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

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

	public class ButtonClick implements ActionListener{
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
