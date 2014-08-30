package com.jmie.fieldplay.storage;
import java.util.ArrayList;

import com.jmie.fieldplay.route.RouteData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class RouteDBHandler extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "routeDB.db";
	public static final String TABLE_ROUTES = "routes";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ROUTENAME = "routename";
	public static final String COLUMN_DESCRIPTION = "routedescription";
	public static final String COLUMN_ROUTEFILE = "routefile";
	public static final String COLUMN_DOWNLOADPROGRESS = "downloadprogress";
	public static final String COLUMN_UNZIPPROGRESS = "unzipprogress";
	public static final String COLUMN_MANAGERID = "managerid";
	public String TAG = "RouteDB Handler";
	
	public RouteDBHandler(Context context, String name, CursorFactory factory, int version){
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	public RouteDBHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ROUTES_TABLE = "CREATE TABLE " +
	             TABLE_ROUTES + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," 
	             + COLUMN_ROUTENAME + " TEXT," 
	             + COLUMN_DESCRIPTION + " TEXT," 
	             + COLUMN_ROUTEFILE + " TEXT,"
	             + COLUMN_DOWNLOADPROGRESS + " INTEGER, "
	             + COLUMN_MANAGERID + " INTEGER, " 
	             + COLUMN_UNZIPPROGRESS + " INTEGER" + ")";
	      db.execSQL(CREATE_ROUTES_TABLE);
	      
		
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
		
		
		routeData.set_id((int)db.insert(TABLE_ROUTES, null, values));
		
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

	private RouteData findRoute(int routeID) {

		RouteData routeData = new RouteData();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_ROUTES, new String[] { COLUMN_ID,
	            COLUMN_ROUTENAME, COLUMN_DESCRIPTION, COLUMN_ROUTEFILE, COLUMN_DOWNLOADPROGRESS,
	            COLUMN_UNZIPPROGRESS, COLUMN_MANAGERID}, COLUMN_ID + "=?",
	            new String[] { String.valueOf(routeID) }, null, null, null, null);
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
		return routeData;
	}
	public RouteData refreshData(RouteData routeData){
		return findRoute(routeData.get_id());
	}

	public boolean deleteRoute(RouteData routeData){
		boolean result = false;
		SQLiteDatabase db = this.getWritableDatabase();
		
			db.delete(TABLE_ROUTES, COLUMN_ID + " = ?",
		            new String[] { String.valueOf(routeData.get_id()) });
			result = true;
	        db.close();
		return result;
	}
	public int numberOfRows(){
		 SQLiteDatabase db = this.getReadableDatabase();
		 int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_ROUTES);
		 return numRows;
	}
	public ArrayList<RouteData> getAllRoutes(){
	      ArrayList<RouteData> array_list = new ArrayList<RouteData>();
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from " +TABLE_ROUTES, null );
	      res.moveToFirst();
	      while(res.isAfterLast() == false){
	    	  RouteData routeData = new RouteData();
				routeData.set_id(Integer.parseInt(res.getString(0)));
				routeData.set_routeName(res.getString(1));
				routeData.set_routeDescription(res.getString(2));
				routeData.set_routeFile(res.getString(3));
				routeData.set_downloadProgress(Integer.parseInt(res.getString(4)));
				routeData.set_managerID(Integer.parseInt(res.getString(5)));
				routeData.set_unzipProgress(Integer.parseInt(res.getString(6)));
				array_list.add(routeData);
				res.moveToNext();
	      }
	      res.close();
	      Log.d(TAG, "Retrieved list of size: " + array_list.size());
	   return array_list;
	}
}
