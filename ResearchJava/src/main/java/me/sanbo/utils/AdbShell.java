package me.sanbo.utils;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 暂不支持多个设备. 暂不检查环境变量. 即：默认环境ok
 * @Version: 2.0
 * @Create: 2022-08-13 23:45:35
 * @author: Administrator
 */
public class AdbShell {

    // linux
//    private static String adbPath = "/home/xyf/tools/sdk/platform-tools/adb";
    //windows
    private static String adbPath = "adb";

//    public static void main(String[] args) {
////        System.out.println(System.getenv("ANDROID_HOME"));
////        System.out.println(System.getProperty("ANDROID_HOME"));
////        String res = getStringUseAdb("cat /proc/cpuinfo");
//
//        List<String> res=getArray("find /sdcard/Android/data/ |xargs stat -c '%n %X %Y %Z'");
//        System.out.println(res.size());
//    }

    public static String adb(String subCmd) {
        InputStream is = null;
        try {
            Process proc = Runtime.getRuntime().exec(adbPath + " " + subCmd);
            is = proc.getInputStream();
            return DataConver.parserInputStreamToString(is);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            SafeClose.close(is);
        }
        return "";
    }


    public static String shell(String cmd) {
        return shell(cmd, null, null);
    }

    public static String shell(String cmd, String replaceBeforeStr, String replaceAfterStr) {
        String result = "";
        Process proc = null;
        BufferedInputStream in = null;
        BufferedReader br = null;
        InputStreamReader is = null;
        InputStream ii = null;
        StringBuilder sb = new StringBuilder();
        DataOutputStream os = null;
        OutputStream pos = null;
        try {
            // ADB path
            proc = Runtime.getRuntime().exec(adbPath + " shell");
            pos = proc.getOutputStream();
            os = new DataOutputStream(pos);

            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();

            ii = proc.getInputStream();
            in = new BufferedInputStream(ii);
            is = new InputStreamReader(in);
            br = new BufferedReader(is);
            String line = "", processedLine = "";
            while ((line = br.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    processedLine = processLine(line, replaceBeforeStr, replaceAfterStr);
                    sb.append(processedLine.trim()).append("\n");
                } else {
                    // 空值暂不处理
                }

            }
            if (sb.length() > 0) {
                return sb.substring(0, sb.length() - 1);
            }
            result = String.valueOf(sb);
            if (!TextUtils.isEmpty(result)) {
                result = result.trim();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            SafeClose.close(pos, ii, br, is, in, os);
        }

        return result;
    }

    public static void shell(List<String> cmds) {
        Process proc = null;
        DataOutputStream os = null;
        OutputStream pos = null;
        try {
            // ADB path
            proc = Runtime.getRuntime().exec(adbPath + " shell");
            pos = proc.getOutputStream();
            os = new DataOutputStream(pos);

            for (String cmd : cmds) {
                try{
                    os.write(cmd.getBytes());
                    os.writeBytes("\n");
                    os.flush();
                    os.writeBytes("exit\n");
                    os.flush();
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            SafeClose.close(pos, os);
        }

    }

    public static CopyOnWriteArrayList<String> getArray(String cmd) {
        return getArray(cmd, null, null);
    }

    public static CopyOnWriteArrayList<String> getArray(
            String cmd, String replaceBeforeStr, String replaceAfterStr) {
        Process proc = null;
        BufferedInputStream in = null;
        BufferedReader br = null;
        InputStreamReader is = null;
        InputStream ii = null;
        DataOutputStream os = null;
        OutputStream pos = null;
        CopyOnWriteArrayList<String> results = new CopyOnWriteArrayList<>();
        try {
            proc = Runtime.getRuntime().exec(adbPath + " shell");
            pos = proc.getOutputStream();
            os = new DataOutputStream(pos);

            // donnot use os.writeBytes(commmand), avoid chinese charset error
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.flush();
            // exitValue
            os.writeBytes("exit\n");
            os.flush();
            ii = proc.getInputStream();
            in = new BufferedInputStream(ii);
            is = new InputStreamReader(in);
            br = new BufferedReader(is);
            String line = "", processedLine = "";
            while ((line = br.readLine()) != null) {
                processedLine = "";
                if (!TextUtils.isEmpty(line)) {
                    processedLine = processLine(line, replaceBeforeStr, replaceAfterStr);
                    results.add(processedLine.trim());
                } else {
                    // 空值暂不处理
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            SafeClose.close(pos, ii, br, is, in, os);
        }

        return results;
    }

    private static String processLine(
            String line, String replaceBeforeStr, String replaceAfterStr) {

        // 被替换的值不能为空,不然替换个毛
        if (TextUtils.isEmpty(replaceBeforeStr)) {
            return line;
        } else {
            // 替换后的值
            return line.replaceAll(replaceBeforeStr, replaceAfterStr);

        }
    }

}
