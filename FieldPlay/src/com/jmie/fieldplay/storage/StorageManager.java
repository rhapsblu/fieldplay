package com.jmie.fieldplay.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


import org.xmlpull.v1.XmlPullParserException;



import android.content.Context;
import android.content.SharedPreferences;

import android.os.Environment;
import android.util.Log;



import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.RouteData;


public class StorageManager {
	static final String TAG = "Storage Manager";
	public static final String PREFS_NAME = "FPPrefsFile";
	public static final String ROUTES_DIR= "routes/";
	public static final String IMAGES_DIR= "images/";
	public static final String AUDIO_DIR="audio/";
	public static final String VIDEO_DIR="video/";
	public static final String LAYERS_DIR="layers/";
	public static final String ROUTE_XML="route.xml";
	public static final String ROUTE_NAMES="routenames";
	public static final String ROUTE_CACHE="route_cache";


	public static Route buildRoute(Context c, RouteData routeData){
		Log.d(TAG, "Building route " + routeData.get_routeName());
		Route route = null;
		String routeXMLPath = ROUTES_DIR + routeData.get_routeFile()+"/"+ROUTE_XML;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			//Log.d(TAG, "Media Mounted");
			InputStream inputStream;
			try {
				//Log.d(TAG, "Accessing " + routeXMLPath);
				File inputFile = c.getExternalFilesDir(routeXMLPath);
				inputStream = new FileInputStream(inputFile);
	            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
	            //Log.d(TAG, "Starting parse of " + inputFile.getName());
	            XMLManager xmlManager = new XMLManager();
	            route = xmlManager.parse(bufferedInputStream);
	            bufferedInputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
		route.setRouteData(routeData);
		return route;
	}

	public static void populateDBFromXML(Context c, String routeName, String fileName){
		Log.d(TAG, "Route name retrival " + routeName);
		String[] nameAndDescription = new String[2];

		String routeXMLPath = ROUTES_DIR+ "/" + fileName+"/"+ROUTE_XML;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			Log.d(TAG, "Media Mounted");
			InputStream inputStream;
			try {
				Log.d(TAG, "Accessing " + routeXMLPath);
				File inputFile = c.getExternalFilesDir(routeXMLPath);
				inputStream = new FileInputStream(inputFile);
	            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
	            Log.d(TAG, "Starting name parse of " + inputFile.getName());
	            XMLManager xmlManager = new XMLManager();
	            nameAndDescription = xmlManager.parseNameAndDescription(bufferedInputStream);
	            inputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
		RouteDBHandler dbHandler = new RouteDBHandler(c);
		RouteData routeData = dbHandler.findRoute(routeName);
		routeData.set_routeDescription(nameAndDescription[1]);
		routeData.set_routeName(nameAndDescription[0]);
		routeData.set_routeFile(fileName);
		dbHandler.updateRoute(routeData);
		
	}

	public static String getTilePath(Context c, RouteData routeData, String layerName){
		return ROUTES_DIR + routeData.get_routeFile()+"/" + LAYERS_DIR +"/"+layerName;
	}
	public static String getAudioPath(Context c, RouteData routeData, String audioName) {
		String fullPath = c.getExternalFilesDir(ROUTES_DIR + routeData.get_routeFile()+AUDIO_DIR+audioName).getAbsolutePath();
		return fullPath;
	}
	public static String getImagePath(Context c, RouteData routeData, String imageName) {
		return ROUTES_DIR + routeData.get_routeFile()+"/"+IMAGES_DIR+imageName;
	}
	public static String getVideoPath(Context c, RouteData routeData, String videoName) {
		return ROUTES_DIR + routeData.get_routeFile()+"/"+VIDEO_DIR+videoName;
	}

	public static void saveAudioTourStatus(Context c, boolean audioOn){
	      SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putBoolean("AudioStatus", audioOn);
	      editor.commit();
	}
	public static boolean getAudioTourStatus(Context c){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getBoolean("AudioStatus", false);
	}

	public static Route getCachedRoute(Context c){
		String routeCachePath = c.getCacheDir().getAbsolutePath()+"/"+ROUTE_CACHE;
		File routeFile = new File(routeCachePath);
		Route route = null;
		if(!routeFile.exists()) return null;
		try {
			FileInputStream fis = c.openFileInput(routeCachePath);
			ObjectInputStream is = new ObjectInputStream(fis);
			route = (Route) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return route;
	}
	public static void cacheRoute(Context c, Route route){
		String routeCachePath = c.getCacheDir().getAbsolutePath()+"/"+ROUTE_CACHE;
		
		try {
			FileOutputStream fos = c.openFileOutput(routeCachePath, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(route);
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
