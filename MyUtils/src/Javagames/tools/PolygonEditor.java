package Javagames.tools;

import Javagames.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class PolygonEditor extends SwingFramework {
    /**
     * PolygonEditor扩展了SwingFramework。
     * 除了设置常用的应用程序属性，它还创建了两个过滤器：一个用于*.png文件，一个用于*.xml文件。
     * onCreateAndShowGUI（）方法是SwingFramework添加的新的覆盖。
     * 它允许向编辑器添加其他的GUI组件。
     * 通过调用getMainPanel（），可以添加其他的组件。
     * 这个方法为编辑器创建并挂起了很多控件。
     * 可以创建一个File菜单，它带有Exit菜单项，该项会关闭应用程序。
     * 还创建了一个Help菜单，它启动了一个About对话框。
     * 它还创建了一个工具栏，带有用于管理XML文件的按钮。
     * 在底部创建了一个面板，用来导入和导出一个PNG文件。
     * ++和--按钮控制边界矩形，定义了所加载的PNG文件将要在编辑器中占用的区域。
     * 宽度和高度文本字段确定了导出的文件的大小。
     */
    private static final float BOUNDS = 0.80f;
    private static final float DELTA = BOUNDS / 32.0f;
    private File currentDirectory;
    private ExampleFileFilter imageFilter;
    private ExampleFileFilter xmlFilter;
    private JTextField widthControl;
    private JTextField heightControl;
    private float bounds = BOUNDS / 2.0f;
    private ArrayList<Vector2f> polygon;
    private Vector2f mousePos;
    private boolean closed;
    private BufferedImage sprite;
    private BufferedImage scaled;

    public PolygonEditor() {
        appBorder = new Color(0xFFEBCD);
        appBackground = Color.BLACK;
        appFont = new Font("Courier New", Font.PLAIN, 14);
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appMaintainRatio = true;
        appBorderScale = 0.95f;
        appTitle = "Polygon Editor";
        appWorldWidth = BOUNDS;
        appWorldHeight = BOUNDS;
        imageFilter = new ExampleFileFilter("Image File", new String[]{"png"});
        xmlFilter = new ExampleFileFilter("Model File", new String[]{"xml"});
        currentDirectory = new File(".");
    }

    @Override
    protected void onCreateAndShowGUI() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                /**
                 * public final void dispatchEvent(AWTEvent e)为组件或其子组件之一指派事件。
                 */
                PolygonEditor.this.dispatchEvent(new WindowEvent(
                        PolygonEditor.this, WindowEvent.WINDOW_CLOSING
                ));
            }
        });
        menu.add(item);
        menuBar.add(menu);
        menu = new JMenu("Help");
        item = new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                /**
                 * public static void showMessageDialog(Component parentComponent,
                 * Object message,
                 * String title,
                 * int messageType)
                 * throws HeadlessException调出对话框，它显示使用由 messageType 参数确定的默认图标的 message。
                 *
                 * 参数：
                 * parentComponent - 确定在其中显示对话框的 Frame；如果为 null 或者 parentComponent 不具有 Frame，则使用默认的 Frame
                 * message - 要显示的 Object
                 * title - 对话框的标题字符串
                 * messageType - 要显示的消息类型：ERROR_MESSAGE、INFORMATION_MESSAGE、WARNING_MESSAGE、QUESTION_MESSAGE 或 PLAIN_MESSAGE
                 *
                 * public static final int INFORMATION_MESSAGE用于信息消息
                 */
                JOptionPane.showMessageDialog(
                        PolygonEditor.this, "About this app!!!",
                        "About", JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        menu.add(item);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        //Lets make a toolbar...
        /**
         * 示例的工具栏按钮，它用于创建一个新的文件。注意，UIManager.getIcon（）方法调用获取了一个特定于平台的图标。
         */
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        JButton b = new JButton(UIManager.getIcon("FileChooser.fileIcon"));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNew();
            }
        });
        bar.add(b);
        b = new JButton(UIManager.getIcon("FileChooser.directoryIcon"));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadXML();
            }
        });
        bar.add(b);
        b = new JButton(UIManager.getIcon("FileChooser.floppyDriveIcon"));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveXML();
            }
        });
        bar.add(b);
        getMainPanel().add(bar, BorderLayout.NORTH);
        JPanel p = new JPanel();
        JButton increase = new JButton("++");
        increase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increaseBounds();
            }
        });
        p.add(increase);
        JButton decrease = new JButton("--");
        decrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decreaseBounds();
            }
        });
        p.add(decrease);
        p.add(new JLabel("Width"));
        widthControl = new JTextField(3);
        /**
         * public void setHorizontalAlignment(int alignment)设置文本的水平对齐方式。有效值包括：
         * JTextField.LEFT
         * JTextField.CENTER
         * JTextField.RIGHT
         * JTextField.LEADING
         * JTextField.TRAILING
         * 当设置对齐方式时，调用 invalidate 和 repaint，并且激发 PropertyChange 事件（"horizontalAlignment"）。
         */
        widthControl.setHorizontalAlignment(JTextField.CENTER);
        widthControl.setText("256");
        p.add(widthControl);
        p.add(new JLabel("Height"));
        heightControl = new JTextField(3);
        heightControl.setHorizontalAlignment(JTextField.CENTER);
        heightControl.setText("256");
        p.add(heightControl);
        JButton button = new JButton("Export");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportImage();
            }
        });
        p.add(button);
        button = new JButton("Import");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importImage();
            }
        });
        p.add(button);
        getMainPanel().add(p, BorderLayout.SOUTH);
    }

    protected void onNew() {
        polygon.clear();
        sprite = null;
        scaled = null;
    }

    /**
     * onLoadXMl（）方法是使用SwingFileChooser管理，保存和加载文件的一个示例。
     */
    protected void onLoadXML() {
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setFileFilter(xmlFilter);
        /**
         * public int showOpenDialog(Component parent)
         * throws HeadlessException弹出一个 "Open File" 文件选择器对话框。注意，approve 按钮上显示的文本由 L&F 决定。
         *
         * 参数：
         * parent - 该对话框的父组件，可以为 null；详情请参阅 showDialog
         * 返回：
         * 该文件选择器被弹下时的返回状态：
         * JFileChooser.CANCEL_OPTION
         * JFileChooser.APPROVE_OPTION
         * JFileChooser.ERROR_OPTION 如果发生错误或者该对话框已被解除
         */
        int retVal = chooser.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            currentDirectory = file;
            parseModel(file);
        }
    }

    private void parseModel(File file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            Document root = XMLUtility.parseDocument(reader);
            /**
             * Element getDocumentElement()这是一种便捷属性，该属性允许直接访问文档的文档元素的子节点。
             */
            parseModel(root.getDocumentElement());
            closed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * 读取XML文档的每一个节点
     * 读取bounds数据和coord数据
     */
    private void parseModel(Element model) {
        bounds = Float.parseFloat(model.getAttribute("bounds"));
        polygon.clear();
        for (Element coord : XMLUtility.getAllElements(model, "coord")) {
            float x = Float.parseFloat(coord.getAttribute("x"));
            float y = Float.parseFloat(coord.getAttribute("y"));
            polygon.add(new Vector2f(x, y));
        }
    }

    protected void onSaveXML() {
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setFileFilter(xmlFilter);
        int retVal = chooser.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            currentDirectory = file;
            if (file.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(
                        this, "Overwrite existing file?"
                );
                if (overwrite == JOptionPane.YES_OPTION) {
                    writeXML(file);
                }
            } else {
                writeXML(file);
            }
        }
    }

    private void writeXML(File file) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
            writeXML(out);
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (Exception e) {}
        }
    }

    private void writeXML(PrintWriter out) {
        out.println("<model bounds=\"" + bounds + "\">");
        for (Vector2f point : polygon) {
            out.println(
                    "\t<coord x=\"" + point.x + "\"y=\"" + point.y + "\">"
            );
        }
    }

    protected void increaseBounds() {
        bounds += DELTA;
        if (bounds > BOUNDS) {
            bounds = BOUNDS;
        }
    }

    protected void decreaseBounds() {
        bounds -= DELTA;
        if (bounds < DELTA) {
            bounds = DELTA;
        }
    }

    /**
     * 图像是按照像素宽度和高度来保存的，但是在编辑器中是按照世界单位来显示的，一个导入图像需要缩放到适合于边界之中。
     */
    protected void exportImage() {
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setFileFilter(imageFilter);
        int retVal = chooser.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            currentDirectory = file;
            if (file.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(
                        this, "Overwrite existing file?"
                );
                if (overwrite == JOptionPane.YES_OPTION) {
                    exportImage(file);
                }
            } else {
                exportImage(file);
            }
        }
    }

    private void exportImage(File file) {
        int imageWidth = Integer.parseInt(widthControl.getText());
        int imageHeight = Integer.parseInt(heightControl.getText());
        BufferedImage image = new BufferedImage(
                imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB
        );
        Matrix3x3f view = Utility.createViewport(bounds, bounds, imageWidth, imageHeight);
        Graphics2D g2d = image.createGraphics();
        drawPolygon(g2d, view);
        g2d.dispose();
        try {
            System.out.println(
                    "Export: " + ImageIO.write(image, "png", file)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void importImage() {
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setFileFilter(imageFilter);
        int retVal = chooser.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            currentDirectory = file;
            try {
                sprite = ImageIO.read(file);
                scaled = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        polygon = new ArrayList<Vector2f>();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        //images.FlyingSpritesExample中确定的最佳渲染方案
        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        drawSprite(g);
        drawAxisLine(g);
        drawPolygon(g, getViewportTransform());
        drawBoundingBox(g);
    }

    private void drawSprite(Graphics g) {
        if (sprite == null) {
            return;
        }
        Vector2f topLeft = new Vector2f(-bounds / 2.0f, bounds / 2.0f);
        Vector2f bottomRight = new Vector2f(bounds / 2.0f, -bounds / 2.0f);
        Matrix3x3f view = getViewportTransform();
        topLeft = view.mul(topLeft);
        bottomRight = view.mul(bottomRight);
        int width = (int) Math.abs(topLeft.x - bottomRight.x);
        int height = (int) Math.abs(topLeft.y - bottomRight.y);
        if (scaled == null || scaled.getWidth() != width || scaled.getHeight() != height) {
            scaled = Utility.scaleImage(sprite, width, height);
        }
        g.drawImage(scaled, (int) topLeft.x, (int) topLeft.y, null);
    }

    private void drawAxisLine(Graphics g) {
        Matrix3x3f view = getViewportTransform();
        Vector2f right = new Vector2f(appWorldWidth / 2.0f, 0.0f);
        Vector2f left = new Vector2f(-right.x, 0.0f);
        Utility.drawPolygon(g, new Vector2f[]{left, right}, view, Color.WHITE);
        Vector2f top = new Vector2f(0.0f, appWorldHeight / 2.0f);
        Vector2f bottom = new Vector2f(0.0f, -top.y);
        Utility.drawPolygon(g, new Vector2f[]{top, bottom}, view, Color.WHITE);
    }

    private void drawPolygon(Graphics g, Matrix3x3f view) {
        if (closed && polygon.size() > 0) {
            Utility.drawPolygon(g, polygon, view, Color.GREEN);
            return;
        }
        for (int i = 0; i < polygon.size() - 1; i++) {
            Utility.drawPolygon(g, new Vector2f[]{polygon.get(i), polygon.get(i + 1)}, view, Color.GREEN);
        }
        if (!(polygon.isEmpty() || closed)) {
            Vector2f P = polygon.get(polygon.size() - 1);
            Vector2f S = mousePos;
            Utility.drawPolygon(g, new Vector2f[]{P, S}, view, Color.GREEN);
        }
    }

    private void drawBoundingBox(Graphics g) {
        Vector2f[] bb = {
                new Vector2f(-bounds / 2.0f, bounds / 2.0f),
                new Vector2f(bounds / 2.0f, bounds / 2.0f),
                new Vector2f(bounds / 2.0f, -bounds / 2.0f),
                new Vector2f(-bounds / 2.0f, -bounds / 2.0f),
        };
        Utility.drawPolygon(g, bb, getViewportTransform(), Color.WHITE);
    }

    public static void main(String[] args) {
        launchApp(new PolygonEditor());
    }
}
