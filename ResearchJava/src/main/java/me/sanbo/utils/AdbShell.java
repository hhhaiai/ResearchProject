package me.sanbo.utils;

import me.sanbo.utils.callback.ICallback;

import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 暂不支持多个设备. 暂不检查环境变量. 即：默认环境ok
 * @Version: 2.0
 * @Create: 2022-08-13 23:45:35
 * @author: Administrator
 */
public class AdbShell {

//    public static void main(String[] args) {
//        String res = getStringUseAdb("cat /proc/cpuinfo");
//        System.out.println(res);
//    }

    public static String getStringUseAdb(String cmd) {
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
            proc = Runtime.getRuntime().exec("adb shell");
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
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (sb.length() > 0) {
                return sb.substring(0, sb.length() - 1);
            }
            result = String.valueOf(sb);
            if (!TextUtils.isEmpty(result)) {
                result = result.trim();
            }
        } catch (Throwable e) {
        } finally {
            SafeClose.close(pos, ii, br, is, in, os);
        }

        return result;
    }

    public static CopyOnWriteArrayList<String> getArrayUseAdb(String cmd) {
        return getArrayUseAdb(cmd, null, null);
    }

    public static CopyOnWriteArrayList<String> getArrayUseAdb(
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
            proc = Runtime.getRuntime().exec("adb shell");
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
            // 替换和被替换的值不能同时为空,不然替换个毛线
            if (TextUtils.isEmpty(replaceAfterStr)) {
                return line;
            } else {
                // 替换后的值
                return line.replace(replaceBeforeStr, replaceAfterStr);
            }
        }
    }

}
