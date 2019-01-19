package Javagames.completegame.object;

import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;

public class Bullet {
    private Vector2f velocity;
    private Vector2f position;
    private Color color;
    private float radius;

    public Bullet(Vector2f position, float angle) {
        this.position = position;
        velocity = Vector2f.polar(angle, 1.0f);
        radius = 0.006f;
        color = Color.GREEN;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        Utility.fillOval(g, position, radius, view, color);
    }

    public void update(float time) {
        position = position.add(velocity.mul(time));
    }
}
