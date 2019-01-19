package Javagames.images;

import Javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TransparentImageExample extends SimpleFramework {
    //透明图像的使用
    //对于任何类型的，非方块形的游戏精灵，围绕边缘的透明像素都是必要的，因为这使得没有围绕游戏对象的边框
    //图像必须有透明的属性，TYPE_INT_ARGB
    private BufferedImage img;
    private float shift;

    public TransparentImageExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Transparent Image Example";
        appBackground = Color.DARK_GRAY;
    }

    @Override
    protected void initialize() {
        super.initialize();
        img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        int w = 8;
        int h = 8;
        int dx = img.getWidth() / w;
        int dy = img.getHeight() / h;
        for(int i=0;i<w;i++) {
            for(int j=0;j<h;j++) {
                if ((i + j) % 2 == 0) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(i * dx, j * dy, dx, dy);
                }
            }
        }
        g2d.dispose();
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        int ribbonHeight = canvas.getHeight() / 5;
        shift += delta * ribbonHeight;
        if (shift > ribbonHeight) {
            shift -= ribbonHeight;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //draw shift background
        int hx = canvas.getHeight() / 5;
        g.setColor(Color.LIGHT_GRAY);
        for (int i = -1; i < 5; i++) {
            g.fillRect(0, (int) shift + hx * i, canvas.getWidth(), hx / 2);
        }
        int x = (canvas.getWidth() - img.getWidth()) / 2;
        int y = (canvas.getHeight() - img.getHeight()) / 2;
        g.drawImage(img, x, y, null);
    }

    public static void main(String[] args) {
        launchApp(new TransparentImageExample());
    }
}
