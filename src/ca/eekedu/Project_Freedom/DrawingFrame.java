package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
import ca.eekedu.Project_Freedom.Drawings.Drawing.DrawObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static ca.eekedu.Project_Freedom.MainGame.*;

public class DrawingFrame extends JFrame implements Runnable{

    public static int startX = 0;
    public static int mouseX = 0;
	public static int startY = 0;
	public static int mouseY = 0;
	public static boolean pressed = false;
    public static boolean center = false;
	public static boolean colorPick = false;
	public static DIRECTION dir = DIRECTION.None;
    public static GraphicsDrawing drawer = new GraphicsDrawing();
    public static Robot mouseRobot = null;
	public static HashMap<Integer, DrawObject> drawObjects = new HashMap<Integer, DrawObject>();
    public boolean doEdit = false;
	public int curDrawingIndex = 0;
    Cursor customCurs;

    DrawingFrame (int width, int height) throws Exception{
		setTitle("Drawing Frame");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setAutoRequestFocus(true);
		setUndecorated(true);
		setSize(width, height);
		setLocation(0, 0);
		setBackground(new Color(255, 255, 255, 1));
		if (!System.getProperty("os.name").contains("Windows")) {
			setOpacity(0.5F);
		}
		add(drawer);
        setVisible(true);
		toFront();
        drawObjects = new HashMap<>();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Image cursImg = toolkit.getImage("images/cursor/curs.png");
		Point hotSpot = new Point(0, 0);
		customCurs = toolkit.createCustomCursor(cursImg, hotSpot, "Cursor");
		setCursor(customCurs);

		mouseRobot = new Robot();

		addWindowListener(new WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	saveDrawing();
		    }
		});

		addWindowFocusListener(new WindowFocusListener() {

			public void windowLostFocus(WindowEvent e) {
				if (!colorPick) {
					mainGame.toFront();
					if (dHelper != null) {
						dHelper.toFront();
						toFront();
					}
				}
			}
			public void windowGainedFocus(WindowEvent e) {}
		});

		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == keybinds.get("CLICK_M")){
					int mouseMod = (keybinds.get("MOUSE_P") - 1) * 2;
					if (mouseMod == 0) mouseMod = 1;
					mouseRobot.mouseRelease(16 / mouseMod);
				} else if (e.getKeyCode() == keybinds.get("CHAR_UP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
						e.getKeyCode() == keybinds.get("CHAR_LEFT") || e.getKeyCode() == keybinds.get("CHAR_RIGHT")) {
					if (keysPressed.containsKey(e.getKeyCode())){
						keysPressed.remove(e.getKeyCode(), 0);
					}
				} else if (e.getKeyCode() == keybinds.get("CENTER_B")){
					center = false;
                    if (dir == DIRECTION.NE) {
                        int prevY = startY;
                        startY = mouseY;
                        mouseY = prevY;
                    } else if (dir == DIRECTION.SW) {
                        int prevX = startX;
                        startX = mouseX;
                        mouseX = prevX;
                    } else if (dir == DIRECTION.NW) {
                        int prevX = startX, prevY = startY;
                        startX = mouseX;
                        startY = mouseY;
                        mouseX = prevX;
                        mouseY = prevY;
                    }
                    mouseRobot.mouseMove(mouseX, mouseY);
					mousePos();
				}
			}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					if (!pressed){
						saveDrawing();
					} else {
						pressed = false;
					}
				} else if (e.getKeyCode() == keybinds.get("CLICK_M")){
					int mouseMod = (keybinds.get("MOUSE_P") - 1) * 2;
					if (mouseMod == 0) mouseMod = 1;
					mouseRobot.mousePress(16 / mouseMod);
				} else if (e.getKeyCode() == keybinds.get("COLOR_C")){
					Color prevColor = drawColor;
					colorPick = true;
					drawColor = JColorChooser.showDialog(getFocusOwner(), "Choose drawing color", drawColor);
					if (drawColor == null){
						drawColor = prevColor;
					} else {
						getBackColor();
					}
					colorPick = false;
				} else if (e.getKeyCode() == keybinds.get("COLOR_B")) {
					drawColor = drawColor.brighter();
					getBackColor();
				} else if (e.getKeyCode() == keybinds.get("COLOR_D")) {
					drawColor = drawColor.darker();
					getBackColor();
				} else if (e.getKeyCode() == keybinds.get("CHAR_UP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
						e.getKeyCode() == keybinds.get("CHAR_LEFT") || e.getKeyCode() == keybinds.get("CHAR_RIGHT")) {
					keysPressed.put(e.getKeyCode(), 0);
				} else if (e.getKeyCode() == keybinds.get("CENTER_B") && pressed && !center) {
					center = true;
                    int newMouseX = (dir == DIRECTION.NE || dir == DIRECTION.SE) ?
                            mouseX - (dHelper.getWidth() /2):
						startX - (dHelper.getWidth() / 2);
                    int newMouseY = (dir == DIRECTION.SW || dir == DIRECTION.SE) ?
                            mouseY - (dHelper.getHeight() / 2) :
                            startY -  (dHelper.getHeight() /2);
					mouseRobot.mouseMove(newMouseX, newMouseY);
					mousePos();
				} else if (e.getKeyCode() == keybinds.get("SELECT_O")) {
					if (!pressed && !center){
						if (!drawObjects.isEmpty()) {
							int minDistance = 1000000000;
							Point newPos = new Point(0, 0);
							DrawObject obj = new DrawObject();
							for (DrawObject object: drawObjects.values()){
								if (!object.type.equals(DRAWMODE.FreeDraw)) {
									Point center = new Point(object.endPoints.x - ((object.endPoints.x - object.position.x) / 2),
											object.endPoints.y - ((object.endPoints.y - object.position.y) / 2));
									if (center.distance(mouseX, mouseY) < minDistance) {
										minDistance = (int) center.distance(mouseX, mouseY);
										newPos = new Point(center.x, center.y);
										obj = object;
									}
								}
							}
							int mouseMod = (keybinds.get("MOUSE_P") - 1) * 2;
							if (mouseMod == 0) mouseMod = 1;
							mouseRobot.mouseRelease(16 / mouseMod);
							mouseRobot.mouseMove(newPos.x, newPos.y);
							drawColor = obj.color;
							drawMode = obj.type;
							dHelper.setLocation(obj.position);
							dHelper.setSize(Math.abs(obj.endPoints.x - obj.position.x), Math.abs(obj.endPoints.y - obj.position.y));
							pressed = true;
							center = true;
                            dir = DIRECTION.SE;
                            mousePos();
						}
					}
				}
			}
		});

		addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == keybinds.get("MOUSE_P")) {
					if (pressed && drawMode != DRAWMODE.FreeDraw) {
						Point startPos;
                        Point endPos;
                        startPos = new Point(startX, startY);
						endPos = new Point(mouseX, mouseY);
						DrawObject drawObject = new DrawObject(drawMode, startPos, endPos, drawColor);
						drawObjects.put(drawObjects.size(), drawObject);
						pressed = false;
						mousePos();
                        drawer.update();
                        dHelper.setLocation(1, 1);
						dHelper.setSize(1, 1);
					} else {
						dHelper.setLocation(1, 1);
						dHelper.setSize(1, 1);
					}
				}
			}
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == keybinds.get("MOUSE_P")) {
					if (!pressed && drawMode != DRAWMODE.FreeDraw) {
						pressed = true;
						startX = mouseX; startY = mouseY;
						mousePos();
					} else if (drawMode == DRAWMODE.FreeDraw) {
						startX = mouseX;
						startY = mouseY;
					}
				}
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});

        addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				mousePos();
			}
			public void mouseDragged(MouseEvent e) {
				mousePos();
				if (drawMode == DRAWMODE.FreeDraw) {
					DrawObject object = new DrawObject(drawMode, new Point(startX, startY), new Point(mouseX, mouseY), drawColor);
					drawObjects.put(drawObjects.size(), object);
					drawer.update();
					startX = mouseX;
					startY = mouseY;
				}
			}
		});

        addMouseWheelListener(e -> {
            {
                if (e.getPreciseWheelRotation() < 0){
					drawMode = drawMode.previous();
				} else if (e.getPreciseWheelRotation() > 0){
					drawMode = drawMode.next();
				}
			}
		});
		mousePos();
	}

	DrawingFrame(int width, int height, HashMap<Integer, DrawObject> objects) throws Exception {
		this(width, height);
        drawObjects = new HashMap<>();
		for (DrawObject object : objects.values()) {
			drawObjects.put(drawObjects.size(), new DrawObject(object.type, object.position, object.endPoints, object.color));
		}
		doEdit = true;
	}
	
	@SuppressWarnings("incomplete-switch")
	public static void mousePos() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		if (center){
			startX = p.x - (dHelper.getWidth() / 2);
			startY = p.y - (dHelper.getHeight() / 2);
			mouseX = p.x + (dHelper.getWidth() / 2);
			mouseY = p.y + (dHelper.getHeight() / 2);
		} else {
			mouseX = p.x;
			mouseY = p.y;
		}
		if (pressed){
            DIRECTION prev = dir;
            if (mouseY < startY){
				if (mouseX < startX){
					dir = DIRECTION.NW;
				} else {
					dir = DIRECTION.NE;
				}
			} else if (mouseY > startY){
				if (mouseX < startX){
					dir = DIRECTION.SW;
				} else {
					dir = DIRECTION.SE;
				}
			} else {
				dir = DIRECTION.None;
			}
			if (dir != DIRECTION.None && drawMode != DRAWMODE.FreeDraw) {
				switch (dir){
					case NE: {
						dHelper.setLocation(startX, mouseY);
						dHelper.setSize(mouseX - startX, startY - mouseY);
						break;
					}
					case NW: {
						dHelper.setLocation(mouseX, mouseY);
						dHelper.setSize(startX - mouseX, startY - mouseY);
						break;
					}
					case SE: {
						dHelper.setLocation(startX, startY);
						dHelper.setSize(mouseX - startX, mouseY - startY);
						break;
					}
					case SW: {
						dHelper.setLocation(mouseX, startY);
						dHelper.setSize(startX - mouseX, mouseY - startY);
						break;
					}
				}
			}
            if (center) {
                dir = prev;
            }
            dHelper.repaint();
		}
	}
	
	public static void getBackColor(){
		float hsv[] = new float[3];
		Color.RGBtoHSB(255 - drawColor.getRed(), 255 - drawColor.getGreen(), 255 - drawColor.getBlue(), hsv);
	    hsv[0] = (hsv[0] + 180) % 360;
	    Color newColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
	    dHelper.setBackground(newColor);
		dHelper.drawPanel.setBackground(newColor);
	}

	public void run() {
	}

    public void saveDrawing(){
		if (!drawObjects.isEmpty()){
            String name;
            if (!doEdit){
				do {
					name = JOptionPane.showInputDialog(this, "Enter a name for your drawing");
				} while (name == null || name.equals(""));
			} else {
				name = drawingsList.get(curDrawingIndex).drawingName;
			}
			BufferedImage screenshot = new BufferedImage(draw.getWidth(), draw.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    	Graphics2D g2 = screenshot.createGraphics();
            drawer.paint(g2);
            Drawing drawing = new Drawing(name, screenshot, drawObjects);
			if (doEdit){
				drawingsList.replace(curDrawingIndex, drawing);
			} else {
				drawingsList.put(drawingCount, drawing);
				drawingCount++;
			}
		}
    	mode = GAMEMODE.Game;
    	dHelper.dispose();
    	dispose();
		mainGame.toFront();
		mainGame.requestFocus();
	}

    public enum DIRECTION {None, NE, NW, SE, SW}

}
