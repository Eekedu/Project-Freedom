package ca.eekedu.Project_Freedom;

import static ca.eekedu.Project_Freedom.MainGame.*;
import ca.eekedu.Project_Freedom.Drawings.Drawing;
import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

public class Drawings extends HashMap<Integer, Drawing> {

	private static final long serialVersionUID = -4802139613225189806L;

	public static class Drawing {
		
		String drawingName = "";
		DrawObject[] objects = null;
		Drawing(String name, DrawObject[] objects){
			this.drawingName = name;
			this.objects = objects;
		}

		public static class DrawObject {
			DRAWMODE type = DRAWMODE.Line;
			Point position = new Point(0, 0);
			Point endPoints = new Point(0, 0);
			Color color = new Color(0, 0, 0);
			
			DrawObject(DRAWMODE type, Point position, Point endPoints, Color color){
				this.type = type; this.position = position; this.endPoints = endPoints; this.color = color;
			}
		}
	}
	
}