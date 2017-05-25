package com.grgbanking.ct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 通过<b>单例模式</b>获取DatabaseHelper的实例
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";

    private static DatabaseHelper mInstance;
    private static SQLiteDatabase readableDatabase;
    private static SQLiteDatabase writableDatabase;

    private DatabaseHelper(Context context, String DB_NAME, int DB_VERSION) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static DatabaseHelper getInstance(Context context, String DB_NAME, int DB_VERSION) {
        if (context == null || DB_NAME == null) {
            return null;
        }
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context, DB_NAME, DB_VERSION);
        }
        return mInstance;
    }

    public SQLiteDatabase getReadSQLiteDatabaseInstance() {
        if (readableDatabase == null) {
            readableDatabase = getReadableDatabase();
        }
        return readableDatabase;
    }

    public SQLiteDatabase getWriteSQLiteDatabaseInstance() {
        if (writableDatabase == null) {
            writableDatabase = getWritableDatabase();
        }
        return writableDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        createTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public synchronized Cursor queryMethod(String table, String[] columns,
                                           String selection, String[] selectionArgs, String groupBy,
                                           String having, String orderBy) {
        SQLiteDatabase readableDatabase = getReadSQLiteDatabaseInstance();
        Cursor cursor = readableDatabase.query(table, columns, selection,
                selectionArgs, groupBy, having, orderBy);
        return cursor;
        // read operation
    }

    public synchronized int updateMethod(String table, ContentValues values,
                                         String whereClause, String[] whereArgs) {
        SQLiteDatabase writableDatabase = getWriteSQLiteDatabaseInstance();
        return writableDatabase.update(table, values, whereClause, whereArgs);
    }

    public synchronized long saveMethod(String table, String nullColumnHack,
                                        ContentValues values) {
        SQLiteDatabase writableDatabase = getWriteSQLiteDatabaseInstance();
        return writableDatabase.insert(table, nullColumnHack, values);
    }

    public synchronized void execSQLMethod(String sql) {
        if (sql == null || sql.length() < 1) {
            return;
        }
        SQLiteDatabase writableDatabase = getWriteSQLiteDatabaseInstance();
        writableDatabase.execSQL(sql);
    }

    public synchronized Cursor rawQueryMethod(String sql, String[] selectionArgs) {
        if (sql == null || sql.length() < 1) {
            return null;
        }
        SQLiteDatabase writableDatabase = getWriteSQLiteDatabaseInstance();
        return writableDatabase.rawQuery(sql, selectionArgs);
    }

    public void closeSQLiteDatabase() {
        if (writableDatabase != null) {
            writableDatabase.close();
            return;
        }
        if (readableDatabase != null) {
            readableDatabase.close();
            return;
        }
    }

    /**
     * �������SQL��������
     */
    public String getCreateTableSQL(String tableName, String[] paramList) {
        if (paramList == null || paramList.length < 1) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append(" (");
        sb.append("tableid");
        sb.append(" INTEGER PRIMARY KEY");
        sb.append(",");
        int paramListLength = paramList.length;
        for (int i = 0; i < paramListLength; i++) {
            sb.append(paramList[i]);
            sb.append(" TEXT");
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append(")");
        return sb.toString();
    }

    /**
     * �жϱ��Ƿ����
     */
    public boolean tableIsExits(DatabaseHelper dh, String tableName) {
        if (dh == null || tableName == null) {
            return false;
        }
        String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = dh.rawQueryMethod(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ������ݼ�¼
     *
     * @param tableName ����
     * @return
     */
    public int getTableCount(DatabaseHelper dh, String tableName, String whereSql) {
        int count = 0;
        String sql = "select count(*) from " + tableName;
        if (whereSql != null) {
            sql = sql + " where " + whereSql;
        }
        Cursor cursor = rawQueryMethod(sql, null);
        if (cursor != null) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    count = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return count;
    }

    /**
     * ������
     */
    public void createTable(SQLiteDatabase db) {

        db.execSQL(PersonTableHelper.createTableSql);
    }


}