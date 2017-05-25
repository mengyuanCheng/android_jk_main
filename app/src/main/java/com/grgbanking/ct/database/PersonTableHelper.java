package com.grgbanking.ct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jims
 *
 */
public class PersonTableHelper {

	String user_id;
	String user_name;
	String login_name;
	String password;
	String selected;
	
	public static String[] tableParameters =  { "user_id","user_name", "login_name", "password","selected"};
	public static final String createTableSql = "CREATE TABLE  if not exists "+ContentCommon.TABLE_NAME_PERSON+" (tableid INTEGER PRIMARY KEY,user_id TEXT,user_name TEXT,login_name TEXT, password TEXT,selected TEXT)";
	
	
	public static long savePerson(Context context,Person person){
		if(context == null || person == null){
			return -1;
		}
		DatabaseHelper dh = DatabaseHelper.getInstance(context, ContentCommon.DB_NAME, ContentCommon.DB_VERSION);
		return dh.saveMethod(ContentCommon.TABLE_NAME_PERSON, null, doContentValues(person));
	}
	
	public static int updateEntity(Context context,Person person){
		if(context == null || person == null){
			return -1;
		}
		DatabaseHelper dh = DatabaseHelper.getInstance(context, ContentCommon.DB_NAME, ContentCommon.DB_VERSION);
		int update = dh.updateMethod(ContentCommon.TABLE_NAME_PERSON,  doContentValues(person), "tableid = ?", new String[]{String.valueOf(person.getTableid())});
		return update;
	}
	
	public static void delEntity(Context context, Person person){
		if (context == null || person == null) {
			return;
		}
		DatabaseHelper dh = DatabaseHelper.getInstance(context, ContentCommon.DB_NAME,ContentCommon.DB_VERSION);
		String sql = "DELETE FROM "+ContentCommon.TABLE_NAME_PERSON+" WHERE tableid ="+person.getTableid();
		dh.execSQLMethod(sql);
	}
	
	public static List<Person> queryAllPerson(Context context){
		List<Person> querylist = new ArrayList<Person>();
		DatabaseHelper dh = DatabaseHelper.getInstance(context, ContentCommon.DB_NAME, ContentCommon.DB_VERSION);
		Cursor cursor = dh.queryMethod(ContentCommon.TABLE_NAME_PERSON, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				querylist.add(doCurSorToEntity(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return querylist;
	}
	
	public static Person queryEntity(Context context){
		DatabaseHelper dh = DatabaseHelper.getInstance(context, ContentCommon.DB_NAME, ContentCommon.DB_VERSION);
		Cursor cursor = dh.queryMethod(ContentCommon.TABLE_NAME_PERSON, null, null, null, null, null, null);
		Person entity;
		if (cursor != null && cursor.moveToFirst()) {
			do {
				entity = doCurSorToEntity(cursor);
			} while (cursor.moveToNext());
		}else{
			entity = null;
		}
		cursor.close();
		return entity;
	}
	
	protected static Person doCurSorToEntity(Cursor cursor){
		int count = 0;
		Person person = new Person();
		person.setTableid(cursor.getInt(count ++));
		person.setUser_id(cursor.getString(count ++));
		person.setUser_name(cursor.getString(count ++));
		person.setLogin_name(cursor.getString(count ++));
		person.setPassword(cursor.getString(count ++));
		person.setSelected(cursor.getString(count ++));
		return person;
	}
	
	protected static ContentValues doContentValues(Person person){
		ContentValues cv = new ContentValues();
		cv.put("user_id", person.getUser_id());
		cv.put("user_name", person.getUser_name());
		cv.put("login_name", person.getLogin_name());
		cv.put("password", person.getPassword());
		cv.put("selected", person.getSelected());
		return cv;
	}
}

