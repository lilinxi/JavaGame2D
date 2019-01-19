package Javagames.filesandres;

import Javagames.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceLoaderExample {
    //使用文件加载辅助类的示例
    public ResourceLoaderExample() {}

    public void runTest() {
        Class<?> clazz = ResourceLoaderExample.class;
        System.out.println("************************************************************");
        //load absolute resource
        String filePath = "not/used";
        String resPath = "/Javagames/filesandres/Test1.txt";
        InputStream in = ResourceLoader.load(clazz, filePath, resPath);
        printResource(in);
        System.out.println("************************************************************");
        //load relative resource
        filePath = "not/used";
        resPath = "Test2.txt";
        in = ResourceLoader.load(clazz, filePath, resPath);
        printResource(in);
        System.out.println("************************************************************");
        //load relative file path
        filePath = "D:/i编程/IntelliJ IDEA Community Edition 2017.3.4/" +
                "workspace/MyUtils/src/Javagames/filesandres/Test3.txt";
        resPath = "/not/available";
        in = ResourceLoader.load(clazz, filePath, resPath);
        printResource(in);
        System.out.println("************************************************************");
        //error with both is null
        filePath = "fat/finger";
        resPath = "fat/finger/too";
        in = ResourceLoader.load(clazz, filePath, resPath);
        if (in == null) {
            System.out.println("Error: File Not Found");
        } else {
            printResource(in);
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
        new ResourceLoaderExample().runTest();
    }
}
