package me.sanbo.impl.filecache_can_use;

import me.sanbo.json.JSONArray;
import me.sanbo.json.JSONException;
import me.sanbo.json.JSONObject;
import me.sanbo.utils.FileUtils;

public class ConveryRrightRest2CSV {
    public static void main(String[] args) throws JSONException {
        String info = FileUtils.readContent("../data/all-useinfo-android_right_result-71833e14.json");
        JSONArray arr = new JSONArray(info);
        System.out.println(arr.length());
        FileUtils.saveTextToFile("../data/all-useinfo-android_right_result-71833e14.csv"
                , "app名称,app包名,版本名称,版本值,最小支持版本,目标SDK,活跃开始时间,活跃结束时间", false);
        //app名称,app包名,版本名称,版本值,最小支持版本,目标SDK,活跃开始时间,活跃结束时间
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj =arr.optJSONObject(i);
            if (obj==null||obj.length()<1){
                System.err.println("["+i+"] the obj is null!");
                continue;
            }

            String oneLin=obj.optString("appName")
                    +","+obj.optString("pkgName")
                    +","+obj.optString("versionName")
                    +","+obj.optString("versionCode")
                    +","+obj.optString("minSdk")
                    +","+obj.optString("targetSdk")
                    +","+obj.optString("begin")
                    +","+obj.optString("close")
                    ;
            FileUtils.saveTextToFile("../data/all-useinfo-android_right_result-71833e14.csv"
                    , oneLin, true);
        }
    }
}
