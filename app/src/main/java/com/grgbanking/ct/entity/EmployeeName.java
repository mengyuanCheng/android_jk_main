package com.grgbanking.ct.entity;


import com.grgbanking.ct.http.ResultInfo;

import java.io.Serializable;

public class EmployeeName extends ResultInfo implements Serializable{
		
	private String employeeName;
		
	private String rfid;

	private String employeeBank;
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

	public String getEmployeeBank() {
		return employeeBank;
	}

	public void setEmployeeBank(String employeeBank) {
		this.employeeBank = employeeBank;
	}

	@Override
	public String toString() {
		return "EmployeeName{" +
				"employeeName='" + employeeName + '\'' +
				", rfid='" + rfid + '\'' +
				", employeeBank='" + employeeBank + '\'' +
				'}';
	}
}
