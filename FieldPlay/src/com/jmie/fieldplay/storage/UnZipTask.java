package com.jmie.fieldplay.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.jmie.fieldplay.route.RouteData;
import com.jmie.fieldplay.route.RouteLoaderActivity;

import android.os.AsyncTask;
import android.util.Log;

public class UnZipTask extends AsyncTask<File, Integer, Boolean> {

	 static final String TAG = "UnZipTask";
	 RouteLoaderActivity loader;
	 private RouteDBHandler routeDB;
	 private int size;
	 private String routeName;

	 private String fileName;
	 
	 public UnZipTask(RouteLoaderActivity loader, String routeName){
		 this.loader = loader;
		 routeDB = new RouteDBHandler(loader);
		 this.routeName = routeName;
	 }

       @Override
       protected Boolean doInBackground(File... params) {

           String destinationPath = params[1].getPath();
           File archive = params[0];

           try {
               ZipFile zipfile = new ZipFile(archive);
               
               size = zipfile.size();
               int count = 0;
               for (Enumeration<?> e = zipfile.entries(); e.hasMoreElements();) {
                   ZipEntry entry = (ZipEntry) e.nextElement();
                   if(count==0)fileName = entry.getName();
                   unzipEntry(zipfile, entry, destinationPath);
                   count++;
                   int progress = (count*100)/size;
                   if(progress%5==0) onProgressUpdate(progress);
                   //onProgressUpdate((count*100)/size);
               }
               
           } catch (Exception e) {
               Log.e(TAG, "Error while extracting file " + archive, e);
               return false;
           }

           return true;
       }

       protected void onProgressUpdate(Integer progress){
    	   
    	   RouteData routeData = routeDB.findRoute(routeName);
    	   routeData.set_unzipProgress(progress);
    	   routeDB.updateRoute(routeData);
			loader.runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			    	Log.d(TAG, "Update Called");
			    	loader.updateAdapter();
			    }
			} );
    	   Log.d(TAG, "Unzip Progress: " + progress);
       }
       @Override
       protected void onPostExecute(Boolean result) {
    	   RouteData routeData = routeDB.findRoute(routeName);
    	   routeData.set_unzipProgress(100);
    	  routeDB.updateRoute(routeData);
    	  
    	   StorageManager.populateDBFromXML(loader, routeName, fileName);
			loader.runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			    	Log.d(TAG, "Update Called");
			    	loader.updateAdapter();
			    }
			} );

       }

       private void unzipEntry(ZipFile zipfile, ZipEntry entry,
               String outputDir) throws IOException {

           if (entry.isDirectory()) {
           //	Log.d(TAG, "Is directory " + entry.getName());
               createDir(new File(outputDir, entry.getName()));
               return;
           }

           File outputFile = new File(outputDir, entry.getName());
           if (!outputFile.getParentFile().exists()) {
           	//Log.d(TAG, "Creating directory from png path" + entry.getName());
               createDir(outputFile.getParentFile());
           }

           //Log.v(TAG, "Extracting: " + entry);
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
           	Log.v(TAG, "Directory exists " + dir.getName());
               return;
           }
           Log.v(TAG, "Creating dir " + dir.getName());
           if (!dir.mkdirs()) {
               throw new RuntimeException("Can not create dir " + dir);
           }
       }
   }