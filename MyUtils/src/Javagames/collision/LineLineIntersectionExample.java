package Javagames.collision;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LineLineIntersectionExample extends SimpleFramework {
    //检测两条线段是否相交
    /**
     * AB的垂直向量n2 = (B - A)┴
     * CD的垂直向量n1 = (D - C)┴
     * 判断线段AB与直线CD相交：
     * AB：x = A + t1(B - A)
     * n1*(x - C) = 0
     * 判断0 < t1 < 1是否成立，同理还需要判断线段CD与直线AB相交
     * t1 = n1*(C - A)/n1*(B - A)
     * t2 = n2*(A - C)/n2*(D - C)
     * 首先需要检查分母是否为零，若分母为零则说明两直线平行，没有交点。
     * 由于t1和t2都需要判断，现进行如下简化：
     * 由A┴*B = -A*B┴进行化简
     * t1 = n1*(C - A)/n1*(B - A) = (D - C)┴*(C - A)/(D - C)┴*(B - A) = (D - C)┴*(A - C)/(D - C)┴*(A - B)
     * t2 = n2*(A - C)/n2*(D - C) = (B - A)┴*(A - C)/(B - A)┴*(D - C) = (B - A)┴*(A - C)/(D - C)┴*(A - B)
     * let d = (D - C)┴*(A - C)
     * e = (B - A)┴*(A - C)
     * f = (D - C)┴*(A - B)
     * t1 = d/f
     * t2 = e/f
     * 由于除法的代价比较高，所以在执行除法前先验证值的范围是否在0和1之间
     */
    private static final float EPSILON = 0.00001f;
    private Vector2f A, B, C, D;
    private Vector2f intersection;

    public LineLineIntersectionExample() {
        appWidth = 640;
        appHeight = 640;
        appTitle = "Line Line Intersection";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        D = getWorldMousePosition();
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1)) {
            if (A == null) {
                A = D;
            } else if (B == null) {
                B = D;
            } else {
                C = D;
            }
        }
        if (mouse.buttonDownOnce(MouseEvent.BUTTON3)) {
            A = B = C = D = null;
            intersection = null;
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        if (!(A == null || B == null || C == null || D == null)) {
            float[] t = lineLineIntersection(A, B, C, D);
            if (t == null) {
                intersection = null;
            } else {
                intersection = A.add(B.sub(A).mul(t[0]));
                //intersection = C.add(D.sub(C).mul(t[1]));
            }
        }
    }

    private float[] lineLineIntersection(
            Vector2f A, Vector2f B, Vector2f C, Vector2f D
    ) {
        Vector2f DsubC = D.sub(C);
        Vector2f DsubCperp = DsubC.perp();
        Vector2f AsubB = A.sub(B);
        float f = DsubCperp.dot(AsubB);
        if (Math.abs(f) < EPSILON) {
            return null;
        }
        Vector2f AsubC = A.sub(C);
        float d = DsubCperp.dot(AsubC);
        if (f > 0) {
            if (d < 0 || d > f) {
                return null;
            }
        } else {
            if (d > 0 || d < f) {
                return null;
            }
        }
        Vector2f BsubA = B.sub(A);
        Vector2f BsubAperp = BsubA.perp();
        float e = BsubAperp.dot(AsubC);
        if (f > 0) {
            if (e < 0 || e > f) {
                return null;
            }
        } else {
            if (e > 0 || e < f) {
                return null;
            }
        }
        return new float[]{
                d / f, e / f
        };
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        textPos = Utility.drawString(g, 20, textPos,
                "Left Mouse to place points",
                "Right Mouse to clear points");
        g.setColor(intersection == null ? Color.RED : Color.GREEN);
        drawLine(g, A, B);
        drawLine(g, C, D);
        drawCrossHairs(g);
    }

    private void drawLine(Graphics g, Vector2f v0, Vector2f v1) {
        if (v0 != null) {
            Matrix3x3f view = getViewportTransform();
            Vector2f va = view.mul(v0);
            if (v1 == null) {
                g.fillRect((int) va.x, (int) va.y, 1, 1);
            } else {
                Vector2f vb = view.mul(v1);
                g.drawLine((int) va.x, (int) va.y, (int) vb.x, (int) vb.y);
            }
        }
    }

    private void drawCrossHairs(Graphics g) {
        if (intersection != null) {
            Matrix3x3f view = getViewportTransform();
            Vector2f intView = view.mul(intersection);
            g.setColor(Color.BLACK);
            int x = (int) intView.x;
            int y = (int) intView.y;
            g.drawLine(x, y - 20, x, y + 20);
            g.drawLine(x - 20, y, x + 20, y);
        }
    }

    public static void main(String[] args) {
        launchApp(new LineLineIntersectionExample());
    }
}
