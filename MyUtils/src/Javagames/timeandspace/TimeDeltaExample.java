package Javagames.timeandspace;

import Javagames.util.FrameRate;
import Javagames.util.KeyboardInput;
import Javagames.util.RelativeMouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class TimeDeltaExample extends JFrame implements Runnable {
    //绘制在指定时间旋转一周的点
    //计算时间增量
    //新的位置 = 旧的位置 + 帧速率 * 时间增量
    //把一秒拆成好多份纳秒
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private Canvas canvas;
    private float angle;
    private float step;
    private long sleep;

    public TimeDeltaExample() {}

    protected void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setSize(480, 480);
        canvas.setBackground(Color.WHITE);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Time Delta Example");
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
        //返回最准确的可用系统计时器的当前值，以纳秒为单位。
        //此方法只能用于测量已过的时间，与系统或钟表时间的其他任何时间概念无关。
        // 返回值表示从某一固定但任意的时间算起的毫微秒数（或许从以后算起，所以该值可能为负）。
        long curTime = System.nanoTime();
        long lastTime = curTime;
        double nsPerFrame;
        while (running) {
            curTime = System.nanoTime();
            nsPerFrame = curTime - lastTime;
            gameLoop(nsPerFrame / 1.0E9);
            lastTime = curTime;
        }
    }

    private void gameLoop(double delta) {
        processInput(delta);
        updateObject(delta);
        renderFrame();
        sleep(sleep);
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
        angle = 0.0f;
        //4秒旋转一周
        step = (float) Math.PI / 2.0f;
    }

    private void processInput(double delta) {
        keyboard.poll();
        mouse.poll();
        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) {
            sleep += 10;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
            sleep -= 10;
        }
        //如果帧速率过低，会漏掉键盘按下的事件
        if (sleep > 1000) {
            sleep = 1000;
        }
        if (sleep < 0) {
            sleep = 0;
        }
    }

    private void updateObject(double delta) {
        angle += step * delta;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
    }

    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        frameRate.calculate();
        g.drawString(frameRate.getFrameRate(), 30, 30);
        g.drawString("Up arrow increases sleep time", 30, 45);
        g.drawString("Down arrow decreases sleep time", 30, 60);
        g.drawString("Sleep time(ms): " + sleep, 30, 75);
        int x = canvas.getWidth() / 4;
        int y = canvas.getHeight() / 4;
        int w = canvas.getWidth() / 2;
        int h = canvas.getHeight() / 2;
        g.drawOval(x, y, w, h);
        //polar -> coordinates
        float rw = w / 2;//radius width
        float rh = h / 2;//radius height
        int rx = (int) (rw * Math.cos(angle));
        int ry = (int) (rh * Math.sin(angle));
        int cx = (int) (rx + w);
        int cy = (int) (ry + h);
        //draw clock hand
        g.drawLine(w, h, cx, cy);
        //draw dot at end of hand
        g.setColor(Color.RED);
        g.fillRect(cx - 2, cy - 2, 4, 4);
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
        final TimeDeltaExample app = new TimeDeltaExample();
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
    }
}
