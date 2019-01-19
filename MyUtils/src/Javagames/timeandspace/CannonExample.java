package Javagames.timeandspace;

import Javagames.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class CannonExample extends JFrame implements Runnable {
    //绘制大炮和炮弹，根据时间增量模仿重力
    //采取世界坐标向屏幕坐标转换的方式，不保持高宽比
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private Canvas canvas;

    private Vector2f[] cannon;
    private Vector2f[] cannonCpy;
    private float cannonRot, cannonDelta;

    private Vector2f bullet;
    private Vector2f bulletCpy;
    private Vector2f velocity;

    public CannonExample() {}

    protected void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setSize(640, 480);
        canvas.setBackground(Color.WHITE);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Cannon Example");
        setIgnoreRepaint(true);
        pack();
        //Add key listener
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);
        //Add mouse listener
        //For full screen : mouse=new RelativeMouseInput(this);
        mouse = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);
        setVisible(true);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        running=true;
        initialize();
        //返回最准确的可用系统计时器的当前值，以纳秒为单位。
        //此方法只能用于测量已过的时间，与系统或钟表时间的其他任何时间概念无关。
        // 返回值表示从某一固定但任意的时间算起的毫微秒数（或许从以后算起，所以该值可能为负）。
        long curTime = System.nanoTime();
        long lastTime = curTime;
        double nsPerFrame;
        while (running) {
            curTime = System.nanoTime();
            nsPerFrame = curTime - lastTime;
            gameLoop(nsPerFrame / 1.0E9);
            lastTime = curTime;
        }
    }

    private void initialize() {
        frameRate = new FrameRate();
        frameRate.initialize();
        velocity = new Vector2f();
        cannonRot = 0.0f;
        cannonDelta = (float) Math.toRadians(90.0);
        cannon = new Vector2f[]{
                new Vector2f(-0.5f, 0.125f),
                new Vector2f(0.5f, 0.125f),
                new Vector2f(0.5f, -0.125f),
                new Vector2f(-0.5f, -0.125f),
        };
        cannonCpy = new Vector2f[cannon.length];
        Matrix3x3f scale = Matrix3x3f.scale(.75f, .75f);
        for(int i=0;i<cannon.length;i++) {
            cannon[i] = scale.mul(cannon[i]);
        }
    }

    private void gameLoop(double delta) {
        processInput(delta);
        updateObject(delta);
        renderFrame();
        sleep(10L);
    }

    private void renderFrame() {
        do{
            do{
                Graphics g=null;
                try{
                    g=bs.getDrawGraphics();
                    //渲染代码
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                }finally {
                    if(g!=null){
                        g.dispose();
                    }
                }
            }while(bs.contentsRestored());//返回绘制缓冲区最近是否从丢失状态恢复，并重新初始化为默认背景色（白色）。
            bs.show();
        }while(bs.contentsLost());//返回上次调用 getDrawGraphics 后绘制缓冲区是否丢失。
    }

    private void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {}
    }

    private void processInput(double delta) {
        keyboard.poll();
        mouse.poll();
        if (keyboard.keyDown(KeyEvent.VK_A)) {
            cannonRot += cannonDelta * delta;
        }
        if (keyboard.keyDown(KeyEvent.VK_D)) {
            cannonRot -= cannonDelta * delta;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            //new velocity
            Matrix3x3f mat = Matrix3x3f.translate(7.0f, 0.0f);
            mat = mat.mul(Matrix3x3f.rotate(cannonRot));
            velocity = mat.mul(new Vector2f());
            //place bullet at cannon end
            mat = Matrix3x3f.translate(.375f, 0.0f);
            mat = mat.mul(Matrix3x3f.rotate(cannonRot));
            mat = mat.mul(Matrix3x3f.translate(-2.0f, -2.0f));
            bullet = mat.mul(new Vector2f());
        }
    }

    private void updateObject(double delta) {
        Matrix3x3f mat = Matrix3x3f.rotate(cannonRot);
        mat = mat.mul(Matrix3x3f.translate(-2.0f, -2.0f));
        for(int i=0;i<cannon.length;i++) {
            cannonCpy[i] = mat.mul(cannon[i]);
        }
        if (bullet != null) {
            velocity.y += -9.8f * delta;
            bullet.x += velocity.x * delta;
            bullet.y += velocity.y * delta;
            bulletCpy = new Vector2f(bullet);
            if (bullet.y < -2.5f) {
                bullet = null;
            }
        }
    }

    private void render(Graphics g) {
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        frameRate.calculate();
        g.drawString(frameRate.getFrameRate(), 30, 30);
        g.drawString("(A) to raise,(D) to lower", 30, 45);
        g.drawString("Press [SPACE] to fire cannon", 30, 60);
        String vel = String.format("Velocity(%.2f,%.2f)", velocity.x, velocity.y);
        g.drawString(vel, 30, 75);
        //世界为5*5
        float worldWidth = 5.0f;
        float worldHeight = 5.0f;
        float screenWidth = canvas.getWidth() - 1;
        float screenHeight = canvas.getHeight() - 1;
        float sx = screenWidth / worldWidth;
        float sy = screenHeight / worldHeight;
        float tx = screenWidth / 2.0f;
        float ty = screenHeight / 2.0f;
        Matrix3x3f viewport = Matrix3x3f.scale(sx, -sy);
        viewport = viewport.mul(Matrix3x3f.translate(tx, ty));
        for (int i = 0; i < cannon.length; i++) {
            cannonCpy[i] = viewport.mul(cannonCpy[i]);
        }
        drawPolygon(g, cannonCpy);
        g.setColor(Color.BLUE);
        if (bullet != null) {
            bulletCpy = viewport.mul(bulletCpy);
            g.fillRect((int) bulletCpy.x - 5, (int) bulletCpy.y - 5, 10, 10);
        }
    }

    private void drawPolygon(Graphics g, Vector2f[] polygon) {
        Vector2f P;
        Vector2f S = polygon[polygon.length - 1];
        for(int i=0;i<polygon.length;i++) {
            P = polygon[i];
            g.drawLine((int) S.x, (int) S.y, (int) P.x, (int) P.y);
            S = P;
        }
    }

    protected void onWindowClosing() {
        try {
            running = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        final CannonExample app = new CannonExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.onWindowClosing();
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGUI();
            }
        });
    }
}
