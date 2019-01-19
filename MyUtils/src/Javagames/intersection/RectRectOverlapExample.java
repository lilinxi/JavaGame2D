package Javagames.intersection;

import Javagames.util.Matrix3x3f;
import Javagames.util.SimpleFramework;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;

public class RectRectOverlapExample extends SimpleFramework {
    //分割轴方法测试矩形重叠，与线段类似，需要比较四分之一矩形的投影，即相邻边的投影和
    // 其中取正规化向量的矩形的投影有一边为零，一边投影为它的长度
    private Vector2f[] rect;
    private Vector2f[] rect0;
    private Vector2f rect0Pos;
    private float rect0Angle;
    private Vector2f[] rect1;
    private Vector2f rect1Pos;
    private float rect1Angle;
    private boolean intersection = false;
    private float rotateDelta = (float) Math.PI / 4;

    public RectRectOverlapExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Rect Rect Overlap";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        //set rectangles for testing
        rect = new Vector2f[]{
                new Vector2f(-0.25f, 0.25f),
                new Vector2f(0.25f, 0.25f),
                new Vector2f(0.25f, -0.25f),
                new Vector2f(-0.25f, -0.25f),
        };
        rect0 = new Vector2f[rect.length];
        rect0Pos = new Vector2f();
        rect0Angle = 0.0f;
        rect1 = new Vector2f[rect.length];
        rect1Pos = new Vector2f();
        rect1Angle = 0.0f;
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        //convert mouse coordinate for testing
        rect1Pos = getWorldMousePosition();
        //rotate rectangles
        if (keyboard.keyDown(KeyEvent.VK_A)) {
            rect0Angle += rotateDelta * delta;
        }
        if (keyboard.keyDown(KeyEvent.VK_S)) {
            rect0Angle -= rotateDelta * delta;
        }
        if (keyboard.keyDown(KeyEvent.VK_Q)) {
            rect1Angle += rotateDelta * delta;
        }
        if (keyboard.keyDown(KeyEvent.VK_W)) {
            rect1Angle -= rotateDelta * delta;
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        //translate objects
        Matrix3x3f mat = Matrix3x3f.rotate(rect0Angle);
        mat = mat.mul(Matrix3x3f.translate(rect0Pos));
        rect0 = Utility.transform(rect, mat);
        mat = Matrix3x3f.rotate(rect1Angle);
        mat = mat.mul(Matrix3x3f.translate(rect1Pos));
        rect1 = Utility.transform(rect, mat);
        //test for intersection
        intersection = rectRectIntersection(rect0, rect1);
    }

    private boolean rectRectIntersection(Vector2f[] A, Vector2f[] B) {
        //进行比较的是两个矩形中点连线的向量在垂直标准向量上的投影和两个四分之一矩形在垂直标准向量上的投影之和
        //一共有4个垂直标准向量，所以需要比较四次，都小于则不存在分割轴
        Vector2f N0 = A[0].sub(A[1]).div(2.0f);
        Vector2f N1 = A[1].sub(A[2]).div(2.0f);
        Vector2f CA = A[0].add(A[2]).div(2.0f);
        float D0 = N0.len();
        float D1 = N1.len();
        N0 = N0.div(D0);
        N1 = N1.div(D1);
        Vector2f N2 = B[0].sub(B[1]).div(2.0f);
        Vector2f N3 = B[1].sub(B[2]).div(2.0f);
        Vector2f CB = B[0].add(B[2]).div(2.0f);
        float D2 = N2.len();
        float D3 = N3.len();
        N2 = N2.div(D2);
        N3 = N3.div(D3);
        Vector2f C = CA.sub(CB);
        float DA = D0;//矩形另一边投影为零，这一边投影为这一边的长度
        float DB = D2 * Math.abs(N2.dot(N0));
        DB += D3 * Math.abs(N3.dot(N0));
        if (DA + DB < Math.abs(C.dot(N0))) {
            return false;
        }
        DA = D1;//矩形另一边投影为零，这一边投影为这一边的长度
        DB = D2 * Math.abs(N2.dot(N1));
        DB += D3 * Math.abs(N3.dot(N1));
        if (DA + DB < Math.abs(C.dot(N1))) {
            return false;
        }
        DA = D2;//矩形另一边投影为零，这一边投影为这一边的长度
        DB = D0 * Math.abs(N0.dot(N2));
        DB += D1 * Math.abs(N1.dot(N2));
        if (DA + DB < Math.abs(C.dot(N2))) {
            return false;
        }
        DA = D3;//矩形另一边投影为零，这一边投影为这一边的长度
        DB = D0 * Math.abs(N0.dot(N3));
        DB += D1 * Math.abs(N1.dot(N3));
        if (DA + DB < Math.abs(C.dot(N3))) {
            return false;
        }
        return true;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //render instructions
        g.drawString("Intersection: " + intersection, 30, 45);
        g.drawString("A,S keys to rotate rect1", 30, 60);
        g.drawString("Q,W keys to rotate rect1", 30, 75);
        //draw rectangles
        Matrix3x3f view = getViewportTransform();
        Utility.drawPolygon(g, rect0, view, intersection ? Color.RED : Color.GREEN);
        Utility.drawPolygon(g, rect1, view, intersection ? Color.RED : Color.GREEN);
    }

    public static void main(String[] args) {
        launchApp(new RectRectOverlapExample());
    }
}
