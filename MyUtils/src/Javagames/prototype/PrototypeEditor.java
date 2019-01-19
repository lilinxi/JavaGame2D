package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PrototypeEditor extends SimpleFramework {
    //使用Swing工具来创建游戏工具
    //根据绘图的位置来输出Vector2f[]
    private ArrayList<Vector2f> polygon;
    private Vector2f mousePos;
    private boolean closed;

    public PrototypeEditor() {
        appTitle = "Prototype Editor 1.0";
        appWidth = 640;
        appHeight = 640;
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        polygon = new ArrayList<Vector2f>();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        mousePos = getWorldMousePosition();
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1)) {
            polygon.add(mousePos);
        }
        if (mouse.buttonDownOnce(MouseEvent.BUTTON2)) {
            closed = !closed;
        }
        if (mouse.buttonDownOnce(MouseEvent.BUTTON3)) {
            if (!polygon.isEmpty()) {
                polygon.remove(polygon.size() - 1);
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_C)) {
            polygon.clear();
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            printPolygon();
        }
    }

    private void printPolygon() {
        System.out.println("Vector2f[] v=new Vector2f[] {");
        for (Vector2f v : polygon) {
            System.out.print("new Vector2f(");
            System.out.print(v.x+"f,");
            System.out.print(v.y + "f)");
            System.out.println(",");
        }
        System.out.println("};");
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("Close Polygon: " + closed, 30, 45);
        g.drawString("Left Mouse: Add point", 30, 60);
        g.drawString("Right Mouse: Remove point", 30, 75);
        g.drawString("Center Mouse: Toggle Close", 30, 90);
        g.drawString("C: Clear Polygon", 30, 105);
        g.drawString("Space Bar: Print Polygon", 30, 120);
        Matrix3x3f view = getViewportTransform();
        drawAxisLines(g, view);
        drawPolygon(g, view);
    }

    private void drawAxisLines(Graphics g, Matrix3x3f view) {
        Vector2f left = new Vector2f(appWorldWidth / 2.0f, 0.0f);
        Vector2f right = new Vector2f(-left.x, 0.0f);
        Utility.drawPolygon(g, new Vector2f[]{left, right}, view, Color.BLUE);
        Vector2f top = new Vector2f(0.0f, appWorldHeight / 2.0f);
        Vector2f bottom = new Vector2f(0.0f, -top.y);
        Utility.drawPolygon(g, new Vector2f[]{top, bottom}, view, Color.BLUE);
    }

    private void drawPolygon(Graphics g, Matrix3x3f view) {
        //如果绘制结束，即图形封闭，则绘制整个Polygon
        //否则绘制前n-1条边，和动态的最后绘制的点与鼠标位置的连线
        if (closed && polygon.size() > 0) {
            Utility.drawPolygon(g, polygon, view, Color.BLACK);
            return;
        }
        for (int i = 0; i < polygon.size() - 1; i++) {
            Utility.drawPolygon(g, new Vector2f[]{polygon.get(i), polygon.get(i + 1)}, view, Color.BLACK);
        }
        if (!(polygon.isEmpty() || closed)) {
            Vector2f P = polygon.get(polygon.size() - 1);
            Vector2f S = mousePos;
            Utility.drawPolygon(g, new Vector2f[]{P, S}, view, Color.BLACK);
        }
    }

    public static void main(String[] args) {
        launchApp(new PrototypeEditor());
    }
}
