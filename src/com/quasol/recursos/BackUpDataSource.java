package com.quasol.recursos;

import org.json.JSONArray;
import org.json.JSONObject;
import com.quasol.recursos.MySQLiteOpenHelper.TableBackUp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BackUpDataSource {
	private SQLiteDatabase db;
	private MySQLiteOpenHelper dbHelper;
	private String[] columnas = { TableBackUp.COLUMN_ID,TableBackUp.COLUMN_SHEET_ROUTE,TableBackUp.COLUMN_DATETIME,TableBackUp.COLUMN_EVENT,TableBackUp.COLUMN_JSON};

	public BackUpDataSource(Context context) {
		dbHelper = MySQLiteOpenHelper.getInstance(context);
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean CreateRoute(String sheet,String date_time, String event, String json){
		ContentValues values = new ContentValues();
		values.put(TableBackUp.COLUMN_SHEET_ROUTE, sheet);
		values.put(TableBackUp.COLUMN_DATETIME, date_time);
		values.put(TableBackUp.COLUMN_EVENT, event);
		values.put(TableBackUp.COLUMN_JSON, json);
		long recive=db.insert(TableBackUp.TABLE_BACKUP, null, values);
		if(recive == -1) return false;
		return true;
	}

	public JSONArray getAllRoutes() {
		JSONArray JSONRoutes = new JSONArray();
		try {
			Cursor cursor = db.query(TableBackUp.TABLE_BACKUP, columnas, null, null,null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				JSONObject auxRoute=new JSONObject();
				auxRoute.put("id_route", cursor.getInt(0));
				auxRoute.put("sheet_route", cursor.getString(1));
				auxRoute.put("date_time", cursor.getString(2));
				auxRoute.put("event", cursor.getString(3));
				auxRoute.put("info_json", cursor.getString(4));
				JSONRoutes.put(auxRoute);
				cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return JSONRoutes;
	}

	public void deleteRoutes() {
		dbHelper.cleanRoutes(db);
	}
}
