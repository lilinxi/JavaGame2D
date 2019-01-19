package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class PrototypeGame extends SimpleFramework {
    //原型游戏
    private static final int STAR_COUNT = 1500;
    private static final int ASTEROID_COUNT = 4;
    private PrototypeShip ship;
    private PolygonWrapper wrapper;
    private PrototypeAsteroidFactory factory;
    private ArrayList<PrototypeBullet> bullets;
    private ArrayList<PrototypeAsteroid> asteroids;
    private Random rand;
    private Vector2f[] stars;
    private Color[] colors;

    public PrototypeGame() {
        appBorderScale = 0.9f;
        appWidth = 640;
        appHeight = 640;
        appMaintainRatio = true;
        appSleep = 1L;
        appTitle = "Prototype Game";
    }

    @Override
    protected void initialize() {
        super.initialize();
        //create game objects
        rand = new Random();
        bullets = new ArrayList<PrototypeBullet>();
        asteroids = new ArrayList<PrototypeAsteroid>();
        wrapper = new PolygonWrapper(appWorldWidth, appWorldHeight);
        ship = new PrototypeShip(wrapper);
        factory = new PrototypeAsteroidFactory(wrapper);
        createStars();
        createAsteroids();
    }

    //this creates the random stars for the background
    private void createStars() {
        stars = new Vector2f[STAR_COUNT];
        colors = new Color[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            float x = rand.nextFloat() * 2.0f - 1.0f;
            float y = rand.nextFloat() * 2.0f - 1.0f;
            stars[i] = new Vector2f(x, y);
            float color = rand.nextFloat();
            colors[i] = new Color(color, color, color);
        }
    }

    //create the random asteroids
    private void createAsteroids() {
        asteroids.clear();
        for(int i=0;i<ASTEROID_COUNT;i++) {
            Vector2f position = getAsteroidStartPosition();
            asteroids.add(factory.createLargeAsteroid(position));
        }
    }

    //create random position for an asteroid
    //陨石的初始位置在半径为minimum的圆外，防止游戏一开始飞船便撞到陨石
    private Vector2f getAsteroidStartPosition() {
        float angle = (float) Math.toRadians(rand.nextInt(360));
        float minimum = appWorldWidth / 4.0f;
        float extra = rand.nextFloat() * minimum;
        float radius = minimum + extra;
        return Vector2f.polar(angle, radius);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        //fly the ship
        if (keyboard.keyDown(KeyEvent.VK_LEFT)) {
            ship.rotateLeft(delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_RIGHT)) {
            ship.rotateRight(delta);
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            bullets.add(ship.launchBullet());
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE)) {
            createAsteroids();
        }
        ship.setThrusting(keyboard.keyDown(KeyEvent.VK_UP));
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        updateAsteroids(delta);
        updateBullets(delta);
        updateShip(delta);
    }

    private void updateAsteroids(float delta) {
        for (PrototypeAsteroid asteroid : asteroids) {
            asteroid.update(delta);
        }
    }

    private void updateBullets(float delta) {
        ArrayList<PrototypeBullet> copy =
                new ArrayList<PrototypeBullet>(bullets);
        //以为foreach中药移除项，所以必须事先创建一个副本
        for (PrototypeBullet bullet : copy) {
            updateBullet(delta, bullet);
        }
    }

    //check for bullet collisions
    private void updateBullet(float delta, PrototypeBullet bullet) {
        bullet.update(delta);
        if (wrapper.hasLeftWorld(bullet.getPosition())) {
            bullets.remove(bullet);
        } else {
            ArrayList<PrototypeAsteroid> ast =
                    new ArrayList<PrototypeAsteroid>(asteroids);
            for (PrototypeAsteroid asteroid : ast) {
                if (asteroid.contains(bullet.getPosition())) {
                    bullets.remove(bullet);
                    asteroids.remove(asteroid);
                    spawnBabies(asteroid);
                }
            }
        }
    }

    //create smaller asteroids when one is broken apart
    private void spawnBabies(PrototypeAsteroid asteroid) {
        if (asteroid.getSize() == PrototypeAsteroid.Size.Large) {
            asteroids.add(factory.createMediumAsteroid(asteroid.getPosition()));
            asteroids.add(factory.createMediumAsteroid(asteroid.getPosition()));
        }
        if (asteroid.getSize() == PrototypeAsteroid.Size.Medium) {
            asteroids.add(factory.createSmallAsteroid(asteroid.getPosition()));
            asteroids.add(factory.createSmallAsteroid(asteroid.getPosition()));
        }
    }

    //update the ship object
    private void updateShip(float delta) {
        ship.update(delta);
        boolean isHit = false;
        for (PrototypeAsteroid asteroid : asteroids) {
            if (ship.isTouching(asteroid)) {
                isHit = true;
            }
        }
        ship.setDamaged(isHit);
    }

    @Override
    protected void render(Graphics g) {
        //render instructions
        super.render(g);
        g.drawString("Rotate: Left/Right Arrow", 30, 45);
        g.drawString("Thrust: Up Arrow", 30, 60);
        g.drawString("Fire: Space Bar", 30, 75);
        g.drawString("Press ESC to respawn", 30, 90);
        Graphics2D g2d = (Graphics2D) g;
        //开启抗锯齿功能，以使线条绘制的得更为平滑
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //draw game object
        Matrix3x3f view = getViewportTransform();
        drawStars(g2d, view);
        drawAsteroids(g2d, view);
        drawBullets(g2d, view);
        drawShip(g2d, view);
    }

    private void drawStars(Graphics2D g, Matrix3x3f view) {
        for(int i=0;i<stars.length;i++) {
            Utility.drawPolygon(g, new Vector2f[]{stars[i]}, view, colors[i]);
        }
    }

    private void drawAsteroids(Graphics2D g, Matrix3x3f view) {
        for (PrototypeAsteroid asteroid : asteroids) {
            asteroid.draw(g, view);
        }
    }

    private void drawBullets(Graphics2D g, Matrix3x3f view) {
        for (PrototypeBullet b : bullets) {
            b.draw(g, view);
        }
    }

    private void drawShip(Graphics2D g, Matrix3x3f view) {
        ship.draw(g, view);
    }

    public static void main(String[] args) {
        launchApp(new PrototypeGame());
    }
}
