package me.sanbo.utils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright © 2020 sanbo Inc. All rights reserved.
 * Description: 文件读写类
 * Version: 1.0
 * Create: 2020-12-16 14:05:44
 * Author: sanbo
 */
public class FileUtils {

    // public static void main(String[] args) {
    // System.out.println(FileUtils.getDestopFilePath("result.txt"));
    // }

    public static String getDestopFilePath(String fileName) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File home = fsv.getHomeDirectory();
        if (home.getAbsolutePath().endsWith("Desktop")) {
            return new File(home, fileName).getAbsolutePath();
        } else {
            File desktop = new File(home, "Desktop");
            return new File(desktop, fileName).getAbsolutePath();
        }
    }

    /**
     * 读取文件内容,将文件内容读取成直接字节数组
     *
     * @param fileFullPathWithName 文件的全路径名称
     * @param fileFullPathWithName 文件的全路径名称
     * @return public static byte[] readForBytes(String fileFullPathWithName) {
     *         RandomAccessFile raf = null;
     *         try {
     *         // 传输文件内容
     *         byte[] buffer = new byte[1024 * 1002]; // 3的倍数
     *         raf = new RandomAccessFile(fileFullPathWithName, "r");
     *         long size = raf.read(buffer);
     *         while (size > -1) {
     *         if (size == buffer.length) {
     *         return Base64.getEncoder().encode(buffer);
     *         } else {
     *         byte tmp[] = new byte[(int) size];
     *         System.arraycopy(buffer, 0, tmp, 0, (int) size);
     *         return Base64.getEncoder().encode(tmp);
     *         }
     *         }
     *         } catch (Throwable e) {
     *         e.printStackTrace();
     *         } finally {
     *         close(raf);
     *         }
     *         <p>
     *         return new byte[]{};
     *         }
     *         <p>
     *         <p>
     *         <p>
     *         <p>
     *         <p>
     *         /**
     *         读取文件内容,将文件内容读取成字符串
     * @return 文件内容
     */
    public static String readContent(String fileFullPathWithName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileFullPathWithName);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            return new String(buffer, "UTF-8");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(fis);
        }
        return "";
    }

    /**
     * 读取文件内容,将文件内容按行分割以列表形式返回
     *
     * @param fileFullPathWithName 文件的全路径名称
     * @return
     */
    public static List<String> readForArray(String fileFullPathWithName) {
        FileInputStream fis = null;
        List<String> lists = new ArrayList<String>();
        try {
            if (TextUtils.isEmpty(fileFullPathWithName)) {
                return lists;
            }
            File file = new File(fileFullPathWithName);
            if (!isOk(file)) {
                return lists;
            }
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            lists = new ArrayList<String>(Arrays.asList(new String(buffer, "UTF-8").split("\n")));
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(fis);
        }
        return lists;
    }

    private static void close(Closeable fis) {
        try {
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void delete(String fn ){
        if (TextUtils.isEmpty(fn)){
            return;
        }
        delete(new File(fn));
    }

    private static void delete(File file) {
        if (file.exists()){
            file.deleteOnExit();
        }
    }

    /**
     * 保存文件到指定文件
     *
     * @param fileFullPathWithName 待保存的文件。如不存在，则会新建
     * @param saveContent          待保存的内容
     * @param append               是否追加保存
     */
    public static void saveTextToFile(
            final String fileFullPathWithName, final String saveContent, boolean append) {
        saveTextToFile(new File(fileFullPathWithName), saveContent, append);
    }

    public static void saveTextToFile(final File file, final String saveContent, boolean append) {
        FileWriter fileWriter = null;
        try {
            if (!isOk(file)) {
                return;
            }
            fileWriter = new FileWriter(file, append);
            fileWriter.write(saveContent);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(fileWriter);
        }
    }

    public static boolean isOk(File file) {
        try {
            if (file == null) {
                return false;
            }
            File par = file.getParentFile();
            if (par != null) {
                if (!par.exists()) {
                    par.mkdirs();
                }
            }

            if (!file.exists()) {
                file.createNewFile();
                file.setExecutable(true);
                file.setReadable(true);
                file.setWritable(true);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

}
