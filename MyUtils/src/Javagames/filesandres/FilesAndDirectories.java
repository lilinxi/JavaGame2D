package Javagames.filesandres;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilesAndDirectories {
    //遍历一个文件树
    public FilesAndDirectories() {}

    public void runTest() {
        //list files and folders
        String dir = "D:\\i编程\\IntelliJ IDEA Community Edition 2017.3.4\\workspace\\MyUtils";
        File file = new File(dir);
        //从第0层，即根目录开始递归遍历
        displayInfo(0, file);
    }

    private void displayInfo(int depth, File file) {
        //Name,Date,Size,Attr
        boolean executable = file.canExecute();
        boolean readable = file.canRead();
        boolean writable = file.canWrite();
        boolean hidden = file.isHidden();
        boolean directory = file.isDirectory();
        long lastModified = file.lastModified();
        long length = file.length();
        String name = file.getName();
        //create ASCII file structure
        StringBuilder buf = new StringBuilder();
        for(int i=0;i<depth;i++) {
            buf.append("|\t\t");
        }
        if (directory) {
            buf.append("+");
        }
        if (name.isEmpty()) {
            buf.append(".");
        } else {
            buf.append(name);
        }
        //add modification date
        buf.append("\t\t");
        Date date = new Date(lastModified);
        buf.append(new SimpleDateFormat().format(date));
        buf.append("\t\t");
        //add file size in kilobytes
        long kb = length / 1024;
        //public class DecimalFormat extends NumberFormat
        // DecimalFormat 是 NumberFormat 的一个具体子类，用于格式化十进制数字。
        DecimalFormat format = new DecimalFormat();
        //public boolean isGroupingUsed()如果此格式中使用了分组，则返回 true。
        // 例如，在 English 语言环境中，如果使用了分组，则数 1234567 将被格式化为 "1,234,567"。
        // 组分隔符以及每个组的大小是与语言环境相关的，由 NumberFormat 的子类确定。
        format.setGroupingUsed(true);
        //格式化数字为字符串
        buf.append(format.format(kb));
        buf.append("KB");
        //add read,write,execute attribute flags
        buf.append("\t\t");
        if (hidden) {
            buf.append(".");
        }
        if (readable) {
            buf.append("R");
        }
        if (writable) {
            buf.append("W");
        }
        if (executable) {
            buf.append("X");
        }
        //print everything to the command line
        System.out.println(buf.toString());
        //public File[] listFiles()返回一个抽象路径名数组，这些路径名表示此抽象路径名表示的目录中的文件。
        //如果此抽象路径名不表示一个目录，那么此方法将返回 null。
        // 否则返回一个 File 对象数组，每个数组元素对应目录中的每个文件或目录。
        // 表示目录本身及其父目录的名称不包括在结果中。
        // 得到的每个抽象路径名都是根据此抽象路径名，使用 File(File, String) 构造方法构造的。
        // 所以，如果此路径名是绝对路径名，那么得到的每个路径名都是绝对路径名；
        // 如果此路径名是相对路径名，那么得到的每个路径名都是相对于同一目录的路径名。
        //
        //不保证所得数组中的相同字符串将以特定顺序出现，特别是不保证它们按字母顺序出现。
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                displayInfo(depth + 1, child);
            }
        }
    }

    public static void main(String[] args) {
        new FilesAndDirectories().runTest();
    }
}
