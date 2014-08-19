package com.jmie.fieldplay.route;

import java.io.File;


import java.util.List;


import com.jmie.fieldplay.R;

import com.jmie.fieldplay.map.FPMapActivity;
import com.jmie.fieldplay.storage.RouteDBHandler;
import com.jmie.fieldplay.storage.StorageManager;
import com.jmie.fieldplay.storage.UnZipTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;


public class RouteLoaderActivity extends Activity
	implements RouteDetailsFragment.OnRouteSelectedListener{
	
	static final int PICK_DOWNLOAD_REQUEST = 0;

	private RoutesAdapter routesAdapter;
	static final String TAG = "Route Load Activity";

	private RouteData selectedRouteData;

	private List<RouteData> routeDataList;

	DownloadManager downloadManager;
	private RouteDBHandler routeDB;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_loader);

		routeDB = new RouteDBHandler(this);
		routeDataList = routeDB.getAllRoutes();

	
		ListView lv = (ListView) findViewById(R.id.list);

		routesAdapter = new RoutesAdapter(this, routeDataList);

		lv.setAdapter(routesAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
	            for (int j = 0; j < parentAdapter.getChildCount(); j++)
	                parentAdapter.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

	            // change the background color of the selected element
	            view.setBackgroundColor(Color.LTGRAY);
//				TextView clickedView = (TextView) view;

				//selectedRoute = storage.getReadName(RouteLoaderActivity.this, clickedView.getText().toString());
				selectedRouteData = routeDataList.get(position);
				deliverToFragment(selectedRouteData);
	
			}
		});
	}
	private void deliverToFragment(RouteData routeData){
        RouteDetailsFragment routeDetailFrag = (RouteDetailsFragment)
                getFragmentManager().findFragmentById(R.id.details_fragment);
        routeDetailFrag.displayRouteDetails(routeData.get_routeDescription());
        
	}
	@Override
	public void onResume(){
		super.onResume();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_loader, menu);
		return true;
	}
//	private HashMap<String, String> createRouteItem(String key, String name){
//		HashMap<String, String> routeItem = new HashMap<String, String>();
//		routeItem.put(key, name);
//		return routeItem;
//	}

	@Override
	public void onRouteSelected() {
		Intent i = new Intent(RouteLoaderActivity.this, FPMapActivity.class);
		i.putExtra("com.jmie.fieldplay.routeData", selectedRouteData);
		startActivity(i);
		
	}

	public void updateAdapter() {
		routeDataList.clear();
		routesAdapter.notifyDataSetChanged();
//		for(String routeStorageName: StorageManager.getRouteStorageNames(this)){
//			routeMeta.add(new StorageNameMeta(StorageManager.storageNameToRouteName(c, routeStorageName), routeStorageName));
//		}
		routeDataList =  routeDB.getAllRoutes();
		routesAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.add_route:
	        	Intent i = new Intent(RouteLoaderActivity.this, RouteAddActivity.class);
	        	startActivityForResult(i, PICK_DOWNLOAD_REQUEST);
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode== PICK_DOWNLOAD_REQUEST){
			if(resultCode == RESULT_OK){
				Boolean isLocal = data.getBooleanExtra("com.jmie.fieldplay.local", false);
				String location = data.getStringExtra("com.jmie.fieldplay.location");
				if(!isLocal)startDownload(location);
			}
		}
	}
	private void startDownload(String location){
		RouteData routeData = new RouteData();
		routeData.set_routeName(location);
		routeData.set_downloadProgress(0);
		routeData.set_unzipProgress(0);

		downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(location);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		routeData.set_managerID(downloadManager.enqueue(request));
		Log.d(TAG, "Starting download of " + location);
		routeDB.addRoute(routeData);
		new DownloadProgressUpdateTask().execute(location, null, null);
		updateAdapter();
		
	}
	private void startUnzip(String location, RouteData routeData){
		new UnZipTask(this, routeData.get_routeName()).execute(new File(location), this.getExternalFilesDir(StorageManager.ROUTES_DIR));
	}
	private class DownloadProgressUpdateTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String ... locations) {
			Log.d(TAG, "Starting download monitor");
			while(true){
				RouteData routeData = routeDB.findRoute(locations[0]);
				DownloadManager.Query query = new DownloadManager.Query();
				query.setFilterById(routeData.get_managerID());
				Cursor cursor = downloadManager.query(query);
				
				if(cursor.moveToFirst()){
					int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
					int status = cursor.getInt(columnIndex);
					columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
					int byteSoFar = cursor.getInt(columnIndex);
					columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
					int totalBytes = cursor.getInt(columnIndex);
					if(status == DownloadManager.STATUS_SUCCESSFUL){
						//pass to unzip routine
						columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
						String fileLocation = cursor.getString(columnIndex);
						routeData.set_routeName(fileLocation);
						routeData.set_downloadProgress(100);
						routeDB.updateRoute(routeData);
						Log.d(TAG, "Download done");
						runOnUiThread(new Runnable() {
						    @Override
						    public void run() {
						    	Log.d(TAG, "Update Called");
						    	updateAdapter();
						    }
						} );
						startUnzip(fileLocation, routeData);
						break;
					}
					else if(status == DownloadManager.STATUS_RUNNING){

						int progress = (byteSoFar*100)/totalBytes;
						routeData.set_downloadProgress(progress);
						routeDB.updateRoute(routeData);
						Log.d(TAG, "Downloading Progress: " + progress);
						runOnUiThread(new Runnable() {
						    @Override
						    public void run() {
						    	Log.d(TAG, "Update Called");
						    	updateAdapter();
						    }
						} );
						
					}
					else if(status == DownloadManager.STATUS_FAILED){
						routeDB.deleteRoute(locations[0]);
						break;
					}
					try {

						Thread.sleep(500);
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}   

			}
			return null;
		}
			
	}
}
