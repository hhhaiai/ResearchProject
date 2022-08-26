package me.sanbo.impl.keep_days;

import me.sanbo.utils.*;

import java.io.File;
import java.util.*;

/**
 * 保持活跃天数
 */
public class KeepDay {
    public static void main(String[] args) {
        run();
    }

    private static String FN_app_alivedays = "../data/modify-keep-duration/p_app_alivedays.csv";
    private static String FN_app_alivedays_count = "../data/modify-keep-duration/p_app_alivedays_count.csv";
    private static String FN_app_alivedays_chu_total = "../data/modify-keep-duration/p_app_alivedays_chu_total.csv";
    private static String FN_total_alivedays_chu_total = "../data/modify-keep-duration/p_total_alivedays_chu_total.csv";



    private static String baseFilepath = "../data/modify-keep-duration/pkg-modiytime-all.csv";
    // 应用列表
    public static Set<String> appLists = new HashSet<String>();
    //解析数据获取 应用名: 活跃日期 (yyyy-MM-dd:long)
    public static Map<String, Map<String, Long>> appnameAndAliveDate = new HashMap<String, Map<String, Long>>();
    //解析数据获取 应用名: <最小日期，最大日期>
    public static Map<String, Pair<Long, Long>> appnameAndMin_MaxTime = new HashMap<String, Pair<Long, Long>>();
    //计算获取 应用名: 活跃天数-安装天数
    public static Map<String, Pair<Long, Long>> appnameAndAlive_Total = new HashMap<String, Pair<Long, Long>>();
    // 计算获取 应用名: 活跃天数/安装天数  - 只有一天的不参与计算
    public static Map<String, String> appnameAndAlivedaysChuTotalDays = new HashMap<String, String>();
    // 计算获取   (活跃天数/安装天数) 所有应用的之和  / 应用数   - 只有一天的不参与计算
    public static Pair<Long, Long> aoo = new Pair<Long, Long>(0L, 0L);



    private static void run() {
        fullData();
        System.out.println("==============数据采集完成===========");
        // 计算
        mm();
        //保存
        cass();
    }

    private static void mm() {
        // appnameAndAlivedays_TotalDays
        for (Map.Entry<String, Pair<Long, Long>> entry : appnameAndMin_MaxTime.entrySet()) {
            String appName = entry.getKey();
            if (appnameAndAlivedaysChuTotalDays.containsKey(appName)) {
                continue;
            }
            Pair<Long, Long> min_max = entry.getValue();
            if (min_max.first != 0
                    && min_max.second != 0
                    && min_max.second != min_max.first
            ) {
                int aliveDays = appnameAndAliveDate.get(appName).size();
                int durDays = DateUtils.differentDays(min_max.first, min_max.second);
                appnameAndAlivedaysChuTotalDays.put(appName, Bilo.ccc(aliveDays, durDays));
                appnameAndAlive_Total.put(appName, new Pair<Long, Long>((long) aliveDays, (long) durDays));
            }
        }

        Map<String, Pair<Long, Long>> m =new HashMap<String, Pair<Long, Long>>(appnameAndAlive_Total);
        for (Map.Entry<String, Pair<Long, Long>> entry : m .entrySet()) {
            String appName=entry.getKey();
            Pair<Long, Long> v =entry.getValue();
            if (v.first<2||v.second<2){
                appnameAndAlive_Total.remove(appName);
                appnameAndAlivedaysChuTotalDays.remove(appName);
                continue;
            }
            aoo.first += v.first;
            aoo.second += v.second;
        }
    }

    private static void cass() {
        //appnameAndAliveDate  应用名: 活跃日期 (yyyy-MM-dd:long)
        FileUtils.saveTextToFile(FN_app_alivedays, "app名字,活跃日期", false);
        //appnameAndAlive_Total  应用名: 活跃天数-安装天数
        FileUtils.saveTextToFile(FN_app_alivedays_count, "app名字,活跃天数,安装天数", false);
//        appnameAndAlivedaysChuTotalDays  应用名: 活跃天数/安装天数  - 只有一天的不参与计算
        FileUtils.saveTextToFile(FN_app_alivedays_chu_total, "app名字,活跃天占比(活跃天数/安装天数)", false);
        FileUtils.saveTextToFile(FN_total_alivedays_chu_total, "总活跃天数,总安装天数,应用数,总活跃天占比(活跃占比之和/应用数)", false);

        //FN_app_alivedays
        for (Map.Entry<String, Map<String, Long>> entry : appnameAndAliveDate.entrySet()) {
            String appName = entry.getKey();
            Set<String> days = entry.getValue().keySet();
            for (String day : days) {
                String line_app_alivedays = appName + "," + day;
                FileUtils.saveTextToFile(FN_app_alivedays, line_app_alivedays, true);
            }
        }
        //appnameAndAlive_Total
        for (Map.Entry<String, Pair<Long, Long>> entry : appnameAndAlive_Total.entrySet()) {
            String appName = entry.getKey();
            Pair<Long, Long> aliveTotal = entry.getValue();
            String line_app_alivedays_count = appName + "," + aliveTotal.first + "," + aliveTotal.second;
            FileUtils.saveTextToFile(FN_app_alivedays_count, line_app_alivedays_count, true);
        }
        //FN_app_alivedays_chu_total
        //Map<String, String> appnameAndAlivedaysChuTotalDays

        for (Map.Entry<String, String> entry : appnameAndAlivedaysChuTotalDays.entrySet()) {
            String appName = entry.getKey();
            String chu = entry.getValue();
            String line_app_alivedays_chu_total = appName + "," + chu;
            FileUtils.saveTextToFile(FN_app_alivedays_chu_total, line_app_alivedays_chu_total, true);
        }

        //        FileUtils.saveTextToFile(FN_total_alivedays_chu_total, "总活跃天数,总安装天数,应用数,总活跃天占比(活跃占比之和/应用数)", false);

        String line_total_alivedays_chu_total = aoo.first + "," + aoo.second + "," + appnameAndAlive_Total.size() + "," +
                Bilo.cccD(Bilo.cccD(aoo.first, aoo.second), appnameAndAlive_Total.size());
        FileUtils.saveTextToFile(FN_total_alivedays_chu_total, line_total_alivedays_chu_total, true);

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
            long modifyTime = Long.valueOf(args[2].trim().replace("\\s+", ""));
            String path = args[3].trim().replace("\\s+", "");
            if (TextUtils.isEmpty(appName)) {
                continue;
            }
            if (appName.contains("application-label-zh-CN:")) {
                appName = appName.replaceAll("application-label-zh-CN:", "");
            }
            if (TextUtils.isEmpty(appName)) {
                continue;
            }
            String date = DateUtils.getData(modifyTime);
            //appLists  应用列表
            appLists.add(appName);
            // appnameAndAliveDate  应用名: 活跃日期 (yyyy-MM-dd:long)
            Map<String, Long> aliveDate = new HashMap<String, Long>();
            if (appnameAndAliveDate.containsKey(appName)) {
                aliveDate = appnameAndAliveDate.get(appName);
            }
            if (!aliveDate.containsKey(date)) {
                aliveDate.put(date, modifyTime);
                appnameAndAliveDate.put(appName, aliveDate);
            }
            //appnameAndMin_MaxTime 应用名: <最小日期，最大日期>
            Pair<Long, Long> minmaxPair = new Pair<>(0L, 0L);
            if (appnameAndMin_MaxTime.containsKey(appName)) {
                minmaxPair = appnameAndMin_MaxTime.get(appName);
            }
            //默认放最小时间。--策略 。 新来的比最小 -小，之前小的和大的比
            long min = minmaxPair.first;
            long max = minmaxPair.second;
            if (min == 0) {
                minmaxPair.first = modifyTime;
                minmaxPair.second = modifyTime;
            } else {
                if (modifyTime < min) {
                    minmaxPair.first = modifyTime;
                } else {
                    if (modifyTime > max) {
                        minmaxPair.second = modifyTime;
                    }
                }
            }
            appnameAndMin_MaxTime.put(appName, minmaxPair);
        }
    }

}
