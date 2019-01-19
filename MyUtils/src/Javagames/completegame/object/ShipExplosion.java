package Javagames.completegame.object;

import Javagames.util.Matrix3x3f;
import Javagames.util.Vector2f;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class ShipExplosion {
    private static final int MAX_PARTICLES = 150;
    private static final int MAX_RINGS = 5;
    private Vector<Particle> particles;
    private Random random = new Random();
    private Vector2f pos;
    private Vector<Color> colors;

    public ShipExplosion(Vector2f pos) {
        this.pos = pos;
        createParticles();
    }

    private void createColors() {
        colors = new Vector<Color>();
        colors.add(Color.WHITE);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);
        colors.add(Color.PINK);
    }

    private void createParticles() {
        createColors();
        particles = new Vector<Particle>();
        for(int ring=0;ring<MAX_RINGS;ring++) {
            float velocity = 0.25f + random.nextFloat() * 1.0f;
            float lifeSpan = random.nextFloat() * 1.0f;
            float radius = 0.003f + random.nextFloat() * 0.003f;
            for (int i = 0; i < MAX_PARTICLES; i++) {
                Particle p = new Particle();
                p.setPosition(pos);
                p.setRadius(radius);
                p.setLifeSpan(lifeSpan);
                p.setColor(colors.get(random.nextInt(colors.size())));
                float angel = (float) Math.toRadians(random.nextInt(360));
                p.setVector(angel, velocity);
                particles.add(p);
            }
        }
    }

    public void update(float delta) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(delta);
            if (p.hasDied()) {
                it.remove();
            }
        }
    }

    public void render(Graphics2D g, Matrix3x3f view) {
        for (Particle p : particles) {
            p.draw(g, view);
        }
    }

    public boolean isFinished() {
        return particles.size() == 0;
    }
}
