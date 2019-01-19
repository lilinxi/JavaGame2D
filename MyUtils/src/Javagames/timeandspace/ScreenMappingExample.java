package Javagames.timeandspace;

import Javagames.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class ScreenMappingExample extends JFrame implements Runnable {
    //首先在世界坐标中进行初始化，在绘制时再将世界坐标映射为屏幕坐标
    //屏幕映射，世界坐标到屏幕坐标
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private Canvas canvas;
    private Vector2f[] tri;
    private Vector2f[] triWorld;
    private Vector2f[] rect;
    private Vector2f[] rectWorld;

    public ScreenMappingExample() {}

    protected void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setSize(640, 480);
        canvas.setBackground(Color.WHITE);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Screen Mapping Example");
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
        tri = new Vector2f[]{
                new Vector2f(0.0f, 0.5f),
                new Vector2f(-0.5f, -0.5f),
                new Vector2f(0.5f, -0.5f),
        };
        triWorld = new Vector2f[tri.length];
        rect = new Vector2f[]{
                new Vector2f(-1.0f, 1.0f),
                new Vector2f(1.0f, 1.0f),
                new Vector2f(1.0f, -1.0f),
                new Vector2f(-1.0f, -1.0f),
        };
        rectWorld = new Vector2f[rect.length];
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
    }

    private void updateObject(double delta) {}

    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        frameRate.calculate();
        g.drawString(frameRate.getFrameRate(), 30, 30);
        //世界为2*2
        float worldWidth = 2.0f;
        float worldHeight = 2.0f;
        float screenWidth = canvas.getWidth() - 1;
        float screenHeight = canvas.getHeight() - 1;
        float sx = screenWidth / worldWidth;
        float sy = screenHeight / worldHeight;
        float tx = screenWidth / 2.0f;
        float ty = screenHeight / 2.0f;
        Matrix3x3f viewport = Matrix3x3f.scale(sx, -sy);
        viewport = viewport.mul(Matrix3x3f.translate(tx, ty));
        for (int i = 0; i < tri.length; i++) {
            triWorld[i] = viewport.mul(tri[i]);
        }
        drawPolygon(g, triWorld);
        for (int i = 0; i < rect.length; i++) {
            rectWorld[i] = viewport.mul(rect[i]);
        }
        drawPolygon(g, rectWorld);
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
        final ScreenMappingExample app = new ScreenMappingExample();
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
