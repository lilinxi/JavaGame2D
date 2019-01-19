package Javagames.completegame.object;

import Javagames.util.Matrix3x3f;
import Javagames.util.Vector2f;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public abstract class Explosion {
    protected Vector<Particle> particles;
    protected Vector2f pos;
    protected Random random = new Random();
    protected Vector<Color> colors;

    public Explosion(Vector2f pos) {
        this.pos = pos;
        createParticles();
    }

    protected abstract void createParticles();

    protected void update(float delta) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(delta);
            if (p.hasDied())
                it.remove();
        }
    }

    protected void render(Graphics2D g, Matrix3x3f view) {
        for (Particle p : particles) {
            p.draw(g, view);
        }
    }

    protected boolean isFinished() {
        return particles.size() == 0;
    }
}
