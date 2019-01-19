package Javagames.transform;

import Javagames.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class MatrixMultiplyExample extends JFrame implements Runnable {
    //绘制地球围绕太阳旋转，月球围绕地球旋转
    //地球的转换矩阵最后要乘以太阳的转换矩阵
    //月球的转换矩阵最后要乘以地球的转换矩阵
    //乘之前的矩阵为相对运动矩阵
    private static final int SCREEN_W = 640;
    private static final int SCREEN_H = 480;
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private float earthRot, earthDelta;
    private float moonRot, moonDelta;
    private boolean showStars;
    private int stars[];
    private Random rand = new Random();

    public MatrixMultiplyExample() {}

    protected void createAndShowGUI() {
        Canvas canvas = new Canvas();
        canvas.setSize(SCREEN_W, SCREEN_H);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Matrix Multiply Example");
        setIgnoreRepaint(true);
        pack();
        //Add key listener
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);
        //Add mouse listener
        //For full screen : mouse=new RelativeMouseInput(this);
        mouse = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);
        setVisible(true);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        running=true;
        initialize();
        while (running) {
            gameLoop();
        }
    }

    private void gameLoop() {
        processInput();
        renderFrame();
        sleep(10L);
    }

    private void renderFrame() {
        do{
            do{
                Graphics g=null;
                try{
                    g=bs.getDrawGraphics();
                    //渲染代码
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                }finally {
                    if(g!=null){
                        g.dispose();
                    }
                }
            }while(bs.contentsRestored());//返回绘制缓冲区最近是否从丢失状态恢复，并重新初始化为默认背景色（白色）。
            bs.show();
        }while(bs.contentsLost());//返回上次调用 getDrawGraphics 后绘制缓冲区是否丢失。
    }

    private void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {}
    }

    private void initialize() {
        frameRate = new FrameRate();
        frameRate.initialize();
        earthDelta = (float) Math.toRadians(0.4);
        moonDelta = (float) Math.toRadians(4.8);
        showStars = true;
        stars = new int[1000];
        for(int i=0;i<stars.length-1;i+=2) {
            stars[i] = rand.nextInt(SCREEN_W);
            stars[i + 1] = rand.nextInt(SCREEN_H);
        }
    }

    private void processInput() {
        keyboard.poll();
        mouse.poll();
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            showStars = !showStars;
        }
    }

    private void render(Graphics g) {
        g.setColor(Color.GREEN);
        frameRate.calculate();
        g.drawString(frameRate.getFrameRate(), 30, 30);
        g.drawString("Press [SPACE] to toggle stars", 30, 45);
        //draw stars...
        if (showStars) {
            g.setColor(Color.WHITE);
            for(int i=0;i<stars.length-1;i+=2) {
                g.fillRect(stars[i], stars[i + 1], 1, 1);
            }
        }
        //draw the sun...
        Matrix3x3f sunMat = Matrix3x3f.translate(SCREEN_W / 2, SCREEN_H / 2);
        Vector2f sun = sunMat.mul(new Vector2f());
        g.setColor(Color.YELLOW);
        g.fillOval((int) sun.x - 50, (int) sun.y - 50, 100, 100);
        g.setColor(Color.WHITE);
        g.drawArc((int) SCREEN_W / 4,
                (int) sun.y - SCREEN_W / 4,
                SCREEN_W / 2,
                SCREEN_W / 2,
                0,
                360);
        //draw the Earth...
        Matrix3x3f earthMat = Matrix3x3f.translate(SCREEN_W / 4, 0);
        earthMat = earthMat.mul(Matrix3x3f.rotate(earthRot));
        earthMat = earthMat.mul(sunMat);
        earthRot += earthDelta;
        Vector2f earth = earthMat.mul(new Vector2f());
        g.setColor(Color.BLUE);
        g.fillOval((int) earth.x - 10, (int) earth.y - 10, 20, 20);
        g.setColor(Color.WHITE);
        g.drawOval((int) earth.x - 30, (int) earth.y - 30, 60, 60);
        //draw the Moon...
        Matrix3x3f moonMat = Matrix3x3f.translate(30, 0);
        moonMat = moonMat.mul(Matrix3x3f.rotate(moonRot));
        moonMat = moonMat.mul(earthMat);
        moonRot += moonDelta;
        Vector2f moon = moonMat.mul(new Vector2f());
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval((int) moon.x - 5, (int) moon.y - 5, 10, 10);
    }

    protected void onWindowClosing() {
        try {
            running = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
            final MatrixMultiplyExample app = new MatrixMultiplyExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.onWindowClosing();
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGUI();
            }
        });
        /*
        仿射变换类
        AffineTransform trans = new AffineTransform();
        trans.setToIdentity();
        trans.concatenate(AffineTransform);
        trans.rotate(double radians);
        trans.scale(double sx, double sy);
        trans.translate(double tx, double ty);
        trans.shear(double sx,double sy);
        Shape trans.createTransformedShape(Shape);
        e.g.*/
        AffineTransform trans = new AffineTransform();
        trans.translate(5.0, 7);
        trans.rotate(Math.PI);
        System.out.println(trans);
        AffineTransform t1 = new AffineTransform();
        t1.translate(5, 7);
        AffineTransform t2 = new AffineTransform();
        t2.rotate(Math.PI);
        t1.concatenate(t2);
        System.out.println(t1);
        /*
        变换trans等同于t1
        */
    }
}
