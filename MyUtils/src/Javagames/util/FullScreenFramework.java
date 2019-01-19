package Javagames.util;

import java.awt.*;
import java.awt.image.VolatileImage;

public class FullScreenFramework extends GameFramework {
    /**
     * FullScreenFramework位于Javagames.util包中，它的工作方式不同。
     * 它不仅需要改变显示模式，还会处理高宽比。
     * 这通过把屏幕渲染成为一个VolatileImage来完成，它可以很快地复制到缓存策略中。
     * createFramework（）方法创建了帧，保存了当前的显示模式，设置了输入，使用该泛型方法创建了缓存策略，修改了显示模式，
     * 并且，如果保持了高宽比的话，还创建了相应的VolatileImage。
     * 注意，getScreenWidth（）和getScreenHeight（）方法返回了帧大小或者VolatileImage大小。
     * renderFrame（）方法要么以前面的全屏框架相同的方式来渲染，要么使用VolatileImage来保持高宽比。
     * onShutDown（）方法将显示模式返回最初的大小。
     */
    /**
     * public abstract class GraphicsConfiguration extends Object
     * GraphicsConfiguration 类描述图形目标（如打印机或监视器）的特征。
     * 有许多与单一图形设备关联的 GraphicsConfiguration 对象，表示不同的绘图模式或能力。相应的本机结构也将因平台而异。
     */
    private static final int BIT_DEPTH = 32;
    private VolatileImage vi;
    private GraphicsConfiguration gc;
    private DisplayMode currentDisplayMode;

    @Override
    protected void createFramework() {
        setIgnoreRepaint(true);
        setUndecorated(true);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
        currentDisplayMode = gd.getDisplayMode();
        if (!gd.isFullScreenSupported()) {
            System.err.println("ERROR: Not Supported!!!");
            System.exit(0);
        }
        if (appMaintainRatio) {
            setBackground(appBorder);
            setupViewport(appWidth, appHeight);
            createVolatileImage();
        } else {
            setBackground(appBackground);
        }
        gd.setFullScreenWindow(this);
        gd.setDisplayMode(new DisplayMode(appWidth, appHeight, BIT_DEPTH, DisplayMode.REFRESH_RATE_UNKNOWN));
        setupInput(this);
        createBufferStrategy(this);
    }

    @Override
    public int getScreenWidth() {
        return appMaintainRatio ? vw : getWidth();
    }

    @Override
    public int getScreenHeight() {
        return appMaintainRatio ? vh : getHeight();
    }

    private void renderVolatileImage(Graphics g) {
        do {
            int returnCode = vi.validate(gc);
            if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                createVolatileImage();
            }
            Graphics2D g2d = null;
            try {
                g2d = vi.createGraphics();
                g2d.setBackground(appBackground);
                g2d.clearRect(0, 0, getScreenWidth(), getScreenHeight());
                render(g2d);
            }finally {
                if (g2d != null) {
                    g2d.dispose();
                }
            }
            g.drawImage(vi, vx, vy, null);
        } while (vi.contentsLost());
    }

    private void createVolatileImage() {
        if (vi != null) {
            vi.flush();
            vi = null;
        }
        vi = gc.createCompatibleVolatileImage(getScreenWidth(), getScreenHeight());
    }

    @Override
    protected void renderFrame(Graphics g) {
        if (appMaintainRatio) {
            g.clearRect(0, 0, getWidth(), getHeight());
            renderVolatileImage(g);
        } else {
            g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
            render(g);
        }
    }

    @Override
    protected void onShutDown() {
        super.onShutDown();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setDisplayMode(currentDisplayMode);
        gd.setFullScreenWindow(null);
    }
}
