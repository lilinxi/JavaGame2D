package Javagames.images;

import Javagames.util.SimpleFramework;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageCreatorExample extends SimpleFramework {
    //使用BufferedImage创建图像
    //使用ImageIO.write()将图像写入硬盘
    //使用ImageIO.read()从硬盘中读取图像
    private static final int IMG_WIDTH = 256;
    private static final int IMG_HEIGHT = 256;
    private static final int SQUARES = 8;
    private Random rand = new Random();
    private BufferedImage sprite;
    private String loadedFile;

    public ImageCreatorExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 1L;
        appTitle = "Image Creator Example";
        appBackground = Color.DARK_GRAY;
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            createFile("jpg", "image-creator.jpg");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            createFile("bmp", "image-creator.bmp");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            createFile("gif", "image-creator.gif");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_4)) {
            createFile("png", "image-creator.png");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_5)) {
            loadFile("image-creator.jpg");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_6)) {
            loadFile("image-creator.bmp");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_7)) {
            loadFile("image-creator.gif");
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_8)) {
            loadFile("image-creator.png");
        }
    }

    private void createFile(String type, String fileName) {
        try {
            sprite = createCustomImage();
            File file = new File(fileName);
            /**
             * public static boolean write(RenderedImage im,
             *                              String formatName,
             *                              File output)throws IOException
             * 使用支持给定格式的任意 ImageWriter 将一个图像写入 File。如果已经有一个 File 存在，则丢弃其内容。
             */
            if (!ImageIO.write(sprite, type, file)) {
                throw new IOException("No '" + type + "' image writer found");
            }
            loadedFile = "SAVED: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }
    }

    private void loadFile(String fileName) {
        try {
            sprite = ImageIO.read(new File(fileName));
            loadedFile = "LOADED: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }
    }

    private BufferedImage createCustomImage() {
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        int dx = image.getWidth() / SQUARES;
        int dy = image.getHeight() / SQUARES;
        for(int i=0;i<SQUARES;i++) {
            for(int j=0;j<SQUARES;j++) {
                g2d.setColor(new Color(rand.nextInt()));
                g2d.fillRect(i * dx, j * dy, dx, dy);
            }
        }
        g2d.setColor(Color.GREEN);
        g2d.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
        g2d.dispose();
        return image;
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("(1) Save JPG", 30, 45);
        g.drawString("(2) Save BMP", 30, 60);
        g.drawString("(3) Save GIF", 30, 75);
        g.drawString("(4) Save PNG", 30, 90);
        g.drawString("(5) Load JPG", 30, 105);
        g.drawString("(6) Load BMP", 30, 120);
        g.drawString("(7) Load GIF", 30, 135);
        g.drawString("(8) Load PNG", 30, 150);
        if (sprite != null) {
            int x = (canvas.getWidth() - sprite.getWidth()) / 2;
            int y = (canvas.getHeight() - sprite.getHeight()) / 2;
            g.drawImage(sprite, x, y, null);
            g.drawString(loadedFile, x, y + sprite.getHeight() + 15);
        }else {
            g.drawString("ERROR - File Not Found!",
                    canvas.getWidth() / 3, canvas.getHeight() / 3);
        }
    }

    public static void main(String[] args) {
        launchApp(new ImageCreatorExample());
    }
}
