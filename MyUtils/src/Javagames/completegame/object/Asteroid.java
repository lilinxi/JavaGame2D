package Javagames.completegame.object;

import Javagames.util.Matrix3x3f;
import Javagames.util.Sprite;
import Javagames.util.Utility;
import Javagames.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Asteroid {
    public enum Size{
        Large,
        Medium,
        Small
    }

    private PolygonWrapper wrapper;
    private Size size;
    private Sprite sprite;
    private float rotation;
    private float rotationDelta;
    private Vector2f[] polygon;
    private Vector2f position;
    private Vector2f velocity;
    private ArrayList<Vector2f[]> collisionList;
    private ArrayList<Vector2f> positionList;

    public Asteroid(PolygonWrapper wrapper) {
        this.wrapper = wrapper;
        collisionList = new ArrayList<Vector2f[]>();
        positionList = new ArrayList<Vector2f>();
        velocity = getRandomVelocity();
        rotationDelta = getRandomRotationDelta();
    }

    private Vector2f getRandomVelocity() {
        float angle = getRandomRadians(0, 360);
        float radius = getRandomFloat(0.06f, 0.3f);
        return Vector2f.polar(angle, radius);
    }

    private float getRandomRotationDelta() {
        float radians = getRandomRadians(5, 45);
        return new Random().nextBoolean() ? radians : -radians;
    }

    private float getRandomRadians(int minDegree, int maxDegree) {
        int rand = new Random().nextInt(maxDegree - minDegree + 1);
        return (float) Math.toRadians(rand + minDegree);
    }

    private float getRandomFloat(float min, float max) {
        float rand = new Random().nextFloat();
        return rand * (max - min) + min;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setPolygon(Vector2f[] polygon) {
        this.polygon = polygon;
    }

    public Vector2f[] getPolygon() {
        return polygon;
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

    public void update(float time) {
        position = position.add(velocity.mul(time));
        position = wrapper.wrapPosition(position);
        rotation += rotationDelta * time;
        collisionList.clear();
        Vector2f[] world = transformPolygon();
        collisionList.add(world);
        wrapper.wrapPolygon(world, collisionList);
        positionList.clear();
        positionList.add(position);
        wrapper.wrapPositions(world, position, positionList);
    }

    private Vector2f[] transformPolygon() {
        Matrix3x3f mat = Matrix3x3f.rotate(rotation);
        mat = mat.mul(Matrix3x3f.translate(position));
        return Utility.transform(polygon, mat);
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        for (Vector2f pos : positionList) {
            sprite.render(g, view, pos, rotation);
        }
    }

    public boolean contains(Vector2f point) {
        for (Vector2f[] polygon : collisionList) {
            if (Utility.pointInPolygon(point, polygon)) {
                return true;
            }
        }
        return false;
    }
}
