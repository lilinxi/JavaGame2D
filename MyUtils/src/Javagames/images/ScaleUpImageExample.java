package Javagames.images;

import Javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;

public class ScaleUpImageExample extends SimpleFramework {
    //探索不同的缩放图像的算法
    //使用双线性分布法
    private static final int IMG_WIDTH = 64;
    private static final int IMG_HEIGHT = 64;
    private BufferedImage sprite;
    //几种缩放算法
    private Image averaged;
    private double averagedSpeed;
    private BufferedImage nearestNeighbor;
    private double nearestSpeed;
    private BufferedImage bilinear;
    private double bilinearSpeed;
    private BufferedImage bicubic;
    private double bicubicSpeed;
    private BufferedImage stepDownBilinear;
    private double stepDownBilinearSpeed;
    private BufferedImage stepDownBicubic;
    private double stepDownBicubicSpeed;

    public ScaleUpImageExample() {
        appWidth = 900;
        appHeight = 830;
        appBackground = Color.DARK_GRAY;
        appSleep = 1L;
        appTitle = "Scale Up Image Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("Creating test image...");
        createTestImage();
        System.out.println("Generating Averaged Image");
        long start = System.nanoTime();
        for(int i=0;i<100;i++) {
            generateAveragedInstance();
        }
        long end = System.nanoTime();
        averagedSpeed = (end - start) / 1.0E6;//毫秒
        averagedSpeed /= 100;
        System.out.println("Generating Nearest Neighbor");
        start = System.nanoTime();
        for(int i=0;i<100;i++) {
            nearestNeighbor = scaleWithGraphics(
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );
        }
        end = System.nanoTime();
        nearestSpeed = (end - start) / 1.0E6;
        nearestSpeed /= 100;
        System.out.println("Generating Bilinear");
        start = System.nanoTime();
        for(int i=0;i<100;i++) {
            bilinear = scaleWithGraphics(
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
        }
        end = System.nanoTime();
        bilinearSpeed = (end - start) / 1.0E6;
        bilinearSpeed /= 100;
        System.out.println("Generating Bicubic");
        start = System.nanoTime();
        for(int i=0;i<100;i++) {
            bicubic = scaleWithGraphics(
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
        }
        end = System.nanoTime();
        bicubicSpeed = (end - start) / 1.0E6;
        bicubicSpeed /= 100;
        System.out.println("Generating Step Down Bilinear");
        start = System.nanoTime();
        for(int i=0;i<100;i++) {
            stepDownBilinear = scaleDownImage(
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
        }
        end = System.nanoTime();
        stepDownBilinearSpeed = (end - start) / 1.0E6;
        stepDownBilinearSpeed /= 100;
        System.out.println("Generating Step Down Bicubic");
        start = System.nanoTime();
        for(int i=0;i<100;i++) {
            stepDownBicubic = scaleDownImage(
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
        }
        end = System.nanoTime();
        stepDownBicubicSpeed = (end - start) / 1.0E6;
        stepDownBicubicSpeed /= 100;
    }

    private void createTestImage() {
        sprite = new BufferedImage(
                IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = sprite.createGraphics();
        //draw checker board
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMG_WIDTH / 2, IMG_HEIGHT / 2);
        g2d.fillRect(IMG_WIDTH / 2, IMG_HEIGHT / 2, IMG_WIDTH / 2, IMG_HEIGHT / 2);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(IMG_WIDTH / 2, 0, IMG_WIDTH / 2, IMG_HEIGHT / 2);
        g2d.fillRect(0, IMG_HEIGHT / 2, IMG_WIDTH / 2, IMG_HEIGHT / 2);
        //draw red diamond
        g2d.setColor(Color.RED);
        g2d.drawLine(0, sprite.getHeight() / 2, sprite.getWidth() / 2, 0);
        g2d.drawLine(sprite.getWidth() / 2, 0, sprite.getWidth(), sprite.getHeight() / 2);
        g2d.drawLine(sprite.getWidth(), sprite.getHeight() / 2, sprite.getWidth() / 2, sprite.getHeight());
        g2d.drawLine(sprite.getWidth() / 2, sprite.getHeight(), 0, sprite.getHeight() / 2);
        //draw circle
        g2d.drawOval(0, 0, sprite.getWidth(), sprite.getHeight());
        //draw hash lines
        g2d.setColor(Color.GREEN);
        int dx = sprite.getWidth() / 4;
        for(int i=0;i<sprite.getWidth();i+=dx) {
            g2d.drawLine(i + 7, 0, i + 7, sprite.getHeight());
        }
        g2d.setColor(Color.BLUE);
        dx = sprite.getHeight() / 4;
        for (int i = 0; i < sprite.getHeight(); i += dx) {
            g2d.drawLine(0, i + 7, sprite.getWidth(), i + 7);
        }
        //gradient circle
        float x1 = sprite.getWidth() / 4;
        float y1 = sprite.getHeight() / 4;
        float x2 = sprite.getWidth() * 3 / 4;
        float y2 = sprite.getHeight() * 3 / 4;
        //GradientPaint 类提供了使用线性颜色渐变模式填充 Shape 的方法。
        GradientPaint gp = new GradientPaint(
                x1, y1, Color.BLACK, x2, y2, Color.WHITE
        );
        g2d.setPaint(gp);
        g2d.fillOval(
                sprite.getWidth() / 4, sprite.getHeight() / 4,
                sprite.getWidth() / 2, sprite.getHeight() / 2
        );
        g2d.dispose();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //Test Image
        g.drawImage(
                sprite, (canvas.getWidth() - sprite.getWidth()) / 2, 50, null
        );
        //public abstract int getWidth(ImageObserver observer)确定图像的宽度。
        // 如果宽度未知，则此方法返回 -1，然后通知指定的 ImageObserver 对象。
        //参数：
        //observer - 等待加载图像的对象
        int sw = averaged.getWidth(null);
        int sh = averaged.getHeight(null);
        int pos = sprite.getHeight() + 100;
        int textPos = pos + sh;
        //Averaged Image
        int imgPos = (sw + 50) * 0 + 50;
        g.drawImage(averaged, imgPos, pos, null);
        String time = String.format("%.4f ms", averagedSpeed);
        g.drawString("Averaged", imgPos, textPos + 15);
        g.drawString(time, imgPos, textPos + 30);
        //Nearest Image
        imgPos = (sw + 50) * 1 + 50;
        g.drawImage(nearestNeighbor, imgPos, pos, null);
        time = String.format("%.4f ms", nearestSpeed);
        g.drawString("Nearest", imgPos, textPos + 15);
        g.drawString(time, imgPos, textPos + 30);
        //Bilinear Image
        imgPos = (sw + 50) * 2 + 50;
        g.drawImage(bilinear, imgPos, pos, null);
        time = String.format("%.4f ms", bilinearSpeed);
        g.drawString("Bilinear", imgPos, textPos + 15);
        g.drawString(time, imgPos, textPos + 30);
        pos += nearestNeighbor.getHeight() + 100;
        textPos = pos + nearestNeighbor.getHeight();
        //Bicubic Image
        imgPos = (sw + 50) * 0 + 50;
        g.drawImage(bicubic, imgPos, pos, null);
        time = String.format("%.4f ms", bicubicSpeed);
        g.drawString("Bicubic", imgPos, textPos + 15);
        g.drawString(time, imgPos, textPos + 30);
        //Step Down Bilinear Image
        imgPos = (sw + 50) * 1 + 50;
        g.drawImage(stepDownBilinear, imgPos, pos, null);
        time = String.format("%.4f ms", stepDownBilinearSpeed);
        g.drawString("Bilinear-Step", imgPos, textPos + 15);
        g.drawString(time, imgPos, textPos + 30);
        //Step Down Bicubic Image
        imgPos = (sw + 50) * 2 + 50;
        g.drawImage(stepDownBicubic, imgPos, pos, null);
        time = String.format("%.4f ms", stepDownBicubicSpeed);
        g.drawString("Bicubic-Step", imgPos, textPos + 15);
        g.drawString(time, imgPos, textPos + 30);
    }

    private void generateAveragedInstance() {
        averaged = sprite.getScaledInstance(
                (int) (sprite.getWidth() * 3.7),
                (int) (sprite.getHeight() * 3.7),
                Image.SCALE_AREA_AVERAGING
        );
        averaged.getSource().startProduction(getConsumer());
    }

    private BufferedImage scaleWithGraphics(Object hintValue) {
        BufferedImage image = new BufferedImage(
                (int) (sprite.getWidth() * 3.7),
                (int) (sprite.getHeight() * 3.7),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hintValue);
        g2d.drawImage(sprite, 0, 0, image.getWidth(), image.getHeight(), null);
        g2d.dispose();
        return image;
    }

    private BufferedImage scaleDownImage(Object hintValue) {
        BufferedImage ret = sprite;
        int targetWidth = (int) (sprite.getWidth() * 3.7);
        int targetHeight = (int) (sprite.getHeight() * 3.7);
        int w = sprite.getWidth();
        int h = sprite.getHeight();
        do {
            w = (int) (w * 1.5);
            if (w > targetWidth) {
                w = targetWidth;
            }
            h = (int) (h * 1.5);
            if (h > targetHeight) {
                h = targetHeight;
            }
            BufferedImage tmp = new BufferedImage(
                    w, h, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = tmp.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hintValue);
            g2d.drawImage(ret, 0, 0, w, h, null);
            g2d.dispose();
            ret = tmp;
        } while (w != targetWidth || h != targetHeight);
        return ret;
    }

    private ImageConsumer getConsumer() {
        return new ImageConsumer() {
            @Override
            public void setDimensions(int width, int height) {}

            @Override
            public void setProperties(Hashtable<?, ?> props) {}

            @Override
            public void setColorModel(ColorModel model) {}

            @Override
            public void setHints(int hintflags) {}

            @Override
            public void setPixels(int x, int y, int w, int h, ColorModel model,
                                  byte[] pixels, int off, int scansize) {}

            @Override
            public void setPixels(int x, int y, int w, int h, ColorModel model,
                                  int[] pixels, int off, int scansize) {}

            @Override
            public void imageComplete(int status) {}
        };
    }

    public static void main(String[] args) {
        launchApp(new ScaleUpImageExample());
    }
}
