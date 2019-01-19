package Javagames.completegame.object;

import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;

public class Particle {
    private Vector2f pos;
    private Vector2f curPos;
    private Vector2f vel;
    private Vector2f curVel;
    private Color color;
    private float lifeSpan;
    private float time;
    private float radius;

    public Particle() {}

    public void setPosition(Vector2f pos) {
        this.pos = pos;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setVector(float angel, float r) {
        vel = Vector2f.polar(angel, r);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setLifeSpan(float lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public void update(float delta) {
        time += delta;
        curVel = vel.mul(time);
        curPos = pos.add(curVel);
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        Utility.drawOval(g, curPos, radius, view, color);
    }

    public boolean hasDied() {
        return time > lifeSpan;
    }
}
