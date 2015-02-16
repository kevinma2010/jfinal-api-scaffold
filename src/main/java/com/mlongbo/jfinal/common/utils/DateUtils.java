package com.mlongbo.jfinal.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  时间工具
 * @author malongbo
 */
public final class DateUtils {
    /**
     *  获得当前时间
     *  格式为：yyyy-MM-dd HH:mm:ss
    */
    public static String getNowTime() {
        Date nowday = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
        String time = sdf.format(nowday);
        return time;
    }

    /**
     * 获取当前系统时间戳
     * @return
     */
    public static Long getNowTimeStamp() {
        return System.currentTimeMillis();
    }

    public static Long getNowDateTime() {
        return new Date().getTime()/1000;
//        return new Date().getTime()/1000;
    }

    /**
     * 自定义日期格式
     * @param format
     * @return
     */
    public static String getNowTime(String format) {
        Date nowday = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);// 精确到秒
        String time = sdf.format(nowday);
        return time;
    }

    /**
     * 将时间字符转成Unix时间戳
     * @param timeStr
     * @return
     * @throws java.text.ParseException
     */
    public static Long getTime(String timeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
        Date date = sdf.parse(timeStr);
        return date.getTime()/1000;
    }

    /**
     * 将Unix时间戳转成时间字符
     * @param timestamp
     * @return
     */
    public static String getTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
        Date date = new Date(timestamp*1000);
        return sdf.format(date);
    }

    /**
     * 获取半年后的时间
     * 时间字符格式为：yyyy-MM-dd HH:mm:ss
     * @return 时间字符串
     */
    public static String getHalfYearLaterTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒

        Calendar calendar = Calendar.getInstance();
        int currMonth = calendar.get(Calendar.MONTH) + 1;

        if (currMonth >= 1 && currMonth <= 6) {
            calendar.add(Calendar.MONTH, 6);
        } else {
            calendar.add(Calendar.YEAR, 1);
            calendar.set(Calendar.MONTH, currMonth - 6 - 1);
        }


        return sdf.format(calendar.getTime());
    }
}
