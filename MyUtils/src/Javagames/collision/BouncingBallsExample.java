package Javagames.collision;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class BouncingBallsExample extends SimpleFramework {
    //小球与矩形边框碰撞的示例
    private static final float WORLD_LEFT = -1.0f;
    private static final float WORLD_RIGHT = 1.0f;
    private static final float WORLD_TOP = 1.0f;
    private static final float WORLD_BOTTOM = -1.0f;
    private static final int MAX_BALLS = 4096;
    private static final int MIN_BALLS = 4;
    private static final int MULTIPLE = 2;

    class Ball {
        Vector2f position;
        Vector2f velocity;
        float radius;
        Color color;
    }

    private Ball[] balls;
    private int ballCount;

    public BouncingBallsExample() {
        appTitle = "Bouncing Balls Example";
        appHeight = 640;
        appWidth = 640;
        appFPSColor = Color.WHITE;
        appBorder = Color.ORANGE;
        appBackground = Color.DARK_GRAY;
        appWorldWidth = 2.0f;
        appWorldHeight = 2.0f;
        appSleep = 1L;
        appMaintainRatio = true;
        appBorderScale = 0.9f;
    }

    @Override
    protected void initialize() {
        super.initialize();
        balls = new Ball[0];
        ballCount = 64;
    }

    private void createBalls() {
        Random rand = new Random();
        balls = new Ball[ballCount];
        for(int i=0;i<balls.length;i++) {
            balls[i] = new Ball();
            balls[i].velocity =
                    new Vector2f(rand.nextFloat(), rand.nextFloat());
            float r = 0.025f + rand.nextFloat() / 8.0f;
            balls[i].position = new Vector2f(WORLD_LEFT - r, WORLD_TOP - r);
            balls[i].radius = r;
            float color = rand.nextFloat();
            balls[i].color = new Color(color, color, color);
        }
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            createBalls();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            ballCount *= MULTIPLE;
            if (ballCount > MAX_BALLS) {
                ballCount = MAX_BALLS;
            }
            createBalls();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
            ballCount /= MULTIPLE;
            if (ballCount < MIN_BALLS) {
                ballCount = MIN_BALLS;
            }
            createBalls();
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        for (Ball ball : balls) {
            ball.position = ball.position.add(ball.velocity.mul(delta));
            if (ball.position.x - ball.radius < WORLD_LEFT) {
                ball.position.x = WORLD_LEFT + ball.radius;
                ball.velocity.x = -ball.velocity.x;
            } else if (ball.position.x + ball.radius > WORLD_RIGHT) {
                ball.position.x = WORLD_RIGHT - ball.radius;
                ball.velocity.x = -ball.velocity.x;
            }
            if (ball.position.y + ball.radius > WORLD_TOP) {
                ball.position.y = WORLD_TOP - ball.radius;
                ball.velocity.y = -ball.velocity.y;
            } else if (ball.position.y - ball.radius < WORLD_BOTTOM) {
                ball.position.y = WORLD_BOTTOM + ball.radius;
                ball.velocity.y = -ball.velocity.y;
            }
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        for (Ball ball : balls) {
            drawOval(g, ball);
        }
        g.setColor(Color.WHITE);
        //draw direction on top of bouncing balls
        textPos = Utility.drawString(g, 20, textPos,
                "Ball Count: " + ballCount,
                "Press [SPACE] to launch",
                "Press Up arrow to increase ball count",
                "Press Down arrow to decrease ball count");
    }

    private void drawOval(Graphics g, Ball ball) {
        Matrix3x3f view = getViewportTransform();
        Vector2f center = ball.position;
        float radius = ball.radius;
        Vector2f topLeft =
                new Vector2f(center.x - radius, center.y + radius);
        topLeft = view.mul(topLeft);
        Vector2f bottomRight =
                new Vector2f(center.x + radius, center.y - radius);
        bottomRight = view.mul(bottomRight);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleWidth = (int) (bottomRight.x - topLeft.x);
        int circleHeight = (int) (bottomRight.y - topLeft.y);
        g.setColor(ball.color);
        g.fillOval(circleX, circleY, circleWidth, circleHeight);
    }

    public static void main(String[] args) {
        launchApp(new BouncingBallsExample());
    }
}
