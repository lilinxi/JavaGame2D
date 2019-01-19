package Javagames.tools;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExampleFileFilter extends FileFilter {
    /**
     * 在保存和加载文件对话框中，该类都将用来过滤文件。
     * 扩展了Swing的FileFilter，添加了一个说明，还有一个方法用来根据文件扩展名过滤掉不想要的文件。
     * 通过传入一个说明以及允许的扩展名的列表，使只有允许的文件可见。
     */
    private String description;
    private String[] filters;

    public ExampleFileFilter(String description, String[] filters) {
        this.description = description;
        this.filters = filters;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f);
        if (extension != null) {
            for (String filter : filters) {
                if (extension.equalsIgnoreCase(filter)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
