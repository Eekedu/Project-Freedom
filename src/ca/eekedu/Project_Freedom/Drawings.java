package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.TreeMap;

import static ca.eekedu.Project_Freedom.MainGame.DRAWMODE;

public class Drawings extends TreeMap<Integer, Drawing> {

	public static class Drawing extends SimulationBody {

        String drawingName = "";
        int weight = 0;
        BufferedImage screenshot;
        HashMap<Integer, DrawObject> objects = new HashMap<>();
		Drawing(String name, BufferedImage screenshot, HashMap<Integer, DrawObject> objects){
			this.drawingName = name;
			this.screenshot = screenshot;
			this.objects = objects;
		}

		public static class DrawObject {
			DRAWMODE type = DRAWMODE.Line;
			Point position = new Point(0, 0);
			Point endPoints = new Point(0, 0);
			Color color = new Color(0, 0, 0);

			DrawObject() {
			}
			DrawObject(DRAWMODE type, Point position, Point endPoints, Color color) {
				this.type = type; this.position = position; this.endPoints = endPoints; this.color = color;
			}
		}
	}
	
}
