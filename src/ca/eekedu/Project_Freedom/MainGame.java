package ca.eekedu.Project_Freedom;

/*Version: Alpha 0.3.5-2
	Made by Brettink (brett_wad_12@hotmail.com)
	Classes include: MainGame, GraphicsGame, DrawingFrame, GraphicsDrawing,
						DrawHelperFrame, KeyBinds, Drawings (Drawing, and DrawObject)
	Classes modified to fit project parameters: SimulationBody, Graphics2DRenderer
*/

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.BoundsListener;
import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	public static MySwingWorker run;
	public static MySwingWorker run2;
	static Timer update = new Timer(0, null);
	static World world = new World();
	static SimulationBody charBody = new SimulationBody(new Color(145, 145, 145));
	static int RESOLUTION_WIDTH = 1080;
	static int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	static int SYSTEM_MAXDRAW_WIDTH = 0;
	static int SYSTEM_MAXDRAW_HEIGHT = 0;
	static SimulationBody floor = new SimulationBody(Color.BLACK);
	static TreeMap<Long, SimulationBody> floorNoEnd = new TreeMap<>();
	static AxisAlignedBounds bounds;
	int P_RESOLUTION_WIDTH = 1080;
	int P_RESOLUTION_HEIGHT = 720;

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
				} else if (e.getKeyCode() == keybinds.get("CHAR_JUMP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
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
						if (RESOLUTION_WIDTH != 1280) {
							RESOLUTION_WIDTH = 1280;
							RESOLUTION_HEIGHT = 800;
							P_RESOLUTION_WIDTH = 1080;
							P_RESOLUTION_HEIGHT = 720;
							positionWindowAndSize();
						}
					} else if (e.getKeyCode() == keybinds.get("SIZE_DOWN")){
						if (RESOLUTION_WIDTH != 1080) {
							RESOLUTION_WIDTH = 1080;
							RESOLUTION_HEIGHT = 720;
							P_RESOLUTION_WIDTH = 1280;
							P_RESOLUTION_HEIGHT = 800;
							positionWindowAndSize();
						}
					}
				}
				if (e.getKeyCode() == keybinds.get("CHAR_JUMP") || e.getKeyCode() == keybinds.get("CHAR_DOWN") ||
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
				} else if (e.getKeyCode() == KeyEvent.VK_F5) {
					try {
						String x2 = JOptionPane.showInputDialog("Enter x,y co-ord");
						String[] split = x2.split(",|:");
						double x = Double.parseDouble(split[0].trim());
						double y = Double.parseDouble(split[1].trim());
						double xBef = charBody.getTransform().getTranslationX();
						charBody.setLocation(x, y);
						double xDiff = charBody.getTransform().getTranslationX() - xBef;
						if (xDiff > 2000.00 || xDiff < -2000.00) {
							populate_Floor();
						}
						charBody.setAsleep(false);
					} catch (Exception e2) {
					}
				}
			}
		});
		add(graphics);
		setVisible(true);

		world.setGravity(new Vector2(0.0, 9.807));
		Settings s = new Settings();
		s.setLinearTolerance(0.005);
		s.setMaximumRotation(0.05);
		world.setSettings(s);
		bounds = new AxisAlignedBounds(RESOLUTION_WIDTH * 9, RESOLUTION_HEIGHT * 9);
		bounds.translate(0.0, -(RESOLUTION_HEIGHT * 3) - 300);

		world.setBounds(bounds);

		BodyFixture charFixture = new BodyFixture(new Circle(50.0));
		charFixture.setDensity(0.1);
		charFixture.setFriction(1.0);
		charFixture.setRestitution(0.0);

		charBody.addFixture(charFixture);
		charBody.setMass(new Mass(new Vector2(0.0, 0.0), 25.0, 1.0));
		charBody.setAutoSleepingEnabled(true);
		charBody.setAngularDamping(0.1);
		charBody.setLinearDamping(0.05);
		charBody.setLocation(0.0, 10.0);

		floor.addFixture(new Rectangle(RESOLUTION_WIDTH, 50.0));
		floor.setMass(MassType.INFINITE);

		populate_Floor();

		SimulationBody rando = new SimulationBody(Color.BLUE);
		rando.setLocation(20.0, 20.0);
		BodyFixture f = new BodyFixture(new Rectangle(200.0, 50.0));
		f.setDensity(1.0);
		f.setFriction(1.0);
		rando.addFixture(f);
		rando.setMass(new Mass(new Vector2(0.0, 0.0), 500.0, 0.0));
		rando.setAutoSleepingEnabled(true);
		rando.setAngularDamping(1.0);
		rando.setLinearDamping(0.0);
		rando.setAutoSleepingEnabled(true);
		world.addListener(new BoundsListener() {
			@Override
			public <E extends Collidable<T>, T extends Fixture> void outside(E e) {
				SimulationBody en = (SimulationBody) e;
				if (!en.equals(floor)) en.setLocation(en.getTransform().getTranslationX() / 10,
						(P_RESOLUTION_HEIGHT - en.getTransform().getTranslationY() + 200) / 10);
				en.setActive(false);
			}
		});

		world.addBody(charBody);
		world.addBody(rando);
		world.addBody(floor);
		world.addBody(floorNoEnd.get((long) -RESOLUTION_WIDTH / 10));
		world.addBody(floorNoEnd.get(0L));
		world.addBody(floorNoEnd.get((long) RESOLUTION_WIDTH / 10));

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

		run = new MySwingWorker(draw);
		run2 = new MySwingWorker(dHelper);

		run.execute();
		run2.execute();
	}

	public static void refresh(TreeMap<Integer, Drawings.Drawing.DrawObject> objects) {
		int index = draw.curDrawingIndex;
		draw = null;
		try {
			dHelper = new DrawHelperFrame();
			draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT, objects);
			draw.curDrawingIndex = index;
			run = new MySwingWorker(draw);
			run2 = new MySwingWorker(dHelper);

			run.execute();
			run2.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				bounds.translate(new Vector2(charBody.getChangeInPosition().x, 0.0));
				world.update(1.0, 0.05);
				graphics.update();

				if (mode.equals(GAMEMODE.Game)) {
					mainGame.requestFocus();
				}

				if (draw != null) {
					if (!draw.isVisible()) {
						dHelper = null;
						draw = null;
					} else if (draw.refreshMe) {
						dHelper.dispose();
						dHelper = null;
						draw.dispose();
						draw.refreshMe = false;
						TreeMap<Integer, Drawings.Drawing.DrawObject> newObjects = new TreeMap<>();
						for (Drawings.Drawing.DrawObject object : drawObjects.values()) {
							newObjects.put(newObjects.size(), object);
						}
						refresh(newObjects);
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
			if (key.equals(keybinds.get("CHAR_JUMP"))) {
				if (mode.equals(GAMEMODE.Game)) {
					if (!charBody.getInContactBodies(false).isEmpty()) {
						charBody.applyImpulse(new Vector2(charBody.getLinearVelocity().x * 1000, -75000.0));
					}
				}
			}
			if (key.equals(keybinds.get("MOUSE_UP")) && pressed) mouseRobot.mouseMove(mouseX, mouseY - 1);
			mousePos();
			if (key.equals(keybinds.get("CHAR_DOWN")))
				if (mode.equals(GAMEMODE.Game)) charBody.applyImpulse(new Vector2(0.0, 20.0));
				else if (pressed) mouseRobot.mouseMove(mouseX, mouseY + 1);
			mousePos();
			if (key.equals(keybinds.get("CHAR_LEFT")))
				if (mode.equals(GAMEMODE.Game)) charBody.applyForce(new Vector2(-500.0, 0));
				else if (pressed) mouseRobot.mouseMove(mouseX - 1, mouseY);
			mousePos();
			if (key.equals(keybinds.get("CHAR_RIGHT")))
				if (mode.equals(GAMEMODE.Game)) charBody.applyForce(new Vector2(500.0, 0));
				else if (pressed) mouseRobot.mouseMove(mouseX + 1, mouseY);
			mousePos();
		}
		if ((long) (graphics.worldX + 1620) > ((floorNoEnd.lastKey() * 10) + 1080)) {
			world.removeBody(floorNoEnd.get(floorNoEnd.firstKey()));
			floorNoEnd.remove(floorNoEnd.firstKey());
			SimulationBody floor_P100 = new SimulationBody(Color.BLACK);
			floor_P100.addFixture(new Rectangle(1080, 50));
			floor_P100.setMass(MassType.INFINITE);
			floor_P100.setLocation((((double) floorNoEnd.lastKey() * 10) + 1080) / 10, 2.5);
			world.addBody(floor_P100);
			floorNoEnd.put((floorNoEnd.lastKey() + 108), floor_P100);
		} else if ((long) (graphics.worldX - 1620) < ((floorNoEnd.firstKey() * 10) - 1080)) {
			world.removeBody(floorNoEnd.get(floorNoEnd.lastKey()));
			floorNoEnd.remove(floorNoEnd.lastKey());
			SimulationBody floor_M100 = new SimulationBody(Color.BLACK);
			floor_M100.addFixture(new Rectangle(1080, 50));
			floor_M100.setMass(MassType.INFINITE);
			floor_M100.setLocation((((double) floorNoEnd.firstKey() * 10) - 1080) / 10, 2.5);
			world.addBody(floor_M100);
			floorNoEnd.put((floorNoEnd.firstKey() - 108), floor_M100);
		}
	}

	public void populate_Floor() {
		if (!floorNoEnd.isEmpty()) {
			for (SimulationBody body : floorNoEnd.values()) {
				world.removeBody(body);
			}
			floorNoEnd.clear();
		}
		SimulationBody floor_M100 = new SimulationBody(Color.BLACK);
		SimulationBody floor_100 = new SimulationBody(Color.BLACK);
		SimulationBody floor_P100 = new SimulationBody(Color.BLACK);
		floor_M100.addFixture(new Rectangle(1080, 50));
		floor_100.addFixture(new Rectangle(1080, 50));
		floor_P100.addFixture(new Rectangle(1080, 50));
		floor_M100.setMass(MassType.INFINITE);
		floor_100.setMass(MassType.INFINITE);
		floor_P100.setMass(MassType.INFINITE);
		floor_M100.setLocation((((int) (graphics.worldX / 1080)) * 108) - 108, 2.5);
		floor_100.setLocation((((int) (graphics.worldX / 1080)) * 108), 2.5);
		floor_P100.setLocation((((int) (graphics.worldX / 1080)) * 108) + 108, 2.5);
		floorNoEnd.put((long) (((int) (graphics.worldX / 1080)) * 108) - 108, floor_M100);
		floorNoEnd.put((long) (((int) (graphics.worldX / 1080)) * 108), floor_100);
		floorNoEnd.put((long) (((int) (graphics.worldX / 1080)) * 108) + 108, floor_P100);
	}

	public void positionWindowAndSize() {
		setSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);

		int posX = (SYSTEM_RES_WIDTH / 2) - (RESOLUTION_WIDTH / 2);
		int posY = (SYSTEM_RES_HEIGHT / 2) - (RESOLUTION_HEIGHT / 2);
		setLocation(posX, posY);
		/*Polygon po = new Polygon();
		po.addPoint(0,0);
		po.addPoint(RESOLUTION_WIDTH, 0);
		po.addPoint(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
		boolean flip = false;
		for (int i = (RESOLUTION_WIDTH - (RESOLUTION_WIDTH / 40)); i > 0; i -= RESOLUTION_WIDTH / 40){
			po.addPoint(i, (flip)? RESOLUTION_HEIGHT: RESOLUTION_HEIGHT - 15);
			flip = !flip;
		}
		po.addPoint(0,RESOLUTION_HEIGHT);
		po.addPoint(0,0);
		setShape(po);*/
		graphics.scale();
		floor.setLocation(0.0, 2.5);
		for (Body body : world.getBodies().stream().filter(p -> !p.equals(floor)).collect(Collectors.toSet())) {
			SimulationBody simBody = (SimulationBody) body;
			double getRot = simBody.getTransform().getRotation();
			double getPosX = simBody.getTransform().getTranslationX();
			double getPosY = simBody.getTransform().getTranslationY();

			simBody.setLocation(getPosX / 10, (P_RESOLUTION_HEIGHT - getPosY) / 10, getRot);
			simBody.setAsleep(false);
		}

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
			if (r == draw) {
				draw.requestFocus();
			}
			return new Thread(r);
		}
	}

}
