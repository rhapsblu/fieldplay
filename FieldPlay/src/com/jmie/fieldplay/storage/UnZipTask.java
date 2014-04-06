package com.jmie.fieldplay.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.jmie.fieldplay.route.RouteLoaderActivity;

import android.os.AsyncTask;
import android.util.Log;

public class UnZipTask extends AsyncTask<File, Void, Boolean> {
//	 private String routeName;
	 static final String TAG = "UnZipTask";
	 RouteLoaderActivity loader;
	 public UnZipTask(RouteLoaderActivity loader){
		 this.loader = loader;
	 }
//       @SuppressWarnings("rawtypes")
       @Override
       protected Boolean doInBackground(File... params) {

           String destinationPath = params[1].getPath();
           File archive = params[0];
//           routeName = archive.getName();
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
    	   loader.updateNames();
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
           	//Log.v(TAG, "Directory exists " + dir.getName());
               return;
           }
           //Log.v(TAG, "Creating dir " + dir.getName());
           if (!dir.mkdirs()) {
               throw new RuntimeException("Can not create dir " + dir);
           }
       }
   }