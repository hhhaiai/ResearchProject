package me.sanbo.impl;

import me.sanbo.utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 以应用为维度计算命中率
 */
public class RightByAPP2_2_3 {
    private static Map<String, Integer> appAndCountInDur = new HashMap<String, Integer>();
    private static Map<String, Integer> appAndTotal = new HashMap<String, Integer>();

    public static void main(String[] args) {
        //get okcount
        getCount();
        getTotalCount();
        System.out.println("appAndCountInDur:" + appAndCountInDur.size());
        System.out.println("appAndTotal:" + appAndTotal.size());
        // 对比，然后保存
        cmpAndSave();
    }

    private static void cmpAndSave() {
        FileUtils.saveTextToFile("../ada/app_right_total.csv", "", false);
        for (Map.Entry<String, Integer> entry : appAndCountInDur.entrySet()) {
            String appName = entry.getKey();
            int countInDur = entry.getValue();
            int countTotal = appAndTotal.get(appName);
            String line = appName
                    + "," + countInDur
                    + "," + countTotal;
            FileUtils.saveTextToFile("../ada/app_right_total.csv", line, true);

        }
    }

    private static void getTotalCount() {
        List<String> lines = FileUtils.readForArray("../ada/app_point.csv");
        System.out.println("getTotalCount lines: " + lines.size());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String[] ss = line.split(",");
            if (ss.length != 3) {
                System.err.println("单行解析失败: " + line);
                continue;
            }
            String appName = ss[0].trim();
            long begin = Long.valueOf(ss[1].trim());
            String filePath = ss[2].trim();
            if (appAndTotal.containsKey(appName)) {
                int count = appAndTotal.get(appName);
                appAndTotal.put(appName, count + 1);
            } else {
                appAndTotal.put(appName, 1);
            }
        }
    }


    private static void getCount() {
        List<String> lines = FileUtils.readForArray("../ada/app_point_inTime.csv");
        System.out.println("getPkgAndPair lines: " + lines.size());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String[] ss = line.split(",");
            if (ss.length != 3) {
                System.err.println("单行解析失败: " + line);
                continue;
            }
            String appName = ss[0].trim();
            long begin = Long.valueOf(ss[1].trim());
            String filePath = ss[2].trim();
            if (appAndCountInDur.containsKey(appName)) {
                int count = appAndCountInDur.get(appName);
                appAndCountInDur.put(appName, count + 1);
            } else {
                appAndCountInDur.put(appName, 1);
            }
        }
    }
}
