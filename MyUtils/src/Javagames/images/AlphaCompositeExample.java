package Javagames.images;

import Javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class AlphaCompositeExample extends SimpleFramework {
    //展示了不同的alpha混合模式
    //在目标图像上绘制源图像
    /**
     * AlphaComposite.SRC,源图像完全覆盖目标图像
     * AlphaComposite.DST,目标图像完全覆盖源图像
     * AlphaComposite.SRC_IN,只绘制源色在目标色不透明区域中的部分
     * AlphaComposite.DST_IN,只绘制目标色在源色不透明区域中的部分
     * AlphaComposite.SRC_OUT,只绘制源色在目标色不透明区域之外的部分
     * AlphaComposite.DST_OUT,只绘制目标色在源色不透明区域之外的部分
     * AlphaComposite.SRC_OVER,全部绘制，但源色在目标色之上，源色存在透明度时，重叠部分显示混合颜色
     * AlphaComposite.DST_OVER,全部绘制，但目标色在源色之上，目标色存在透明度时，重叠部分显示混合颜色
     * AlphaComposite.SRC_ATOP,只在目标色不透明区域上绘制，且源色在目标色之上，源色存在透明度时，重叠部分显示混合颜色
     * AlphaComposite.DST_ATOP,只在源色不透明区域上绘制，且目标色在源色之上，目标色存在透明度时，重叠部分显示混合颜色
     * AlphaComposite.XOR,只绘制源色和目标色在彼此不透明区域之外的部分，透明区域只显示另一种颜色
     * AlphaComposite.CLEAR,源色和目标色都不绘制
     */
    private String[] compositeName = {
            "SRC",//将源色复制到目标色
            "DST",//目标色未修改
            "SRC_IN",//只复制在目标色中的源色
            "DST_IN",//只复制在源色中的目标色
            "SRC_OUT",//目标色只被外部的源色所替代
            "DST_OUT",//只复制目标色的外部部分
            "SRC_OVER",//源色复制到目标色之上
            "DST_OVER",//目标色复制到源色之上
            "SRC_ATOP",//目标色内部的源色，复制到目标色之上
            "DST_ATOP",//源色内部的目标色，复制到源色之上
            "XOR",//复制源色和目标色彼此之外的那部分
            "CLEAR",//目标色和目标alpha值都被清除
    };

    private int[] compositeRule = {
            AlphaComposite.SRC,
            AlphaComposite.DST,
            AlphaComposite.SRC_IN,
            AlphaComposite.DST_IN,
            AlphaComposite.SRC_OUT,
            AlphaComposite.DST_OUT,
            AlphaComposite.SRC_OVER,
            AlphaComposite.DST_OVER,
            AlphaComposite.SRC_ATOP,
            AlphaComposite.DST_ATOP,
            AlphaComposite.XOR,
            AlphaComposite.CLEAR,
    };

    private int compositeIndex;
    private float srcAlpha;
    private float dstAlpha;
    private float extAlpha;
    private BufferedImage sprite;
    private BufferedImage sourceImage;
    private BufferedImage destinationImage;

    public AlphaCompositeExample() {
        appBackground = Color.DARK_GRAY;
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Alpha Composite Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
        srcAlpha = 1.0f;//源Alpha值
        dstAlpha = 1.0f;//目标Alpha值
        extAlpha = 1.0f;//额外Alpha值
        sprite = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
        sourceImage = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
        destinationImage = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
        createImages();
    }

    private void createImages() {
        /**
         * public abstract void setComposite(Composite comp)
         * 为 Graphics2D 上下文设置 Composite。
         * Composite 用于所有绘制方法中，如 drawImage、drawString、draw 和 fill。
         * 它指定新的像素如何在呈现过程中与图形设备上的现有像素组合
         */
        /**
         * public static AlphaComposite getInstance(int rule,
         *                                          float alpha)
         * 创建一个 AlphaComposite 对象，它具有指定的规则和用来乘源色 alpha 值的常量 alpha 值。
         * 在将源色与目标色合成前，要将源色乘以指定的 alpha 值
         */
        //source image
        Graphics2D g2d = sourceImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g2d.fillRect(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        Polygon p = new Polygon();
        p.addPoint(0, 0);
        p.addPoint(sourceImage.getWidth(), 0);
        p.addPoint(sourceImage.getWidth(), (int) (sourceImage.getHeight() / 1.5));
        g2d.setColor(new Color(1.0f, 1.0f, 0.0f, srcAlpha));
        g2d.fill(p);
        g2d.dispose();
        //destination image
        g2d = destinationImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g2d.fillRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        p = new Polygon();
        p.addPoint(0, 0);
        p.addPoint(destinationImage.getWidth(), 0);
        p.addPoint(0, (int) (destinationImage.getHeight() / 1.5));
        g2d.setColor(new Color(0.0f, 0.0f, 1.0f, dstAlpha));
        g2d.fill(p);
        //在目标图像上绘制源图像
        int rule = compositeRule[compositeIndex];
        g2d.setComposite(AlphaComposite.getInstance(rule, extAlpha));
        g2d.drawImage(sourceImage, 0, 0, null);
        g2d.dispose();
        //checkerboard background
        g2d = sprite.createGraphics();
        int dx = (sprite.getWidth()) / 8;
        int dy = (sprite.getHeight()) / 8;
        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                g2d.setColor((i + j) % 2 == 0 ? Color.BLACK : Color.WHITE);
                g2d.fillRect(i * dx, j * dy, dx, dy);
            }
        }
        g2d.drawImage(destinationImage, 0, 0, null);
        g2d.dispose();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            compositeIndex--;
            if (compositeIndex < 0) {
                compositeIndex = compositeRule.length - 1;
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
            compositeIndex++;
            if (compositeIndex > compositeRule.length - 1) {
                compositeIndex = 0;
            }
        }
        if (keyboard.keyDown(KeyEvent.VK_A)) {
            srcAlpha = dec(srcAlpha, delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_Q)) {
            srcAlpha = inc(srcAlpha, delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_S)) {
            dstAlpha = dec(dstAlpha, delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_W)) {
            dstAlpha = inc(dstAlpha, delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_D)) {
            extAlpha = dec(extAlpha, delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_E)) {
            extAlpha = inc(extAlpha, delta);
        }
        createImages();
    }

    private float dec(float val, float delta) {
        val -= 0.5f * delta;
        if (val < 0.0f) {
            val = 0.0f;
        }
        return val;
    }

    private float inc(float val, float delta) {
        val += 0.5f * delta;
        if (val > 1.0f) {
            val = 1.0f;
        }
        return val;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D g2d = (Graphics2D) g;
        int xPos = 30;
        int yPos = 45;
        g2d.drawString("UP/DOWN Arrow to select", xPos, yPos);
        yPos += 15;
        for(int i=0;i<compositeName.length;i++) {
            if (i == compositeIndex) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.GREEN);
            }
            g2d.drawString(compositeName[i], xPos, yPos);
            yPos += 15;
        }
        g2d.setColor(Color.GREEN);
        yPos += 15;
        g2d.drawString(String.format("Q|A : SRC_ALPHA=%.4f", srcAlpha), xPos, yPos);
        yPos += 15;
        g2d.drawString(String.format("W|S : DST_ALPHA=%.4f", dstAlpha), xPos, yPos);
        yPos += 15;
        g2d.drawString(String.format("E|D : EXT_ALPHA=%.4f", extAlpha), xPos, yPos);
        yPos += 15;
        int x = (canvas.getWidth() - destinationImage.getWidth() - 50);
        int y = (canvas.getHeight() - destinationImage.getHeight()) / 2;
        g2d.drawImage(sprite, x, y, null);
    }

    public static void main(String[] args) {
        launchApp(new AlphaCompositeExample());
    }
}
