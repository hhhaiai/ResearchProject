package me.sanbo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 日期操作相关类
 * @Version: 1.0
 * @Create: 2022-08-26 21:05:20
 * @author: sanbo
 */
public class DateUtils {

//    public static void main(String[] args) {
//        Date  begin  = new Date(2022, 5, 10);
//        Date  end=  new Date(2025, 7, 10);
//        System.out.println(differentDays(begin,end));
//    }

    public static final String getData(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(makesureMilliSeconds(timestamp)));
    }

    /**
     * 根据规则获取固定日期的格式
     *
     * @param dateFormatPattern
     * @param timestamp
     * @return
     */
    public static final String get(String dateFormatPattern, long timestamp) {
        return new SimpleDateFormat(dateFormatPattern).format(new Date(timestamp));
    }

    public static int differentDays(long min, long max) {
        return differentDays(new Date(makesureMilliSeconds(min)), new Date(makesureMilliSeconds(max)));
    }

    public static int differentDays(Date beforeDate, Date currentDate) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beforeDate);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(currentDate);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1)+1;
        } else    //不同年
        {
//            System.out.println("判断day2 - day1 : " + (day2 - day1));
            return day2 - day1+1;
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
