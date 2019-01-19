package Javagames.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utility {
    /*****************************************************************************************************************/
    /*****************************************坐标转换工具************************************************************/
    /*****************************************************************************************************************/
    public static Matrix3x3f createViewport(
            float worldWidth, float worldHeight,
            float screenWidth, float screenHeight) {
        //缩放并平移
        //由世界坐标转换为屏幕坐标
        float sx = (screenWidth - 1) / worldWidth;
        float sy = (screenHeight - 1) / worldHeight;
        float tx = (screenWidth - 1) / 2.0f;
        float ty = (screenHeight - 1) / 2.0f;
        Matrix3x3f viewport = Matrix3x3f.scale(sx, -sy);
        viewport = viewport.mul(Matrix3x3f.translate(tx, ty));
        return viewport;
    }

    public static Matrix3x3f createReverseViewport(
            float worldWidth, float worldHeight,
            float screenWidth, float screenHeight) {
        //平移并缩放
        //由鼠标屏幕坐标转换为世界坐标
        float sx = worldWidth / (screenWidth - 1);
        float sy = worldHeight / (screenHeight - 1);
        float tx = (screenWidth - 1) / 2.0f;
        float ty = (screenHeight - 1) / 2.0f;
        Matrix3x3f viewport = Matrix3x3f.translate(-tx, -ty);
        viewport = viewport.mul(Matrix3x3f.scale(sx, -sy));
        return viewport;
    }

    public static Vector2f[] transform(Vector2f[] poly, Matrix3x3f mat) {
        Vector2f[] copy = new Vector2f[poly.length];
        for(int i=0;i<poly.length;i++) {
            copy[i] = mat.mul(poly[i]);
        }
        return copy;
    }

    /*****************************************************************************************************************/
    /*****************************************图形绘制工具************************************************************/
    /*****************************************************************************************************************/
    //当Vector2f[]只有两个点时，绘制线段
    //当Vector2f[]只有一个点时，绘制点
    public static void drawPolygon(Graphics g, Vector2f[] polygon) {
        if (polygon.length == 1) {
            g.drawRect((int) polygon[0].x, (int) polygon[0].y, 1, 1);
        }
        Vector2f P;
        Vector2f S = polygon[polygon.length - 1];
        for(int i=0;i<polygon.length;i++) {
            P = polygon[i];
            g.drawLine((int) S.x, (int) S.y, (int) P.x, (int) P.y);
            S = P;
        }
    }

    public static void drawPolygon(Graphics g, List<Vector2f> polygon) {
        if (polygon.size() == 1) {
            g.drawRect((int) polygon.get(0).x, (int) polygon.get(0).y, 1, 1);
        }
        Vector2f S = polygon.get(polygon.size() - 1);
        for (Vector2f P : polygon) {
            g.drawLine((int) S.x, (int) S.y, (int) P.x, (int) P.y);
            S = P;
        }
    }

    public static void drawPolygon(Graphics g, Vector2f[] polygon, Color color) {
        g.setColor(color);
        drawPolygon(g, polygon);
    }

    public static void drawPolygon(Graphics g, List<Vector2f> polygon, Color color) {
        g.setColor(color);
        drawPolygon(g, polygon);
    }

    public static void drawPolygon(Graphics g, Vector2f[] polygon, Matrix3x3f view) {
        Vector2f[] tmp = transform(polygon, view);
        drawPolygon(g, tmp);
    }

    public static void drawPolygon(Graphics g, List<Vector2f> polygon, Matrix3x3f view) {
        drawPolygon(g, polygon.toArray(new Vector2f[0]), view);
    }

    public static void drawPolygon(Graphics g, Vector2f[] polygon, Matrix3x3f view, Color color) {
        g.setColor(color);
        drawPolygon(g,polygon,view);
    }

    public static void drawPolygon(Graphics g, List<Vector2f> polygon, Matrix3x3f view, Color color) {
        g.setColor(color);
        drawPolygon(g, polygon, view);
    }

    public static void fillPolygon(Graphics g, Vector2f[] polygon) {
        Polygon p = new Polygon();
        for (Vector2f v : polygon) {
            p.addPoint((int) v.x, (int) v.y);
        }
        g.fillPolygon(p);
    }

    public static void fillPolygon(Graphics g, List<Vector2f> polygon) {
        Polygon p = new Polygon();
        for (Vector2f v : polygon) {
            p.addPoint((int) v.x, (int) v.y);
        }
        g.fillPolygon(p);
    }

    public static void fillPolygon(Graphics g, Vector2f[] polygon, Color color) {
        g.setColor(color);
        fillPolygon(g, polygon);
    }

    public static void fillPolygon(Graphics g, List<Vector2f> polygon, Color color) {
        g.setColor(color);
        fillPolygon(g, polygon);
    }

    public static void fillPolygon(Graphics g, Vector2f[] polygon, Matrix3x3f view) {
        Vector2f[] tmp = transform(polygon, view);
        fillPolygon(g, tmp);
    }

    public static void fillPolygon(Graphics g, List<Vector2f> polygon, Matrix3x3f view) {
        fillPolygon(g, polygon.toArray(new Vector2f[0]), view);
    }

    public static void fillPolygon(Graphics g, Vector2f[] polygon, Matrix3x3f view, Color color) {
        g.setColor(color);
        fillPolygon(g, polygon, view);
    }

    public static void fillPolygon(Graphics g, List<Vector2f> polygon, Matrix3x3f view, Color color) {
        g.setColor(color);
        fillPolygon(g, polygon, view);
    }

    public static void drawRect(Graphics g, Vector2f topLeft, Vector2f bottomRight) {
        int rectX = (int) topLeft.x;
        int rectY = (int) topLeft.y;
        int rectWidth = (int) (bottomRight.x - topLeft.x);
        int rectHeight = (int) (bottomRight.y - topLeft.y);
        g.drawRect(rectX, rectY, rectWidth, rectHeight);
    }

    public static void drawRect(Graphics g, Vector2f topLeft, Vector2f bottomRight, Color color) {
        g.setColor(color);
        drawRect(g, topLeft, bottomRight);
    }

    public static void drawRect(Graphics g, Vector2f topLeft, Vector2f bottomRight, Matrix3x3f view) {
        topLeft = view.mul(topLeft);
        bottomRight = view.mul(bottomRight);
        int rectX = (int) topLeft.x;
        int rectY = (int) topLeft.y;
        int rectWidth = (int) (bottomRight.x - topLeft.x);
        int rectHeight = (int) (bottomRight.y - topLeft.y);
        g.drawRect(rectX, rectY, rectWidth, rectHeight);
    }

    public static void drawRect(Graphics g, Vector2f topLeft, Vector2f bottomRight, Matrix3x3f view, Color color) {
        g.setColor(color);
        drawRect(g, topLeft, bottomRight, view);
    }

    public static void fillRect(Graphics g, Vector2f topLeft, Vector2f bottomRight) {
        int rectX = (int) topLeft.x;
        int rectY = (int) topLeft.y;
        int rectWidth = (int) (bottomRight.x - topLeft.x);
        int rectHeight = (int) (bottomRight.y - topLeft.y);
        g.fillRect(rectX, rectY, rectWidth, rectHeight);
    }

    public static void fillRect(Graphics g, Vector2f topLeft, Vector2f bottomRight, Color color) {
        g.setColor(color);
        fillRect(g, topLeft, bottomRight);
    }

    public static void fillRect(Graphics g, Vector2f topLeft, Vector2f bottomRight, Matrix3x3f view) {
        topLeft = view.mul(topLeft);
        bottomRight = view.mul(bottomRight);
        int rectX = (int) topLeft.x;
        int rectY = (int) topLeft.y;
        int rectWidth = (int) (bottomRight.x - topLeft.x);
        int rectHeight = (int) (bottomRight.y - topLeft.y);
        g.fillRect(rectX, rectY, rectWidth, rectHeight);
    }

    public static void fillRect(Graphics g, Vector2f topLeft, Vector2f bottomRight, Matrix3x3f view, Color color) {
        g.setColor(color);
        fillRect(g, topLeft, bottomRight, view);
    }

    public static void drawOval(Graphics g, Vector2f center, float radius) {
        Vector2f topLeft =
                new Vector2f(center.x - radius, center.y + radius);
        Vector2f bottomRight =
                new Vector2f(center.x + radius, center.y - radius);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleWidth = (int) (bottomRight.x - topLeft.x);
        int circleHeight = (int) (bottomRight.y - topLeft.y);
        g.drawOval(circleX, circleY, circleWidth, circleHeight);
    }

    public static void drawOval(Graphics g, Vector2f center, float radius, Color color) {
        g.setColor(color);
        drawOval(g, center, radius);
    }

    public static void drawOval(Graphics g, Vector2f center, float radius, Matrix3x3f view) {
        Vector2f topLeft =
                new Vector2f(center.x - radius, center.y + radius);
        topLeft = view.mul(topLeft);
        Vector2f bottomRight =
                new Vector2f(center.x + radius, center.y - radius);
        bottomRight = view.mul(bottomRight);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleWidth = (int) (bottomRight.x - topLeft.x);
        int circleHeight = (int) (bottomRight.y - topLeft.y);
        g.drawOval(circleX, circleY, circleWidth, circleHeight);
    }

    public static void drawOval(Graphics g, Vector2f center, float radius, Matrix3x3f view, Color color) {
        g.setColor(color);
        drawOval(g, center, radius, view);
    }

    public static void fillOval(Graphics g, Vector2f center, float radius) {
        Vector2f topLeft =
                new Vector2f(center.x - radius, center.y + radius);
        Vector2f bottomRight =
                new Vector2f(center.x + radius, center.y - radius);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleWidth = (int) (bottomRight.x - topLeft.x);
        int circleHeight = (int) (bottomRight.y - topLeft.y);
        g.fillOval(circleX, circleY, circleWidth, circleHeight);
    }

    public static void fillOval(Graphics g, Vector2f center, float radius, Color color) {
        g.setColor(color);
        fillOval(g, center, radius);
    }

    public static void fillOval(Graphics g, Vector2f center, float radius, Matrix3x3f view) {
        Vector2f topLeft =
                new Vector2f(center.x - radius, center.y + radius);
        topLeft = view.mul(topLeft);
        Vector2f bottomRight =
                new Vector2f(center.x + radius, center.y - radius);
        bottomRight = view.mul(bottomRight);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleWidth = (int) (bottomRight.x - topLeft.x);
        int circleHeight = (int) (bottomRight.y - topLeft.y);
        g.fillOval(circleX, circleY, circleWidth, circleHeight);
    }

    public static void fillOval(Graphics g, Vector2f center, float radius, Matrix3x3f view, Color color) {
        g.setColor(color);
        fillOval(g, center, radius, view);
    }

    /*****************************************************************************************************************/
    /*****************************************相交测试工具************************************************************/
    /*****************************************************************************************************************/
    public static boolean pointInPolygon(Vector2f point, Vector2f[] polygon) {
        boolean inside = false;
        if (polygon.length > 2) {
            Vector2f start = polygon[polygon.length - 1];
            boolean startAbove = start.y >= point.y;
            for(int i=0;i<polygon.length;i++) {
                Vector2f end = polygon[i];
                boolean endAbove = end.y >= point.y;
                if (startAbove != endAbove) {
                    float m = (end.y - start.y) / (end.x - start.x);
                    float x = start.x + (point.y - start.y) / m;
                    if (x >= point.x) {
                        inside = !inside;
                    }
                }
                startAbove = endAbove;
                start = end;
            }
        }
        return inside;
    }

    public static boolean pointInPolygon(Vector2f point, List<Vector2f> polygon) {
        return pointInPolygon(point, polygon.toArray(new Vector2f[0]));
    }

    public static boolean pointInPolygon(Vector2f point, List<Vector2f> poly, boolean winding) {
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

    public static boolean pointInPolygon(Vector2f point, Vector2f[] poly, boolean winding) {
        List<Vector2f> tmp = new ArrayList<Vector2f>();
        for (Vector2f p : poly) {
            tmp.add(p);
        }
        return pointInPolygon(point, tmp, winding);
    }

    //AABB矩形分别为矩形的左下角和右上角，且矩形各边应该和坐标轴平行
    public static boolean pointInAABB(Vector2f p, Vector2f min, Vector2f max) {
        return p.x > min.x && p.x < max.x &&
                p.y > min.y && p.y < max.y;
    }

    public static boolean pointInCircle(Vector2f p, Vector2f c, float r) {
        Vector2f dist = p.sub(c);
        return dist.lenSqr() < r * r;
    }

    public static boolean intersectAABB(
            Vector2f minA,Vector2f maxA,Vector2f minB,Vector2f maxB) {
        if (minA.x > maxB.x || minB.x > maxA.x) {
            return false;
        }
        if (minA.y > maxB.y || minB.y > maxA.y) {
            return false;
        }
        return true;
    }

    public static boolean intersectCircle(
            Vector2f c0,float r0,Vector2f c1,float r1) {
        Vector2f c = c0.sub(c1);
        float r = r0 + r1;
        return c.lenSqr() < r * r;
    }

    public static boolean intersectCircleAABB(
            Vector2f c, float r, Vector2f min, Vector2f max) {
        float d = 0.0f;
        if (c.x < min.x) {
            d += (c.x - min.x) * (c.x - min.x);
        }
        if (c.x > max.x) {
            d += (c.x - max.x) * (c.x - max.x);
        }
        if (c.y < min.y) {
            d += (c.y - min.y) * (c.y - min.y);
        }
        if (c.y > max.y) {
            d += (c.y - max.y) * (c.y - max.y);
        }
        return d < r * r;
    }

    private boolean intersectLineLine(
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

    public static boolean intersectRectRect(Vector2f[] A, Vector2f[] B) {
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

    //定义小于EPSILON的float值为零
    public static final float EPSILON = 0.000001f;

    public static Float[] lineRectIntersection(
            Vector2f O, Vector2f d, Vector2f[] rect
    ) {
        /**
         * 返回直线与矩形的交点数组，
         * 参数：
         *      线段起始点，线段的单位方向向量，矩形坐标
         * 需要判断返回值是否为null且在线段的长度范围内，
         * O.add(d.mul(Float[i])为交点坐标。
         */
        float largestMin = -Float.MAX_VALUE;
        float smallestMax = Float.MAX_VALUE;
        float swap;
        for (int i = 0; i < 2; i++) {
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

    public static Float[] lineCircleIntersection(
            Vector2f O, Vector2f d, Vector2f C, float r
    ) {
        /**
         * 返回直线与圆的交点数组，
         * 参数：
         *      线段起始点，线段的单位方向向量，圆心坐标和半径
         * 需要判断返回值是否为null且在线段的长度范围内，
         * O.add(d.mul(Float[i])为交点坐标。
         */
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
        return new Float[]{-b - root, -b + root};
    }

    public static Float[] lineLineIntersection(
            Vector2f A, Vector2f B, Vector2f C, Vector2f D
    ) {
        /**
         * 返回直线与直线的交点数组，
         * 参数：
         *      线段起始点，线段的终点
         * 返回值为null或位于0,1之间
         * A.add(B.sub(A).mul(Float[0]))为交点坐标
         * C.add(D.sub(C).mul(Float[1]))为交点坐标
         */
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
        return new Float[]{
                d / f, e / f
        };
    }

    public static Vector2f getReflectionVector(Vector2f V, Vector2f n) {
        //返回向量V关于向量n的反射向量
        Vector2f Vn = n.mul(V.dot(n));//V在垂直向量上的分向量
        Vector2f Vp = V.sub(Vn);//V在直线方向上的分向量
        return Vp.sub(Vn);
    }

    /*****************************************************************************************************************/
    /*****************************************文字绘制工具************************************************************/
    /*****************************************************************************************************************/
    //制作绘制字符串的工具
    //给定起始位置和字符串，并保持起始位置在左上角
    //一旦绘制了文本，将返回新的Y的位置，方便继续绘制
    public static int drawString(
            Graphics g, int x, int y, String... str
    ) {
        FontMetrics fm = g.getFontMetrics();
        int height = fm.getAscent() + fm.getDescent() + fm.getLeading();
        for (String s : str) {
            g.drawString(s, x, y + fm.getAscent());
            y += height;
        }
        return y;
    }

    public static int drawString(
            Graphics g, int x, int y, List<String> str
    ) {
        return drawString(g, x, y, str.toArray(new String[0]));
    }

    public static int drawString(
            Graphics g, int x, int y, String str
    ) {
        return drawString(g, x, y, new String[]{str});
    }

    //绘制居中字符串，接收屏幕的宽度作为参数
    public static int drawCenteredString(
            Graphics g, int w, int y, String... str
    ) {
        FontMetrics fm = g.getFontMetrics();
        int height = fm.getAscent() + fm.getDescent() + fm.getLeading();
        for (String s : str) {
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(s, g);
            int x = (w - (int) bounds.getWidth()) / 2;
            g.drawString(s, x, y + fm.getAscent());
            y += height;
        }
        return y;
    }

    public static int drawCenteredString(
            Graphics g, int w, int y, List<String> str
    ) {
        return drawCenteredString(g, w, y, str.toArray(new String[0]));
    }

    public static int drawCenterString(
            Graphics g, int w, int y, String str
    ) {
        return drawCenteredString(g, w, y, new String[]{str});
    }

    /*****************************************************************************************************************/
    /*****************************************文件读取工具************************************************************/
    /*****************************************************************************************************************/
    public static byte[] readBytes(InputStream in) {
        try {
            BufferedInputStream buf = new BufferedInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            while ((read = buf.read()) != -1) {
                out.write(read);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /*****************************************************************************************************************/
    /*****************************************图像缩放工具************************************************************/
    /*****************************************************************************************************************/
    public static BufferedImage scaleImage(
            BufferedImage toScale, int targetWidth, int targetHeight
    ) {
        int width = toScale.getWidth();
        int height = toScale.getHeight();
        if (targetWidth == width && targetHeight == height) {
            return toScale;
        } else if (targetWidth < width || targetHeight < height) {
            return scaleDownImage(toScale, targetWidth, targetHeight);
        } else {
            return scaleUpImage(toScale, targetWidth, targetHeight);
        }
    }

    private static BufferedImage scaleUpImage(
            BufferedImage toScale, int targetWidth, int targetHeight
    ) {
        BufferedImage image = new BufferedImage(
                targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(toScale, 0, 0, image.getWidth(), image.getHeight(), null);
        g2d.dispose();
        return image;
    }

    private static BufferedImage scaleDownImage(
            BufferedImage toScale, int targetWidth, int targetHeight
    ) {
        int w = toScale.getWidth();
        int h = toScale.getHeight();
        do {
            w = w / 2;
            if (w < targetWidth) {
                w = targetWidth;
            }
            h = h / 2;
            if (h < targetHeight) {
                h = targetHeight;
            }
            BufferedImage tmp = new BufferedImage(
                    w, h, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = tmp.createGraphics();
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(toScale, 0, 0, w, h, null);
            g2d.dispose();
            toScale = tmp;
        } while (w != targetWidth || h != targetHeight);
        return toScale;
    }
}
