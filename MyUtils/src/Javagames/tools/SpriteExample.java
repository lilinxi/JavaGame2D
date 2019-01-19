package Javagames.tools;

import Javagames.util.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class SpriteExample extends WindowFramework {
    private Sprite sprite;
    private Vector2f pos;
    private Vector2f vel;
    private float rot;
    private float rotDelta;

    public SpriteExample() {
        appTitle = "Sprite Example";
        pos = new Vector2f();
        vel = new Vector2f(0.25f, -0.3f);
        rotDelta = (float) Math.toRadians(90.0);
    }

    @Override
    protected void initialize() {
        super.initialize();
        InputStream in = ResourceLoader.load(
                SpriteExample.class,
                "res/assets/images/large0.png",
                "/./res/assets/images/large0.png"
        );
        try {
            BufferedImage image = ImageIO.read(in);
            Vector2f topLeft = new Vector2f(-0.25f, 0.25f);
            Vector2f bottomRight = new Vector2f(0.25f, -0.25f);
            sprite = new Sprite(image, topLeft, bottomRight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        pos = pos.add(vel.mul(delta));
        if (pos.x < -appWorldWidth / 2.0f) {
            pos.x = -appWorldWidth / 2.0f;
            vel.x = -vel.x;
        } else if (pos.x > appWorldWidth / 2.0f) {
            pos.x = appWorldWidth / 2.0f;
            vel.x = -vel.x;
        }
        if (pos.y < -appWorldHeight / 2.0f) {
            pos.y = -appWorldHeight / 2.0f;
            vel.y = -vel.y;
        } else if (pos.y > appWorldHeight / 2.0f) {
            pos.y = appWorldHeight / 2.0f;
            vel.y = -vel.y;
        }
        rot += rotDelta * delta;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Utility.drawRect(g, new Vector2f(), new Vector2f(getScreenWidth() - 1, getScreenHeight() - 1), Color.GREEN);
        sprite.render((Graphics2D) g, getViewportTransform(), pos, rot);
    }

    public static void main(String[] args) {
        launchApp(new SpriteExample());
    }
}
