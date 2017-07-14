package ca.eekedu.Project_Freedom;

/*Version: Alpha 0.3.1
	Made by Brettink (brett_wad_12@hotmail.com)
	Classes include: MainGame, GraphicsGame, DrawingFrame, GraphicsDrawing,
						DrawHelperFrame, KeyBinds, Drawings (Drawing, and DrawObject)
	Classes modified to fit project parameters: SimulationBody, Graphics2DRenderer
*/

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import static ca.eekedu.Project_Freedom.DrawingFrame.*;

public class MainGame extends JFrame implements Runnable {

	public static HashMap<Integer, Integer> keysPressed = new HashMap<>();
	public static MainGame mainGame = null;
	public static GraphicsGame graphics = new GraphicsGame();
	public static KeyBinds keybinds = new KeyBinds();
	public static Drawings drawingsList = new Drawings();
	public static GAMEMODE mode = GAMEMODE.Game;
	public static DRAWMODE drawMode = DRAWMODE.Line;
	public static DrawingFrame draw = null;
	public static DrawHelperFrame dHelper = null;
	public static Color drawColor = Color.RED;
	public static int drawingCount = 0;
	static Timer update = new Timer(0, null);
	static World world = new World();
	static SimulationBody charBody = new SimulationBody(new Color(145, 145, 145));
	static int RESOLUTION_WIDTH = 1080;
	static int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	static int SYSTEM_MAXDRAW_WIDTH = 0;
	static int SYSTEM_MAXDRAW_HEIGHT = 0;
	SimulationBody floor = new SimulationBody(Color.BLACK);

	MainGame() throws AWTException {

		setTitle("Project Freedom");
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT){
					if (keysPressed.containsKey(e.getKeyCode())){
						keysPressed.remove(e.getKeyCode(), 0);
					}
				} else if (e.getKeyCode() == keybinds.get("CHAR_UP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
						e.getKeyCode() == keybinds.get("CHAR_LEFT") || e.getKeyCode() == keybinds.get("CHAR_RIGHT")){
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
				} else if (keysPressed.containsKey(KeyEvent.VK_SHIFT) && graphics.inventory == null) {
					if (e.getKeyCode() == keybinds.get("SIZE_UP")){
						RESOLUTION_WIDTH = 1280;
						RESOLUTION_HEIGHT = 800;
						positionWindowAndSize();
					} else if (e.getKeyCode() == keybinds.get("SIZE_DOWN")){
						RESOLUTION_WIDTH = 1080;
						RESOLUTION_HEIGHT = 720;
						positionWindowAndSize();
					}
				}
				if (e.getKeyCode() == keybinds.get("CHAR_UP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
						e.getKeyCode() == keybinds.get("CHAR_LEFT") || e.getKeyCode() == keybinds.get("CHAR_RIGHT")){
					keysPressed.put(e.getKeyCode(), 0);
				} else if (e.getKeyCode() == keybinds.get("DO_DRAW") && graphics.inventory == null) {
					try {
						doDraw(-1);
					} catch (Exception e1) {
						System.out.println("Ooops Something went wrong!");

					}
				} else if (e.getKeyCode() == keybinds.get("INVENT_B")){
					if (graphics.inventory == null){
						graphics.createInventory();
					} else {
						graphics.remove(graphics.inventory);
						graphics.inventory = null;
						revalidate();
						repaint();
					}
				}
			}
		});
		add(graphics);
		setVisible(true);

		world.setGravity(new Vector2(0.0, 9.7));

		charBody.addFixture(new Circle(50.0));
		Transform t = new Transform();
		t.translate(100.0, 50.0);
		charBody.setTransform(t);
		charBody.setMass(new Mass(new Vector2(0.0, 0.0), 20.0, 1.0));
		charBody.setAutoSleepingEnabled(true);
		charBody.setAngularDamping(0.1);
		charBody.setLinearDamping(0.1);

		world.addBody(charBody);
		world.addBody(floor);

		positionWindowAndSize();
	}

	public static void doDraw(int pos) throws Exception {
		dHelper = new DrawHelperFrame();
		if (pos == -1) {
			draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT);
			draw.curDrawingIndex = drawingCount;
		} else {
			draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT, drawingsList.get(pos).objects);
			draw.curDrawingIndex = pos;
			graphics.inventory = null;
		}
		mode = GAMEMODE.Draw;
		getBackColor();

		MySwingWorker run = new MySwingWorker(draw);
		MySwingWorker run2 = new MySwingWorker(dHelper);

		run.execute();
		run2.execute();
	}

	public static void main(String[] args) throws AWTException {
		Dimension system_resolution = Toolkit.getDefaultToolkit().getScreenSize();
		SYSTEM_RES_WIDTH = system_resolution.width;
		SYSTEM_RES_HEIGHT = system_resolution.height;
		java.awt.Rectangle window = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		SYSTEM_MAXDRAW_WIDTH = window.width;
		SYSTEM_MAXDRAW_HEIGHT = window.height;
		mainGame = new MainGame();

		ActionListener updateTimer = e -> {
			{
				if (!mainGame.isVisible() && update.isRunning()) {
					mainGame.setVisible(true);
					JOptionPane.showMessageDialog(mainGame, "Game window had a problem starting right away",
							"Minor error", JOptionPane.PLAIN_MESSAGE);
				}

				world.update(1.0, 0.05);
				graphics.update();

				if (mode == GAMEMODE.Game) {
					mainGame.requestFocus();
				}
				if (draw != null) {
					if (!draw.isVisible()) {
						dHelper = null;
						draw = null;
					}
				}
				checkControls();
			}
		};

		update = new Timer(5, updateTimer); //Smooth update of graphics, reduced lag
		update.start();

		MySwingWorker main = new MySwingWorker(mainGame);
		main.execute();

	}

	public static void checkControls() {
		for (Integer key : keysPressed.keySet()) {
			if (key.equals(keybinds.get("CHAR_UP")))
				if (mode == GAMEMODE.Game) charBody.applyImpulse(new Vector2(0.0, -20.0));
				else if (pressed) mouseRobot.mouseMove(mouseX, mouseY - 1);
			mousePos();
			if (key.equals(keybinds.get("CHAR_DOWN")))
				if (mode == GAMEMODE.Game) charBody.applyImpulse(new Vector2(0.0, 20.0));
				else if (pressed) mouseRobot.mouseMove(mouseX, mouseY + 1);
			mousePos();
			if (key.equals(keybinds.get("CHAR_LEFT")))
				if (mode == GAMEMODE.Game) charBody.applyImpulse(new Vector2(-5.0, 0.0));
				else if (pressed) mouseRobot.mouseMove(mouseX - 1, mouseY);
			mousePos();
			if (key.equals(keybinds.get("CHAR_RIGHT")))
				if (mode == GAMEMODE.Game) charBody.applyImpulse(new Vector2(5.0, 0.0));
				else if (pressed) mouseRobot.mouseMove(mouseX + 1, mouseY);
			mousePos();
		}
	}

	public void positionWindowAndSize() {
		setSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);

		int posX = (SYSTEM_RES_WIDTH / 2) - (RESOLUTION_WIDTH / 2);
		int posY = (SYSTEM_RES_HEIGHT / 2) - (RESOLUTION_HEIGHT / 2);
		setLocation(posX, posY);
		graphics.scale();
		world.removeBody(floor);
		floor = new SimulationBody(Color.BLACK);
		floor.addFixture(new Rectangle(RESOLUTION_WIDTH, 40.0));
		floor.setMass(MassType.INFINITE);
		floor.translate(RESOLUTION_WIDTH / 2, RESOLUTION_HEIGHT - 20);
		charBody.translate(0.0, -100.0);
		world.addBody(floor);
	}

	public void run() {
	}

	public enum GAMEMODE {Menu, Game, Draw}

	public enum DRAWMODE {
		Line, FreeDraw, EmptyRect, FilledRect, Oval, FilledOval;
		private static DRAWMODE[] vals = values();

		public DRAWMODE next() {
			if (this.ordinal() == vals.length - 1) {
				return vals[0];
			}
			return vals[(this.ordinal()) + 1];
		}

		public DRAWMODE previous() {
			if (this.ordinal() == 0) {
				return vals[vals.length - 1];
			}
			return vals[(this.ordinal()) - 1];
		}
	}

	public static class MySwingWorker extends SwingWorker<Thread, Runnable> {
		Runnable r;

		MySwingWorker(Runnable r) {
			this.r = r;
		}
		protected Thread doInBackground() throws Exception {
			return new Thread(r);
		}
	}

}
