package com.grgbanking.ct.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 简单 日志操作工器
 * @author Administrator
 *
 */
public class LogUtils {
	final static String TAG = "GBG_LOG";
	static boolean PRING_LOG_STATE = true;

	public static void printLog(String log) {
		if (!log.equals(null) && PRING_LOG_STATE) {
			Log.v(TAG, log);
		}
	}

	public static void toastLog(Context context, String log) {
		if (context != null && !log.equals(null)) {
			Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
		}
	}
}
