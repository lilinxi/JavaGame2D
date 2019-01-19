package Javagames.text;

import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import com.sun.org.apache.regexp.internal.RE;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class TextMetricsExample extends SimpleFramework {
    //
    //使用文本度量进行布局
    //
    //LineMetrics.getBaselineOffsets()[Font.CENTER_BASELINE]绘制中央基线
    //
    //TextLayout绘制3条基线和Ascent，Descent，Leading
    //Advance: 字符串长度
    //Visible-Advance: 字符串长度，去掉尾部空格
    //Bounds: 返回此TextLayout的边界Rectangle2D
    //
    //FontMetrics类并非获取字体相关信息的唯一方法
    //FontMetrics对象提供了一个LineMetrics对象，它拥有额外的信息，例如一种字体的中央基线
    //TextLayout对象甚至更有用，它可以通过一个Font，一个FontRenderContext和一个String构建该对象
    //文体布局对象以浮点值而不是整数的形式提供了很多信息，因此，可以尽可能精确的放置
    public TextMetricsExample() {
        appWidth = 640;
        appHeight = 480;
        appSleep = 10L;
        appTitle = "Text Metrics Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        Font font = new Font("Time New Roman", Font.BOLD | Font.ITALIC, 40);
        g2d.setFont(font);
        g2d.setColor(Color.GREEN);
        String str = "Groovy Baby BLAH";
        int x = 50;
        int y = 50;
        g2d.drawString(str, x, y);
        //Text Layout gives floating point values
        //FontRenderContext 类是正确测量文本所需的信息容器。
        // 因为将轮廓映射到像素的规则不同，而且应用程序提供的呈现提示不同，所以文本的测量也有所不同。
        //
        //其中一条信息是将印刷点缩放成像素的转换信息。
        // （一个点被定义成恰好是一英寸的 1/72，这与点的传统机械测量稍有不同。）
        // 由于舍入到像素边界和字体设计者已指定的提示等因素，
        // 在 600dpi 设备上以 12pt 呈现的字符可能与在 72dpi 设备上以 12pt 呈现的同一个字符大小不同。
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout tl = new TextLayout(str, font, frc);
        //draw another line, should be at
        //y + ascent + decent + leading
        int newY = y +
                (int) (tl.getAscent() + tl.getDescent() + tl.getLeading());
        g2d.drawString(str, x, newY);
        //draw centered Text
        //first lets draw the center of the window...
        g2d.setColor(Color.GRAY);
        int sw = canvas.getWidth();
        int sh = canvas.getHeight();
        int cx = sw / 2;
        int cy = sh / 2;
        g2d.drawLine(0, cy, sw, cy);
        g2d.drawLine(cx, 0, cx, sh);
        String center = "Should Center: Center Baby's @";
        //to calculate the x, need the width...
        int stringWidth = g2d.getFontMetrics().stringWidth(center);
        //返回指定 Graphics 上下文中指定 String 的 LineMetrics 对象。
        //LineMetrics 类允许访问沿着一行布局字符和多行布局所需要的规格。
        // LineMetrics 对象封装了与文本排列相关的测量信息。
        //abstract  float[] getBaselineOffsets()
        //          返回文本的基线偏移量（相对于文本的基线）。
        float dy = g2d.getFontMetrics().getLineMetrics(center, g2d).getBaselineOffsets()[Font.CENTER_BASELINE];
        g2d.drawString(center, cx - stringWidth / 2, cy - dy);
        //draw the pixel where we are drawing the text...
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x - 1, y - 1, 3, 3);
        ArrayList<String> console = new ArrayList<String>();
        console.add("Baseline: " + tl.getBaseline());
        //ROMAN_BASELINE
        //public static final int ROMAN_BASELINE布置文本时，在大多数 Roman 脚本中使用的基线。
        //
        //CENTER_BASELINE
        //public static final int CENTER_BASELINE布置文本时，在表意文字的脚本（如中文、日文和韩文）中使用的基线。
        //
        //HANGING_BASELINE
        //public static final int HANGING_BASELINE布置文本时，在 Devanigiri 和类似脚本中使用的基线。
        float[] baselineOffsets = tl.getBaselineOffsets();
        console.add("Baseline-Offset[ROMAN]: " +
                baselineOffsets[Font.ROMAN_BASELINE]);
        console.add("Baseline-Offset[CENTER]: " +
                baselineOffsets[Font.CENTER_BASELINE]);
        console.add("Baseline-Offset[HANGING]: " +
                baselineOffsets[Font.HANGING_BASELINE]);
        console.add("Ascent: " + tl.getAscent());
        console.add("Descent: " + tl.getDescent());
        console.add("Leading: " + tl.getLeading());
        //返回此 TextLayout 的 advance。该 advance 是从原点到最右边（最底部）字符的 advance 的距离。
        // 返回值位于相对于基线的坐标中
        console.add("Advance: " + tl.getAdvance());
        //返回此 TextLayout 的 advance，减去结尾空格。
        console.add("Visible-Advance: " + tl.getVisibleAdvance());
        //返回此 TextLayout 的边界。
        console.add("Bounds: " + toString(tl.getBounds()));
        Font propFont = new Font("Courier New", Font.BOLD, 14);
        g2d.setFont(propFont);
        int xLeft = x;
        int xRight = xLeft + (int) tl.getVisibleAdvance();
        //draw baseline
        g2d.setColor(Color.WHITE);
        int baselineY = y + (int) baselineOffsets[Font.ROMAN_BASELINE];
        g2d.drawLine(xLeft, baselineY, xRight, baselineY);
        g2d.drawString("roman baseline", xRight, baselineY);
        //draw center
        g2d.setColor(Color.BLUE);
        int centerY = y + (int) baselineOffsets[Font.CENTER_BASELINE];
        g2d.drawLine(xLeft, centerY, xRight, centerY);
        g2d.drawString("center baseline", xRight, centerY);
        //draw hanging
        g2d.setColor(Color.GRAY);
        int hangingY = y + (int) baselineOffsets[Font.HANGING_BASELINE];
        g2d.drawLine(xLeft, hangingY, xRight, hangingY);
        g2d.drawString("hanging baseline", xRight, hangingY);
        //draw Ascent
        g2d.setColor(Color.YELLOW);
        int propY = y - (int) tl.getAscent();
        g2d.drawLine(xLeft, propY, xRight, propY);
        TextLayout temp = new TextLayout("hanging baseline", propFont, g2d.getFontRenderContext());
        g2d.drawString("Ascent", xRight + temp.getVisibleAdvance(), propY);
        //draw Descent
        g2d.setColor(Color.RED);
        propY = y + (int) tl.getDescent();
        g2d.drawLine(xLeft, propY, xRight, propY);
        g2d.drawString("Descent", xRight, propY);
        //draw leading
        g2d.setColor(Color.GREEN);
        propY = y + (int) tl.getDescent() + (int) tl.getLeading();
        g2d.drawLine(xLeft, propY, xRight, propY);
        temp = new TextLayout("Descent", propFont, g2d.getFontRenderContext());
        g2d.drawString("Leading", xRight + temp.getVisibleAdvance(), propY);
        //draw console output...
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("Courier New", Font.BOLD, 12));
        Utility.drawString(g2d, 20, 300, console);
    }

    private String toString(Rectangle2D r) {
        return "[x=" + r.getX() + ",y=" + r.getY() + ",w=" + r.getWidth() + ",h=" + r.getHeight() + "]";
    }

    public static void main(String[] args) {
        launchApp(new TextMetricsExample());
    }
}
