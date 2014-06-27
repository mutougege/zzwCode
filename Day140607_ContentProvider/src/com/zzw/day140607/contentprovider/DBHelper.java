package com.zzw.day140607.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, StuInfos.DBNAME, null, StuInfos.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table " + StuInfos.TNAME + "(" 
				+ StuInfos.ID + " integer primary key autoincrement not null," 
				+ StuInfos.USERNAME + " text not null,"
				+ StuInfos.DATE + " interger not null," 
				+ StuInfos.SEX + " text not null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		String sql="drop table if exits "+StuInfos.TNAME;
        db.execSQL(sql);
        
        db.execSQL("create table " + StuInfos.TNAME + "(" 
				+ StuInfos.ID + " integer primary key autoincrement not null," 
				+ StuInfos.USERNAME + " text not null,"
				+ StuInfos.DATE + " interger not null," 
				+ StuInfos.SEX + " text not null);");
	}

	
	public void add(String username, String date, String sex) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(StuInfos.USERNAME, username);
		values.put(StuInfos.DATE, date);
		values.put(StuInfos.SEX, sex);
		
		db.insert(StuInfos.TNAME, "", values);
	}
}