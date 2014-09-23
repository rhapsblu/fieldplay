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
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import android.widget.ListView;


public class RouteLoaderActivity extends Activity
	implements RouteDetailsFragment.OnRouteSelectedListener{
	
	static final int PICK_DOWNLOAD_REQUEST = 0;

	private RoutesAdapter routesAdapter;


	private RouteData selectedRouteData;
	private ListView lv;

	private List<RouteData> routeDataList;

	private RouteDBHandler routeDB;
	private String TAG = "FP Route Load";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_loader);

		routeDB = new RouteDBHandler(this);
		routeDataList = routeDB.getAllRoutes();

	
		lv = (ListView) findViewById(R.id.list);
		
		routesAdapter = new RoutesAdapter(this);
		
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
		if(routeData == null){
			routeDetailFrag.clear();
		}
		else{
			routeDetailFrag.displayRouteDetails(routeData.get_routeDescription());
		}
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

	@Override
	public void onRouteSelected() {
		if(selectedRouteData.get_unzipProgress() <100){
			Toast.makeText(this, "Route still loading, please wait", Toast.LENGTH_LONG).show();
		}
		else{
			Intent i = new Intent(RouteLoaderActivity.this, FPMapActivity.class);
			i.putExtra("com.jmie.fieldplay.routeData", selectedRouteData);
			startActivity(i);
		}
		
	}
	public List<RouteData> getRouteList(){
		return routeDataList;
	}
	public void updateAdapter() {
		routeDataList.clear();
		routeDataList =  routeDB.getAllRoutes();
		routesAdapter.notifyDataSetChanged();
		lv.setAdapter(routesAdapter);
		
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		
		if(item.getItemId()==R.id.add_route){
        	Intent i = new Intent(RouteLoaderActivity.this, RouteAddActivity.class);
        	startActivityForResult(i, PICK_DOWNLOAD_REQUEST);
        	return true;
		}
		else if(item.getItemId()==R.id.delete_route){
			AlertDialog.Builder builder = new AlertDialog.Builder(RouteLoaderActivity.this, AlertDialog.THEME_HOLO_DARK);
			if(selectedRouteData==null){
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			           }
			       });
				builder.setTitle("Select a route");
				builder.setMessage("Please select a route to delete");
			}
			else{
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   routeDB.deleteRoute(selectedRouteData);
			        	   selectedRouteData = null;
			        	   deliverToFragment(selectedRouteData);
			        	   updateAdapter();
			           }
			       });
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			           }
			       });
				builder.setTitle("Delete Route");
				builder.setMessage("Are you sure you like to delete this route?");
			}
			builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode== PICK_DOWNLOAD_REQUEST){
			if(resultCode == RESULT_OK){
				Boolean isLocal = data.getBooleanExtra("com.jmie.fieldplay.local", false);
				String location = data.getStringExtra("com.jmie.fieldplay.location");
				if(isLocal){
					RouteData routeData = new RouteData();
					routeData.set_routeName(location);
					routeData.set_downloadProgress(100);
					routeData.set_unzipProgress(0);
					routeDB.addRoute(routeData);
					startUnzip(location, routeData);
				}
				else if(!isLocal)startDownload(location);
			}
		}
	}
	private void startDownload(String location){
		RouteData routeData = new RouteData();
		routeData.set_routeName(location);
		routeData.set_downloadProgress(0);
		routeData.set_unzipProgress(0);

		DownloadManager downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(location);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		routeData.set_managerID(downloadManager.enqueue(request));
		routeDB.addRoute(routeData);
		new DownloadProgressUpdateTask(downloadManager).execute(routeData, null, null);
		updateAdapter();
		
	}
	private void startUnzip(String location, RouteData routeData){
		new UnZipTask(this, routeData).execute(new File(location), this.getExternalFilesDir(StorageManager.ROUTES_DIR));
	}
	private class DownloadProgressUpdateTask extends AsyncTask<RouteData, Void, Void>{
		DownloadManager downloadManager;
		public DownloadProgressUpdateTask(DownloadManager downloadManager){
			this.downloadManager=downloadManager;
		}
		@Override
		protected Void doInBackground(RouteData ... locations) {

			while(true){
				RouteData routeData = locations[0];
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
						if(fileLocation.endsWith(".fpr")){
							routeData.set_routeName(fileLocation);
							routeData.set_downloadProgress(100);
							routeDB.updateRoute(routeData);

							runOnUiThread(new Runnable() {
							    @Override
							    public void run() {
							    	updateAdapter();
							    }
							} );
							startUnzip(fileLocation, routeData);
						}	
						else {
							
							final String badFile = new File(fileLocation).getName();
							routeDB.deleteRoute(routeData);
							runOnUiThread(new Runnable() {
							    @Override
							    public void run() {
							    	updateAdapter();
									AlertDialog.Builder builder = new AlertDialog.Builder(RouteLoaderActivity.this, AlertDialog.THEME_HOLO_DARK);

									builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								           public void onClick(DialogInterface dialog, int id) {

								           }
								       });
									builder.setTitle("Not a FieldPlay route!");
									builder.setMessage("The download \n\n"+ badFile + "\n\nis not a valid Field Play Route");
									Log.e(TAG, badFile + " does not have the proper extension");
								// Create the AlertDialog
									AlertDialog dialog = builder.create();
									dialog.show();
							    }
							} );

							
						}
						cursor.close();
						break;
					}
					else if(status == DownloadManager.STATUS_RUNNING){

						int progress = (byteSoFar*100)/totalBytes;
						routeData.set_downloadProgress(progress);
						routeDB.updateRoute(routeData);

						runOnUiThread(new Runnable() {
						    @Override
						    public void run() {
						    	updateAdapter();
						    }
						} );
						
					}
					else if(status == DownloadManager.STATUS_FAILED){
						routeDB.deleteRoute(locations[0]);
						cursor.close();
						break;
					}
					cursor.close();
					try {

						Thread.sleep(2000);
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
