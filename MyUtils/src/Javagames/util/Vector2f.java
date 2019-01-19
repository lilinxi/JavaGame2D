package Javagames.util;

public class Vector2f {
    public float x;
    public float y;
    public float w;
    //只有w=1时，平移矩阵才有效
    //对点，w = 1，可以平移
    //对向量，w = 0，向量不能平移

    public Vector2f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.w = 1.0f;
    }

    public Vector2f(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
        this.w = v.w;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
        this.w = 1.0f;
    }

    public Vector2f(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.w = w;
    }

    public void translate(float tx, float ty) {
        x += tx;
        y += ty;
    }

    public void scale(float s) {
        x *= s;
        y *= s;
    }

    public void scale(float sx, float sy) {
        x *= sx;
        y *= sy;
    }

    //参数为弧度制的角
    public void rotate(float rad) {
        float tmp = (float) (x * Math.cos(rad) - y * Math.sin(rad));
        y = (float) (x * Math.sin(rad) + y * Math.cos(rad));
        x=tmp;
    }

    public void shear(float s) {
        float tmp = x + s * y;
        y = y + s * x;
        x = tmp;
    }

    public void shear(float sx, float sy) {
        float tmp = x + sx * y;
        y = y + sy * x;
        x=tmp;
    }

    public Vector2f inv() {
        return new Vector2f(-x, -y);
    }

    public Vector2f add(Vector2f v) {
        return new Vector2f(x + v.x, y + v.y);
    }

    public Vector2f sub(Vector2f v) {
        return new Vector2f(x - v.x, y - v.y);
    }

    public Vector2f mul(float scalar) {
        return new Vector2f(scalar * x, scalar * y);
    }

    public Vector2f div(float scalar) {
        return new Vector2f(x / scalar, y / scalar);
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lenSqr() {
        return x * x + y * y;
    }

    public Vector2f norm() {
        //返回正规化向量
        return div(len());
    }

    public Vector2f perp() {
        //返回垂直向量
        return new Vector2f(-y, x);
    }

    public float dot(Vector2f v) {
        //返回两个向量的点积
        return x * v.x + y * v.y;
    }

    public float angle() {
        //返回向量的角度，弧度表示，PI到-PI
        return (float) Math.atan2(y, x);
    }

    public static Vector2f polar(float angle, float radius) {
        //由弧度单位的角度值和半径创建向量
        return new Vector2f(
                radius * (float) (Math.cos(angle)),
                radius * (float) (Math.sin(angle))
        );
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", x, y);
    }
}
