package com.grgbanking.ct.database;

import java.io.Serializable;

/**
 * �豸��
 * 
 * @author Jims
 * 
 */
public class Person implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Person() {

	}

	/** ��ID ϵͳĬ������ֵ */
	int tableid;
	String user_id;
	String user_name;
	String login_name;
	String password;
	String selected;

	public int getTableid() {
		return tableid;
	}

	public void setTableid(int tableid) {
		this.tableid = tableid;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return user_id + "/" + user_name + "/" + user_name + "/" + password
				+ "/" + selected;
	}
}
