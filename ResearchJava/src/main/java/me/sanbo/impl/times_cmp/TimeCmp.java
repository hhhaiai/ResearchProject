package me.sanbo.impl.times_cmp;

import me.sanbo.utils.Bilo;
import me.sanbo.utils.FileUtils;
import me.sanbo.utils.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 三维时间统计及调整
 * @Version: 1.0
 * @Create: 2022-08-27 11:39:26
 * @author: Administrator
 */
public class TimeCmp {


    // 源文件
    private static String baseFilepath = "../data/times-cmp/pkg-times-all.csv";
    // 源数据解析  应用名称: amc时间一致次数（访问时间+修改时间+创建时间）
    // 文件名为p_xxx为temp file
    private static String FN_app_same_amc_apptotal = "../data/times-cmp/p_app_same_amc_apptotal.csv";
    private static String FN_app_same_am_apptotal = "../data/times-cmp/p_app_same_am_apptotal.csv";
    private static String FN_app_same_mc_apptotal = "../data/times-cmp/p_app_same_mc_apptotal.csv";

    private static String FN_same_amc_apptotal = "../data/times-cmp/p_same_amc_apptotal.csv";
    private static String FN_same_am_apptotal = "../data/times-cmp/p_same_am_apptotal.csv";
    private static String FN_same_mc_apptotal = "../data/times-cmp/p_same_mc_apptotal.csv";

    public static void main(String[] args) {
        run();
    }


    private static Map<String, Integer> appnameAndSameamc = new HashMap<String, Integer>();
    // 源数据解析  应用名称: am时间一致次数（访问时间+修改时间）
    private static Map<String, Integer> appnameAndSameam = new HashMap<String, Integer>();
    // 源数据解析  应用名称: mc时间一致次数（修改时间+创建时间）
    private static Map<String, Integer> appnameAndSamemc = new HashMap<String, Integer>();
    // 源数据解析  应用名称: 应用对应条目数量
    private static Map<String, Integer> appnameAndTotalCount = new HashMap<String, Integer>();
    // 总次数
    private static int totalCount = 0;
    // amc一致条目
    private static int sameAmc = 0;
    // am一致条目
    private static int sameAm = 0;
    // mc一致条目
    private static int sameMc = 0;



    private static void saveDatas() {
        FileUtils.saveTextToFile(FN_app_same_amc_apptotal, "app名称,增改查一致数量,app路径总量,整体占比", false);
        FileUtils.saveTextToFile(FN_app_same_am_apptotal, "app名称,改查一致数量,app路径总量,整体占比", false);
        FileUtils.saveTextToFile(FN_app_same_mc_apptotal, "app名称,增改一致数量,app路径总量,整体占比", false);

        FileUtils.saveTextToFile(FN_same_amc_apptotal, "增改查一致数量,app路径总量,整体占比", false);
        FileUtils.saveTextToFile(FN_same_am_apptotal, "改查一致数量,app路径总量,整体占比", false);
        FileUtils.saveTextToFile(FN_same_mc_apptotal, "增改一致数量,app路径总量,整体占比", false);

        //FN_app_same_amc_apptotal
        for (Map.Entry<String, Integer> entry : appnameAndSameamc.entrySet()) {
            String appName = entry.getKey();
            int amcSameCount = entry.getValue();
            long totalCount = appnameAndTotalCount.get(appName);
            if (amcSameCount < 2 || totalCount < 2) {
                continue;
            }
            String line_app_same_amc_apptotal = appName + "," + amcSameCount + "," + totalCount + "," + Bilo.ccc(amcSameCount, totalCount);
            FileUtils.saveTextToFile(FN_app_same_amc_apptotal, line_app_same_amc_apptotal, true);
        }

        //FN_app_same_am_apptotal
        for (Map.Entry<String, Integer> entry : appnameAndSameam.entrySet()) {
            String appName = entry.getKey();
            int amSameCount = entry.getValue();
            long totalCount = appnameAndTotalCount.get(appName);
            if (amSameCount < 2 || totalCount < 2) {
                continue;
            }
            String line_app_same_am_apptotal = appName + "," + amSameCount + "," + totalCount + "," + Bilo.ccc(amSameCount, totalCount);
            FileUtils.saveTextToFile(FN_app_same_am_apptotal, line_app_same_am_apptotal, true);
        }
        //FN_app_same_mc_apptotal
        for (Map.Entry<String, Integer> entry : appnameAndSamemc.entrySet()) {
            String appName = entry.getKey();
            int mcSameCount = entry.getValue();
            long totalCount = appnameAndTotalCount.get(appName);
            if (mcSameCount < 2 || totalCount < 2) {
                continue;
            }
            String line_app_same_mc_apptotal = appName + "," + mcSameCount + "," + totalCount + "," + Bilo.ccc(mcSameCount, totalCount);
            FileUtils.saveTextToFile(FN_app_same_mc_apptotal, line_app_same_mc_apptotal, true);
        }
        if (sameAmc > 2 && totalCount > 2) {
            FileUtils.saveTextToFile(FN_same_amc_apptotal, sameAmc + "," + totalCount + "," + Bilo.ccc(sameAmc, totalCount), true);
        }
        if (sameAm > 2 && totalCount > 2) {
            FileUtils.saveTextToFile(FN_same_am_apptotal, sameAm + "," + totalCount + "," + Bilo.ccc(sameAm, totalCount), true);
        }

        if (sameMc > 2 && totalCount > 2) {
            FileUtils.saveTextToFile(FN_same_mc_apptotal, sameMc + "," + totalCount + "," + Bilo.ccc(sameMc, totalCount), true);

        }
    }

    private static void run() {
        fullData();

        // 保存数据
        saveDatas();
    }



    private static void fullData() {

        File f = new File(baseFilepath);

        if (!f.exists()) {
            System.err.println(f.getAbsolutePath() + " is not exists!" );
        }
        List<String> lines = FileUtils.readForArray(f.getAbsolutePath());
        System.out.println(f.getAbsolutePath() + "-----" + lines.size());

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            String[] args = line.split("," );
            if (args.length != 6) {
                System.err.println(line + " The len is not 4!" );
                continue;
            }
            String appName = args[0].trim().replace("\\s+", "" );
            String pkgName = args[1].trim().replace("\\s+", "" );
            long accessTime = Long.valueOf(args[2].trim().replace("\\s+", "" ));
            long modifyTime = Long.valueOf(args[3].trim().replace("\\s+", "" ));
            long creationTime = Long.valueOf(args[4].trim().replace("\\s+", "" ));
            String path = args[5].trim().replace("\\s+", "" );

            if (TextUtils.isEmpty(appName)) {
                continue;
            }
            if (appName.contains("application-label-zh-CN:" )) {
                appName = appName.replaceAll("application-label-zh-CN:", "" );
            }
            if (TextUtils.isEmpty(appName)) {
                continue;
            }


            //total count
            totalCount += 1;
            if (appnameAndTotalCount.containsKey(appName)) {
                appnameAndTotalCount.put(appName, appnameAndTotalCount.get(appName) + 1);
            } else {
                appnameAndTotalCount.put(appName, 1);
            }
            //amc same
            if (accessTime == modifyTime && modifyTime == creationTime) {
                sameAmc += 1;
                if (appnameAndSameamc.containsKey(appName)) {
                    appnameAndSameamc.put(appName, appnameAndSameamc.get(appName) + 1);
                } else {
                    appnameAndSameamc.put(appName, 1);
                }
                continue;
            }

            // am same
            if (accessTime == modifyTime && modifyTime != creationTime) {
                sameAm += 1;
                if (appnameAndSameam.containsKey(appName)) {
                    appnameAndSameam.put(appName, appnameAndSameam.get(appName) + 1);
                } else {
                    appnameAndSameam.put(appName, 1);
                }
            }

            // mc same
            if (accessTime != modifyTime && modifyTime == creationTime) {
                sameMc += 1;
                if (appnameAndSamemc.containsKey(appName)) {
                    appnameAndSamemc.put(appName, appnameAndSamemc.get(appName) + 1);
                } else {
                    appnameAndSamemc.put(appName, 1);
                }
            }

        }
    }


}
