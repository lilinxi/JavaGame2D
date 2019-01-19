package Javagames.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SwingFramework extends GameFramework {
    /**
     * SwingFramework位于Javagames.util包中，它扩展了GameFramework并添加了Swing组件。这个框架用于为游戏资源创建一个较好的编辑器。
     * getScreenWidth（）和getScreenHeight（）方法返回画布的大小，这将是使用游戏渲染的GUI的一部分。
     * renderFrame（）方法的在WindowFramework中相同。
     * 有趣的事情发生在createFramework（）方法中。
     * 首先，外观更新为Nimbus版本。
     * 然后，创建了一个主面板。
     * 这个主面板在另一个面板上创建了一个游戏画布，使用一个空的布局管理器来维护高宽比，并且使用一个BorderLayout将整个内容添加到主面板的中央。
     * 使用该画布创建了常用的输入和缓存策略。
     * 由于使用了空的布局，因此在任何时候，当更新画布的大小和位置时，都会调用repaint（）方法。
     * 此外，注意，这个框架创建了另一个要覆盖的方法。
     * 在创建了Swing组件后，调用onCreateAndShowGUI（），但这是在GUI可见之前，因此还可以添加其他的组件。
     * 之后还将使用更新的PolygonEditor来扩展该类。
     */
    protected Canvas canvas;
    private JPanel mainPanel;
    private JPanel centerPanel;

    protected JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getCenterPanel(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setBackground(appBorder);
            centerPanel.setLayout(null);
            centerPanel.add(getCanvas());
        }
        return centerPanel;
    }

    private Canvas getCanvas() {
        if (canvas == null) {
            canvas = new Canvas();
            canvas.setBackground(appBackground);
        }
        return canvas;
    }

    private void setUpLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onCreateAndShowGUI() {}

    @Override
    protected void createFramework() {
        setUpLookAndFeel();
        getContentPane().add(getMainPanel());
        setLocationByPlatform(true);
        setSize(appWidth, appHeight);
        setTitle(appTitle);
        getContentPane().setBackground(appBorder);
        setSize(appWidth, appHeight);
        getContentPane().addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                onComponentResized(e);
            }
        });
        setupInput(getCanvas());
        onCreateAndShowGUI();
        setVisible(true);
        createBufferStrategy(getCanvas());
        getCanvas().requestFocus();
    }

    protected void onComponentResized(ComponentEvent e) {
        Dimension size = getContentPane().getSize();
        setupViewport(size.width, size.height);
        getCanvas().setLocation(vx, vy);
        getCanvas().setSize(vw, vh);
        //改变大小时重新绘制
        getCanvas().repaint();
    }

    @Override
    protected void renderFrame(Graphics g) {
        g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
        render(g);
    }

    @Override
    public int getScreenWidth() {
        return getCanvas().getWidth();
    }

    @Override
    public int getScreenHeight() {
        return getCanvas().getHeight();
    }

    public static void main(String[] args) {
        launchApp(new SwingFramework());
    }
}
