package com.jmie.fieldplay.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParserException;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.jmie.fieldplay.Route;

public class StorageManager extends Observable{
	static final String TAG = "Storage Manager";
	public static final String PREFS_NAME = "FPPrefsFile";
	//public static final String ROUTES_DIR= "/Android/data/com.jmie.fieldplay/routes/";
	public static final String ROUTES_DIR= "routes/";
	public static final String IMAGES_DIR= "images/";
	public static final String AUDIO_DIR="audio/";
	public static final String VIDEO_DIR="video/";
	public static final String ROUTE_XML="route.xml";
	


	public void loadDownloadedZips(Context c) {
		String state = Environment.getExternalStorageState();


		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			
			File applicationDirectory = c.getExternalFilesDir(ROUTES_DIR);
			List<File> zips = findDownloads(downloadDirectory);
			unzipAndSave(zips, applicationDirectory);
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
	}
	public void transferDefaultRoute(Context c) {
		Log.d(TAG, "Transfering default route");
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			byte[] readData = new byte[1024];
			File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			InputStream is = c.getResources().openRawResource(com.jmie.fieldplay.R.raw.fp_test_route);
			File destination = new File(downloadDirectory.getPath() + "/" + "fp_test_route.zip");
			File destination2 = new File(downloadDirectory.getPath() + "/" + "fp_crest_route.zip");

			try {
				FileOutputStream fos = new FileOutputStream(destination);
	               int i = is.read(readData);

	                while (i != -1) {
	                    fos.write(readData, 0, i);
	                    i = is.read(readData);
	                }

	                fos.close();
	    			InputStream is2 = c.getResources().openRawResource(com.jmie.fieldplay.R.raw.fp_crest_route);
	                FileOutputStream fos2 = new FileOutputStream(destination2);
	                int  j = is2.read(readData);
	                while(j!=-1){
	                	fos2.write(readData, 0, j);
	                	j=is2.read(readData);
	                }
	                fos2.close();
	                Log.d(TAG, "Transfered resource " + destination.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
	}
	public List<String> getRouteNames(Context c){
		Log.d(TAG, "Fetching Route names");
		List<String> routes = new ArrayList<String>();
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			Log.d(TAG, "Getting external Route");
			File routeDirectory = c.getExternalFilesDir(ROUTES_DIR);
			File[] contents = routeDirectory.listFiles();
			for(File f: contents) {
				if(f.getName().startsWith("fp"))
					routes.add(f.getName());
			}
			Log.d(TAG, "Fetched " + routes.size() + " routes");
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
		return routes;
	}
	public List<File> findDownloads(File downloadDirectory){
		List<File> fpZips = new ArrayList<File>();

		File[] contents = downloadDirectory.listFiles();
		for(File f: contents) {
			if(f.getName().startsWith("fp")&&f.getName().endsWith("zip"))
				fpZips.add(f);
		}
	
		return fpZips;
	}
	private void unzipAndSave(List<File> zipFiles, File applicationDirectory){
		for(File f: zipFiles){
			Log.d(TAG, "unzipping " + f.getPath() + " to " + applicationDirectory.getPath());
	        AsyncTask uzdst = new UnZipTask().execute(f, applicationDirectory);
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
	public Route buildRoute(Context c, String routeStorageName){
		
		Log.d(TAG, "Building route " + routeStorageName);
		Route route = null;
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
	            Log.d(TAG, "Starting parse of " + inputFile.getName());
	            XMLManager xmlManager = new XMLManager();
	            route = xmlManager.parse(bufferedInputStream);
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
	public Route buildRouteByName(Context c, String name){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String storageName = settings.getString(name, "N/A");
		Log.e(TAG, "No Map for storage name found");
		return buildRoute(c, storageName);
	}
	public static String getAudioPath(Context c, String routeName, String audioName) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String storageName = settings.getString(routeName, "N/A");
		return ROUTES_DIR + storageName+"/"+AUDIO_DIR+audioName;
	}
	public static String getImagePath(Context c, String routeName, String imageName) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String storageName = settings.getString(routeName, "N/A");
		return ROUTES_DIR + storageName+"/"+IMAGES_DIR+imageName;
	}
	public static String getVideoPath(Context c, String routeName, String videoName) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String storageName = settings.getString(routeName, "N/A");
		return ROUTES_DIR + storageName+"/"+VIDEO_DIR+videoName;
	}
	public static void saveCurrentRoute(Context c, Route route){
	      SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("CurrentRoute", route.getName());
	      editor.commit();
	}
	public void setReadName(Context c, String storageName, String readName) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(readName, storageName);
		editor.commit();
	}
	public String getReadName(Context c, String readName){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getString(readName, "N/A");
	}
	public boolean isFirstRun(Context c){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Log.d(TAG, "First run is " + settings.getBoolean("FirstRun", true));
		return settings.getBoolean("FirstRun", true);
	}
	public void setFirstRun(Context c){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("FirstRun", false);
		editor.commit();
	}
	public static String getCurrentRoute(Context c){
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getString("CurrentRoute", "none");
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

	public void update(Observable observable, Object data){
		//Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show();
	}
	 private class UnZipTask extends AsyncTask<File, Void, Boolean> {
		 private String routeName;
//	        @SuppressWarnings("rawtypes")
	        @Override
	        protected Boolean doInBackground(File... params) {
	
	            String destinationPath = params[1].getPath();
	            File archive = params[0];
	            routeName = archive.getName();
	            try {
	                ZipFile zipfile = new ZipFile(archive);
	                for (Enumeration<?> e = zipfile.entries(); e.hasMoreElements();) {
	                    ZipEntry entry = (ZipEntry) e.nextElement();
	                    unzipEntry(zipfile, entry, destinationPath);
	                }
	            } catch (Exception e) {
	                Log.e(TAG, "Error while extracting file " + archive, e);
	                return false;
	            }

	            return true;
	        }

	        @Override
	        protected void onPostExecute(Boolean result) {
	            setChanged();
	            notifyObservers(routeName);
	        }

	        private void unzipEntry(ZipFile zipfile, ZipEntry entry,
	                String outputDir) throws IOException {

	            if (entry.isDirectory()) {
	                createDir(new File(outputDir, entry.getName()));
	                return;
	            }

	            File outputFile = new File(outputDir, entry.getName());
	            if (!outputFile.getParentFile().exists()) {
	                createDir(outputFile.getParentFile());
	            }

	            Log.v(TAG, "Extracting: " + entry);
	            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
	            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

	            try {
	                IOUtils.copy(inputStream, outputStream);
	            } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
	                outputStream.close();
	                inputStream.close();
	            }
	        }

	        private void createDir(File dir) {
	            if (dir.exists()) {
	                return;
	            }
	            Log.v(TAG, "Creating dir " + dir.getName());
	            if (!dir.mkdirs()) {
	                throw new RuntimeException("Can not create dir " + dir);
	            }
	        }
	    }
}
