package me.sanbo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MDate {

    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static String getTime(long time) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
    }

    public static final String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    }

    public static final String getNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(System.currentTimeMillis()));
    }

    public static final int getHour() {
        return Integer.valueOf(
                new SimpleDateFormat("HH").format(new Date(System.currentTimeMillis())));
    }

    /**
     * 毫秒转换 HH:mm:ss:SSS
     *
     * @param time
     * @return
     */
    public static String convertLongTimeToHmsForUsed(long time) {
        long hours = time / (3600000);
        long minutes = (time % 3600000) / 60000;
        long seconds = (time % 60000) / 1000;
        long ms = time % 60000;
        return String.format("%02d:%02d:%02d %03d", hours, minutes, seconds, ms);
    }

    public static final String getDateFromTimestamp(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 时区设置8正常，如设置0会导致误差几个小时
        //        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return formatter.format(timestamp);
    }

    public static final String get(String pattern, long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        // 时区设置8正常，如设置0会导致误差几个小时
        //        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return formatter.format(timestamp);
    }

    public static final String get(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        // 时区设置8正常，如设置0会导致误差几个小时
        //        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return formatter.format(System.currentTimeMillis());
    }

    public static final String getYmdFromTimestamp(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(timestamp);
    }

    public static final int getHourFromTimestamp(long timestamp) {
        return Integer.valueOf(new SimpleDateFormat("HH").format(timestamp));
    }

    public static final int getDaysFromTimestamp(long timestamp) {
        return Integer.valueOf(new SimpleDateFormat("dd").format(timestamp));
    }

    public static final boolean isToday(long timestamp) {
        return getDaysFromTimestamp(timestamp) == getDaysFromTimestamp(System.currentTimeMillis());
    }

    /**
     * 未来时间戳判断
     *
     * @param timestamp
     * @return true: 未来的时间
     * false: 发生过的时间
     */
    public static final boolean isFromFuture(long timestamp) {
        return System.currentTimeMillis() < timestamp;
    }

    /**
     * 在某个时间内
     *
     * @param timestamp  需要判断的时间戳
     * @param durationMs 特定时间间隔，单位毫秒
     * @return true 在特定时间内
     * false 不在特定时间内
     */
    public static final boolean isWithinCertainPeriod(long timestamp, long durationMs) {
        return Math.abs(System.currentTimeMillis() - timestamp) <= durationMs;
    }

    public static final long getDuration(long timeA, long timeB) {
        return Math.abs(timeB - timeA);
    }

    public static int getIntToday() {
        String todafy =
                new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));
        return Integer.parseInt(todafy);
    }
}
