package com.grgbanking.ct.entity;


import com.grgbanking.ct.http.ResultInfo;

import java.io.Serializable;

public class EmployeeName extends ResultInfo {
		
	private String employeeName;
		
	private String rfid;
    public static final String CODE_REN="0";

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}



}
