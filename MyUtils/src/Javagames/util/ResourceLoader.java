package Javagames.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceLoader {
    //加载文件辅助类，
    public static InputStream load(
            Class<?> clazz, String filePath, String resPath) {
        //从src开始的路径，
        //clazz开始的路径或进入src开始的路径
        //try the resource first
        InputStream in = null;
        if (!(resPath == null || resPath.isEmpty())) {
            in = clazz.getResourceAsStream(resPath);
        }
        if (in == null) {
            //try the file path
            try {
                in = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return in;
    }
}
