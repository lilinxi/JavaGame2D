package Javagames.render;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RenderThreadExample extends JFrame implements Runnable {
    //创建定制的渲染进程
    private volatile boolean running;
    private Thread gameThread;

    public RenderThreadExample(){}

    protected void createAndShowGUI(){
        setSize(320,240);
        setTitle("Render Thread");
        setVisible(true);
        gameThread=new Thread(this);
        gameThread.start();
    }

    public void run(){
        running=true;
        while(running){
            //渲染代码
            System.out.println("Game Loop");
            sleep(10);
        }
    }

    private void sleep(long sleep){
        try{
            Thread.sleep(sleep);
        }catch (InterruptedException ex){}
    }

    protected void onWindowClosing(){
        try{
            System.out.println("Stopping Thread...");
            running=false;
            //public final void join()
            //                throws InterruptedException
            // 等待该线程终止
            gameThread.join();
            System.out.println("Stopped!!!");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args){
        final RenderThreadExample app=new RenderThreadExample();
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
        System.out.println("This might well be displayed before the other message.");
    }
}
