package me.sanbo.utils;

import java.text.NumberFormat;

/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 比例计算
 * @Version: 1.0
 * @Create: 2022-08-26 20:43:59
 * @author: sanbo
 */
public class Bilo {

    public static String ccc(double divisor, double dividend) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        return numberFormat.format(divisor / dividend * 100) + "%";
    }

    public static double cccD(double divisor, double dividend) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        return Double.valueOf(numberFormat.format(divisor / dividend * 100)) ;
    }

}
