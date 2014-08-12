package com.jmie.fieldplay.storage;
import com.jmie.fieldplay.route.RouteData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
public class RouteDBHandler extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "routeDB.db";
	public static final String TABLE_ROUTES = "routes";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ROUTENAME = "routename";
	public static final String COLUMN_DESCRIPTION = "routedescription";
	public static final String COLUMN_ROUTEFILE = "routefile";
	public static final String COLUMN_DOWNLOADPROGRESS = "downloadprogress";
	public static final String COLUMN_UNZIPPROGRESS = "unzipprogress";
	public static final String COLUMN_MANAGERID = "managerid";
	
	public RouteDBHandler(Context context, String name, CursorFactory factory, int version){
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
	             TABLE_ROUTES + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," 
	             + COLUMN_ROUTENAME + " TEXT," 
	             + COLUMN_DESCRIPTION + " TEXT," 
	             + COLUMN_ROUTEFILE + " TEXT,"
	             + COLUMN_DOWNLOADPROGRESS + " INTEGER"
	             + COLUMN_MANAGERID + " INTEGER" 
	             + COLUMN_UNZIPPROGRESS + " INTEGER" + ")";
	      db.execSQL(CREATE_PRODUCTS_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
	      onCreate(db);
		
	}
	public void addRoute (RouteData routeData){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ROUTENAME,  routeData.get_routeName());
		values.put(COLUMN_DESCRIPTION, routeData.get_routeDescription());
		values.put(COLUMN_ROUTEFILE, routeData.get_routeFile());
		values.put(COLUMN_DOWNLOADPROGRESS, routeData.get_downloadProgress());
		values.put(COLUMN_MANAGERID, routeData.get_managerID());
		values.put(COLUMN_UNZIPPROGRESS, routeData.get_unzipProgress());
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.insert(TABLE_ROUTES, null, values);
		
		db.close();
	}
	public int updateRoute(RouteData routeData){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ROUTENAME,  routeData.get_routeName());
		values.put(COLUMN_DESCRIPTION, routeData.get_routeDescription());
		values.put(COLUMN_ROUTEFILE, routeData.get_routeFile());
		values.put(COLUMN_DOWNLOADPROGRESS, routeData.get_downloadProgress());
		values.put(COLUMN_MANAGERID, routeData.get_managerID());
		values.put(COLUMN_UNZIPPROGRESS, routeData.get_unzipProgress());
		
		SQLiteDatabase db = this.getWritableDatabase();
		int i = db.update(TABLE_ROUTES, values, COLUMN_ID+" = ?", new String[] { String.valueOf(routeData.get_id()) });
		db.close();
		return i;
	}
	public RouteData findRoute(String routeName) {
		String query = "Select * FROM " + TABLE_ROUTES + " WHERE " + COLUMN_ROUTENAME + " =  \"" + routeName + "\"";
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		RouteData routeData = new RouteData();
		
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			routeData.set_id(Integer.parseInt(cursor.getString(0)));
			routeData.set_routeName(cursor.getString(1));
			routeData.set_routeDescription(cursor.getString(2));
			routeData.set_routeFile(cursor.getString(3));
			routeData.set_downloadProgress(Integer.parseInt(cursor.getString(4)));
			routeData.set_managerID(Integer.parseInt(cursor.getString(5)));
			routeData.set_unzipProgress(Integer.parseInt(cursor.getString(6)));

			cursor.close();
		} else {
			routeData = null;
		}
	        db.close();
		return routeData;
	}
	
	public boolean deleteRoute(String routeName) {
		
		boolean result = false;
		
		String query = "Select * FROM " + TABLE_ROUTES + " WHERE " + COLUMN_ROUTENAME + " =  \"" + routeName + "\"";

		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		RouteData routeData = new RouteData();
		
		if (cursor.moveToFirst()) {
			routeData.set_id(Integer.parseInt(cursor.getString(0)));
			db.delete(TABLE_ROUTES, COLUMN_ID + " = ?",
		            new String[] { String.valueOf(routeData.get_id()) });
			cursor.close();
			result = true;
		}
	        db.close();
		return result;
	}
}
