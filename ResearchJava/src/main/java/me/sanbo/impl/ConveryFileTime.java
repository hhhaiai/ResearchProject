package me.sanbo.impl;

import me.sanbo.json.JSONException;
import me.sanbo.utils.FileUtils;

import java.util.List;

/**
 * 单位转换. 文件源文件时间是秒，统一转为毫秒
 */
public class
ConveryFileTime {
    public static void main(String[] args) throws JSONException {
        List<String> lines = FileUtils.readForArray("../data/result.csv");
        System.out.println(lines.size());
        String line0 = lines.get(0);
        FileUtils.saveTextToFile("../data/result-newtime.csv", line0, false);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] ss = line.split(",");
//            System.out.println(ss.length);
            if (ss.length != 10) {
                //暂时看58有一行异常.丢弃处理
                //58同城,com.wuba,10.22.6,102206,21,28,1655127002,1655127002,1655127002,/sdcard/Android/data/com.wuba/cache/wuba/house/categoryCache/json_https:/houserentapp.58.com/house/Api_get_tab_config_zufang_{"placeholder":"兼容","list_from":"zufangiconfrom58b"}_bj_10.22.6
                System.err.println("[" + ss.length + "]this line is has Error! line:" + line);
                continue;
            }
            //app名称,,,,,,,,,
            String newLine = ss[0] // app名称
                    + "," + ss[1] // app包名
                    + "," + ss[2] // 版本名称
                    + "," + ss[3] // 版本值
                    + "," + ss[4] // 最小支持版本
                    + "," + ss[5] // 目标SDK
                    + "," + makesureMilliSeconds(Long.valueOf(ss[6].trim())) // access时间
                    + "," + makesureMilliSeconds(Long.valueOf(ss[7].trim()))  // 修改时间
                    + "," + makesureMilliSeconds(Long.valueOf(ss[8].trim()))  // 创建时间
                    + "," + ss[9] // 路径
                    ;
            FileUtils.saveTextToFile("../data/result-newtime.csv", newLine, true);
        }
    }


    /**
     * 确保参数为毫秒。即13位
     *
     * @param time
     * @return
     */
    public static long makesureMilliSeconds(long time) {
        return makeAfterZeroTimeLen(time, 13);
    }

    /**
     *  后补0确保数字的个数。一般用于时间的转换，秒转毫秒
     * @param num  要处理的数字
     * @param targetLen 目标长度
     * @return
     */
    public static long makeAfterZeroTimeLen(long num, int targetLen) {
        String tts = String.valueOf(num);
        int len = tts.length();
        if (len < targetLen) {
            // 如转换为毫秒，该部分逻辑如下：
            // 小于13位,则代表差N位,方法补充N个0，等价于: now * 10的N次方
            return num * ((long) Math.pow(10, targetLen - len));
        } else if (len > targetLen) {
            return Long.valueOf(tts.substring(0, targetLen));
        }
        return num;
    }

    /**
     * 工具方法。获取时间位数
     *
     * @param time
     * @return
     */
    public static int sizeof(long time) {
        return String.valueOf(time).length();
    }

}
