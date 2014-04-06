package com.jmie.fieldplay.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.R.id;
import com.jmie.fieldplay.R.layout;
import com.jmie.fieldplay.R.menu;
import com.jmie.fieldplay.map.FPMapActivity;
import com.jmie.fieldplay.route.RouteDetailsFragment.OnRouteSelectedListener;
import com.jmie.fieldplay.storage.StorageManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RouteLoaderActivity extends Activity
	implements RouteDetailsFragment.OnRouteSelectedListener{
	private StorageManager storage;
	//private List<Map<String, String>> routeList = new ArrayList<Map<String, String>>();
	//private Map<String, String> routeDescriptions = new HashMap<String, String>();
	//private Map<String, String> nameToStorage= new HashMap<String, String>();
	//private SimpleAdapter simpleAdpt;
	private ArrayAdapter<StorageNameMeta> arrayAdapter;
	static final String TAG = "Route Load Activity";
	private String selectedRoute;
	private List<StorageNameMeta> routeMeta = new ArrayList<StorageNameMeta>();
	private Context c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_loader);
		c = this;
		if(StorageManager.isFirstRun(this)) StorageManager.firstRunSetup(this);
		
		for(String routeStorageName: StorageManager.getRouteStorageNames(this)){
			routeMeta.add(new StorageNameMeta(StorageManager.storageNameToRouteName(c, routeStorageName), routeStorageName));
		}

	
		ListView lv = (ListView) findViewById(R.id.list);
	
		//simpleAdpt = new SimpleAdapter(this, routeList, android.R.layout.simple_list_item_1, new String[]{"routeItem"}, new int[] {android.R.id.text1});
		
		//simpleAdpt = new SimpleAdapter(this, routeList, android.R.layout.activity_list_item, new String[]{"routeItem"}, new int[] {android.R.id.text1});
		arrayAdapter = new ArrayAdapter<StorageNameMeta>(this, android.R.layout.simple_list_item_1, routeMeta);
		lv.setAdapter(arrayAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
	            for (int j = 0; j < parentAdapter.getChildCount(); j++)
	                parentAdapter.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

	            // change the background color of the selected element
	            view.setBackgroundColor(Color.LTGRAY);
				TextView clickedView = (TextView) view;

				//selectedRoute = storage.getReadName(RouteLoaderActivity.this, clickedView.getText().toString());
				selectedRoute = routeMeta.get(position).getStorageName();
				deliverToFragment(StorageManager.getRouteDescription(c, selectedRoute));
	
			}
		});
	}
	private void deliverToFragment(String description){
        RouteDetailsFragment routeDetailFrag = (RouteDetailsFragment)
                getFragmentManager().findFragmentById(R.id.details_fragment);
        routeDetailFrag.displayRouteDetails(description);
        
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
		i.putExtra("com.jmie.fieldplay.routeName", selectedRoute);
		startActivity(i);
		
	}

	public void updateNames() {
		routeMeta.clear();
		for(String routeStorageName: StorageManager.getRouteStorageNames(this)){
			routeMeta.add(new StorageNameMeta(StorageManager.storageNameToRouteName(c, routeStorageName), routeStorageName));
		}
		arrayAdapter.notifyDataSetChanged();
	}
	private class StorageNameMeta{
		private String routeName;
		private String routeStorageName;
		private StorageNameMeta(String routeName, String routeStorageName){
			this.routeName = routeName;
			this.routeStorageName = routeStorageName;
		}
		private String getStorageName(){
			return routeStorageName;
		}
		@Override
		public String toString(){
			return routeName;
		}
	}
}
