package Javagames.collision;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;

public class LineCircleIntersectionExample extends SimpleFramework {
    //检测线段与圆是否相交
    /**
     * 直线参数方程：
     *  P = O + t*d （d为直线方向向量，O为起点，P为终点）
     * 直线与圆相交时，P在圆上，即P到圆心C的距离与圆的半径r相同。
     * \\P - C\\ = r
     * \\O + t*d - C\\ = r
     * (O + t*d - C)^2 - r^2 = 0
     * t^2(d*d) + 2t[d*(O - C)] +(O - C)*(O - C) - r^2 = 0
     * 如果d正规化了：
     * t^2 + 2t[d*(O - C)] +(O - C)*(O - C) - r^2 = 0
     * 令a = 1,b = d*(O - C),c = (O - C)*(O - C) - r^2
     * 则(a)t^2 + (2b)t + (c) = 0
     * t = -b ± (b^2 - c)^½
     */
    private Vector2f p0;
    private Vector2f p1;
    private Vector2f center;
    private float radius;
    private Float t0, t1;
    private Vector2f plane0;
    private Vector2f plane1;
    private Vector2f segment0;
    private Vector2f segment1;

    public LineCircleIntersectionExample() {
        appWidth = 640;
        appHeight = 640;
        appTitle = "Line Circle Intersection";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        radius = 0.125f;
        center = new Vector2f();
        p0 = new Vector2f(-0.65f, -0.2f);
        p1 = new Vector2f(0.50f, 0.2f);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        center = getWorldMousePosition();
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        Vector2f d = p1.sub(p0);
        float len = d.len();
        d = d.norm();
        plane0 = plane1 = null;
        segment0 = segment1 = null;
        t0 = t1 = null;
        float[] intersections =
                lineCircleIntersection(p0, d, center, radius);
        if (intersections != null) {
            t0 = intersections[0];
            plane0 = p0.add(d.mul(t0));
            if (t0 >= 0.0f && t0 <= len) {
                segment0 = plane0;
            }
            t1 = intersections[1];
            plane1 = p0.add(d.mul(t1));
            if (t1 >= 0.0f && t1 <= len) {
                segment1 = plane1;
            }
        }
    }

    private float[] lineCircleIntersection(
            Vector2f O, Vector2f d, Vector2f C, float r
    ) {
        Vector2f V = O.sub(C);
        float b = d.dot(V);
        float bb = b * b;
        float rr = r * r;
        float VV = V.dot(V);
        float c = VV - rr;
        if (bb < c) {
            return null;
        }
        float root = (float) Math.sqrt(bb - c);
        return new float[]{-b - root, -b + root};
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        textPos = Utility.drawString(g, 20, textPos,
                "T0: " + t0,
                "T1: " + t1);
        Matrix3x3f view = getViewportTransform();
        Utility.drawPolygon(g, new Vector2f[]{p0, p1}, view);
        Utility.drawOval(g, center, radius, view);
        drawIntersections(g, plane0, segment0);
        drawIntersections(g, plane1, segment1);
    }

    private void drawIntersections(
            Graphics g, Vector2f planeIntersection, Vector2f lineIntersection
    ) {
        Matrix3x3f view = getViewportTransform();
        if (planeIntersection != null) {
            g.setColor(Color.BLACK);
            Vector2f intCpy = view.mul(planeIntersection);
            g.drawLine((int) intCpy.x - 20, (int) intCpy.y,
                    (int) intCpy.x + 20, (int) intCpy.y);
            g.drawLine((int) intCpy.x, (int) intCpy.y - 20,
                    (int) intCpy.x, (int) intCpy.y + 20);
        }
        if (lineIntersection != null) {
            g.setColor(Color.BLUE);
            Vector2f intCpy = view.mul(lineIntersection);
            g.drawLine((int) intCpy.x - 20, (int) intCpy.y,
                    (int) intCpy.x + 20, (int) intCpy.y);
            g.drawLine((int) intCpy.x, (int) intCpy.y - 20,
                    (int) intCpy.x, (int) intCpy.y + 20);
        }
    }

    public static void main(String[] args) {
        launchApp(new LineCircleIntersectionExample());
    }
}
