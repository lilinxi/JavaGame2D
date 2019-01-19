package Javagames.render;

import Javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class ActiveRenderingExample extends JFrame implements Runnable {
    //创建主动渲染的窗口
    //
    //public abstract class BufferStrategy extends Object
    // BufferStrategy 类表示用来在特定的 Canvas 或 Window 上组织复杂内存的机制。
    // 硬件和软件限制决定了是否能够实现特定的缓冲区策略以及如何实现它。
    // 从创建 Canvas 或 Window 时所用 GraphicsConfiguration 的性能中可以发觉这些限制。
    //
    //值得注意的是，术语 buffer 和 surface 意思相同：视频设备内存或系统内存中的连续内存区域。
    //
    //存在几种类型的复杂缓冲区策略，包括连续环形缓冲和 blit 缓冲。
    // 连续环形缓冲（即双缓冲或三次缓冲）是最常见的缓冲区策略：
    // 将一个应用程序绘制到单个后备缓冲区，然后只用一个步骤将其内容移入到前端（显示），
    // 这可通过复制数据或移动视频指针完成。移动视频指针可交换缓冲区，于是绘制的第一个缓冲区成为前端缓冲区，
    // 或称设备上当前所显示的内容；这称为页面翻转。
    //
    //作为一种替代方式，可以复制后备缓冲区的内容，即使用链而不是移动视频指针进行位图传输 (blitted)。
    //
    //
    //双缓冲：
    //                    ***********         ***********
    //                    *         * ------> *         *
    // [到显示器]    <---- * Front B *   显示  * Back B. * <---- 呈现
    //                    *         * <------ *         *
    //                    ***********         ***********
    //
    //三次缓冲：
    //
    // [到      ***********         ***********        ***********
    //显示器]   *         * --------+---------+------> *         *
    //    <---- * Front B *   显示  * Mid. B. *        * Back B. * <---- 呈现
    //          *         * <------ *         * <----- *         *
    //          ***********         ***********        ***********
    //
    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;

    public ActiveRenderingExample(){
        frameRate = new FrameRate();
    }

    protected void createAndShowGUI(){
        Canvas canvas=new Canvas();
        canvas.setSize(320, 240);
        canvas.setBackground(Color.BLACK);
        //public void setIgnoreRepaint(boolean ignoreRepaint)
        // 设置是否应该忽略从操作系统接受的绘制消息。
        // 这不会影响 AWT 在软件中生成的绘制事件，除非它们是对操作系统级别绘制消息的直接响应。
        // 这是很有用的，例如正在全屏模式中运行并且需要更佳的性能，或者使用页面翻转作为缓冲策略时。
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setTitle("Active Rendering");
        setIgnoreRepaint(true);
        pack();
        setVisible(true);
        //public void createBufferStrategy(int numBuffers)
        // 创建一个新的策略，用于对此组件的多次缓冲。
        // 多次缓冲对于提高呈现性能很有用。
        // 此方法试图使用提供的缓冲区数创建最佳可用策略。
        // 它始终创建一个使用该数量缓冲区的 BufferStrategy。
        // 首先尝试页面翻转策略，然后尝试使用加速缓冲区的位图传输策略。
        // 最后，使用非加速的位图传输策略。
        //每次调用此方法时，都会丢弃此组件现有的缓冲区策略。
        //使用双缓冲策略
        canvas.createBufferStrategy(2);
        //public BufferStrategy getBufferStrategy()
        // 返回由此组件使用的 BufferStrategy。如
        // 果尚未创建 BufferStrategy 或者已经用完了内存，则此方法返回 null
        bs=canvas.getBufferStrategy();
        gameThread=new Thread(this);
        gameThread.start();
    }

    public void run(){
        running=true;
        frameRate.initialize();
        while(running){
            gameLoop();
        }
    }

    public void gameLoop(){
        do{
            // The following loop ensures that the contents of the drawing buffer
            // are consistent in case the underlying surface was recreated
            do{
                Graphics g=null;
                try{
                    // Get a new graphics context every time through the loop
                    // to make sure the strategy is validated
                    g=bs.getDrawGraphics();
                    //渲染代码
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                }finally {
                    if(g!=null){
                        g.dispose();
                    }
                }
                // Repeat the rendering if the drawing buffer contents
                // were restored
            }while(bs.contentsRestored());//返回绘制缓冲区最近是否从丢失状态恢复，并重新初始化为默认背景色（白色）。
            bs.show();
            // Repeat the rendering if the drawing buffer was lost
        }while(bs.contentsLost());//返回上次调用 getDrawGraphics 后绘制缓冲区是否丢失。
    }

    private void render(Graphics g){
        frameRate.calculate();
        g.setColor(Color.GREEN);
        g.drawString(frameRate.getFrameRate(), 20, 20);
    }

    protected void onWindowClosing(){
        try{
            running=false;
            gameThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args){
        final ActiveRenderingExample app=new ActiveRenderingExample();
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
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
