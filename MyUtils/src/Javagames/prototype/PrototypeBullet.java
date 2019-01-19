package Javagames.prototype;

import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;

public class PrototypeBullet {
    //创建一个子弹的实例，需要提供位置参数和速度的角度参数
    private Vector2f velocity;
    private Vector2f position;
    private Color color;
    private float radius;

    public PrototypeBullet(Vector2f position, float angle) {
        this.position = position;
        velocity = Vector2f.polar(angle, 1.0f);
        radius = 0.006f;
        color = Color.GREEN;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        Utility.fillOval(g, position, radius, view, color);
    }

    //每帧调用，由速度更新位置
    public void update(float time) {
        position = position.add(velocity.mul(time));
    }
}
