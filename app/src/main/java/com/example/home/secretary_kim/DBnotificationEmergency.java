package com.example.home.secretary_kim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBnotificationEmergency extends SQLiteOpenHelper {
    public DBnotificationEmergency(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists EmerNotiOnOff("
                + "id integer, "
                + "OnOff text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists EmerNotiOnOff";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(int id, String OnOff) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("OnOff", OnOff); // 1 is On, 0 is OFF
        db.insert("EmerNotiOnOff", null, values);
        db.close();
    }


    public void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("EmerNotiOnOff",  "id="+id, null);
        db.close();
    }


    public void update(int id, String OnOff) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("id", id);
        values.put("OnOff", OnOff); // 1 is On, 0 is OFF
        db.update("EmerNotiOnOff", values, "id="+id, null);
        db.close();
    }

    public String getResult() {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM EmerNotiOnOff", null);

        while (cursor.moveToNext()) {
            result = cursor.getString(1);
        }

        return result;
    }
}
