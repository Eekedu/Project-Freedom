package ca.eekedu.Project_Freedom;

/**
 * Version: Alpha 0.3.9-2
 * Made by Brettink (brett_wad_12@hotmail.com)
 *      Classes include: MainGame, GraphicsGame, DrawingFrame (GraphicsDrawing class embedded),
 *      DrawHelperFrame, KeyBinds, Drawings (Drawing, and DrawObject classes embedded),
 *      AudioFile, Notifications (Notification class embedded)
 Classes modified to fit project parameters: SimulationBody, Graphics2DRenderer, Player (from jlme library)
*/

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.BoundsListener;
import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ca.eekedu.Project_Freedom.DrawingFrame.*;

public class MainGame extends JFrame implements Runnable {

	final static Logger logger = LogManager.getLogger(MainGame.class.getName());
	public static volatile Notifications notificationHandler;
	/**
	 * All the variables needed to run the program
	 */
	static HashMap<Integer, Integer> keysPressed = new HashMap<>();
	static MainGame mainGame = null;
	static GraphicsGame graphics = new GraphicsGame();
	static KeyBinds keybinds = new KeyBinds();
	static Drawings drawingsList = new Drawings();
	static GAMEMODE mode = GAMEMODE.Game;
	static DRAWMODE drawMode = DRAWMODE.Line;
	static DrawingFrame draw = null;
	static DrawHelperFrame dHelper = null;
	static Color drawColor = Color.RED;
	static int drawingCount = 0;
	static Timer update = new Timer(0, null);
	static World world = new World();
	static SimulationBody characterBody = new SimulationBody(new Color(145, 145, 145));
	static int RESOLUTION_WIDTH = 1080;
	static int RESOLUTION_HEIGHT = 720;
	static int SYSTEM_RES_WIDTH = 0;
	static int SYSTEM_RES_HEIGHT = 0;
	static int SYSTEM_MAXDRAW_WIDTH = 0;
	static int SYSTEM_MAXDRAW_HEIGHT = 0;
	static SimulationBody floor = new SimulationBody(Color.BLACK);
	static TreeMap<Long, SimulationBody> floorNoEnd = new TreeMap<>();
	static AxisAlignedBounds bounds;
	static int P_RESOLUTION_HEIGHT = 720;
	static AudioPlaylist musicPlaylist;
	int P_RESOLUTION_WIDTH = 1080;

	/**
	 *  Default and only Constructor
	 * @throws AWTException
	 */
	MainGame() throws AWTException {
		try {
			notificationHandler = new Notifications();
		} catch (Exception msg) {
			JOptionPane.showMessageDialog(this,
					"Notifications could not be setup\n" + msg.getMessage());
			logger.error(msg.getMessage());
		}
		setTitle("Project Freedom");
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stopAll();
			}
		});
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
				} else if (keysPressed.containsKey(KeyEvent.VK_SHIFT)) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						float gain = (float) (Math.exp((musicPlaylist.volume * Math.log(10.0)) / 20.0));
						if (gain > 0.025) {
							gain -= 0.025;
							double a = (Math.log(gain) / Math.log(10.0) * 20.0);
							BigDecimal newVol = new BigDecimal(a);
							float ne = newVol.setScale(1, BigDecimal.ROUND_HALF_EVEN).floatValue();
							musicPlaylist.setVolume(ne);
							notificationHandler.addNotification("Volume decreased to: " + (int) (gain * 100),
									Notifications.NOTIFICATION_TYPE.INFORMATION);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						float gain = (float) (Math.exp((musicPlaylist.volume * Math.log(10.0)) / 20.0));
						if (gain < 0.975) {
							gain += 0.025;
							double a = (Math.log(gain) / Math.log(10.0) * 20.0);
							BigDecimal newVol = new BigDecimal(a);
							float ne = newVol.setScale(1, BigDecimal.ROUND_HALF_EVEN).floatValue();
							musicPlaylist.setVolume(ne);
							notificationHandler.addNotification("Volume increased to: " + (int) (gain * 100),
									Notifications.NOTIFICATION_TYPE.INFORMATION);
						}
					}
				}
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT){
					keysPressed.put(e.getKeyCode(), 0);
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
					stopAll();
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
						notificationHandler.addNotification("An error has occurred trying to open the drawing window - "
								+ e1.getMessage(), Notifications.NOTIFICATION_TYPE.ERROR);

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
						JPanel input = new JPanel();
						JTextField xF = new JTextField(5);
						JTextField yF = new JTextField(5);
						input.add(new JLabel("<html>Max-X = 5000<br/>Max-Y = 512</html>"));
						input.add(Box.createHorizontalStrut(30));
						input.add(new JLabel("x:"));
						input.add(xF);
						input.add(Box.createHorizontalStrut(10));
						input.add(new JLabel("y:"));
						input.add(yF);
						xF.addAncestorListener(new RequestFocusListener());
						int msg = JOptionPane.showConfirmDialog(null, input, "Enter Coords",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (msg != JOptionPane.CANCEL_OPTION) {
							double x = Double.parseDouble(xF.getText().trim());
							double y = Double.parseDouble(yF.getText().trim());
							if (x <= 5000 | y <= 512) {
								double xBef = characterBody.getTransform().getTranslationX();
								characterBody.setLocation(x, y);
								double xDiff = characterBody.getTransform().getTranslationX() - xBef;
								if (xDiff > 2000.00 || xDiff < -2000.00) {
									populate_Floor();
								}
								characterBody.setAsleep(false);
							}
						}
					} catch (Exception e2) {
						notificationHandler.addNotification("You must enter proper coordinates",
								Notifications.NOTIFICATION_TYPE.INFORMATION);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && musicPlaylist != null) {
					try {
						musicPlaylist.stopNext();
					} catch (Exception e2) {
						notificationHandler.addNotification("Could not play next music file - " + e2.getMessage(),
								Notifications.NOTIFICATION_TYPE.ERROR);
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

		characterBody.addFixture(charFixture, new Point(100, 100));
		characterBody.setMass(new Mass(new Vector2(0.0, 0.0), 25.0, 10000.0));
		characterBody.setAutoSleepingEnabled(true);
		characterBody.setAngularDamping(0.1);
		characterBody.setLinearDamping(0.05);
		characterBody.setLocation(0.0, 0.0);
		characterBody.setTexture(graphics.wheel);

		floor.addFixture(new Rectangle(RESOLUTION_WIDTH, 50.0));
		floor.setMass(MassType.INFINITE);

		populate_Floor();

		SimulationBody randomBody = new SimulationBody(Color.BLUE.darker());
		randomBody.setLocation(20.0, 40.0);
		Vector2[] vertices = new Vector2[3];
		vertices[0] = new Vector2(600.0, 0.0);
		vertices[1] = new Vector2(600.0, 400.0);
		vertices[2] = new Vector2(0.0, 400.0);
		BodyFixture f = new BodyFixture(new Polygon(vertices));
		f.setDensity(1.0);
		f.setFriction(0.5);
		randomBody.addFixture(f, new Point(250, 250));
		randomBody.setMass(new Mass(new Vector2(0.0, 0.0), 500.0, 1000));
		randomBody.updateMass();
		randomBody.setAutoSleepingEnabled(true);
		randomBody.setAngularDamping(0.0);
		randomBody.setLinearDamping(0.0);
		randomBody.setTexture(graphics.wood);
		world.addListener(new BoundsListener() {
			@Override
			public <E extends Collidable<T>, T extends Fixture> void outside(E e) {
				SimulationBody en = (SimulationBody) e;
				if (!en.equals(floor)) en.setLocation(en.getTransform().getTranslationX() / 10,
						(P_RESOLUTION_HEIGHT - en.getTransform().getTranslationY() + 200) / 10);
				en.setActive(false);
			}
		});

		world.addBody(characterBody);
		world.addBody(randomBody);
		world.addBody(floor);
		world.addBody(floorNoEnd.get((long) -RESOLUTION_WIDTH / 10));
		world.addBody(floorNoEnd.get(0L));
		world.addBody(floorNoEnd.get((long) RESOLUTION_WIDTH / 10));
		try {
			musicPlaylist = new AudioPlaylist(AudioPlaylist.LOOPTYPE.REPEATALL, -2.5F);
			Stream<Path> paths = Files.walk(Paths.get("music/"));
			paths.filter(Files::isRegularFile).filter(p -> p.toString().contains(".mp3"))
					.forEach(p -> musicPlaylist.add("music/" + p.getFileName().toString()));
			musicPlaylist.play();
		} catch (Exception e) {
			notificationHandler.addNotification("Error loading music - " + e.getMessage(),
					Notifications.NOTIFICATION_TYPE.ERROR);
		}
		positionWindowAndSize();
	}

	/**
	 *  Open the Drawing Window
	 * @param pos position of drawing in the DrawingsList
	 * @throws Exception
	 */
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

	}

	/**
	 * Not fully working yet
	 * @param objects
	 */
	public static void refresh(TreeMap<Integer, Drawings.Drawing.DrawObject> objects) {
		int index = draw.curDrawingIndex;
		draw = null;
		try {
			dHelper = new DrawHelperFrame();
			draw = new DrawingFrame(SYSTEM_MAXDRAW_WIDTH, SYSTEM_MAXDRAW_HEIGHT, objects);
			draw.curDrawingIndex = index;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Main method at startup
	 * @param args
	 * @throws AWTException
	 */
	public static void main(String[] args) throws AWTException {
		Dimension system_resolution = Toolkit.getDefaultToolkit().getScreenSize();
		SYSTEM_RES_WIDTH = system_resolution.width;
		SYSTEM_RES_HEIGHT = system_resolution.height;
		java.awt.Rectangle window = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		SYSTEM_MAXDRAW_WIDTH = window.width;
		SYSTEM_MAXDRAW_HEIGHT = window.height;
		mainGame = new MainGame();
		ActionListener updateTimer = e -> {
			if (!mainGame.isVisible() && update.isRunning()) {
				mainGame.setVisible(true);
				JOptionPane.showMessageDialog(mainGame, "Game window had a problem starting right away",
						"Minor error", JOptionPane.PLAIN_MESSAGE);
			}

			bounds.translate(new Vector2(characterBody.getChangeInPosition().x, 0.0));
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
			if (!keysPressed.isEmpty()) {
				checkControls();
			}
		};

		update = new Timer(5, updateTimer); //Smooth update of graphics, reduced lag
		update.start();
		Thread r = new Thread(mainGame);
		r.setDaemon(true);
		r.start();
	}

	/**
	 * Check for controls needed only when pressed
	 */
	public static void checkControls() {
		for (Integer key : keysPressed.keySet()) {
			if (key.equals(keybinds.get("CHAR_JUMP"))) {
				if (mode.equals(GAMEMODE.Game)) {
					if (!characterBody.getInContactBodies(false).isEmpty()) {
						characterBody.applyForce(new Vector2(0.0, -75000.0));
					}
				}
			}
			if (key.equals(keybinds.get("MOUSE_UP")) && pressed) mouseRobot.mouseMove(mouseX, mouseY - 1);
			mousePos();
			if (key.equals(keybinds.get("CHAR_DOWN")))
				if (mode.equals(GAMEMODE.Game)) characterBody.applyImpulse(new Vector2(0.0, 20.0));
				else if (pressed) mouseRobot.mouseMove(mouseX, mouseY + 1);
			mousePos();
			if (key.equals(keybinds.get("CHAR_LEFT")))
				if (mode.equals(GAMEMODE.Game)) characterBody.applyForce(new Vector2(-500.0, 0));
				else if (pressed) mouseRobot.mouseMove(mouseX - 1, mouseY);
			mousePos();
			if (key.equals(keybinds.get("CHAR_RIGHT")))
				if (mode.equals(GAMEMODE.Game)) characterBody.applyForce(new Vector2(500.0, 0));
				else if (pressed) mouseRobot.mouseMove(mouseX + 1, mouseY);
			mousePos();
		}
		if ((long) (graphics.worldX + 1620) > ((floorNoEnd.lastKey() * 10) + 1080)) {
			world.removeBody(floorNoEnd.get(floorNoEnd.firstKey()));
			floorNoEnd.remove(floorNoEnd.firstKey());
			SimulationBody floor_P100 = new SimulationBody(Color.BLACK);
			floor_P100.addFixture(new Rectangle(1080, 50));
			floor_P100.setMass(MassType.INFINITE);
			floor_P100.setLocation((((double) floorNoEnd.lastKey() * 10) + 1080) / 10, -7.5);
			world.addBody(floor_P100);
			floorNoEnd.put((floorNoEnd.lastKey() + 108), floor_P100);
		} else if ((long) (graphics.worldX - 1620) < ((floorNoEnd.firstKey() * 10) - 1080)) {
			world.removeBody(floorNoEnd.get(floorNoEnd.lastKey()));
			floorNoEnd.remove(floorNoEnd.lastKey());
			SimulationBody floor_M100 = new SimulationBody(Color.BLACK);
			floor_M100.addFixture(new Rectangle(1080, 50));
			floor_M100.setMass(MassType.INFINITE);
			floor_M100.setLocation((((double) floorNoEnd.firstKey() * 10) - 1080) / 10, -7.5);
			world.addBody(floor_M100);
			floorNoEnd.put((floorNoEnd.firstKey() - 108), floor_M100);
		}
	}

	public void stopAll() {
        if (musicPlaylist != null && musicPlaylist.isFilled()) {
            musicPlaylist.loopType = AudioPlaylist.LOOPTYPE.NOREPEAT;
			musicPlaylist.stop();
		}
		update.stop();
		dispose();
	}

	/**
	 * Populated the ground for the main character to land on
	 */
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
		floor_M100.setLocation((((int) (graphics.worldX / 1080)) * 108) - 108, -7.5);
		floor_100.setLocation((((int) (graphics.worldX / 1080)) * 108), -7.5);
		floor_P100.setLocation((((int) (graphics.worldX / 1080)) * 108) + 108, -7.5);
		floorNoEnd.put((long) (((int) (graphics.worldX / 1080)) * 108) - 108, floor_M100);
		floorNoEnd.put((long) (((int) (graphics.worldX / 1080)) * 108), floor_100);
		floorNoEnd.put((long) (((int) (graphics.worldX / 1080)) * 108) + 108, floor_P100);
	}

	/**
	 * Position window relative to size and system resolution
	 */
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
		floor.setLocation(0.0, -7.5);
		for (Body body : world.getBodies().stream().filter(p -> !p.equals(floor)).collect(Collectors.toSet())) {
			SimulationBody simBody = (SimulationBody) body;
			double getRot = simBody.getTransform().getRotation();
			double getPosX = simBody.getTransform().getTranslationX();
			double getPosY = simBody.getTransform().getTranslationY();

			simBody.setLocation(getPosX / 10, ((P_RESOLUTION_HEIGHT - 100) - getPosY) / 10, getRot);
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

	public class RequestFocusListener implements AncestorListener {
		private boolean removeListener;

		public RequestFocusListener() {
			this(true);
		}

		public RequestFocusListener(boolean removeListener) {
			this.removeListener = removeListener;
		}

		@Override
		public void ancestorAdded(AncestorEvent e) {
			JComponent component = e.getComponent();
			component.requestFocusInWindow();

			if (removeListener)
				component.removeAncestorListener(this );
		}

		@Override
		public void ancestorMoved(AncestorEvent e) {
		}

		@Override
		public void ancestorRemoved(AncestorEvent e) {
		}
	}

}
