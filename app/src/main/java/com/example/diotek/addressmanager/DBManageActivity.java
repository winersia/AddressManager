package com.example.diotek.addressmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by diotek on 2015-07-20.
 */
public class DBManageActivity extends SQLiteOpenHelper implements BaseVariables{

    Context mContext = null;

    private static DBManageActivity mDBManageActivity = null;

    //private�̹Ƿ� getInstance�Լ��� �̟G�Ͽ� ��ü�� �޾ƿ´�.
    //�̱������� ����
    public static DBManageActivity getInstance(Context context) {

        //DB�� ���� ��� ������ �� return
        if(mDBManageActivity == null) {
            mDBManageActivity = new DBManageActivity(context, DB_ADDRESS, null, DB_VERSION);
        }
        //�̹� �����ϴ� ��� ������ ���� return
        return mDBManageActivity;
    }

    //������
    private DBManageActivity(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, dbName, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //���̺� ����
        try {//try catch���� ������� ������ ���� ������� �ʴ´�.
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESS +
                    "(" + "_index INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "phone_number TEXT, " +
                    "home_number TEXT, " +
                    "company_number TEXT, "+
                    "e_mail TEXT ); ");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //������ ���̺��� �����ϰ� ���ο� ���̺��� �����Ѵ�.
        if( oldVersion < newVersion ){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
            onCreate(db);
        }
    }

    //db������ ��ȯ
    public SQLiteDatabase returnDatabase() {
        return getReadableDatabase();
    }

    //������ ����
    public long insert(ContentValues addRowValue) {
        return getWritableDatabase().insert(TABLE_ADDRESS, null, addRowValue);
    }

    //������ ����
    public int update(ContentValues addRowValue,String whereClause, String[] whereArgs ) {
        return getWritableDatabase().update(TABLE_ADDRESS, addRowValue, whereClause, whereArgs);
    }

    //������ ����
    public int delete(String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(TABLE_ADDRESS, whereClause, whereArgs);
    }

    //������ ����
    public Cursor query( String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return getReadableDatabase().query(TABLE_ADDRESS, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
}

