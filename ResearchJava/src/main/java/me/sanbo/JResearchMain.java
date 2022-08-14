package me.sanbo;


import me.sanbo.model.PModel;
import me.sanbo.utils.AdbShell;
import me.sanbo.utils.FileUtils;
import me.sanbo.utils.TextUtils;
import me.sanbo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 入口类
 * @Version: 1.0
 * @Create: 2022-08-13 23:33:39
 * @author: Administrator
 */
public class JResearchMain {




    /**
     * 命令: stat -c '%n %X %Y %Z',意义：
     *      * %X: Access unix time
     *      * %Y: Mod unix time
     *      * %Z: Creation unix time
     * @param fileName
     */
    public static void getAllAliveHistory(String fileName) {

        List<PModel> models = new ArrayList<PModel>();
        String[] pres = new String[]{"/sdcard/Android/data/", "/sdcard/Android/media/", "/sdcard/Android/obb/", "/sdcard/Android/obj/"};
        for (String pre : pres) {
            getHistoryAndParser(models, pre);
        }

        System.out.println("(" + Version.version() + ") . 处理完毕,数据条目:" + models.size() + " ; 即将进入保存阶段...");
        FileUtils.saveTextToFile(fileName, PModel.getTitle(), false);
        if (models.size() > 0) {
            for (int i = 0; i < models.size(); i++) {
                PModel module = models.get(i);
                FileUtils.saveTextToFile(fileName, module.toCsvString(), true);
            }
        }
        System.out.println("(" + Version.version()+") 保存完毕.");
    }


    /**
     * parser path to package and other info
     * @param models
     * @param baseDirName
     */
    private static void getHistoryAndParser(List<PModel> models, String baseDirName) {
        List<String> res = AdbShell.getArray("find " + baseDirName + " |xargs stat -c '%n %X %Y %Z'");
        if (res == null || res.size() < 1) {
            System.err.println("(" + Version.version() + ")[" + baseDirName + "] not has data, will return!");
            return;
        }
        for (int i = 0; i < res.size(); i++) {
            String line = res.get(i);
            if (TextUtils.isEmpty(line)) {
                return;
            }
            // 1.获取包名
            String tmp = line.replaceAll(baseDirName, "");
            String pkgName = "";
            //support  this data
            // /sdcard/Android/data/ 1636446312 1636446312 1653303830
            // /sdcard/Android/obj/.howto 1636446312 1636446312 1653303830
            if (tmp.startsWith(" ") || tmp.startsWith(".")) {
                continue;
            }

            //support /sdcard/Android/data/com.kmxs.reader
            if (tmp.contains("/")) {
                pkgName = tmp.substring(0, tmp.indexOf("/"));
            } else {
                pkgName = tmp.substring(0, tmp.indexOf(" "));
            }
            // is android package
            if (!Utils.isEfficientPkg(pkgName)) {
                continue;
            }

//            System.out.println("pkg:" + pkgName);
//            //2.尝试拆分路径及时间
            String[] pathAndTimes = line.split("\\s+");
            // support filepath/filename contain " "
            if (pathAndTimes.length != 4) {
                System.err.println(line + "----->" + tmp);
                // @todo need masure support some path.
            }
            if (pathAndTimes.length == 4) {
                String path = TextUtils.replaceSpaceForFilePath(pathAndTimes[0]);
                long accessTime = Long.valueOf(pathAndTimes[1]);
                long modTime = Long.valueOf(pathAndTimes[2]);
                long creationTime = Long.valueOf(pathAndTimes[3]);
//                System.out.println(path + "\r\n\t\tAccess LinuxTime:" + accessTime + "\r\n\t\tMod Linux Time:" + modTime + "\r\n\t\tCreation Linux Time:" + creationTime);

                String appName = Utils.getAppName(pkgName);
                long versionCode = Long.valueOf(Utils.getVersionCode(pkgName));
                String versionName = Utils.getVersionName(pkgName);
                String minSdk = Utils.getMinSdk(pkgName);
                String targetSdk = Utils.getTargetSdk(pkgName);

                PModel model = new PModel(path, appName, pkgName, versionCode, versionName, minSdk, targetSdk, accessTime, modTime, creationTime);
                if (!models.contains(model)) {
                    models.add(model);
                }
            }

        }
    }


    public static void main(String[] args) {
        getAllAliveHistory("result.csv");


//        String line = "/sdcard/Android/data/com.xingin.xhs/cache/xhs_webView_resource/7764b704f3c593ce74ed7a1506e97743.0";
//        // 1.解析包名
//        String tmp = line.replaceAll("/sdcard/Android/data/", "");
//        String pkgName=tmp.substring(0,tmp.indexOf("/"));

    }
}
