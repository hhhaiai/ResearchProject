package me.sanbo.impl.filecache_can_use;

import me.sanbo.json.JSONException;
import me.sanbo.json.JSONObject;
import me.sanbo.utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplacePkgName {

    private static Map<String, Integer> pkgAndIndex = new HashMap<String, Integer>();
    private static int current = 0;
    private static int thisIntext = -1;

    public static void main(String[] args) throws JSONException {

        replace("app_point");
        replace("alive_dur");
        replace("alive_dur1");
        JSONObject json = new JSONObject(pkgAndIndex);
        FileUtils.saveTextToFile("../ada/pkginfo.json", json.toString(), false);

    }

    private static void replace(String fileName) {
        List<String> lines = FileUtils.readForArray("../ada/" + fileName + ".csv");
        System.out.println(fileName + ":" + lines.size());

        for (int i = 0; i < lines.size(); i++) {
            thisIntext = -1;
            String line = lines.get(i).trim();
            String[] ss = line.split(",");
            if (ss.length != 3) {
                System.err.println("单行解析失败: " + line);
                continue;
            }
//            System.out.println(ss.length);
            if (pkgAndIndex.containsKey(ss[0])) {
                thisIntext = pkgAndIndex.get(ss[0]);
            } else {
                current += 1;
                thisIntext = current;
                pkgAndIndex.put(ss[0], thisIntext);
            }

            if (thisIntext < 1) {
                System.err.println("Happen Exception. index is -1!");
            }
            String newLine = thisIntext // app name
                    + "," + ss[1] // modify time
                    + "," + ss[2] // file path
                    ;
            FileUtils.saveTextToFile("../ada/" + fileName + "_replace.csv", newLine, true);
            thisIntext = -1;
        }
    }
}
