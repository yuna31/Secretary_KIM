package com.example.home.secretary_kim;

/**
 * Created by s0woo on 2018-09-10.
 */

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUserORSecretary extends SQLiteOpenHelper {
    public DBUserORSecretary(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists SelectRole("
                + "num integer, "
                + "role text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists SelectRole";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(int num, String role) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("num", num);
        values.put("role", role);
        db.insert("SelectRole", null, values);
        db.close();
    }

    public void update(int num, String role) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("num", num);
        values.put("role", role);
        db.update("SelectRole", values, "num="+num, null);

        db.close();
    }

    /*
    public void delete(int num) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("SelectRole",  "num="+num, null);
        db.close();
    }
    */

    public String getResult() {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM SelectRole", null);

        while (cursor.moveToNext()) {
            result = cursor.getString(1);
        }

        return result;
    }
}
