package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class RandomAsteroidExample extends SimpleFramework {
    //使用PrototypeAsteroidFactory来创建陨石实例的一个例子
    private PrototypeAsteroidFactory factory;
    private ArrayList<PrototypeAsteroid> asteroids;
    private Random random;
    private static final int NUM = 30;

    public RandomAsteroidExample() {
        appBorderScale = 0.9f;
        appWidth = 640;
        appHeight = 640;
        appMaintainRatio = true;
        appSleep = 1L;
        appTitle = "Random Asteroids";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        random = new Random();
        asteroids = new ArrayList<PrototypeAsteroid>();
        PolygonWrapper wrapper = new PolygonWrapper(appWorldWidth, appWorldHeight);
        factory = new PrototypeAsteroidFactory(wrapper);
        createRandomAsteroids();
    }

    private void createRandomAsteroids() {
        asteroids.clear();
        for (int i = 0; i < NUM; i++) {
            asteroids.add(getRandomAsteroid());
        }
    }

    private PrototypeAsteroid getRandomAsteroid() {
        float x = random.nextFloat() * 2.0f - 1.0f;
        float y = random.nextFloat() * 2.0f - 1.0f;
        Vector2f position = new Vector2f(x, y);
        return factory.createAsteroid(position);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            createRandomAsteroids();
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        for (PrototypeAsteroid asteroid : asteroids) {
            asteroid.update(delta);
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("Press [SPACE] to respawn", 30, 45);
        Matrix3x3f view = getViewportTransform();
        for (PrototypeAsteroid asteroid : asteroids) {
            asteroid.draw((Graphics2D) g, view);
        }
    }

    public static void main(String[] args) {
        launchApp(new RandomAsteroidExample());
    }
}
