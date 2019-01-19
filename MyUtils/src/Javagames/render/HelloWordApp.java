package Javagames.render;

import Javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;

public class HelloWordApp extends JFrame {
    //使用被动渲染来更新显示
    private FrameRate frameRate;
    public HelloWordApp(){
        frameRate=new FrameRate();
    }

    protected void createAndShowGUI(){
        GamePanel gamePanel=new GamePanel();
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setPreferredSize(new Dimension(320, 240));
        getContentPane().add(gamePanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Hello World!");
        //public void pack()
        // 调整此窗口的大小，以适合其子组件的首选大小和布局。
        // 如果该窗口和/或其所有者还不可显示，则在计算首选大小之前都将变得可显示。
        // 在计算首选大小之后，将会验证该窗口。
        pack();
        frameRate.initialize();
        setVisible(true);
    }

    //覆盖JPanel的paint（）方法，paint（）方法在绘制JPanel的时候每一帧调用
    private class GamePanel extends JPanel{
        public void paint(Graphics g){
            super.paint(g);
            onPaint(g);
        }
    }

    protected void onPaint(Graphics g){
        frameRate.calculate();
        g.setColor(Color.WHITE);
        g.drawString(frameRate.getFrameRate(), 20, 20);
        repaint();
    }

    public static void main(String[] args){
        final HelloWordApp app=new HelloWordApp();
        //public static void invokeLater(Runnable doRun)
        // 导致 doRun.run() 在 AWT 事件指派线程上异步执行。
        // 在所有挂起的 AWT 事件被处理后才发生。
        // 此方法应该在应用程序线程需要更新该 GUI 时使用。
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGUI();
            }
        });
    }
}
