package Javagames.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class SimpleFramework extends JFrame implements Runnable {
    private BufferStrategy bufferStrategy;
    private volatile boolean running;
    private Thread gameThread;
    //声明为protected以便子类可以覆盖他们
    protected FrameRate frameRate;
    protected Canvas canvas;
    protected RelativeMouseInput mouse;
    protected KeyboardInput keyboard;
    protected Color appBackground = Color.BLACK;
    protected Color appBorder = Color.LIGHT_GRAY;//保持高宽比时的边框颜色
    protected Color appFPSColor = Color.GREEN;
    protected Font appFont = new Font("Courier New", Font.BOLD, 20);
    protected String appTitle = "TBD-Title";
    protected int appWidth = 640;
    protected int appHeight = 480;
    protected float appWorldWidth = 2.0f;
    protected float appWorldHeight = 2.0f;
    protected long appSleep = 10L;
    protected boolean appMaintainRatio = false;//是否保持高宽比
    protected float appBorderScale = 0.8f;//保持高宽比时，游戏区和边框区的比例
    protected int textPos = 0;//new in text！

    public SimpleFramework() {}

    protected void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setBackground(appBackground);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        //设置窗口下次可见时应该出现的位置：本机窗口系统的默认位置，还是当前位置（由 getLocation 返回）。
        setLocationByPlatform(true);
        if (appMaintainRatio) {
            getContentPane().setBackground(appBorder);
            setSize(appWidth, appHeight);
            setLayout(null);
            getContentPane().addComponentListener(new ComponentAdapter() {
                /**
                 * Invoked when the component's size changes.
                 *
                 * @param e
                 */
                @Override
                public void componentResized(ComponentEvent e) {
                    onComponentResized(e);
                }
            });
        } else {
            canvas.setSize(appWidth, appHeight);
            pack();
        }
        setTitle(appTitle);
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);
        mouse = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);
        setVisible(true);
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        canvas.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    protected void onComponentResized(ComponentEvent e) {
        Dimension size = getContentPane().getSize();
        int vw = (int) (size.width * appBorderScale);
        int vh = (int) (size.height * appBorderScale);
        int vx = (size.width - vw) / 2;
        int vy = (size.height - vh) / 2;
        int newW = vw;
        int newH = (int) (vw * appWorldHeight / appWorldWidth);
        if (newH > vh) {
            newW = (int) (vh * appWorldWidth / appWorldHeight);
            newH = vh;
        }
        vx += (vw - newW) / 2;
        vy += (vh - newH) / 2;
        canvas.setLocation(vx, vy);
        canvas.setSize(newW, newH);
    }

    protected Matrix3x3f getViewportTransform() {
        return Utility.createViewport(
                appWorldWidth, appWorldHeight,
                canvas.getWidth(), canvas.getHeight());
    }

    protected Matrix3x3f getReverseViewportTransform() {
        return Utility.createReverseViewport(
                appWorldWidth, appWorldHeight,
                canvas.getWidth(), canvas.getHeight());
    }

    protected Vector2f getWorldMousePosition() {
        Matrix3x3f screenToWorld = getReverseViewportTransform();
        Point mousePos = mouse.getPosition();
        Vector2f screenPos = new Vector2f(mousePos.x, mousePos.y);
        return screenToWorld.mul(screenPos);
    }

    protected Vector2f getRelativeWorldMousePosition() {
        float sx = appWorldWidth / (canvas.getWidth() - 1);
        float sy = appWorldHeight / (canvas.getHeight() - 1);
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
                    //渲染代码
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
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
        //每次render在这里重置textPos的值
        textPos = Utility.drawString(g, 20, 0, frameRate.getFrameRate());
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

    protected static void launchApp(final SimpleFramework app) {
        app.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e
             */
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
