package me.sanbo.demos;

import me.sanbo.utils.AdbShell;
import me.sanbo.utils.FileUtils;
import me.sanbo.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 通过shell方式同意应用的所有权限, 通过adb命令来同意所有权限
 * 查看权限: adb shell dumpsys package com.dragon.read
 * 赋予权限: adb shell pm grant com.eg.android.AlipayGphone android.permission.READ_EXTERNAL_STORAGE
 * 撤销权限: adb shell pm revoke com.eg.android.AlipayGphone android.permission.READ_EXTERNAL_STORAGE
 * @Version: 1.0
 * @Create: 2022-09-28 13:48:35
 * @author: sanbo
 */
public class PmgrantPermissions {

    // 所有权限，可暴力授权
    private static List<String> allAppPermissions = new ArrayList<String>();
    // 单设备的所有权限授予脚本，shell速度比java单进程执行shell快
    private static List<String> all_cmds = new ArrayList<String>();

    public static void main(String[] args) {

        try {
            allAppPermissions = FileUtils.readForArray("allAppPermissions.txt");

            processAll();
//            parserOne("com.ks.kaishustory");
//            parserOne("com.xs.fm");

            if (allAppPermissions.size() > 0) {
                for (String p : allAppPermissions) {
                    if (TextUtils.isEmpty(p)) {
                        continue;
                    }
                    FileUtils.saveTextToFile("allAppPermissions.txt", p, true);
                }
            }

            // save to file
            if (all_cmds.size() > 0) {
                // init file
                FileUtils.delete("all_cmds.sh");
                for (String cmd : all_cmds) {
                    FileUtils.saveTextToFile("all_cmds.sh", "adb shell "+cmd, true);
                }
// one by one work
//                for (String cmd : all_cmds) {
//                    AdbShell.shell(cmd);
//                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void processAll() {
        List<String> pkgs = AdbShell.getArray("pm list package", "package:", "");
//        List<String> pkgs = FileUtils.readForArray("pkg.txt");
        System.out.println(pkgs.size());

        for (int i = 0; i < pkgs.size(); i++) {
            String pkg = pkgs.get(i);
            boolean isSuccess = parserOne(pkg);
            if (isSuccess) {
                System.out.println("进度: " + i + "/" + pkgs.size() + " 【" + pkg + "】");
            } else {
                System.err.println("进度: " + i + "/" + pkgs.size() + " 【" + pkg + "】");
            }
        }
    }


    private static boolean parserOne(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        // setup 1: get all permissions
        List<String> lines = AdbShell.getArray("dumpsys package " + pkg);
        List<String> permissions = new ArrayList<String>();
        boolean isPermissioning = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            if (line.endsWith("permissions:")) {
                isPermissioning = true;
//                System.out.println("get permissions: " + line);
                continue;
            }
            if (line.startsWith("User ") || line.startsWith("disabledComponents:") || line.startsWith("Queries:")
                    || line.startsWith("Hidden:") || line.contains("[") || line.contains("]") || line.contains("|")
                    || !line.contains(".") /* not has .*/
            ) {
                isPermissioning = false;
//                System.out.println("will out of permissions: " + line);
                continue;
            }

//            System.out.println("----->"+isPermissioning);
            if (isPermissioning) {
                if (line.startsWith("applicationInfo")
                        || line.startsWith("legacyNativeLibraryDir=")
                        || line.startsWith("versionName=")
                        || line.startsWith("versionCode=")
                        || line.startsWith("supportsScreens=")
                        || line.startsWith("dataDir=")
                        || line.startsWith("queriesIntents=")
                        || line.startsWith("privateFlags=")
                        || line.startsWith("flags=")
                        || line.startsWith("pkgFlags=")
                        || line.startsWith("signatures=")
                ) {
                    System.err.println(pkg + "----" + line);

                }
                if (line.contains(":")) {
                    String p1 = line.split(":")[0];
//                    System.out.println("p1----->" + p1);
                    permissions.add(p1);
                    if (!allAppPermissions.contains(p1)) {
                        allAppPermissions.add(p1);
                    }
                } else {
//                    System.out.println("p2----->" + line);
                    permissions.add(line);
                    if (!allAppPermissions.contains(line)) {
                        allAppPermissions.add(line);
                    }
                }
            }
        }
//        System.out.println("【" + pkg + "】获取权限完毕, 权限个数:" + permissions.size());
        if (permissions.size() < 1) {
            //System.err.println("\t\t【" + pkg + "】未获取到权限，即将退出!");
            return false;
        }

        for (String p : permissions) {
            all_cmds.add("pm grant " + pkg + " " + p);
        }

//        //not one by one
//        for (String p : permissions) {
//            AdbShell.shell("pm grant " + pkg + " " + p);
//        }

//        // some permission has failed
//        List<String> cmds = new ArrayList<String>();
//        for (String p : permissions) {
//            cmds.add("pm grant " + pkg + " " + p);
//        }
//        System.out.println(cmds.toString());
//        AdbShell.shell(cmds);
        return true;
    }

}
