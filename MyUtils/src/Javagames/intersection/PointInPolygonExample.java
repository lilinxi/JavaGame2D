package Javagames.intersection;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointInPolygonExample extends SimpleFramework {
    //使用从点开始沿x轴的射线与多边形的边相交的次数为奇数还是偶数来判断点是否在多边形内
    //根据多边形的边是否存在相交策略有所不同
    //多边形中的点的测试
    private static final int MAX_POINTS = 10000;
    private ArrayList<Vector2f> poly;
    private ArrayList<Vector2f> polyCpy;
    private ArrayList<Vector2f> inside;
    private ArrayList<Vector2f> outside;
    private Vector2f mousePos;
    private boolean selected;
    private boolean winding;

    public PointInPolygonExample() {
        appWidth = 640;
        appHeight = 640;
        appTitle = "Point In Polygon Example";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        poly = new ArrayList<Vector2f>();
        inside = new ArrayList<Vector2f>();
        outside = new ArrayList<Vector2f>();
        mousePos = new Vector2f();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        mousePos = getWorldMousePosition();
        //draw polygon for algorithm testing
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            winding = !winding;
        }
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1)) {
            poly.add(mousePos);
        }
        if (mouse.buttonDownOnce(MouseEvent.BUTTON3)) {
            poly.clear();
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        //see if the mouse is inside the polygon
        selected = pointInPolygon(mousePos, poly, winding);
        //test random points against the polygon
        Random rand = new Random();
        inside.clear();
        outside.clear();
        for(int i=0;i<MAX_POINTS;i++) {
            float x = rand.nextFloat() * 2.0f - 1.0f;
            float y = rand.nextFloat() * 2.0f - 1.0f;
            Vector2f point = new Vector2f(x, y);
            if (pointInPolygon(point, poly, winding)) {
                inside.add(point);
            } else {
                outside.add(point);
            }
        }
    }

    private boolean pointInPolygon(Vector2f point, List<Vector2f> poly, boolean winding) {
        //point in polygon algorithm
        //对多边形的边不相交的情况，对要测试的点，沿着x轴画一条射线，若射线穿过多边形的边次数为奇数则点在多边形中
        //若多边形的边相交，startAbove加1，endAbove减1，若最后不为0，则点在多边形中
        int inside = 0;
        if (poly.size() > 2) {
            Vector2f start = poly.get(poly.size() - 1);
            boolean startAbove = start.y >= point.y;
            for(int i=0;i<poly.size();i++) {
                Vector2f end = poly.get(i);
                boolean endAbove = end.y >= point.y;
                if (startAbove != endAbove) {
                    float m = (end.y - start.y) / (end.x - start.x);
                    float x = start.x + (point.y - start.y) / m;
                    if (x >= point.x) {
                        if (winding) {
                            inside += startAbove ? 1 : -1;
                        } else {
                            inside = inside == 1 ? 0 : 1;
                        }
                    }
                }
                startAbove = endAbove;
                start = end;
            }
        }
        return inside != 0;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //render instructions
        g.drawString("Winding: " + (winding ? "ON" : "OFF"), 30, 45);
        String mouse = String.format("Mouse: (%.2f,%.2f)", mousePos.x, mousePos.y);
        g.drawString(mouse, 30, 60);
        g.drawString("Left-Click to add points", 30, 75);
        g.drawString("Right-Click to clear points", 30, 90);
        g.drawString("Press [SPACE] to toggle winding", 30, 105);
        Matrix3x3f view = getViewportTransform();
        //draw test polygon
        if (poly.size() > 1) {
            Utility.drawPolygon(g,poly,view,selected ? Color.GREEN : Color.RED);
        }
        //draw inside point black, outside point white
        g.setColor(Color.BLACK);
        for (Vector2f vector : inside) {
            Vector2f point = view.mul(vector);
            g.fillRect((int) point.x, (int) point.y, 1, 1);
        }
        g.setColor(Color.WHITE);
        for (Vector2f vector : outside) {
            Vector2f point = view.mul(vector);
            g.fillRect((int) point.x, (int) point.y, 1, 1);
        }
    }

    public static void main(String[] args) {
        launchApp(new PointInPolygonExample());
    }
}
