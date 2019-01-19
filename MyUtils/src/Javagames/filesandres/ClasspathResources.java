package Javagames.filesandres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClasspathResources {
    //展示了多种不同的加载资源的方式
    //两种路径：
    //  已经进入src的路径
    //  从当前类所在路径开始的路径
    public ClasspathResources() {}

    public void runTest() {
        /**
         * ClassLoader.getSystemResourceAsStream()使用进入src之后的路径
         * ClassLoader uses absolute path. There is
         * No '/' at the beginning of the path!
         */
        System.out.println();
        System.out.println("******************************************");
        System.out.println("ClassLoader - Absolute Path");
        System.out.println();
        InputStream in = ClassLoader.getSystemResourceAsStream(
                "Javagames/filesandres/Test1.txt");
        printResource(in);
        /**
         * getClass().getResourceAsStream()使用当前class的路径
         * getClass() instead of class loader
         * Can be relative
         */
        System.out.println();
        System.out.println("******************************************");
        System.out.println("getClass() - Relative Path");
        System.out.println();
        in = getClass().getResourceAsStream("Test2.txt");
        printResource(in);
        /**
         * getClass().getResourceAsStream()也可以使用进入src的绝对路径，但是路径前需要加‘/'
         * getClass() can also use absolute path,
         * but it needs a '/' at the start of the path
         */
        System.out.println();
        System.out.println("******************************************");
        System.out.println("getClass() - Absolute Path");
        System.out.println();
        in = getClass().getResourceAsStream("/Javagames/filesandres/Test3.txt");
        printResource(in);
        /**
         * 使用ClassName.class()代替getClass(),因为getClass()总是返回最深的子类的路径
         * Because getClass() always returns the subclass,
         * if a subclass is created in another package,
         * the relative path may not be correct.
         * However, using an absolute path doesn't allow
         * packages to be moved around. Use the static class,
         * which also works in static methods.
         */
        System.out.println();
        System.out.println("******************************************");
        System.out.println("getClass() - Absolute Path");
        System.out.println();
        in = ClasspathResources.class.getResourceAsStream("Test3.txt");
        printResource(in);
        /*
        * Either ClassLoader or Class will return null
        * for unknown resources
         */
        in = getClass().getResourceAsStream("fat/finger/mistake");
        if (in == null) {
            System.out.println();
            System.out.println("******************************************");
            System.out.println("Got a null back!!!");
        }
        in = ClassLoader.getSystemResourceAsStream("fat/finger/mistake");
        if (in == null) {
            System.out.println("Got another null back");
        }
    }

    private void printResource(InputStream inputStream) {
        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader buf = new BufferedReader(reader);
            String line = null;
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        new ClasspathResources().runTest();
    }
}
