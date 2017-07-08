package ca.eekedu.Project_Freedom;

import ca.eekedu.Project_Freedom.Drawings.Drawing;
import org.dyn4j.dynamics.Body;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static ca.eekedu.Project_Freedom.MainGame.DRAWMODE;

public class Drawings extends HashMap<Integer, Drawing> {

	private static final long serialVersionUID = -4802139613225189806L;

    public static class Drawing extends Body {

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

			DrawObject(DRAWMODE type, Point position, Point endPoints, Color color) {
				this.type = type; this.position = position; this.endPoints = endPoints; this.color = color;
			}
		}
	}
	
}
