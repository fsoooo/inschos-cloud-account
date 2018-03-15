package com.inschos.cloud.account.assist.kit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeKit {

    public final static long MAX_MILLIS = 2145888000000L;
    public final static long DAY_MILLIS = 86400000L;
    public final static int DAY_LENGTH = 86400;
    public final static long JET_LAG = 28800000L;
    public final static long HOUR_MILLIS = 3600000L;
    public final static String FORMAT_DEFAULT= "yyyy-MM-dd HH:mm:ss";

    /**
     * 当前时间毫秒时间戳
     *
     * @return String
     */
    public static String curTimeMillis2Str() {
        return Long.toString(currentTimeMillis());
    }

    /**
     * 当前时间毫秒时间戳
     *
     * @return long
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 年yyyy 月MM 日dd 时HH 分mm 秒ss SSS 周几几EEEE
     */
    public static String format(String format) {
        return format(format,new Date());
    }

    public static String format(Date date) {
        return format(FORMAT_DEFAULT,date);
    }
    public static String format(String format,Date date) {
        return new SimpleDateFormat(format, Locale.CHINA).format(date);
    }

    public static String format(String format, long millis) {
        return new SimpleDateFormat(format, Locale.CHINA).format(new Date(millis));
    }

    public static String format(String format, String millis) {
        if (StringKit.isEmpty(millis)) {
            return new SimpleDateFormat(format, Locale.CHINA).format(new Date());
        } else {
            return new SimpleDateFormat(format, Locale.CHINA).format(new Date(Long.valueOf(millis)));
        }
    }

    public static long parse(String date) {
        return parse(date,FORMAT_DEFAULT);
    }

    public static long parse( String date, String format) {
        try {
            if (StringKit.isEmpty(date)) {
                return 0;
            }
            return new SimpleDateFormat(format, Locale.CHINA).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date parse2Date(String date) {
        return parse2Date(date,FORMAT_DEFAULT);
    }
    public static Date parse2Date( String date, String format) {
        try {
            if (StringKit.isEmpty(date)) {
                return null;
            }
            return new SimpleDateFormat(format, Locale.CHINA).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到当天的开始时间戳
     *
     * @return
     */
    public static long getDayStartTime() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTimeInMillis();
    }

    /**
     * 得到当天的结束时间戳
     *
     * @return
     */
    public static long getDayEndTime() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 24);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTimeInMillis()-1000L;
    }

    /**
     * 得到指定的开始时间戳
     *
     * @return
     */
    public static long getDayStartTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 得到当天的结束时间戳
     *
     * @return
     */
    public static long getDayEndTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()-1000L;
    }

    /**
     * 获取当月开始时间戳
     *
     * @return
     */
    public static long getMonthStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当月开始时间戳
     *
     * @return
     */
    public static long getMonthStartTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当月结束时间戳
     *
     * @return
     */
    public static long getMonthEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1); // 将小时至0
        cal.set(Calendar.HOUR_OF_DAY, 0); // 将分钟至0
        cal.set(Calendar.MINUTE, 0); // 将秒至0
        cal.set(Calendar.SECOND, 0); // 将毫秒至0
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 1); // 将当前月加1；
        cal.add(Calendar.MILLISECOND, -1); // 在当前月的下一月基础上减去1毫秒
        return cal.getTimeInMillis();
    }

    /**
     * 获取当月结束时间戳
     *
     * @return
     */
    public static long getMonthEndTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.DAY_OF_MONTH, 1); // 将小时至0
        cal.set(Calendar.HOUR_OF_DAY, 0); // 将分钟至0
        cal.set(Calendar.MINUTE, 0); // 将秒至0
        cal.set(Calendar.SECOND, 0); // 将毫秒至0
        cal.set(Calendar.MILLISECOND, 0); // 将当前月加1；
        cal.add(Calendar.MONTH, 1); // 在当前月的下一月基础上减去1毫秒
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当年开始时间戳
     *
     * @return
     */
    public static long getYearStartTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1); // 将小时至0
        cal.set(Calendar.HOUR_OF_DAY, 0); // 将分钟至0
        cal.set(Calendar.MINUTE, 0); // 将秒至0
        cal.set(Calendar.SECOND, 0); // 将毫秒至0
        cal.set(Calendar.MILLISECOND, 0); // 将当前月加1；
        return cal.getTimeInMillis();
    }

    /**
     * 获取当周开始时间戳 <br>
     * 一周从周一 到周日
     *
     * @return
     */
    public static long getWeekStartTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        int diff = 0;
        if (week == 1) {
            week = 8;
        }
        diff = 2 - week;
        cal.add(Calendar.DATE, diff);
        return cal.getTimeInMillis();
    }

    public static long getWeekEndTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        int diff = 0;
        if (week == 1) {
            week = 8;
        }
        diff = 8 - week;
        cal.add(Calendar.DATE, diff);
        return cal.getTimeInMillis();
    }

    public static int getWeek(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);

        int week = cal.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 8;
        }
        return week-1;
    }

    /**
     *  获取指定指定日期的00:00.00的毫秒值
     *
     * @param year  年份
     * @param mouth 月份
     * @param day   日子
     * @return 指定指定日期的00:00.00的毫秒值
     */
    @SuppressWarnings("deprecation")
    public static String getMillisecondByTime(int year, int mouth, int day) {
        return String.valueOf(new Date(year - 1900, mouth - 1, day, 0, 0, 0).getTime());
    }

    /**
     * 获取当月天数
     *
     * @param source
     * @return
     */
    public static int getDayNum(String format, String source) {
        try {
            Date date = new SimpleDateFormat(format, Locale.CHINA).parse(source);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当月天数
     *
     * @param millis
     * @return
     */
    public static int getDayNum(long millis) {
        try {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(millis);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int get(int field, long millis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        return calendar.get(field);
    }

    /**
     * 在millis 基础上做时间操作 +1天 等等操作 通过field来确定纬度
     *
     * @param millis
     * @param field
     *            the calendar field.
     * @param amount
     *            the amount of date or time to be added to the field.
     * @return
     */
    public static long add(long millis, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.add(field, amount);
        return calendar.getTimeInMillis();
    }

    public static int toUTC2Unix(long millis) {
        return toUnix(millis + JET_LAG);
    }

    public static int toUnix(long millis) {
        return (int) (millis / 1000);
    }

    public static long fromUnix(int time) {
        return time * 1000L;
    }

    public static long fromUnixUTC(int time) {
        return time * 1000L - JET_LAG;
    }


}
