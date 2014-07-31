package com.quasol.recursos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static MySQLiteOpenHelper mOpenHelper = null;
	
	private static final String DATABASE_NAME = "BackUp";
	private static final int DATABASE_VERSION = 1;
	
	public static class TableBackUp{
		public static String TABLE_BACKUP = "backup";
		public static String COLUMN_ID = "id";
		public static String COLUMN_SHEET_ROUTE = "id_sheet";
		public static String COLUMN_DATETIME = "date_time";
		public static String COLUMN_EVENT = "event";
		public static String COLUMN_JSON = "info_json";
	}
	
	
	private static final String DATABASE_CREATE = "create table "
			+ TableBackUp.TABLE_BACKUP + "(" + TableBackUp.COLUMN_ID
			+ " integer primary key autoincrement, " + TableBackUp.COLUMN_SHEET_ROUTE
			+ " text not null, " + TableBackUp.COLUMN_DATETIME
			+ " text not null, " + TableBackUp.COLUMN_EVENT
			+ " text not null, " + TableBackUp.COLUMN_JSON
			+ " text not null);";
	
	public static MySQLiteOpenHelper getInstance(Context context){
		if (mOpenHelper == null){
			mOpenHelper = new MySQLiteOpenHelper(context.getApplicationContext());
		}
		return mOpenHelper;
	}
	
	private MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("delete table if exists " + TableBackUp.TABLE_BACKUP);
		onCreate(db);
	}
	
	public void cleanRoutes(SQLiteDatabase db){
		db.delete(TableBackUp.TABLE_BACKUP, null, null);
	}

}
