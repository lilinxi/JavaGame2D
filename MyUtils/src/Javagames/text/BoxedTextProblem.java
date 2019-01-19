package Javagames.text;

import Javagames.util.SimpleFramework;

import java.awt.*;

public class BoxedTextProblem extends SimpleFramework {
    //列举了所有可用的fontFamily
    //字体从基线开始绘制，字体基线上有上升，基线下有下沉
    //矩形从左上角开始绘制
    public BoxedTextProblem() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Boxed Text Problem";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //Box this text...
        g.setColor(Color.BLACK);
        String box = "great Java";
        Font font = new Font("Arial", Font.PLAIN, 24);
        g.setFont(font);
        g.drawString(box, 20, 50);
        g.setColor(Color.RED);
        g.drawRect(20, 50, 200, 20);
    }

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        for (String fontFamily : fontFamilies) {
            System.out.println(fontFamily);
        }
        launchApp(new BoxedTextProblem());
    }
}
