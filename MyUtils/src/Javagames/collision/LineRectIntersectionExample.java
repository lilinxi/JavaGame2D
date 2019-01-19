package Javagames.collision;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LineRectIntersectionExample extends SimpleFramework {
    //检测一条直线是否与一个矩形相交，并且获知它们在哪里相交。
    private static final float EPSILON = 0.00001f;
    private Vector2f[] rect;
    private Vector2f[] rectCpy;
    private Vector2f start;
    private Vector2f end;
    private Vector2f[] intersection;
    private float angle;
    private float rot;

    public LineRectIntersectionExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Line Rect Intersection";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        angle = 0.0f;
        rot = (float) (Math.PI / 6.0);
        rect = new Vector2f[]{
                new Vector2f(-0.25f, 0.25f),
                new Vector2f(0.25f, 0.25f),
                new Vector2f(0.25f, -0.25f),
                new Vector2f(-0.25f, -0.25f)
        };
        rectCpy = new Vector2f[rect.length];
        start = new Vector2f();
        end = new Vector2f();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        end = getWorldMousePosition();
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1)) {
            start = new Vector2f(end);
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        angle += delta * rot;
        rectCpy = Utility.transform(rect, Matrix3x3f.rotate(angle));
        Vector2f d = end.sub(start);
        float len = d.len();
        d = d.norm();
        Float[] tmp = lineRectIntersection(start, d, rectCpy);
        if (tmp != null) {
            intersection = new Vector2f[tmp.length];
            for(int i=0;i<tmp.length;i++) {
                if (tmp[i] > 0.0f && tmp[i] < len) {
                    intersection[i] = start.add(d.mul(tmp[i]));
                }
            }
        } else {
            intersection = null;
        }
    }

    private Float[] lineRectIntersection(
            Vector2f O, Vector2f d, Vector2f[] rect
    ) {
        float largestMin = -Float.MAX_VALUE;
        float smallestMax = Float.MAX_VALUE;
        float swap;
        for(int i=0;i<2;i++) {
            Vector2f n = rect[i].sub(rect[i + 1]);
            n = n.norm();
            float e0 = n.dot(rect[i].sub(O));
            float e1 = n.dot(rect[i + 1].sub(O));
            float f = n.dot(d);
            //如果f=0，则线段垂直这条边，此时若e0与e1同号，则线段在矩形之外，且与另一条线段平行
            if (Math.abs(f) > EPSILON) {
                float t0 = e0 / f;
                float t1 = e1 / f;
                if (t0 > t1) {
                    swap = t0;
                    t0 = t1;
                    t1 = swap;
                }
                largestMin = Math.max(largestMin, t0);
                smallestMax = Math.min(smallestMax, t1);
                if (largestMin > smallestMax) {
                    return null;
                }
                if (smallestMax < 0) {
                    return null;
                }
            } else if (e0 * e1 > 0) {
                return null;
            }
        }
        return new Float[]{largestMin, smallestMax};
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        boolean hasIntersection = intersection != null;
        textPos = Utility.drawString(g, 20, textPos,
                "Intersection: " + hasIntersection,
                "Left-Click to change start position");
        Matrix3x3f view = getViewportTransform();
        rectCpy = Utility.transform(rectCpy, view);
        Utility.drawPolygon(g, rectCpy);
        Utility.drawPolygon(g, new Vector2f[]{start, end}, view);
        if (hasIntersection) {
            g.setColor(Color.BLUE);
            for(int i=0;i<intersection.length;i++) {
                if (intersection[i] != null) {
                    Vector2f temp = view.mul(intersection[i]);
                    g.drawLine((int) temp.x - 20, (int) temp.y,
                            (int) temp.x + 20, (int) temp.y);
                    g.drawLine((int) temp.x, (int) temp.y - 20,
                            (int) temp.x, (int) temp.y + 20);
                }
            }
        }
    }

    public static void main(String[] args) {
        launchApp(new LineRectIntersectionExample());
    }
}
