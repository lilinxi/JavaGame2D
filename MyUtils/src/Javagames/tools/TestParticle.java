package Javagames.tools;

import Javagames.util.Matrix3x3f;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;

public class TestParticle {
    /**
     * 创建一个简单的粒子引擎。
     * TestParticle位于Javagames.tools包中，它是表示一个粒子的一个简单的类。
     * 半径是所渲染的圆的大小，用颜色来填充圆。
     * 向量有一个角度和方向，还有一个以秒为单位的生命周期。
     * update（）方法更新粒子已经存在的时间，并且更新其当前位置。
     * render（）方法绘制缩放到屏幕上的较小的圆，hasDied（）方法检查生命周期是否已经过期。
     */
    private Vector2f pos;
    private Vector2f curPos;
    private Vector2f vel;
    private Vector2f curVel;
    private Color color;
    private float lifeSpan;
    private float time;
    private float radius;

    public TestParticle() {}

    public void setPosition(Vector2f pos) {
        this.pos = pos;
    }

    public void setVector(float angel, float r) {
        vel = Vector2f.polar(angel, r);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setLifeSpan(float lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void update(float delta) {
        time += delta;
        curVel = vel.mul(time);
        curPos = pos.add(curVel);
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        Utility.drawOval(g, curPos, radius, view, color);
    }

    public boolean hasDied() {
        return time > lifeSpan;
    }
}
