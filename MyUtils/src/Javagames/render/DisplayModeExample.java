package Javagames.render;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DisplayModeExample extends JFrame {
    //修改显示模式
    //切换全屏，切换屏幕分辨率
    class DisplayModeWrapper{
        //DisplayMode封装类
        //覆盖toString（）函数使DisplayMode可以打印出来
        //覆盖equals（）函数使相同分辨率的显示模式不重复在单选框中出现
        //
        //public final class DisplayMode extends Object
        // DisplayMode 类封装 GraphicsDevice 的位深、高度、宽度和刷新率。
        // 更改图形设备的显示模式的能力是与平台和配置有关的，可能并不总是可用的
        private DisplayMode dm;

        public DisplayModeWrapper(DisplayMode dm) {
            this.dm=dm;
        }

        public boolean equals(Object obj) {
            DisplayModeWrapper other = (DisplayModeWrapper) obj;
            if (dm.getWidth() != other.dm.getWidth()) {
                return false;
            } else if (dm.getHeight() != other.dm.getHeight()) {
                return false;
            }else{
                return true;
            }
        }

        public String toString(){
            return ""+dm.getWidth()+"*"+dm.getHeight();
        }
    }

    private JComboBox displayModes;
    private GraphicsDevice graphicsDevice;
    private DisplayMode currentDisplayMode;

    public DisplayModeExample() {
        //public abstract class GraphicsEnvironment extends Object
        // GraphicsEnvironment 类描述了 Java(tm) 应用程序在特定平台上可用的 GraphicsDevice 对象和 Font 对象的集合。
        //
        //public static GraphicsEnvironment getLocalGraphicsEnvironment()
        // 返回本地 GraphicsEnvironment。
        //
        //public abstract class GraphicsDevice extends Object
        // GraphicsDevice 类描述可以在特定图形环境中使用的图形设备。
        //
        //public abstract GraphicsDevice getDefaultScreenDevice()
        //                                               throws HeadlessException
        // 返回默认的屏幕 GraphicsDevice。
        //
        //public DisplayMode getDisplayMode()
        // 返回此 GraphicsDevice 的当前显示模式。
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice=ge.getDefaultScreenDevice();
        currentDisplayMode=graphicsDevice.getDisplayMode();
    }

    private JPanel getMainPanel() {
        JPanel p = new JPanel();
        displayModes = new JComboBox(listDisplayModes());
        p.add(displayModes);
        JButton enterButton = new JButton("Enter Full Screen");
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEnterFullScreen();
            }
        });
        p.add(enterButton);
        JButton exitButton = new JButton("Exit Full Screen");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExitFullScreen();
            }
        });
        p.add(exitButton);
        return p;
    }

    private DisplayModeWrapper[] listDisplayModes() {
        ArrayList<DisplayModeWrapper> list = new ArrayList<DisplayModeWrapper>();
        for (DisplayMode mode : graphicsDevice.getDisplayModes()) {
            if (mode.getBitDepth() == 32) {
                DisplayModeWrapper wrap = new DisplayModeWrapper(mode);
                if (!list.contains(wrap)) {
                    list.add(wrap);
                }
            }
        }
        return list.toArray(new DisplayModeWrapper[0]);
        //toArray
        //public <T> T[] toArray(T[] a)
        // 按适当顺序（从第一个到最后一个元素）返回包含此列表中所有元素的数组；
        // 返回数组的运行时类型是指定数组的运行时类型。如果指定的数组能容纳列表，则将该列表返回此处。
        // 否则，将分配一个具有指定数组的运行时类型和此列表大小的新数组。
        //如果指定的数组能容纳队列，并有剩余的空间（即数组的元素比队列多），
        // 那么会将数组中紧接 collection 尾部的元素设置为 null。
    }

    protected void createAndShowGUI() {
        Container canvas=getContentPane();
        canvas.add(getMainPanel());
        canvas.setIgnoreRepaint(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Display Mode Test");
        pack();
        setVisible(true);
    }

    protected void onEnterFullScreen() {
        if (graphicsDevice.isFullScreenSupported()) {
            DisplayMode newMode=getSelectedMode();
            graphicsDevice.setFullScreenWindow(this);
            graphicsDevice.setDisplayMode(newMode);
        }
    }

    protected void onExitFullScreen() {
        graphicsDevice.setDisplayMode(currentDisplayMode);
        graphicsDevice.setFullScreenWindow(null);
    }

    protected DisplayMode getSelectedMode() {
        DisplayModeWrapper wrapper=(DisplayModeWrapper)displayModes.getSelectedItem();
        DisplayMode dm=wrapper.dm;
        int width=dm.getWidth();
        int height=dm.getHeight();
        int bit=32;
        int refresh=DisplayMode.REFRESH_RATE_UNKNOWN;
        return new DisplayMode(width, height, bit, refresh);
    }

    public static void main(String[] args) {
        final DisplayModeExample app=new DisplayModeExample();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.createAndShowGUI();
            }
        });
    }
}
