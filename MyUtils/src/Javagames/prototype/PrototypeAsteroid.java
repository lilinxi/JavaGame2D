package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PrototypeAsteroid {
    //陨石类
    //由包装类构造
    //需要设置，位置，多边形形状和大小
    //速度和旋转随机生成
    public enum Size{
        Large,
        Medium,
        Small
    }

    private PolygonWrapper wrapper;
    private Size size;
    private float rotation;
    private float rotationDelta;
    private Vector2f[] polygon;
    private Vector2f position;
    private Vector2f velocity;
    private ArrayList<Vector2f[]> renderList;

    public PrototypeAsteroid(PolygonWrapper wrapper) {
        this.wrapper = wrapper;
        renderList = new ArrayList<Vector2f[]>();
        velocity = getRandomVelocity();
        rotationDelta = getRandomRotationDelta();
    }

    //由极坐标来创建随机的速度，半径即为速度的绝对值大小
    private Vector2f getRandomVelocity() {
        float angle = getRandomRadians(0, 360);
        float radius = getRandomFloat(0.06f, 0.3f);
        return Vector2f.polar(angle, radius);
    }

    //顺时针旋转和逆时针旋转的概率相同
    private float getRandomRotationDelta() {
        float radians = getRandomRadians(5, 45);
        return new Random().nextBoolean() ? radians : -radians;
    }

    //由角度的旋转速度得到弧度的旋转速度
    private float getRandomRadians(int minDegree, int maxDegree) {
        int rand = new Random().nextInt(maxDegree - minDegree + 1);
        return (float) Math.toRadians(rand + minDegree);
    }

    //获取min到max的随机单精度浮点数，nextFloat（）的返回值为0.0f到1.0f
    private float getRandomFloat(float min, float max) {
        float rand = new Random().nextFloat();
        return rand * (max - min) + min;
    }

    public void setPolygon(Vector2f[] polygon) {
        this.polygon = polygon;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    //每帧调用
    //根据速度更新位置，并判断位置是否越界
    //更新旋转角度
    public void update(float time) {
        position = position.add(velocity.mul(time));
        position = wrapper.wrapPosition(position);
        rotation += rotationDelta * time;
        renderList.clear();
        Vector2f[] world = transformPolygon();
        renderList.add(world);
        wrapper.wrapPolygon(world, renderList);
    }

    private Vector2f[] transformPolygon() {
        Matrix3x3f mat = Matrix3x3f.rotate(rotation);
        mat = mat.mul(Matrix3x3f.translate(position));
        return Utility.transform(polygon, mat);
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        //给定视口矩阵和Graphics对象绘制多边形
        for (Vector2f[] polygon : renderList) {
            Utility.fillPolygon(g, polygon, view, Color.LIGHT_GRAY);
            Utility.drawPolygon(g, polygon, view, Color.BLACK);
        }
    }

    public boolean contains(Vector2f point) {
        for (Vector2f[] polygon : renderList) {
            if (Utility.pointInPolygon(point, polygon, false)) {
                return true;
            }
        }
        return false;
    }
}
