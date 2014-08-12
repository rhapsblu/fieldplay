package com.jmie.fieldplay.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jmie.fieldplay.route.RouteLoaderActivity;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class LoadDefaultRoute extends AsyncTask<File, Void, Boolean>{
	RouteLoaderActivity c;
	static final String TAG = "Default route transfer";
	String state = Environment.getExternalStorageState();
	public LoadDefaultRoute(RouteLoaderActivity c){
		this.c = c;
	}
	@Override
	protected Boolean doInBackground(File... arg0) {
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			byte[] readData = new byte[1024];
			File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//			InputStream is = c.getResources().openRawResource(com.jmie.fieldplay.R.raw.fp_test_route);
//			File destination = new File(downloadDirectory.getPath() + "/" + "fp_test_route.zip");
			File destination2 = new File(downloadDirectory.getPath() + "/" + "fp_crest_route.zip");
//			File destination3 = new File(downloadDirectory.getPath() + "/" + "fp_socorro_demo_route.zip");
//			File destination4 = new File(downloadDirectory.getPath() + "/" + "fp_mapping_meaning_demo.zip");

			try {
//				FileOutputStream fos = new FileOutputStream(destination);
//	               int i = is.read(readData);
//
//	                while (i != -1) {
//	                    fos.write(readData, 0, i);
//	                    i = is.read(readData);
//	                }
//
//	                fos.close();
	    			InputStream is2 = c.getResources().openRawResource(com.jmie.fieldplay.R.raw.fp_crest_route);
	                FileOutputStream fos2 = new FileOutputStream(destination2);
	                int  j = is2.read(readData);
	                while(j!=-1){
	                	fos2.write(readData, 0, j);
	                	j=is2.read(readData);
	                }
	                fos2.close();
//	                InputStream is3 = c.getResources().openRawResource(com.jmie.fieldplay.R.raw.fp_socorro_demo_route);
//	                FileOutputStream fos3 = new FileOutputStream(destination3);
//	                int k = is3.read(readData);
//	                while(k!=-1){
//	                	fos3.write(readData, 0, k);
//	                	k=is3.read(readData);
//	                }
//	                fos3.close();
//	                InputStream is4 = c.getResources().openRawResource(com.jmie.fieldplay.R.raw.fp_mapping_meaning_demo);
//	                FileOutputStream fos4 = new FileOutputStream(destination4);
//	                int l = is4.read(readData);
//	                while(l!=-1){
//	                	fos4.write(readData, 0, l);
//	                	l=is4.read(readData);
//	                }
//	                fos4.close();
//	                Log.d(TAG, "Transfered resource " + destination.getName());
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
		StorageManager.loadDownloadedZips(c);
		return null;
	}

}
