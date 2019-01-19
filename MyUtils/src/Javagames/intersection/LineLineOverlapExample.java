package Javagames.intersection;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LineLineOverlapExample extends SimpleFramework {
    //分割轴法：
    //  如果两个图形间存在一条分割轴，则两个图形不相交。
    //分割轴方法测试线段重叠，需要检查两个正规化向量的正规化轴
    //比较线段的中点的连线的投影与另一个线段的一半在正规化向量上的投影之和,其中取正规化向量的线段的投影为零
    //分割轴法检测任意图形的例子：http://blog.csdn.net/u011373710/article/details/54773171
    private Vector2f P, Q;
    private Vector2f start, end;
    boolean overlap = false;

    public LineLineOverlapExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Line Line Overlap";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        P = new Vector2f(-0.6f, 0.4f);
        Q = new Vector2f(0.6f, -0.4f);
        start = new Vector2f(0.8f, 0.8f);
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
        overlap = lineLineOverlap(P, Q, start, end);
    }

    private boolean lineLineOverlap(
            Vector2f A, Vector2f B, Vector2f P, Vector2f Q) {
        //C0为AB的中点坐标，C1为PQ的中点坐标
        //C两线段的中点连线的向量
        Vector2f C0 = A.add(B).div(2.0f);
        Vector2f C1 = P.add(Q).div(2.0f);
        Vector2f C = C0.sub(C1);
        //两个向量的一半的向量
        Vector2f r0 = A.sub(C0);
        Vector2f r1 = P.sub(C1);
        //两个正规化向量轴
        Vector2f N0 = r0.perp().norm();
        Vector2f N1 = r1.perp().norm();
        //比较两个投影
        //如果中点连线的向量在一条线段的垂直标准向量上的投影大于另一条向量的一半在垂直标准向量上的投影，则存在分割轴
        float abs1 = Math.abs(N0.dot(C));
        float abs2 = Math.abs(N0.dot(r1));
        if (abs1 > abs2) {
            return false;
        }
        abs1 = Math.abs(N1.dot(C));
        abs2 = Math.abs(N1.dot(r0));
        if (abs1 > abs2) {
            return false;
        }
        return true;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("Overlap: " + overlap, 30, 45);
        g.drawString("Left Click for new line", 30, 60);
        Matrix3x3f view = getViewportTransform();
        Utility.drawPolygon(g, new Vector2f[]{Q, P}, view, overlap ? Color.RED : Color.GREEN);
        Utility.drawPolygon(g, new Vector2f[]{start, end}, view, overlap ? Color.RED : Color.GREEN);
    }

    public static void main(String[] args) {
        launchApp(new LineLineOverlapExample());
    }
}
