package Javagames.util;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowFramework extends GameFramework {
    /**
     * WindowFramework位于Javagames.util包中，它扩展了GameFramework以提供一个游戏窗口。
     * 覆盖了createFramework（）方法，创建了Canvas对象。
     * 使用相同的组件监听器来维护高宽比。
     * 注意，setupViewport（）方法和画布的宽度和高度一起使用，并且该画布传递给了setupInput（）和createBufferStrategy（）方法。
     * renderFrame（）方法清除了画布，并且把Graphics对象传递给了render（）方法。
     */
    private Canvas canvas;

    @Override
    protected void createFramework() {
        canvas = new Canvas();
        canvas.setBackground(appBackground);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setLocationByPlatform(true);
        if (appMaintainRatio) {
            getContentPane().setBackground(appBorder);
            setSize(appWidth, appHeight);
            setLayout(null);
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
        } else {
            canvas.setSize(appWidth, appHeight);
            pack();
        }
        setTitle(appTitle);
        setupInput(canvas);
        setVisible(true);
        createBufferStrategy(canvas);
        canvas.requestFocus();
    }

    protected void onComponentResized(ComponentEvent e) {
        Dimension size = getContentPane().getSize();
        setupViewport(size.width, size.height);
        canvas.setLocation(vx, vy);
        canvas.setSize(vw, vh);
    }

    @Override
    public int getScreenWidth() {
        return canvas.getWidth();
    }

    @Override
    public int getScreenHeight() {
        return canvas.getHeight();
    }

    @Override
    protected void renderFrame(Graphics g) {
        g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
        render(g);
    }
}
