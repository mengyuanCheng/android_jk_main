package com.grgbanking.ct.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

public class PdaCashboxInfo {
	private String rfidNum;
	private String boxSn;
	public String getRfidNum() {
		return rfidNum;
	}
	public void setRfidNum(String rfidNum) {
		this.rfidNum = rfidNum;
	}
	public String getBoxSn() {
		return boxSn;
	}
	public void setBoxSn(String boxSn) {
		this.boxSn = boxSn;
	}
	public static List<PdaCashboxInfo> JSONArraytoPdaNetInfo(
			JSONArray jsonArray) {
		List<PdaCashboxInfo> list = null;
		
		if (jsonArray != null && jsonArray.length() > 0) {
			list = new ArrayList<PdaCashboxInfo>();
			
			for (int i = 0; i < jsonArray.length(); i++) {
				PdaCashboxInfo info = new PdaCashboxInfo();
				try {
					info.setRfidNum((String)jsonArray.getJSONObject(i).get("rfidNum"));
					info.setBoxSn((String)jsonArray.getJSONObject(i).get("boxSn"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				list.add(info);
			}
		}
		
		return list;
	}
	
	public static Map<String, String> JSONArraytoPdaNetInfoMap(JSONArray jsonArray) {
		Map<String, String> map = new HashMap<String, String>();
		
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					map.put((String)jsonArray.getJSONObject(i).get("rfidNum"), (String)jsonArray.getJSONObject(i).get("boxSn"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return map;
	}
}
