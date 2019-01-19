package Javagames.input;

import Javagames.util.FrameRate;
import Javagames.util.KeyboardInput;
import Javagames.util.RelativeMouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class RelativeMouseExample extends JFrame implements Runnable {
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private Canvas canvas;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private Point point = new Point(0, 0);
    private boolean disableCursor = false;

    public RelativeMouseExample() {
        frameRate=new FrameRate();
    }

    protected void createAndShowGUI() {
        canvas = new Canvas();
        canvas.setSize(640, 480);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Relative Mouse Example");
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
        frameRate.initialize();
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

    private void processInput() {
        keyboard.poll();
        mouse.poll();
        Point p = mouse.getPosition();
        if (mouse.isRelative()) {
            point.x += p.x;
            point.y += p.y;
        }else{
            point.x = p.x;
            point.y = p.y;
        }
        //Wrap rectangle around the screen
        if (point.x + 25 < 0) {
            point.x = canvas.getWidth() - 1;
        } else if (point.x > canvas.getWidth() - 1) {
            point.x = -25;
        }
        if (point.y + 25 < 0) {
            point.y = canvas.getHeight() - 1;
        } else if (point.y > canvas.getHeight() - 1) {
            point.y = -25;
        }
        //Toggle relative
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            mouse.setRelative(!mouse.isRelative());
        }
        //Toggle cursor
        if (keyboard.keyDownOnce(KeyEvent.VK_C)) {
            disableCursor = !disableCursor;
            if (disableCursor) {
                disableCursor();
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private void render(Graphics g) {
        g.setColor(Color.GREEN);
        frameRate.calculate();
        g.drawString(frameRate.getFrameRate(), 30, 30);
        g.drawString(mouse.getPosition().toString(), 30, 45);
        g.drawString("Relative : " + mouse.isRelative(), 30, 60);
        g.drawString("Press Space to switch mouse modes", 30, 75);
        g.drawString("Press C to toggle cursor", 30, 90);
        g.setColor(Color.WHITE);
        g.drawRect(point.x,point.y,25,25);
    }

    private void disableCursor() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage("");
        Point point = new Point(0, 0);
        String name = "CanBeAnything";
        Cursor cursor = tk.createCustomCursor(image, point, name);
        setCursor(cursor);
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
        final RelativeMouseExample app = new RelativeMouseExample();
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
