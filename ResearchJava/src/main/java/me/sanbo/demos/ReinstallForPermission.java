package me.sanbo.demos;

import me.sanbo.utils.AdbShell;
import me.sanbo.utils.TextUtils;

import java.io.File;
import java.util.List;


/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 重新安装，通过所有权限, 通过adb install -r -g来同意所有权限
 * @Version: 1.0
 * @Create: 2022-09-28 13:48:35
 * @author: sanbo
 */
public class ReinstallForPermission {
    public static void main(String[] args) {

        try {
            processAll();
//            processOne("com.lxzq.mobile.sdaccount");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            new File("me.apk").deleteOnExit();
        }

    }

    private static void processAll() {
        List<String> pkgs = AdbShell.getArray("pm list package -3", "package:", "");
//        List<String> pkgs = FileUtils.readForArray("pkg.txt");
        System.out.println(pkgs.size());

        for (int i = 0; i < pkgs.size(); i++) {
            String pkg = pkgs.get(i);
            if (TextUtils.isEmpty(pkg)) {
                continue;
            }
            boolean isSuccess = processOne(pkg);
            if (isSuccess) {
                System.out.println("进度: " + i + "/" + pkgs.size() + " 【" + pkg + "】");
            } else {
                System.err.println("进度: " + i + "/" + pkgs.size() + " 【" + pkg + "】");
            }

        }
    }

    private static boolean processOne(String pkg) {
        String path = AdbShell.shell("pm path " + pkg);
        if (TextUtils.isEmpty(path)) {
            System.err.println("(" + pkg + ") path is null!");
            return false;
        }
        if (path.startsWith("package:")) {
            path = path.replaceAll("package:", "");
        }

        String result = AdbShell.adb(" pull " + path + " me.apk");
        if (TextUtils.isEmpty(result) || !result.contains("pulled")) {
            System.err.println("pull(" + pkg + ") 失败, error shell command:\r\n\t" + result);
            return false;
        }
        AdbShell.adb(" install -g -r me.apk");
        return true;
    }
}
