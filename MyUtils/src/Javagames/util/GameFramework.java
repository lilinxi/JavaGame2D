package Javagames.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public abstract class GameFramework extends JFrame implements Runnable {
    /**
     * GameFramework类位于Javagames.util包中，它把全屏和窗口框架组合到一个单一的框架中，并且带有钩子以允许使用子类来定制。
     */
    private BufferStrategy bufferStrategy;
    private volatile boolean running;
    private Thread gameThread;
    //声明为protected以便子类可以覆盖他们
    protected int vx;
    protected int vy;
    protected int vw;
    protected int vh;
    protected FrameRate frameRate;
    protected RelativeMouseInput mouse;
    protected KeyboardInput keyboard;
    protected Color appBackground = Color.BLACK;
    protected Color appBorder = Color.LIGHT_GRAY;
    protected Color appFPSColor = Color.GREEN;
    protected Font appFont = new Font("Courier New", Font.PLAIN, 14);
    protected String appTitle = "TBD-Title";
    protected float appBorderScale = 0.8f;
    protected int appWidth = 640;
    protected int appHeight = 640;
    protected float appWorldWidth = 2.0f;
    protected float appWorldHeight = 2.0f;
    protected long appSleep = 10L;
    protected boolean appMaintainRatio = false;
    protected boolean appDisableCursor = false;
    protected int textPos = 0;

    public GameFramework() {}

    protected abstract void createFramework();

    protected abstract void renderFrame(Graphics g);

    public abstract int getScreenWidth();

    public abstract int getScreenHeight();

    protected void createAndShowGUI() {
        createFramework();
        if (appDisableCursor) {
            disableCursor();
        }
        gameThread = new Thread(this);
        gameThread.start();
    }

    protected void setupInput(Component component) {
        keyboard = new KeyboardInput();
        component.addKeyListener(keyboard);
        mouse = new RelativeMouseInput(component);
        component.addMouseListener(mouse);
        component.addMouseMotionListener(mouse);
        component.addMouseWheelListener(mouse);
    }

    protected void createBufferStrategy(Canvas component) {
        component.createBufferStrategy(2);
        bufferStrategy = component.getBufferStrategy();
    }

    protected void createBufferStrategy(Window window) {
        window.createBufferStrategy(2);
        bufferStrategy = window.getBufferStrategy();
    }

    protected void setupViewport(int sw, int sh) {
        int w = (int) (sw * appBorderScale);
        int h = (int) (sh * appBorderScale);
        vw = w;
        vh = (int) (w * appWorldHeight / appWorldWidth);
        if (vh > h) {
            vw = (int) (h * appWorldWidth / appWorldHeight);
            vh = h;
        }
        vx = (sw - vw) / 2;
        vy = (sh - vh) / 2;
    }

    protected Matrix3x3f getViewportTransform() {
        return Utility.createViewport(
                appWorldWidth, appWorldHeight,
                getScreenWidth(), getScreenHeight());
    }

    protected Matrix3x3f getReverseViewportTransform() {
        return Utility.createReverseViewport(
                appWorldWidth, appWorldHeight,
                getScreenWidth(), getScreenHeight());
    }

    protected Vector2f getWorldMousePosition() {
        Matrix3x3f screenToWorld = getReverseViewportTransform();
        Point mousePos = mouse.getPosition();
        Vector2f screenPos = new Vector2f(mousePos.x, mousePos.y);
        return screenToWorld.mul(screenPos);
    }

    protected Vector2f getRelativeWorldMousePosition() {
        float sx = appWorldWidth / (getScreenWidth() - 1);
        float sy = appWorldHeight / (getScreenHeight() - 1);
        Matrix3x3f viewport = Matrix3x3f.scale(sx, -sy);
        Point p = mouse.getPosition();
        return viewport.mul(new Vector2f(p.x, p.y));
    }

    public void run() {
        running = true;
        initialize();
        long curTime = System.nanoTime();
        long lastTime = curTime;
        double nsPerTime;
        while (running) {
            curTime = System.nanoTime();
            nsPerTime = curTime - lastTime;
            gameLoop((float) (nsPerTime / 1.0E9));
            lastTime = curTime;
        }
        terminate();
    }

    protected void initialize() {
        frameRate = new FrameRate();
        frameRate.initialize();
    }

    protected void terminate() {}

    private void gameLoop(float delta) {
        processInput(delta);
        updateObject(delta);
        renderFrame();
        sleep(appSleep);
    }

    private void renderFrame() {
        do{
            do{
                Graphics g=null;
                try{
                    g=bufferStrategy.getDrawGraphics();
                    renderFrame(g);
                }finally {
                    if(g!=null){
                        g.dispose();
                    }
                }
            }while(bufferStrategy.contentsRestored());//返回绘制缓冲区最近是否从丢失状态恢复，并重新初始化为默认背景色（白色）。
            bufferStrategy.show();
        }while(bufferStrategy.contentsLost());//返回上次调用 getDrawGraphics 后绘制缓冲区是否丢失。
    }

    private void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {}
    }

    protected void processInput(float delta) {
        keyboard.poll();
        mouse.poll();
    }

    protected void updateObject(float delta) {}

    protected void render(Graphics g) {
        g.setFont(appFont);
        g.setColor(appFPSColor);
        frameRate.calculate();
        textPos = Utility.drawString(g, 20, 0, frameRate.getFrameRate());
    }

    private void disableCursor() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage("");
        Point point = new Point(0, 0);
        String name = "CanBeAnything";
        Cursor cursor = tk.createCustomCursor(image, point, name);
        setCursor(cursor);
    }

    protected void shutDown() {
        if (Thread.currentThread() != gameThread) {
            try {
                running = false;
                gameThread.join();
                onShutDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    shutDown();
                }
            });
        }
    }

    protected void onShutDown() {}

    protected static void launchApp(final GameFramework app) {
        app.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                app.shutDown();
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
