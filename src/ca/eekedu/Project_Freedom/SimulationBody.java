package ca.eekedu.Project_Freedom;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import static ca.eekedu.Project_Freedom.MainGame.RESOLUTION_HEIGHT;
import static ca.eekedu.Project_Freedom.MainGame.charBody;

/**
 * Custom Body class to add drawing functionality.
 *
 * @author William Bittle
 * @version 3.2.1
 * @since 3.0.0
 */
public class SimulationBody extends Body {
    /**
     * The color of the object
     */
    protected Color color;

    /**
     * Default constructor.
     */
    public SimulationBody() {
        // randomly generate the color
        this.color = new Color(
                (float) Math.random() * 0.5f + 0.5f,
                (float) Math.random() * 0.5f + 0.5f,
                (float) Math.random() * 0.5f + 0.5f);
    }

    /**
     * Constructor.
     *
     * @param color a set color
     */
    public SimulationBody(Color color) {
        this.color = color;
    }

    /**
     * Draws the body.
     * <p>
     * Only coded for polygons and circles.
     *
     * @param g     the graphics object to render to
     * @param scale the scaling factor
     */
    public void render(Graphics2D g, double scale) {
        this.render(g, scale, this.color);
    }

    public void setLocation(double x, double y) {
        this.setLocation(x, y, 0.0);
    }

    public void setLocation(double x, double y, double rotation) {
        this.setTransform(new Transform());
        this.rotate(rotation);
        this.translate(x * 10, RESOLUTION_HEIGHT - (y * 10));
    }

    /**
     * Draws the body.
     * <p>
     * Only coded for polygons and circles.
     *
     * @param g     the graphics object to render to
     * @param scale the scaling factor
     * @param color the color to render the body
     */
    public void render(Graphics2D g, double scale, Color color) {

        if (!(this.equals(charBody) || this.getMass().getType() == MassType.INFINITE)) {
            Transform f = this.getTransform();
            Transform c = charBody.getTransform();
            Vector2 diff = f.getTranslation().difference(c.getTranslation());
	        if (!(diff.x < 1500 && diff.x > -1500)) {
		        if (this.isActive()) {
			        this.setActive(false);
		        }
		        return;
	        } else {
		        if (!this.isActive()) {
			        this.setActive(true);
		        }
	        }
        }
        // point radius
        final int pr = 4;

        // save the original transform
        AffineTransform ot = g.getTransform();

        // transform the coordinate system from world coordinates to local coordinates
        AffineTransform lt = new AffineTransform();
        lt.translate(this.transform.getTranslationX() * scale, this.transform.getTranslationY() * scale);
        lt.rotate(this.transform.getRotation());

        // apply the transform
        g.transform(lt);

        // loop over all the body fixtures for this body
        for (BodyFixture fixture : this.fixtures) {
            this.renderFixture(g, scale, fixture, color);
        }

        // draw a center point
        Ellipse2D.Double ce = new Ellipse2D.Double(
                this.getLocalCenter().x * scale - pr * 0.5,
                this.getLocalCenter().y * scale - pr * 0.5,
                pr,
                pr);
        g.setColor(Color.WHITE);
        g.fill(ce);
        g.setColor(Color.DARK_GRAY);
        g.draw(ce);

        // set the original transform
        g.setTransform(ot);
    }

    /**
     * Renders the given fixture.
     *
     * @param g       the graphics object to render to
     * @param scale   the scaling factor
     * @param fixture the fixture to render
     * @param color   the color to render the fixture
     */
    protected void renderFixture(Graphics2D g, double scale, BodyFixture fixture, Color color) {
        // get the shape on the fixture
        Convex convex = fixture.getShape();

        // brighten the color if asleep
        if (this.isAsleep()) {
            color = color.brighter();
        }

        // render the fixture
        Graphics2DRenderer.render(g, convex, scale, color);
    }
}