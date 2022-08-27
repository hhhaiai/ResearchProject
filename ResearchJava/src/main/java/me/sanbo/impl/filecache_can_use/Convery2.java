package me.sanbo.impl.filecache_can_use;

import me.sanbo.json.JSONArray;
import me.sanbo.json.JSONException;
import me.sanbo.json.JSONObject;
import me.sanbo.utils.FileUtils;

public class Convery2 {
    public static void main(String[] args) throws JSONException {
        String info = FileUtils.readContent("../data/right.json");
        JSONArray arr = new JSONArray(info);
        System.out.println(arr.length());
        FileUtils.saveTextToFile("../data/right.csv"
                , "app名称,活跃开始时间,活跃结束时间", false);
        //app名称,app包名,版本名称,版本值,最小支持版本,目标SDK,活跃开始时间,活跃结束时间
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj =arr.optJSONObject(i);
            if (obj==null||obj.length()<1){
                System.err.println("["+i+"] the obj is null!");
                continue;
            }

            String oneLin=obj.optString("AN")
                    +","+obj.optString("AOT")
                    +","+obj.optString("ACT")
                    ;
            FileUtils.saveTextToFile("../data/right.csv"
                    , oneLin, true);
        }
    }
}
