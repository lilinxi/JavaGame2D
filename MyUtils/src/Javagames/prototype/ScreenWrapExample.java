package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ScreenWrapExample extends SimpleFramework {
    //使用PolygonWrapper来绘制一个矩形，必要的时候绘制其副本
    //并且可以使用空格键来选择鼠标的相对运动和绝对运动
    private Vector2f pos;
    private Vector2f[] poly;
    private ArrayList<Vector2f[]> renderList;
    private PolygonWrapper wrapper;

    public ScreenWrapExample() {
        appBorderScale = 0.9f;
        appWidth = 640;
        appHeight = 640;
        appMaintainRatio = true;
        appTitle = "Screen Wrap Example";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        mouse.setRelative(true);
        renderList = new ArrayList<Vector2f[]>();
        wrapper = new PolygonWrapper(appWorldWidth, appWorldHeight);
        poly = new Vector2f[]{
                new Vector2f(-0.125f, 0.125f),
                new Vector2f(0.125f, 0.125f),
                new Vector2f(0.125f, -0.125f),
                new Vector2f(-0.125f, -0.125f),
        };
        pos = new Vector2f();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (mouse.isRelative()) {
            Vector2f v = getRelativeWorldMousePosition();
            pos = pos.add(v);
        } else {
            pos=getWorldMousePosition();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            mouse.setRelative(!mouse.isRelative());
            if (mouse.isRelative()) {
                pos = new Vector2f();
            }
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        renderList.clear();
        pos = wrapper.wrapPosition(pos);
        Vector2f[] world = Utility.transform(poly, Matrix3x3f.translate(pos));
        //首先需要把原版添加到renderList，然后再判断剩下的3个副本是否添加
        renderList.add(world);
        wrapper.wrapPolygon(world, renderList);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("Press [SPACE] to Toggle mouse", 30, 45);
        g.drawString("Render Numbers: "+renderList.size(), 30, 60);
        //主体为红色，其他为绿色
        g.setColor(Color.RED);
        for (Vector2f[] toRender : renderList) {
            Utility.drawPolygon(g, toRender, getViewportTransform());
            g.setColor(Color.BLUE);
        }
    }

    public static void main(String[] args) {
        launchApp(new ScreenWrapExample());
    }
}
