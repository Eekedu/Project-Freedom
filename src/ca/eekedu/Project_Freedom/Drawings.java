package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.TreeMap;

import static ca.eekedu.Project_Freedom.MainGame.DRAWMODE;

public class Drawings extends TreeMap<Integer, Drawing> {

	public static class Drawing extends SimulationBody {

        String drawingName = "";
        int weight = 0;
        BufferedImage screenshot;
		TreeMap<Integer, DrawObject> objects = new TreeMap<>();

		Drawing(String name, BufferedImage screenshot, TreeMap<Integer, DrawObject> objects) {
			this.drawingName = name;
			this.screenshot = screenshot;
			this.objects = objects;
		}

		public static class DrawObject {
			DRAWMODE type = DRAWMODE.Line;
			Point position = new Point(0, 0);
			Point size = new Point(0, 0);
			Color color = new Color(0, 0, 0);

			DrawObject() {
			}
			DrawObject(DRAWMODE type, Point position, Point endPoints, Color color) {
				this.type = type;
				int startX = position.x;
				int startY = position.y;
				int mouseX = endPoints.x;
				int mouseY = endPoints.y;
				if (!(type.equals(DRAWMODE.FreeDraw) || type.equals(DRAWMODE.Line))) {
					int x = (mouseX < startX) ? mouseX : startX;
					int y = (mouseY < startY) ? mouseY : startY;
					this.position = new Point(x, y);
					int width = (mouseX > startX) ? mouseX - startX : startX - mouseX;
					int height = (mouseY > startY) ? mouseY - startY : startY - mouseY;
					this.size = new Point(width, height);
				} else {
					this.position = position;
					this.size = endPoints;
				}
				this.color = color;
			}
		}
	}
	
}
