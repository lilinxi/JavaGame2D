package Javagames.filesandres;

import java.io.*;

public class ReadingDataFromFiles {
    //从文件中读取数据
    //按字节读取和按字符串读取
    //使用FileInputStream.read()读取下一个字节
    //创建FileReader对象，再利用BufferedReader的readLine()方法一次读取一行数据
    public void runTest() {
        //从src开始的路径！！！
        String path = "./res/assets/text/lorem-ipsum.txt";
        File file = new File(path);
        readInByte(file);
        readInStrings(file);
    }

    public void readInByte(File file) {
        System.out.println();
        System.out.println();
        System.out.println("*****************************************");
        System.out.println("Reading in bytes");
        System.out.println();
        InputStream in = null;
        //使用FileInputStream.read()读取下一个字节
        //
        //public abstract int read()
        //                  throws IOException
        //从输入流中读取数据的下一个字节。返回 0 到 255 范围内的 int 字节值。
        // 如果因为已经到达流末尾而没有可用的字节，则返回值 -1。
        // 在输入数据可用、检测到流末尾或者抛出异常前，此方法一直阻塞。
        try {
            in = new FileInputStream(file);
            int next = -1;
            while ((next = in.read()) != -1) {
                System.out.print((char) next);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException ex) {}
        }
    }

    public void readInStrings(File file) {
        System.out.println();
        System.out.println();
        System.out.println("*****************************************");
        System.out.println("Reading in strings");
        System.out.println();
        Reader reader = null;
        //创建FileReader对象，再利用BufferedReader的readLine()方法一次读取一行数据
        try {
            reader = new FileReader(file);
            BufferedReader buf = new BufferedReader(reader);
            String line = null;
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException ex) {}
        }
    }

    public static void main(String[] args) {
        new ReadingDataFromFiles().runTest();
    }
}
