package Javagames.text;

import Javagames.util.SimpleFramework;
import com.sun.org.apache.bcel.internal.generic.FMUL;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextLayoutExample extends SimpleFramework {
    //使用字体信息绘制一个复古的高分输入界面
    //initialize（）方法负责计算每个字母的宽度，以确定最大宽度
    //由于图形对象在initialize（）中还不可用
    //1. 从JFrame类获取字体度量对象
    //2. 通过给JFrame一个Font对象，从而创建一个FontMetrics对象
    //3. 如果当前类不能访问JFrame，那么创建一个图像并使用其Graphics对象
    //
    private Font font;
    private int maxWidth;

    public TextLayoutExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Text Layout Example";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        font = new Font("Arial", Font.BOLD, 40);
        FontMetrics fm = getFontMetrics(font);
        maxWidth = Integer.MIN_VALUE;
        for (int i = (int) '!'; i < (int) 'z'; i++) {
            String letter = " " + (char) i;
            maxWidth = Math.max(maxWidth, fm.stringWidth(letter));
        }
        //another way，if JFrame can't be used
        BufferedImage img =
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        FontMetrics fontMetrics = g2d.getFontMetrics(font);
        g2d.dispose();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setColor(Color.GREEN);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int height = fm.getAscent() + fm.getDescent() + fm.getLeading();
        int x = 20;
        int y = 50;
        g2d.drawLine(x, y, x + 100, y);
        y += fm.getAscent();
        int count = 0;
        for (int i = (int) '!'; i <= (int) 'z'; i++) {
            String letter = " " + (char) i;
            g2d.drawString(letter, x, y);
            x += maxWidth;
            count++;
            if (count % 10 == 0) {
                y += height;
                x = 20;
            }
        }
    }

    public static void main(String[] args) {
        launchApp(new TextLayoutExample());
    }
}
