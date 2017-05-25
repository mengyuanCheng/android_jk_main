package com.grgbanking.ct.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * Created by cmy on 2016/9/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="datainfo.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("TAG","onCreate被调用了");
        //创建表
        String sql = "CREATE TABLE IF NOT EXISTS person"+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age INTEGER, info TEXT)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
