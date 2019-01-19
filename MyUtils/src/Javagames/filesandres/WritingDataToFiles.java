package Javagames.filesandres;

import java.io.*;
import java.util.Random;

public class WritingDataToFiles {
    //写入数据到文件
    //写入字节和字符串
    //使用FileOutputStream.write()来写入字节
    //使用PrintWriter.println(String)写入字符串
    private Random rand = new Random();

    public void runTest() {
        //从src开始的路径！！！
        writeOutBytes("./res/assets/text/byte-file.bin");
        writeOutStrings("./res/assets/text/string-file.txt");
    }

    public void writeOutBytes(String fileName) {
        System.out.println();
        System.out.println();
        System.out.println("*****************************************");
        File file = new File(fileName);
        OutputStream out = null;
        //使用FileOutputStream.write()来写入字节
        try {
            out = new FileOutputStream(file);
            for (int i = 0; i < 1000; i++) {
                int tmp;
                out.write(tmp = rand.nextInt(256));
                System.out.print((char) tmp + " ");
                if ((i + 1) % 10 == 0) {
                    System.out.println();
                }
            }
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }finally {
            try {
                out.close();
                System.out.println("Wrote: " + file.getPath());
            } catch (Exception ex) {}
        }
    }

    public void writeOutStrings(String fileName) {
        System.out.println();
        System.out.println();
        System.out.println("*****************************************");
        String[] strings = {
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit,",
                "sed do eiusmod tempor incididunt ut labore et dolore magna",
                "aliqua. Ut enim ad minim veniam, quis nostrud exercitation",
                "ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                "Duis aute irure dolor in reprehenderit in voluptate velit",
                "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint",
                "occaecat cupidatat non proident, sunt in culpa qui officia",
                "deserunt mollit anim id est laborum.",
        };
        File file = new File(fileName);
        //使用PrintWriter.println(String)写入字符串
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
            for (String string : strings) {
                out.println(string);
            }
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }finally {
            try {
                out.close();
                System.out.println("Wrote: " + file.getPath());
            } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        new WritingDataToFiles().runTest();
    }
}
