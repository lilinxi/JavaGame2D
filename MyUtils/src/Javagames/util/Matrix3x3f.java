package Javagames.util;

public class Matrix3x3f {
    public static final int DIMENSION = 3;
    private float[][] m = new float[DIMENSION][DIMENSION];
    //m[2][2]=0时，只对第一次向量变换有效，变换后，w = 0

    public Matrix3x3f() {}

    public Matrix3x3f(float[][] m) {
        setMatrix(m);
    }

    public Matrix3x3f add(Matrix3x3f m1) {
        float[][] tmp = new float[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                tmp[i][j] = m[i][j] + m1.m[i][j];
            }
        }
        return new Matrix3x3f(tmp);
    }

    public Matrix3x3f sub(Matrix3x3f m1) {
        float[][] tmp = new float[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                tmp[i][j] = m[i][j] - m1.m[i][j];
            }
        }
        return new Matrix3x3f(tmp);
    }

    public Matrix3x3f mul(Matrix3x3f m1) {
        float[][] tmp = new float[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int k = 0; k < DIMENSION; k++) {
                for(int j=0;j<DIMENSION;j++) {
                    tmp[i][j] += m[i][k] * m1.m[k][j];
                }
            }
        }
        return new Matrix3x3f(tmp);
    }

    public void setMatrix(float[][] m) {
        if (m.length == DIMENSION && m[0].length == DIMENSION) {
            this.m = m;
        }
    }

    public static Matrix3x3f zero() {
        return new Matrix3x3f(new float[DIMENSION][DIMENSION]);
    }

    public static Matrix3x3f identity() {
        float[][] tmp = new float[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            tmp[i][i] = 1.0f;
        }
        return new Matrix3x3f(tmp);
    }

    public static Matrix3x3f translate(Vector2f v) {
        return translate(v.x, v.y);
    }

    public static Matrix3x3f translate(float x, float y) {
        return new Matrix3x3f(new float[][]{
                {1.0f, 0.0f, 0.0f},
                {0.0f, 1.0f, 0.0f},
                {x, y, 1.0f}
        });
    }

    public static Matrix3x3f scale(Vector2f v) {
        return scale(v.x, v.y);
    }

    public static Matrix3x3f scale(float x, float y) {
        return new Matrix3x3f(new float[][]{
                {x, 0.0f, 0.0f},
                {0.0f, y, 0.0f},
                {0.0f, 0.0f, 1.0f}
        });
    }

    public static Matrix3x3f shear(Vector2f v) {
        return shear(v.x, v.y);
    }

    public static Matrix3x3f shear(float x, float y) {
        return new Matrix3x3f(new float[][]{
                {1.0f, y, 0.0f},
                {x, 1.0f, 0.0f},
                {0.0f, 0.0f, 1.0f}
        });
    }

    //参数为弧度制的角
    public static Matrix3x3f rotate(float rad) {
        return new Matrix3x3f(new float[][]{
                {(float) Math.cos(rad), (float) Math.sin(rad), 0.0f},
                {(float) -Math.sin(rad), (float) Math.cos(rad), 0.0f},
                {0.0f, 0.0f, 1.0f}
        });
    }

    public Vector2f mul(Vector2f vec) {
        //行主矩阵，1*M行向量 * M*M变换矩阵
        return new Vector2f(
                vec.x * this.m[0][0]
                        + vec.y * this.m[1][0]
                        + vec.w * this.m[2][0],
                vec.x * this.m[0][1]
                        + vec.y * this.m[1][1]
                        + vec.w * this.m[2][1],
                vec.x * this.m[0][2]
                        + vec.y * this.m[1][2]
                        + vec.w * this.m[2][2]
        );
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for(int i=0;i<DIMENSION;i++) {
            buf.append("[");
            buf.append(m[i][0]);
            buf.append(",\t");
            buf.append(m[i][1]);
            buf.append(",\t");
            buf.append(m[i][2]);
            buf.append("]\n");
        }
        return buf.toString();
    }
}
