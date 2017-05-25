package com.grgbanking.ct.utils;

import java.util.List;

/**
 * 字符串工具类，提供字符串处理相关的常用方法
 * @author zzhi
 *
 */
public class StringTools {
	

	
	/**
	 * 将字符串首字母转换成大写字母
	 * @param str 指定的字符串
	 * @return
	 */
	public static String upperFirstCharacter(String str){
		if(StringTools.isEmpty(str)){
			return str;
		}
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return String.valueOf(chars);
	}
	
	/**
	 * 将字符串首字母转换成小写字母
	 * @param str 指定的字符串
	 * @return
	 */
	public static String lowerFirstCharacter(String str){
		if(StringTools.isEmpty(str)){
			return str;
		}
		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return String.valueOf(chars);
	}
	
	/**
	 * 判断指定的字符串是否为null或者空值""，空格也算空值
	 * @param str 指定字符串
	 * @return
	 */
	public static boolean isEmpty(String str){
		return str==null||str.trim().equals("");
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	


	public static String[] repeatString(String str, int times) {
		String[] result=new String[times];
		for (int i = 0; i < times; i++) {
			result[i]=str;
		}
		return result;
	}
	
	public static long getRandom(int size) {
		Double value = (Math.random() * Math.pow(10, size));
		return value.longValue();
	}
}
