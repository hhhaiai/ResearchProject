package me.sanbo.utils;

public class TextUtils {

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 处理文件路径含空格的情况
     *
     * @param text
     * @return
     */
    public static String replaceSpaceForFilePath(String text) {
        if (text.contains(" ")) {
            text = text.replaceAll(" ", "\\\\ ").trim();
        }
        return text;
    }
//
//    public static String[] split(String line, String regex, boolean isNeedWrapper) {
//        if (line.contains(regex)) {
//            return line.split(wrapper(regex, isNeedWrapper));
//        }
//        return new String[] {};
//    }
//
//    private static String wrapper(String regex, boolean isNeedWrapper) {
//        if (isNeedWrapper) {
//            return String.format("\\%s", regex);
//        } else {
//            return regex;
//        }
//    }
}
