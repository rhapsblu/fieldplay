package com.jmie.fieldplay.route;

import java.io.File;
import java.io.FilenameFilter;


import java.util.ArrayList;
import java.util.List;


import com.jmie.fieldplay.R;

import com.jmie.fieldplay.map.FPMapActivity;
import com.jmie.fieldplay.storage.RouteDBHandler;
import com.jmie.fieldplay.storage.StorageManager;
import com.jmie.fieldplay.storage.UnZipTask;
import com.jmie.fieldplay.zxing.integration.android.IntentIntegrator;
import com.jmie.fieldplay.zxing.integration.android.IntentResult;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import android.widget.ListView;


public class DownloadsViewerActivity extends Activity
	{
	
	static final int PICK_DOWNLOAD_REQUEST = 0;

	private RouteDownloadsAdapter downloadsAdapter;
	static final String TAG = "Route Load Activity";

	private File selectedRoutePath;
	private ListView lv;

	private File[] routePathList;
	private Button loadButton;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloads_folder_viewer);

//populate list here
		File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		FilenameFilter archiveFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(getResources().getString(R.string.route_archive_suffix))) {
					return true;
				} else {
					return false;
				}
			}
		};
		routePathList = downloadsFolder.listFiles(archiveFilter);
		//if pathlist empty show dialog and return
		lv = (ListView) findViewById(R.id.route_path_list);
		loadButton = (Button) findViewById(R.id.load_route);
		loadButton.setEnabled(false);
		loadButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				returnLocation(selectedRoutePath.getAbsolutePath());	
			}
			
		});
		downloadsAdapter = new RouteDownloadsAdapter(this, routePathList);
		
		lv.setAdapter(downloadsAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
	            for (int j = 0; j < parentAdapter.getChildCount(); j++)
	                parentAdapter.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

	            // change the background color of the selected element
	            view.setBackgroundColor(Color.LTGRAY);
				selectedRoutePath = routePathList[position];
				loadButton.setEnabled(true);
			}
		});

	}
	private void returnLocation(String location){
			Intent intent = new Intent();
			intent.putExtra("com.jmie.fieldplay.location", location);
			setResult(RESULT_OK, intent);
			finish();
	}
	@Override
	public void onResume(){
		super.onResume();
		
	}



}
