package com.test.demo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author wanjun
 *
 */
public class DateUtil {

	/**
	 * 将Date类型转换为字符串
	 *
	 * @param date
	 *            日期类型
	 * @return 日期字符串
	 */
	public static String format(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将Date类型转换为字符串
	 *
	 * @param date
	 *            日期类型
	 * @param pattern
	 *            字符串格式
	 * @return 日期字符串
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "null";
		}
		if (pattern == null || pattern.equals("") || pattern.equals("null")) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 将字符串转换为Date类型
	 *
	 * @param date
	 *            字符串类型
	 * @return 日期类型
	 */
	public static Date format(String date) {
		return format(date, null);
	}

	/**
	 * 将字符串转换为Date类型
	 *
	 * @param date
	 *            字符串类型
	 * @param pattern
	 *            格式
	 * @return 日期类型
	 */
	public static Date format(String date, String pattern) {
		if (pattern == null || pattern.equals("") || pattern.equals("null")) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		if (date == null || date.equals("") || date.equals("null")) {
			return new Date();
		}
		Date d = null;
		try {
			d = new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException pe) {
		}
		return d;
	}

	public static Long formatDateTOLong(Date date) {

		return date.getTime();
	}

	public static String longToDate(Long timeInMillis){
		String date = "";
        if (timeInMillis != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = dateFormat.format(calendar.getTime());
        }
        return date;
	}

    public static String long2Date(Long timeInMillis){
		String date = "";
        if (timeInMillis != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
            date = dateFormat.format(calendar.getTime());
        }
        return date;
	}

    public static Date formatLongToDate(Long timeInMillis){
        String d = longToDate(timeInMillis);
        return format(d);
	}

    public static int getDayOfMonth(Long timeInMillis){
        Calendar calendar = Calendar.getInstance();
        if(timeInMillis != null){
            calendar.setTimeInMillis(timeInMillis);
        }
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHourOfDay(Long timeInMillis){
        Calendar calendar = Calendar.getInstance();
        if(timeInMillis != null){
            calendar.setTimeInMillis(timeInMillis);
        }
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d = new Date();
        System.out.println(formatDateTOLong(d));
    }

    public static String getNow() {
        return longToDate(System.currentTimeMillis());
    }
}
