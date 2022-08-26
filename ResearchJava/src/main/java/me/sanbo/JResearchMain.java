package me.sanbo;

import me.sanbo.model.PModel;
import me.sanbo.utils.AdbShell;
import me.sanbo.utils.FileUtils;
import me.sanbo.utils.TextUtils;
import me.sanbo.utils.Utils;
import me.sanbo.json.JSONException;
import me.sanbo.json.JSONObject;

import java.io.File;
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
     * * %X: Access unix time
     * * %Y: Mod unix time
     * * %Z: Creation unix time
     * 
     * @param fileName
     */
    public static void getAllAliveHistory(String fileName) throws JSONException {

        List<PModel> models = new ArrayList<PModel>();
        String[] pres = new String[] { "/sdcard/Android/data/", "/sdcard/Android/media/", "/sdcard/Android/obb/",
                "/sdcard/Android/obj/" };
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
        System.out.println("(" + Version.version() + ") 保存完毕.");
    }

    /**
     * parser path to package and other info
     * 
     * @param models
     * @param baseDirName
     */
    private static void getHistoryAndParser(List<PModel> models, String baseDirName) throws JSONException {
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
            // support this data
            // /sdcard/Android/data/ 1636446312 1636446312 1653303830
            // /sdcard/Android/obj/.howto 1636446312 1636446312 1653303830
            if (tmp.startsWith(" ") || tmp.startsWith(".")) {
                continue;
            }

            // support /sdcard/Android/data/com.kmxs.reader
            if (tmp.contains("/")) {
                pkgName = tmp.substring(0, tmp.indexOf("/"));
            } else {
                pkgName = tmp.substring(0, tmp.indexOf(" "));
            }
            // is android package
            if (!Utils.isEfficientPkg(pkgName)) {
                continue;
            }

            // System.out.println("pkg:" + pkgName);
            // //2.尝试拆分路径及时间
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
                JSONObject info = cacheJSON.optJSONObject(pkgName);
                if (info == null) {
                    System.err.println(path
                            + "\r\n\t\tPkg Name:" + pkgName
                            + "\r\n\t\tAccess LinuxTime:" + accessTime
                            + "\r\n\t\tMod Linux Time:" + modTime
                            + "\r\n\t\tCreation Linux Time:" + creationTime
                            + "\r\n\t\tinfo:" + info);
                    continue;
                }

                String appName = info.optString("appName");
                long versionCode = info.optLong("versionCode");
                String versionName = info.optString("versionName");
                String minSdk = info.optString("minSdk");
                String targetSdk = info.optString("targetSdk");
                PModel model = new PModel(path, appName, pkgName, versionCode, versionName, minSdk, targetSdk,
                        accessTime, modTime, creationTime);
                models.add(model);
            }
        }
    }

    public static JSONObject cacheJSON = new JSONObject();

    public static void main(String[] args) throws JSONException {
        // add cache.
        cache();
        System.out.println("(" + Version.version() + ")caches over. " + cacheJSON.length());
        getAllAliveHistory("data/result.csv");

    }

    private static void cache() throws JSONException {
        String fn = "data/" + Utils.getSerialNo() + ".json";
        File f = new File(fn);
        // 已经采集
        if (f.exists()) {
            if (cacheJSON.length() < 1) {
                cacheJSON = new JSONObject(FileUtils.readContent(fn));
            }
            return;
        }
        List<String> pkgs = Utils.getAllPackage();

        for (int i = 0; i < pkgs.size(); i++) {
            String pkgName = pkgs.get(i);
            String appName = Utils.getAppName(pkgName);
            long versionCode = Long.valueOf(Utils.getVersionCode(pkgName));
            String versionName = Utils.getVersionName(pkgName);
            String minSdk = Utils.getMinSdk(pkgName);
            String targetSdk = Utils.getTargetSdk(pkgName);

            JSONObject info = new JSONObject();
            info.put("appName", appName);
            info.put("pkgName", pkgName);
            info.put("versionCode", versionCode);
            info.put("versionName", versionName);
            info.put("minSdk", minSdk);
            info.put("targetSdk", targetSdk);
            cacheJSON.put(pkgName, info);
            System.out.println("current:" + i + "/" + pkgs.size());
        }

        FileUtils.saveTextToFile(fn, cacheJSON.toString(), false);

    }
}
