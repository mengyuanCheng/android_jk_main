package com.grgbanking.ct.cach;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaLoginMessage;

public class DataCach {

	public static LoginUser loginUser = new LoginUser();
	
	public static String netType = "";
	
	public static LinkedHashMap<String, HashMap<String, Object>> taskMap = new LinkedHashMap<String, HashMap<String, Object>>();
	private static PdaLoginMessage pdaLoginMessage = null;
	public static LinkedHashMap<String, HashMap<String, Object>> boxesMap = new LinkedHashMap<String, HashMap<String, Object>>();
	
	public static void setPdaLoginMessage (PdaLoginMessage pdaLoginMessage) {
		if (DataCach.pdaLoginMessage != null) {
			DataCach.pdaLoginMessage = null;
		}
		DataCach.pdaLoginMessage = pdaLoginMessage;
	}
	
	public static PdaLoginMessage getPdaLoginMessage () {
		return DataCach.pdaLoginMessage;
	}
	
	public static void clearAllDataCach () {
		netType = "";
		taskMap = null;
		taskMap = new LinkedHashMap<String, HashMap<String, Object>>();
		pdaLoginMessage = null;
		boxesMap = null;
		boxesMap = new LinkedHashMap<String, HashMap<String, Object>>();
	}
}
