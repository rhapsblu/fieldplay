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
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.xmlpull.v1.XmlPullParserException;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.jmie.fieldplay.Route;
import com.jmie.fieldplay.RouteLoaderActivity;

public class StorageManager {
	static final String TAG = "Storage Manager";
	public static final String PREFS_NAME = "FPPrefsFile";
	//public static final String ROUTES_DIR= "/Android/data/com.jmie.fieldplay/routes/";
	public static final String ROUTES_DIR= "routes/";
	public static final String IMAGES_DIR= "images/";
	public static final String AUDIO_DIR="audio/";
	public static final String VIDEO_DIR="video/";
	public static final String LAYERS_DIR="layers/";
	public static final String ROUTE_XML="route.xml";
	public static final String ROUTE_NAMES="routenames";
	public static final String ROUTE_CACHE="route_cache";

	public static void firstRunSetup(RouteLoaderActivity loader){
		transferDefaultRoute(loader);
		setFirstRun(loader);
	}
	public static List<String> getRouteStorageNames(Context c){
		loadRouteNames(c);
		String state = Environment.getExternalStorageState();
		List<String> routeNames = new ArrayList<String>();
	     if (Environment.MEDIA_MOUNTED.equals(state)) {	
				Log.d(TAG, "Getting external Route");
				File routeDirectory = c.getExternalFilesDir(ROUTES_DIR);
				File[] contents = routeDirectory.listFiles();
				for(File f: contents) {
					if(f.getName().startsWith("fp")){
						routeNames.add(f.getName());
					}
				}
			} 
			else {
				Log.e(TAG, "External media not available ");
			}
	     return routeNames;

	}
	public static String getRouteDescription(Context c, String routeStorageName){
		Log.d(TAG, "Route description retrival " + routeStorageName);
		String routeName = null;
		
		String routeXMLPath = ROUTES_DIR + routeStorageName+"/"+ROUTE_XML;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			Log.d(TAG, "Media Mounted");
			InputStream inputStream;
			try {
				//Log.d(TAG, "Accessing " + routeXMLPath);
				File inputFile = c.getExternalFilesDir(routeXMLPath);
				inputStream = new FileInputStream(inputFile);
	            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
	            //Log.d(TAG, "Starting name parse of " + inputFile.getName());
	            XMLManager xmlManager = new XMLManager();
	            routeName = xmlManager.parseDescriptionOnly(bufferedInputStream);
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
		return routeName;
	}
	public static Route buildRoute(Context c, String routeStorageName){
		
		Log.d(TAG, "Building route " + routeStorageName);
		Route route = null;
		String routeXMLPath = ROUTES_DIR + routeStorageName+"/"+ROUTE_XML;
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
	            route = xmlManager.parse(bufferedInputStream, routeStorageName);
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
		return route;
	}

	public static void loadDownloadedZips(RouteLoaderActivity loader) {
		String state = Environment.getExternalStorageState();


		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			
			File applicationDirectory = loader.getExternalFilesDir(ROUTES_DIR);
			List<File> zips = StorageManager.findDownloads(downloadDirectory);
			unzipAndSave(zips, applicationDirectory, loader);
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
	}

	private static void transferDefaultRoute(RouteLoaderActivity loader) {
		
		AsyncTask uzdst = new LoadDefaultRoute(loader).execute();
		Log.d(TAG, "Transfering default route");
		Toast.makeText(loader, "Loading default routes", Toast.LENGTH_LONG).show();
		StorageManager.loadDownloadedZips(loader);
	}
	private static void loadRouteNames(Context c){
		 SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	     SharedPreferences.Editor editor = settings.edit();
	     
	     String state = Environment.getExternalStorageState();
	     if (Environment.MEDIA_MOUNTED.equals(state)) {	
				Log.d(TAG, "Getting external Route");
				File routeDirectory = c.getExternalFilesDir(ROUTES_DIR);
				File[] contents = routeDirectory.listFiles();
				for(File f: contents) {
					if(f.getName().startsWith("fp")){
						String routeName = getRouteNameFromXML(c, f.getName());
						editor.putString(f.getName(), routeName);
					}
				}
			} 
			else {
				Log.e(TAG, "External media not available ");
			}
	     
	      editor.commit();
	}

	private static List<File> findDownloads(File downloadDirectory){
		List<File> fpZips = new ArrayList<File>();

		File[] contents = downloadDirectory.listFiles();
		for(File f: contents) {
			if(f.getName().startsWith("fp")&&f.getName().endsWith("zip"))
				fpZips.add(f);
		}
	
		return fpZips;
	}
	private static void unzipAndSave(List<File> zipFiles, File applicationDirectory, RouteLoaderActivity loader){
		for(File f: zipFiles){
			Log.d(TAG, "unzipping " + f.getPath() + " to " + applicationDirectory.getPath());
	        AsyncTask uzdst = new UnZipTask(loader).execute(f, applicationDirectory);
	        try {
				uzdst.get(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static String storageNameToRouteName(Context c, String routeStorageName){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String routeName = settings.getString(routeStorageName, "");
		return routeName;
	}
	private static String getRouteNameFromXML(Context c, String routeStorageName){
		Log.d(TAG, "Route name retrival " + routeStorageName);
		String routeName = null;
		
		String routeXMLPath = ROUTES_DIR + routeStorageName+"/"+ROUTE_XML;
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
	            routeName = xmlManager.parseNameOnly(bufferedInputStream);
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
		return routeName;
	}
	

	public static String getTilePath(Context c, String routeStorageName, String layerName){
		return ROUTES_DIR + routeStorageName+"/" + LAYERS_DIR +"/"+layerName;
	}
	public static String getAudioPath(Context c, String routeStorageName, String audioName) {
		return ROUTES_DIR + routeStorageName+"/"+AUDIO_DIR+audioName;
	}
	public static String getImagePath(Context c, String routeStorageName, String imageName) {
		return ROUTES_DIR + routeStorageName+"/"+IMAGES_DIR+imageName;
	}
	public static String getVideoPath(Context c, String routeStorageName, String videoName) {
		return ROUTES_DIR + routeStorageName+"/"+VIDEO_DIR+videoName;
	}
//	public static void saveCurrentRoute(Context c, Route route){
//	      SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//	      SharedPreferences.Editor editor = settings.edit();
//	      editor.putString("CurrentRoute", route.getName());
//	      editor.commit();
//	}
	private static void setReadName(Context c, String storageName, String readName) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(readName, storageName);
		editor.commit();
	}

	public static boolean isFirstRun(Context c){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Log.d(TAG, "First run is " + settings.getBoolean("FirstRun", true));
		return settings.getBoolean("FirstRun", true);
	}
	private static void setFirstRun(Context c){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("FirstRun", false);
		editor.commit();
	}
//	public static String getCurrentRoute(Context c){
//		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//		return settings.getString("CurrentRoute", "none");
//	}
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
