package me.sanbo.utils;

import java.io.File;
import java.util.List;

public class Utils {

    /**
     * 获取设备上所有的安装列表
     * @return
     */
    public static List<String> getAllPackage() {
        return AdbShell.getArray("pm list package", "package:", "");
    }


    public static String getSerialNo() {
        String result = AdbShell.shell("getprop ro.serialno");
        if (TextUtils.isEmpty(result)) {
            result = AdbShell.adb("get-serialno");
        }
        return result;
    }

    public static String getDPI() {
        return AdbShell.shell("wm density", "Physical\\ density:\\ ", "");
    }

    public static String getWmSize() {
        return AdbShell.shell("wm size", "Physical\\ size:\\ ", "");
    }

    public static String getStoreInfo() {
        return AdbShell.shell("df /data");
    }

    public static String getReleaseVersion() {
        return AdbShell.shell("getprop ro.build.version.release");
    }

    public static String getBrandName() {
        return AdbShell.shell("getprop ro.product.brand");
    }

    public static String getAppName(String packageName) {
        // 1.获取安装路径
        String aptPath = getInstallPath(packageName);
        // 2. 确认aapt
        if (!makesureAAPT()) {
            return "";
        }
        // 3.获取安装名称
        String result = AdbShell.shell("/data/local/tmp/aapt dump badging " + aptPath + " |  grep application-label");
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        //4.解析获取应用名称
        if (result.contains("\n")) {
            String[] lines = result.split("\n");
            if (result.contains("application-label-zh")) {
                for (String line : lines) {
                    return line
                            .replaceAll("application-label-zh:", "")
                            .replaceAll("application-label-zh-CN:", "")
                            .replaceAll("application-label:", "")
                            .replaceAll("\'", "")
                            .replaceAll("\\s+", "")
                            .trim();

                }
            } else if (result.contains("application-label")) {
                for (String line : lines) {
                    return line
                            .replaceAll("application-label-zh:", "")
                            .replaceAll("application-label-zh-CN:", "")
                            .replaceAll("application-label:", "")
                            .replaceAll("\'", "")
                            .replaceAll("\\s+", "")
                            .trim();
                }
            }
        }
        return "";
    }

    /**
     * 确认aapt安装好
     * @return
     */
    public static boolean makesureAAPT() {
        // 1.检查是否有
        boolean isPrepare = isPrepareAAPT();
        if (!isPrepare) {
            //2.查看cpu
            String cpuAbi = getCpuAbi();
            //3.获取本地appt路径
            String aaptPathNative = "tools/aapt/" + cpuAbi + "/bin/aapt";
            File f = new File(aaptPathNative);
            // 4. adb push到手机中
            AdbShell.adb("push " + f.getAbsolutePath() + " /data/local/tmp/");
            //5. 更改权限
            AdbShell.shell("chmod -R 777 /data/local/tmp/aapt");
        } else {
            return true;
        }
        return isPrepareAAPT();
    }

    static String getCpuAbi() {
        return AdbShell.shell("getprop ro.product.cpu.abi");

    }

    static boolean isPrepareAAPT() {
        String path = "/data/local/tmp/aapt";
        String result = AdbShell.shell("ls " + path);
        if (TextUtils.isEmpty(result)) {
            return false;
        }
        return path.equalsIgnoreCase(result);
    }

    public static String getInstallPath(String packageName) {
        return AdbShell.shell("pm path  " + packageName, "package:", "");
    }

    public static String getVersionCode(String packageName) {
        return getBase(packageName, "versionCode");
    }

    public static String getVersionName(String packageName) {
        String result = AdbShell.shell("dumpsys package  " + packageName + "|grep versionName");
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        return result.replaceAll("versionName=", "").replaceAll("\\s+", "").trim();
    }

    public static String getMinSdk(String packageName) {
        return getBase(packageName, "minSdk");
    }

    public static String getTargetSdk(String packageName) {
        return getBase(packageName, "targetSdk");
    }

    private static String getBase(String packageName, String code) {
        String result = AdbShell.shell("dumpsys package  " + packageName + "|grep versionCode");
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        String[] ss = result.split("\\s+");
        for (int i = 0; i < ss.length; i++) {
            String tmpStr = ss[i];
            if (tmpStr.contains(code)) {
                return tmpStr.replaceAll(code + "=", "");
            }
        }
        return "";
    }

    /**
     * 有效包名验证
     *
     * @param pkgName
     * @return true: 有效包名
     *  false:无效包名
     */
    public static boolean isEfficientPkg(String pkgName) {
        if (!TextUtils.isEmpty(pkgName) && !pkgName.startsWith(".") && pkgName.contains(".")) {
            return true;
        } else {
            return false;
        }
    }

}
