package Javagames.images;

import Javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Random;

public class ImageSpeedTest extends SimpleFramework {
    //使用BufferedImage和VolatileImage绘制图像的区别
    //BufferedImage适用于只在启动时加载图像
    //VolatileImage适用于在每一帧中重新绘制图像，其内容可能在任何时候丢失，但是速度更快
    private Random rand = new Random();
    private GraphicsConfiguration gc;
    private BufferedImage bi;
    private VolatileImage vi;

    private boolean realTime = true;
    private boolean bufferedImage = true;

    public ImageSpeedTest() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 0L;
        appTitle = "Image Speed Test";
    }

    @Override
    protected void initialize() {
        super.initialize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
        bi = gc.createCompatibleImage(appWidth, appHeight);
        createVolatileImage();
        renderToBufferedImage();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
            bufferedImage = !bufferedImage;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_R)) {
            realTime = !realTime;
        }
    }

    @Override
    protected void render(Graphics g) {
        if (bufferedImage) {
            renderToBufferedImage(g);
        } else if (realTime) {
            renderToVolatileImageEveryFrame(g);
        } else {
            renderToVolatileImage(g);
        }
        super.render(g);
        //spit out help
        g.drawString("(B)uffered Image: " + bufferedImage, 30, 45);
        g.drawString("(R)eal Time Rendering: " + realTime, 30, 60);
    }

    private void createVolatileImage() {
        if (vi != null) {
            vi.flush();
            vi = null;
        }
        vi = gc.createCompatibleVolatileImage(getWidth(), getHeight());
    }

    private void renderToBufferedImage() {
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(new Color(rand.nextInt()));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    private void renderToBufferedImage(Graphics g) {
        if (realTime) {
            //如果需要每一帧绘制，则重新绘制
            renderToBufferedImage();
        }
        g.drawImage(bi, 0, 0, null);
    }

    private void renderToVolatileImage() {
        Graphics2D g2d = vi.createGraphics();
        g2d.setColor(new Color(rand.nextInt()));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    private void renderToVolatileImage(Graphics g) {
        do {
            /**
             * 如果图像不需要验证，则返回 IMAGE_OK。
             * 如果图像需要恢复，则返回 IMAGE_RESTORED。恢复意味着图像内容可能已受到影响，并且图像可能需要重新呈现。
             * 如果图像与传入 validate 方法的 GraphicsConfiguration 对象不兼容，则返回 IMAGE_INCOMPATIBLE。
             * 不兼容意味着图像可能需要用新的 Component 或 GraphicsConfiguration 重新创建，
             * 以获得一个能够用此 GraphicsConfiguration 成功使用的图像。
             * 不兼容的图像不会检查是否需要恢复，因此在 IMAGE_INCOMPATIBLE 值返回后图像的状态不变，
             * 这个返回值与图像是否需要恢复无关。
             */
            int returnCode = vi.validate(gc);
            if (returnCode == VolatileImage.IMAGE_RESTORED) {
                //Contents need to be restored
                renderToVolatileImage();
            } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                //incompatible GraphicsConfig
                createVolatileImage();
                renderToVolatileImage();
            }
            g.drawImage(vi, 0, 0, null);
        } while (vi.contentsLost());
    }

    private void renderToVolatileImageEveryFrame(Graphics g) {
        do {
            int returnCode = vi.validate(gc);
            if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                //incompatible GraphicsConfig
                createVolatileImage();
            }
            g.drawImage(vi, 0, 0, null);
        } while (vi.contentsLost());
    }

    public static void main(String[] args) {
        launchApp(new ImageSpeedTest());
    }
}
