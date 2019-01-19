package Javagames.images;

import Javagames.util.SimpleFramework;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class FlyingSpritesExample extends SimpleFramework {
    //测试插值算法，渲染方法，抗锯齿，透明边框，绿色边框对图像绘制的影响
    //边框在processInput()中发生改变时需要重新绘制createSprite()
    //插值算法，渲染方法，抗锯齿在渲染时调用
    //两个最好的方法
    //1. 关闭Antialiasing，Bicubic Interpolation，Texture Paint带有透明边框的渲染
    //2. 打开Antialiasing，Bilinear Interpolation，Affine Transform带有透明边框的渲染
    //第二种帧速率更快
    //三种方法绘制精灵
    private static final int IMG_WIDTH = 256;
    private static final int IMG_HEIGHT = 256;
    private enum Interpolation{//插值算法：当图像旋转或缩放时，图像的颜色如何进行插值
        NearestNeighbor,//最近邻近法
        BiLinear,//双线性插值
        BiCubic,//双三次插值
    }
    private enum RotationMethod{//渲染图像的三个方法
        AffineTransform,
        AffineTransformOp,
        TexturePaint,
    }

    private boolean antialiased;//抗锯齿
    private boolean transparent;//透明边框
    private boolean greenBorder;//绿色边框
    private Interpolation interpolation;
    private RotationMethod rotationMethod;
    private BufferedImage sprite;
    private Vector2f[] positions;
    private float[] angles;
    private Vector2f[] velocities;
    private float[] rotations;

    public FlyingSpritesExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 0L;
        appTitle = "Flying Sprites Example";
        appBackground = Color.DARK_GRAY;
    }

    @Override
    protected void initialize() {
        super.initialize();
        positions = new Vector2f[]{
                new Vector2f(-0.15f, 0.3f),
                new Vector2f(0.15f, 0.0f),
                new Vector2f(0.25f, -0.3f),
                new Vector2f(-0.25f, -0.6f),
        };
        velocities = new Vector2f[]{
                new Vector2f(-0.04f, 0.0f),
                new Vector2f(-0.05f, 0.0f),
                new Vector2f(0.06f, 0.0f),
                new Vector2f(0.07f, 0.0f),
        };
        angles = new float[]{
                (float) Math.toRadians(0),
                (float) Math.toRadians(0),
                (float) Math.toRadians(0),
                (float) Math.toRadians(0),
        };
        rotations = new float[]{
                1.0f, 0.75f, 0.5f, 0.25f
        };
        antialiased = false;
        transparent = false;
        greenBorder = false;
        interpolation = Interpolation.NearestNeighbor;
        rotationMethod = RotationMethod.AffineTransform;
        createSprite();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_A)) {
            antialiased = !antialiased;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_I)) {
            Interpolation[] values = Interpolation.values();
            //public final int ordinal()
            // 返回枚举常量的序数（它在枚举声明中的位置，其中初始常量序数为零）。
            int index = (interpolation.ordinal() + 1) % values.length;
            interpolation = values[index];
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_T)) {
            transparent = !transparent;
            createSprite();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_R)) {
            RotationMethod[] methods = RotationMethod.values();
            int index = (rotationMethod.ordinal() + 1) % methods.length;
            rotationMethod = methods[index];
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_G)) {
            greenBorder = !greenBorder;
            createSprite();
        }
    }

    private void createSprite() {
        createCheckerboard();
        if (transparent) {
            addTransparentBorder();
        }
        if (greenBorder) {
            drawGreenBorder();
        }
    }

    private void createCheckerboard() {
        sprite = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = sprite.createGraphics();
        int dx = IMG_WIDTH / 8;
        int dy = IMG_HEIGHT / 8;
        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                if ((i + j) % 2 == 0) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.fillRect(i * dx, j * dy, dx, dy);
            }
        }
        g2d.dispose();
    }

    private void addTransparentBorder() {
        int borderWidth = IMG_WIDTH + 8;
        int borderHeight = IMG_HEIGHT + 8;
        BufferedImage newSprite = new BufferedImage(
                borderWidth, borderHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newSprite.createGraphics();
        g2d.drawImage(sprite, 4, 4, null);
        g2d.dispose();
        sprite = newSprite;
    }

    private void drawGreenBorder() {
        Graphics2D g2d = sprite.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.drawRect(0, 0, sprite.getWidth() - 1, sprite.getHeight() - 1);
        g2d.dispose();
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        for(int i=0;i<positions.length;i++) {
            positions[i] = positions[i].add(velocities[i].mul(delta));
            if (positions[i].x >= 1.0f) {
                positions[i].x = -1.0f;
            } else if (positions[i].x <= -1.0f) {
                positions[i].x = 1.0f;
            }
            if (positions[i].y >= 1.0f) {
                positions[i].y = -1.0f;
            } else if (positions[i].y <= -1.0f) {
                positions[i].y = 1.0f;
            }
            angles[i] += rotations[i] * delta;
        }
    }

    @Override
    protected void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setAntialasing(g2d);
        setInterpolation(g2d);
        switch (rotationMethod) {
            case AffineTransform:
                doAffineTransform(g2d);
                break;
            case AffineTransformOp:
                doAffineTransformOp(g2d);
                break;
            case TexturePaint:
                doTexturePaint(g2d);
                break;
        }
        super.render(g);
        g.drawString("(A)ntialiased: " + antialiased, 30, 45);
        g.drawString("(I)nterpolation: " + interpolation, 30, 60);
        g.drawString("(T)ransparent: " + transparent, 30, 75);
        g.drawString("(R)otationMethod: " + rotationMethod, 30, 90);
        g.drawString("(G)reenBorder: " + greenBorder, 30, 105);
    }

    private void setAntialasing(Graphics2D g2d) {
        if (antialiased) {
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
        } else {
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF
            );
        }
    }

    private void setInterpolation(Graphics2D g2d) {
        if (interpolation == Interpolation.NearestNeighbor) {
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );
        } else if (interpolation == Interpolation.BiLinear) {
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
        } else if (interpolation == Interpolation.BiCubic) {
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
        }
    }

    private AffineTransform createTransform(Vector2f position, float angle) {
        Vector2f screen = getViewportTransform().mul(position);;
        AffineTransform transform = AffineTransform.getTranslateInstance(screen.x, screen.y);
        transform.rotate(angle);
        transform.translate(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
        //仿射变换从下往上依次实现
        return transform;
    }

    private void doAffineTransform(Graphics2D g2d) {
        for(int i=0;i<positions.length;i++) {
            AffineTransform transform = createTransform(positions[i], angles[i]);
            g2d.drawImage(sprite, transform, null);
        }
    }

    private AffineTransformOp createTransformOp(Vector2f position, float angle) {
        AffineTransform transform = createTransform(position, angle);
        if (interpolation == Interpolation.NearestNeighbor) {
            return new AffineTransformOp(
                    transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR
            );
        } else if (interpolation == Interpolation.BiLinear) {
            return new AffineTransformOp(
                    transform, AffineTransformOp.TYPE_BILINEAR
            );
        } else {
            return new AffineTransformOp(
                    transform, AffineTransformOp.TYPE_BICUBIC
            );
        }
    }

    private void doAffineTransformOp(Graphics2D g2d) {
        for(int i=0;i<positions.length;i++) {
            AffineTransformOp op = createTransformOp(positions[i], angles[i]);
            //使用过滤器方法变换图像
            g2d.drawImage(op.filter(sprite, null), 0, 0, null);
        }
    }

    private void doTexturePaint(Graphics2D g2d) {
        for(int i=0;i<positions.length;i++) {
            Rectangle2D anchor =
                    new Rectangle2D.Float(
                            0, 0, sprite.getWidth(), sprite.getHeight()
                    );
            TexturePaint paint = new TexturePaint(sprite, anchor);
            g2d.setPaint(paint);
            AffineTransform transform = createTransform(positions[i], angles[i]);
            g2d.setTransform(transform);
            g2d.fillRect(0, 0, sprite.getWidth(), sprite.getHeight());
            //very important!!!
            g2d.setTransform(new AffineTransform());
        }
    }

    public static void main(String[] args) {
        launchApp(new FlyingSpritesExample());
    }
}
