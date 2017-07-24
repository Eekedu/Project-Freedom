package ca.eekedu.Project_Freedom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Notifications {

	private TreeMap<Integer, Notification> notificationList = new TreeMap<>();
	private BufferedImage[] icon = new BufferedImage[3];

	Notifications() throws Exception {
		icon[0] = ImageIO.read(new File("images/icons/notification/err.png"));
		icon[1] = ImageIO.read(new File("images/icons/notification/msg.png"));
		icon[2] = ImageIO.read(new File("images/icons/notification/inf.png"));
	}

	public boolean addNotification(String message, NOTIFICATION_TYPE type) {
		try {
			notificationList.put((!notificationList.isEmpty()) ? notificationList.lastKey() + 1 : 0,
					new Notification(message, type));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean drawAllNotifications(Graphics2D g) {
		AffineTransform previous = g.getTransform();
		if (!notificationList.isEmpty()) {
			TreeMap<Integer, Map.Entry<Integer, Notification>> toDelete = new TreeMap<>();
			double offset = 0;
			Font prev = g.getFont();
			Font font = new Font("Serif", Font.BOLD, 14);
			g.setFont(font);
			g.translate(4.0, -20.0);
			for (Map.Entry<Integer, Notification> entry : notificationList.entrySet()) {
				Notification notification = entry.getValue();
				if (!(notification.currentAge > 1000) || notification.isMovingOpp) {
					notification.currentAge++;
					g.translate(0.0, 50 + (notification.curYOffset - 50) + offset);
					g.setColor(notification.type.getBackgroundColor());
					FontMetrics fm = g.getFontMetrics();
					int width = fm.stringWidth(notification.message);
					boolean resize = false;
					if (width > 350) {
						resize = true;
						width += 50;
					}
					g.fillRoundRect(0, 0, (resize) ? width : 400, 50, 25, 25);
					g.setColor(Color.BLACK);
					Stroke oldStroke = g.getStroke();
					g.setStroke(new BasicStroke(4.0F));
					g.drawRoundRect(0, 0, (resize) ? width : 400, 50, 25, 25);
					g.setStroke(oldStroke);
					g.drawImage(icon[notification.type.type], 5, 9, 32, 32, null);
					g.setColor(notification.type.getForegroundColor());
					g.drawString(notification.timeOfCreation.toString(), 40.0F, 20.0F);
					g.drawString(notification.message, 40.0F, 40.0F);
					if (notification.isMoving) {
						notification.curYOffset += .75;
						if (notification.curYOffset > 49) {
							notification.curYOffset = 50;
							notification.isMoving = false;
						}
					} else if (notification.isMovingOpp) {
						notification.curYOffset -= .75;
						if (notification.curYOffset < 0) {
							toDelete.put(toDelete.size() + 1, entry);
						}
					}
					offset = 5;
				} else {
					notification.isMovingOpp = true;
				}
			}
			for (Map.Entry<Integer, Notification> entry : toDelete.values()) {
				notificationList.remove(entry.getKey(), entry.getValue());
			}
			g.setFont(prev);
		} else {
			return false;
		}
		g.setTransform(previous);
		return true;
	}

	enum NOTIFICATION_TYPE {
		ERROR(0), NORMAL(1), INFORMATION(2);
		int type = 0;
		private Color backgroundColor;
		private Color foregroundColor;

		NOTIFICATION_TYPE(int type) {
			this.type = type;
			switch (this.type) {
				case 0:
					this.backgroundColor = new Color(80, 80, 80, 160);
					foregroundColor = Color.RED;
					break;
				case 1:
					this.backgroundColor = new Color(80, 80, 80, 160);
					foregroundColor = Color.WHITE;
					break;
				case 2:
					this.backgroundColor = new Color(80, 80, 80, 160);
					foregroundColor = Color.YELLOW;
					break;
			}
		}

		public Color getBackgroundColor() {
			return backgroundColor;
		}

		public Color getForegroundColor() {
			return foregroundColor;
		}
	}

	private class Notification {

		private NOTIFICATION_TYPE type;
		private String message;
		private Date timeOfCreation;
		private int currentAge = 0;
		private boolean isMoving = false;
		private boolean isMovingOpp = false;
		private double curYOffset = 0;

		private Notification() {
		}

		private Notification(String message, NOTIFICATION_TYPE type) {
			this.message = message;
			this.type = type;
			this.timeOfCreation = new Date(System.currentTimeMillis());
			this.isMoving = true;
			this.curYOffset = 0;
		}
	}
}
