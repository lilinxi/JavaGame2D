package Javagames.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {
    /**
     * Sprite类添加到了Javagames.util包中，用于处理按照像素大小将图像绘制到世界空间中的矩形之中。
     * 就像PolygonEditor中的drawSprite（）方法一样，Sprite类有一个左上角和右下角的向量，它们在世界空间的边界矩形中确定。
     */
    private BufferedImage image;
    private BufferedImage scaled;
    private Vector2f topLeft;
    private Vector2f bottomRight;

    public Sprite(
            BufferedImage image, Vector2f topLeft, Vector2f bottomRight
    ) {
        this.image = image;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public void render(Graphics2D g, Matrix3x3f view) {
        render(g, view, new Vector2f(), 0.0f);
    }

    /**
     * 可以以任意的旋转绘制精灵。
     * 渲染方法首先计算矩形的屏幕大小，然后，如果缩放的图像大小不正确，还要缩放最初的图像。
     * AffineTransform是列主序的，所以图像变换的顺序是相反的。
     * 图像平移为居中，然后以旋转角度的取反值来旋转（考虑到在屏幕空间中时按照y轴来翻转的），并且最终变换到屏幕的位置。
     * 还有一个额外的scaleImage（）方法，它在渲染图像之前缩放它，当精灵图像是初次加载时，通过执行缩放可以节省空间。
     * 在加载图像时，只要能够创建世界到屏幕的视口矩阵，这个方法就可以在渲染第一幅图像之前把图像缩放到正确的大小。
     * Position为图像中心点的Position！！！
     */
    public void render(Graphics2D g, Matrix3x3f view, Vector2f position, float angel) {
        if (image != null) {
            Vector2f tl = view.mul(topLeft);
            Vector2f br = view.mul(bottomRight);
            int width = (int) Math.abs(br.x - tl.x);
            int height = (int) Math.abs(br.y - tl.y);
            if (scaled == null || width != scaled.getWidth() || height != scaled.getHeight()) {
                scaled = Utility.scaleImage(image, width, height);
            }
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            Vector2f screen = view.mul(position);
            AffineTransform transform = AffineTransform.getTranslateInstance(screen.x, screen.y);
            transform.rotate(-angel);
            transform.translate(-scaled.getWidth() / 2, -scaled.getHeight() / 2);
            g.drawImage(scaled,transform,null);
        }
    }

    public void scaleImage(Matrix3x3f view) {
        Vector2f screenTopLeft = view.mul(topLeft);
        Vector2f screenBottomRight = view.mul(bottomRight);
        int scaleWidth = (int) Math.abs(screenBottomRight.x - screenTopLeft.x);
        int scaleHeight = (int) Math.abs(screenBottomRight.y - screenTopLeft.y);
        scaled = Utility.scaleImage(image, scaleWidth, scaleHeight);
    }
}
