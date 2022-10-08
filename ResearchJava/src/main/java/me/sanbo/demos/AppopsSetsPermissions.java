package me.sanbo.demos;

import me.sanbo.utils.AdbShell;
import me.sanbo.utils.TextUtils;

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
public class AppopsSetsPermissions {


    public static void main(String[] args) {

        try {

//            processAll();
            parserOne("com.taobao.taobao");


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void processAll() {
        List<String> pkgs = AdbShell.getArray("pm list package", "package:", "");
//        List<String> pkgs = FileUtils.readForArray("pkg.txt");
        System.out.println("pkgs size:" + pkgs.size());

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


    /**
     * └ ─ $adb shell appops get com.taobao.taobao
     * Uid mode: LEGACY_STORAGE: allow
     * COARSE_LOCATION: allow
     * FINE_LOCATION: allow; time=+7d23h29m35s438ms ago
     * GPS: allow; time=+7d23h31m59s63ms ago; duration=+451ms
     * WIFI_SCAN: allow; time=+7d23h29m35s438ms ago
     * WRITE_SETTINGS: default; rejectTime=+1m8s781ms ago
     * SYSTEM_ALERT_WINDOW: ignore
     * READ_CLIPBOARD: allow; time=+1m15s193ms ago
     * WAKE_LOCK: allow; time=+14s424ms ago; duration=+107ms
     * MONITOR_LOCATION: allow; time=+7d23h31m59s63ms ago; duration=+354ms
     * MONITOR_HIGH_POWER_LOCATION: allow; time=+7d23h31m59s63ms ago; duration=+354ms
     * READ_EXTERNAL_STORAGE: allow; time=+1m17s550ms ago
     * WRITE_EXTERNAL_STORAGE: allow; time=+1m15s447ms ago
     * CHANGE_WIFI_STATE: allow; time=+1m16s94ms ago
     * WRITE_MEDIA_AUDIO: deny; rejectTime=+1m15s443ms ago
     * WRITE_MEDIA_VIDEO: deny; rejectTime=+1m15s442ms ago
     * WRITE_MEDIA_IMAGES: deny; rejectTime=+1m15s441ms ago
     * READ_DEVICE_IDENTIFIERS: deny; rejectTime=+53s355ms ago
     * ACCESS_MEDIA_LOCATION: allow; time=+1m17s548ms ago
     * MANAGE_EXTERNAL_STORAGE: default; rejectTime=+1m15s445ms ago
     * NO_ISOLATED_STORAGE: deny; rejectTime=+1m15s445ms ago
     *
     *
     * adb shell appops set com.taobao.taobao MANAGE_EXTERNAL_STORAGE allow --user 0
     * 貌似失败。需要看原因
     *
     * @param pkg
     * @return
     */
    //
    //adb shell appops set cn.com.hebeibank.mbank SYSTEM_ALERT_WINDOW allow
    private static boolean parserOne(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        List<String> lines = AdbShell.getArray("appops get " + pkg);
        return true;
    }

}
