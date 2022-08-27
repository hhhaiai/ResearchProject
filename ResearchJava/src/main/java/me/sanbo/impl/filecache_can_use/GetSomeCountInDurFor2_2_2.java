package me.sanbo.impl.filecache_can_use;

import me.sanbo.json.JSONException;
import me.sanbo.utils.FileUtils;
import me.sanbo.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetSomeCountInDurFor2_2_2 {

    private static Map<String, Pair<Long, Long>> pkgAndPair = new HashMap<String, Pair<Long, Long>>();


    public static void main(String[] args) throws JSONException {

        //获取有效时段信息
        getPkgAndPair();
        getTheinfos();
    }

    private static void getTheinfos() {
        List<String> lines = FileUtils.readForArray("../ada/app_point.csv");

        System.out.println("getPkgAndPair lines: " + lines.size());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String[] ss = line.split(",");
            if (ss.length != 3) {
                System.err.println("单行解析失败: " + line);
                continue;
            }
            String appName = ss[0].trim();
            long modifyTime = Long.valueOf(ss[1].trim());
            String filePath = ss[2];
            if (pkgAndPair.containsKey(appName)) {
                Pair<Long, Long> p = pkgAndPair.get(appName);
                // 时间之间
                if (modifyTime > p.first && modifyTime < p.second) {
                    FileUtils.saveTextToFile("../ada/app_point_inTime.csv", line, true);

                }
            }

        }
    }

    private static void getPkgAndPair() {
        List<String> lines = FileUtils.readForArray("../ada/alive_dur.csv");
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
            long end = Long.valueOf(ss[2].trim());

            Pair<Long, Long> begingAndEnd = new Pair<Long, Long>(0L, 0L);
            if (pkgAndPair.containsKey(appName)) {
                begingAndEnd = pkgAndPair.get(appName);
            }
            if (begingAndEnd.first < begin) {
                begin = begingAndEnd.first;
            }
            if (begingAndEnd.second > end) {
                end = begingAndEnd.second;
            }
            begingAndEnd = new Pair<Long, Long>(begin, end);
            pkgAndPair.put(appName, begingAndEnd);
        }
    }

}
