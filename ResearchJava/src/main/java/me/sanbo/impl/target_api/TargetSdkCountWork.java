package me.sanbo.impl.target_api;

import me.sanbo.utils.Bilo;
import me.sanbo.utils.FileUtils;
import me.sanbo.utils.TextUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.*;

/**
 * 缓存文件及应用目标API级别关联论证
 */
public class TargetSdkCountWork {
    public static void main(String[] args) {


        run();
    }


    // 目标及所有apps
    private static String Name_target_apps = "../data/targetver/p_target_app.csv";
    // 目标及所有apps个数
    private static String Name_target_appscount = "../data/targetver/p_target_appscount.csv";
    // 目标及占比情况   target
    private static String Name_target_zhanbi = "../data/targetver/p_target_zhanbi.csv";

    // 应用名：路径数量
    private static String Name_app_filePathCount = "../data/targetver/p_app_filePathCount.csv";
    // 目标API版本：所有符合的路径数量
    private static String Name_target_totalfilePathCount = "../data/targetver/p_target_totalfilePathCount.csv";

    private static String baseFilepath = "../data/targetver/target-filepath-all.csv";

    // targetSdkVersion 和 包名列表
    private static Map<String, List<String>> targetSdkVersionAndPackages = new HashMap<String, List<String>>();
    // APP列表
    private static Set<String> packageLists = new HashSet<String>();
    // 应用包名:应用名称
    private static Map<String, String> packageAndAppname = new HashMap<String, String>();
    // 应用包名 : 文件列表
    private static Map<String, List<String>> packageAndFilepaths = new HashMap<String, List<String>>();
    // targetSdkVersion :  所有的文件路径
    private static Map<String, List<String>> targetSdkVersionAndFilepaths = new HashMap<String, List<String>>();


    private static void run() {
        fullData();
        System.out.println("=====内存填充数据完毕====");
        cache1();
        FileUtils.saveTextToFile(Name_app_filePathCount, "app名称,文件路径数量", false);
        FileUtils.saveTextToFile(Name_target_totalfilePathCount, "目标API级别,文件路径数量", false);
        for (Map.Entry<String, List<String>> entry : packageAndFilepaths.entrySet()) {
            String pkg = entry.getKey();
            List<String> paths = entry.getValue();
            String line_app_filePathCount = packageAndAppname.get(pkg) + "," + paths.size();
            FileUtils.saveTextToFile(Name_app_filePathCount, line_app_filePathCount, true);
        }

        for (Map.Entry<String, List<String>> entry : targetSdkVersionAndFilepaths.entrySet()) {
            String targetSdkVersion = entry.getKey();
            List<String> paths = entry.getValue();
            String line_target_totalfilePathCount = targetSdkVersion + "," + paths.size();
            FileUtils.saveTextToFile(Name_target_totalfilePathCount, line_target_totalfilePathCount, true);
        }
    }

    private static void cache1() {
        FileUtils.saveTextToFile(Name_target_apps, "目标API级别,app名字", false);
        FileUtils.saveTextToFile(Name_target_appscount, "目标API级别,app数量", false);
        FileUtils.saveTextToFile(Name_target_zhanbi, "目标API级别,app数量占比(app/appTotal)", false);
        for (Map.Entry<String, List<String>> entry : targetSdkVersionAndPackages.entrySet()) {
            String targetSdkVersion = entry.getKey();
            List<String> pkgs = entry.getValue();
            String line_target_appscount = targetSdkVersion + "," + pkgs.size();
            FileUtils.saveTextToFile(Name_target_appscount, line_target_appscount, true);
            String line_target_zhanbi = targetSdkVersion + "," + Bilo.ccc(pkgs.size(), packageLists.size());
            FileUtils.saveTextToFile(Name_target_zhanbi, line_target_zhanbi, true);
            for (String pkg : pkgs) {
                String line_target_apps = targetSdkVersion + "," + packageAndAppname.get(pkg);
                FileUtils.saveTextToFile(Name_target_apps, line_target_apps, true);
            }
        }
    }

    private static void fullData() {
        File f = new File(baseFilepath);

        if (!f.exists()) {
            System.err.println(f.getAbsolutePath() + " is not exists!");
        }
        List<String> lines = FileUtils.readForArray(f.getAbsolutePath());
        System.out.println(f.getAbsolutePath() + "-----" + lines.size());

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            String[] args = line.split(",");
            if (args.length != 4) {
                System.err.println(line + " The len is not 4!");
                continue;
            }
            String appName = args[0].trim().replace("\\s+", "");
            String pkgName = args[1].trim().replace("\\s+", "");
            String targetSdkVersion = args[2].trim().replace("\\s+", "");
            String path = args[3].trim().replace("\\s+", "");

            if (TextUtils.isEmpty(appName)) {
                continue;
            }
            if (appName.contains("application-label-zh-CN:")){
                appName=appName.replaceAll("application-label-zh-CN:","");
            }
            if (TextUtils.isEmpty(appName)) {
                continue;
            }
            // tarcSdkVersion 和 包名列表 targetSdkVersionAndPackages
            List<String> pkgs = new ArrayList<String>();
            if (targetSdkVersionAndPackages.containsKey(targetSdkVersion)) {
                pkgs = targetSdkVersionAndPackages.get(targetSdkVersion);
            }
            if (!pkgs.contains(pkgName)) {
                pkgs.add(pkgName);
                targetSdkVersionAndPackages.put(targetSdkVersion, pkgs);
            }
            // APP列表 packageLists
            packageLists.add(pkgName);
            //应用包名:应用名称 packageAndAppname
            packageAndAppname.put(pkgName, appName);
            // 应用包名:文件列表   packageAndFilepaths
            List<String> thisPackagePaths = new ArrayList<String>();
            if (packageAndFilepaths.containsKey(pkgName)) {
                thisPackagePaths = packageAndFilepaths.get(pkgName);
            }
            if (!thisPackagePaths.contains(path)) {
                thisPackagePaths.add(pkgName);
                packageAndFilepaths.put(pkgName, thisPackagePaths);
            }
            // targetSdkVersion：所有的文件路径  targetSdkVersionAndFilepaths
            List<String> allPaths = new ArrayList<String>();
            if (targetSdkVersionAndFilepaths.containsKey(targetSdkVersion)) {
                allPaths = targetSdkVersionAndFilepaths.get(targetSdkVersion);
            }
            if (!allPaths.contains(path)) {
                allPaths.add(pkgName);
                targetSdkVersionAndFilepaths.put(targetSdkVersion, allPaths);
            }
        }
    }


}
