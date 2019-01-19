package Javagames.images;

import Javagames.util.SimpleFramework;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

public class ColorInterpolationExample extends SimpleFramework {
    //执行颜色插值
    //y = y0 + ( x - x0 )( y1 - y0 ) / ( x1 - x0 )
    private BufferedImage img;
    private int[] pixels;
    private int[] clear;
    private float tlr = 255.0f;
    private float trg = 255.0f;
    private float blb = 255.0f;

    public ColorInterpolationExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 10L;
        appTitle = "Color Interpolation Example";
    }

    @Override
    protected void initialize() {
        super.initialize();
        img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        //get pixels
        //此类扩展了 Raster 以提供像素写入功能
        //得到图像的原始像素值
        WritableRaster raster = img.getRaster();
        DataBuffer dataBuffer = raster.getDataBuffer();
        DataBufferInt data = (DataBufferInt) dataBuffer;
        pixels = data.getData();
        clear = new int[pixels.length];
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        createColorSquare();
    }

    private void createColorSquare() {
        int w = img.getWidth();
        float w0 = 0.0f;
        float w1 = w - 1.0f;
        int h = img.getHeight();
        float h0 = 0.0f;
        float h1 = h - 1.0f;
        /**
         * public static void arraycopy(Object src,
         *                              int srcPos,
         *                              Object dest,
         *                              int destPos,
         *                              int length)
         * 从指定源数组中复制一个数组，复制从指定的位置开始，到目标数组的指定位置结束。
         * 从 src 引用的源数组到 dest 引用的目标数组，数组组件的一个子序列被复制下来。
         * 被复制的组件的编号等于 length 参数。
         * 源数组中位置在 srcPos 到 srcPos+length-1 之间的组件被分别复制到目标数组中的 destPos 到 destPos+length-1 位置。
         * 如果参数 src 和 dest 引用相同的数组对象，
         * 则复制的执行过程就好像首先将 srcPos 到 srcPos+length-1 位置的组件复制到一个带有 length 组件的临时数组，
         * 然后再将此临时数组的内容复制到目标数组的 destPos 到 destPos+length-1 位置一样。
         *
         * 参数：
         * src - 源数组。
         * srcPos - 源数组中的起始位置。
         * dest - 目标数组。
         * destPos - 目标数据中的起始位置。
         * length - 要复制的数组元素的数量。
         */
        System.arraycopy(clear, 0, pixels, 0, pixels.length);
        //左上为红
        //右上为绿
        //左下为蓝
        //右下为黑
        //Top-Left
        //float tlr = 255.0f;
        tlr = tlr - 1.0f;
        if (tlr < 0) {
            tlr = 255.0f;
        }
        float tlg = 0.0f;
        float tlb = 0.0f;
        //Top-Right
        float trr = 0.0f;
        //float trg = 255.0f;
        trg = trg - 1.0f;
        if (trg < 0) {
            trg = 255.0f;
        }
        float trb = 0.0f;
        //Bottom-Left
        float blr = 0.0f;
        float blg = 0.0f;
        //float blb = 255.0f;
        blb = blb - 1.0f;
        if (blb < 0) {
            blb = 255.0f;
        }
        //Bottom-Right
        float brr = 0.0f;
        float brg = 0.0f;
        float brb = 0.0f;
        float h1h0 = h1 - h0;
        float w1w0 = w1 - w0;
        for(int row=0;row<h;row++) {
            //left pixels
            int lr = (int) (tlr + (row - h0) * (blr - tlr) / h1h0);
            int lg = (int) (tlg + (row - h0) * (blg - tlg) / h1h0);
            int lb = (int) (tlb + (row - h0) * (blb - tlb) / h1h0);
            //right pixels
            int rr = (int) (trr + (row - h0) * (brr - trr) / h1h0);
            int rg = (int) (trg + (row - h0) * (brg - trg) / h1h0);
            int rb = (int) (trb + (row - h0) * (brb - trb) / h1h0);
            for(int col=0;col<w;col++) {
                int r = (int) (lr + (col - w0) * (rr - lr) / w1w0);
                int g = (int) (lg + (col - w0) * (rg - lg) / w1w0);
                int b = (int) (lb + (col - w0) * (rb - lb) / w1w0);
                int index = row * w + col;
                pixels[index] = 0xFF << 24 | r << 16 | g << 8 | b;
            }
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        int xPos = (canvas.getWidth() - img.getWidth()) / 2;
        int yPos = (canvas.getHeight() - img.getHeight()) / 2;
        g.drawImage(img, xPos, yPos, null);
    }

    public static void main(String[] args) {
        launchApp(new ColorInterpolationExample());
    }
}
