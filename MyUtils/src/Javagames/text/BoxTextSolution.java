package Javagames.text;

import Javagames.util.SimpleFramework;

import java.awt.*;

public class BoxTextSolution extends SimpleFramework {
    //解决用一个矩形围绕字体的问题
    /**
     * 计算字符串长度：
     * FontMetrics fm = g.getFontMetrics();
     * width = fm.stringWidth(str);
     */
    /**
     * 计算字符串的高度 = Ascent + Decent + Leading
     */
    public BoxTextSolution() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Box Text Solution";
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //Set the font...
        Font font = new Font("Arial", Font.PLAIN, 24);
        g.setFont(font);
        //FontMetrics 类定义字体规格对象，该对象封装将在特定屏幕上呈现特定字体的有关信息
        FontMetrics fm = g.getFontMetrics();
        int x = 20;
        int y = 50;
        //Draw the top...
        String str = "draw the top line";
        g.setColor(Color.DARK_GRAY);
        g.drawString(str, x, y);
        int width = 100;
        g.setColor(Color.RED);
        g.drawLine(x, y, x + width, y);
        //Calculate the string width
        y += 40;
        str = "Calculate correct width";
        g.setColor(Color.DARK_GRAY);
        g.drawString(str, x, y);
        width = fm.stringWidth(str);
        g.setColor(Color.GREEN);
        g.drawLine(x, y, x + width, y);
        //Use Ascent to offset y
        y += 40;
        g.setColor(Color.DARK_GRAY);
        //font ascent 是字体基线到大多数字母数字字符顶部的距离。
        str = "offset text with the Ascent";
        g.drawString(str, x, y + fm.getAscent());
        width = fm.stringWidth(str);
        g.setColor(Color.BLUE);
        g.drawLine(x, y, x + width, y);
        //Ascent+Decent+Leading=Height
        //getDescent()确定此 FontMetrics 对象所描述的 Font 的 font descent。
        // font descent 是字体基线到大多数字母数字字符底部的距离。
        // 在 Font 中，有些字符可能扩展到 font descent 线之下。
        //getLeading()确定此 FontMetrics 对象所描述的 Font 的标准行间距。
        y += 40;
        g.setColor(Color.DARK_GRAY);
        str = "Calculate height of font";
        g.drawString(str, x, y + fm.getAscent());
        width = fm.stringWidth(str);
        g.setColor(Color.BLUE);
        g.drawLine(x, y, x + width, y);
        int height = fm.getAscent() + fm.getDescent() + fm.getLeading();
        g.drawLine(x, y + height, x + width, y + height);
        //Box the text
        y += 40;
        g.setColor(Color.DARK_GRAY);
        str = "Groovy, we got it!!!";
        g.drawString(str, x, y + fm.getAscent());
        width = fm.stringWidth(str);
        g.setColor(Color.BLUE);
        height = fm.getAscent() + fm.getDescent() + fm.getLeading();
        g.drawRect(x, y, width, height);
    }

    public static void main(String[] args) {
        launchApp(new BoxTextSolution());
    }
}
