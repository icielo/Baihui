/**
 * <p>Author		:	cielo</p>
 * <p>Date 			: 	2016 下午4:13:42</p>
 */
package com.lezic.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * 日期工具类
 * 
 * @author cielo
 *
 */
public class UtilDate {

	/** 格式：yyyy-MM-dd */
	public static final String P_YYYYMMDD = "yyyy-MM-dd";

	/** 格式：yyyyMMdd */
	public static final String P_YYYYMMDD_2 = "yyyyMMdd";

	/** 格式：yyyy-MM-dd HH:mm:ss */
	public static final String P_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 将日期对象格式化为指定的日期格式字符串
	 * 
	 * @param date
	 *            日期对象，不能为空，否则返回null
	 * @param pattern
	 *            日期字符串格式，不能为空
	 * @return 日期格式化字符串
	 */
	public static String formatDate(Date date, String pattern) {
		if (date == null || pattern == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	/**
	 * 将当前日期对象格式化为指定的日期格式字符串
	 * @param pattern
	 * @return
	 */
	public static String formatDate(String pattern) {
		return formatDate(new Date(), pattern);
	}

	public static void main(String[] args) {
		System.out.println(UUID.fromString(UUID.randomUUID().toString()));
		System.out.println(UUID.randomUUID());
	}
}
