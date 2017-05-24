package com.grgbanking.ct.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class PdaNetPersonInfo {
	private String netPersonId;
	private String netPersonName;
	private String netPersonRFID;
	public String getNetPersonId() {
		return netPersonId;
	}
	public void setNetPersonId(String netPersonId) {
		this.netPersonId = netPersonId;
	}
	public String getNetPersonName() {
		return netPersonName;
	}
	public void setNetPersonName(String netPersonName) {
		this.netPersonName = netPersonName;
	}
	public String getNetPersonRFID() {
		return netPersonRFID;
	}
	public void setNetPersonRFID(String netPersonRFID) {
		this.netPersonRFID = netPersonRFID;
	}
	
	public static List<PdaNetPersonInfo> JSONArraytoPdaNetPersonInfo (JSONArray jsonArray) {
		List<PdaNetPersonInfo> list = null;
		
		if (jsonArray != null && jsonArray.length() > 0) {
			list = new ArrayList<PdaNetPersonInfo>();
			
			for (int i = 0; i < jsonArray.length(); i++) {
				PdaNetPersonInfo info = new PdaNetPersonInfo();
				try {
					info.setNetPersonId((String)jsonArray.getJSONObject(i).get("netPersonId"));
					info.setNetPersonName((String)jsonArray.getJSONObject(i).get("netPersonName"));
					info.setNetPersonRFID((String)jsonArray.getJSONObject(i).get("netPersonRFID"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				list.add(info);
			}
		}
		
		return list;
	}
}
