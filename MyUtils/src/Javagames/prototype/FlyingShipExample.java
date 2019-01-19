package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class FlyingShipExample extends SimpleFramework {
    //创建一个由方向键控制的飞行的飞船实例，空格键发射子弹
    private PrototypeShip ship;
    private PolygonWrapper wrapper;
    private ArrayList<PrototypeBullet> bullets;

    public FlyingShipExample() {
        appBorderScale = 0.9f;
        appWidth = 640;
        appHeight = 640;
        appMaintainRatio = true;
        appSleep = 1L;
        appTitle = "Flying Ship Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
        bullets = new ArrayList<PrototypeBullet>();
        wrapper = new PolygonWrapper(appWorldWidth, appWorldHeight);
        ship = new PrototypeShip(wrapper);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDown(KeyEvent.VK_LEFT)) {
            ship.rotateLeft(delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_RIGHT)) {
            ship.rotateRight(delta);
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            bullets.add(ship.launchBullet());
        }
        ship.setThrusting(keyboard.keyDown(KeyEvent.VK_UP));
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        ship.update(delta);
        ArrayList<PrototypeBullet> copy
                = new ArrayList<PrototypeBullet>(bullets);
        for (PrototypeBullet bullet : copy) {
            bullet.update(delta);
            if (wrapper.hasLeftWorld(bullet.getPosition())) {
                bullets.remove(bullet);
            }
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("Rotate: Left/Right Arrow", 30, 45);
        g.drawString("Thrust: Up Arrow", 30, 60);
        g.drawString("Fire: Space Bar", 30, 75);
        Matrix3x3f view = getViewportTransform();
        ship.draw((Graphics2D) g, view);
        for (PrototypeBullet b : bullets) {
            b.draw((Graphics2D) g, view);
        }
    }

    public static void main(String[] args) {
        launchApp(new FlyingShipExample());
    }
}
