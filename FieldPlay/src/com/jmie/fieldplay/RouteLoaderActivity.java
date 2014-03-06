package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jmie.fieldplay.storage.StorageManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RouteLoaderActivity extends Activity{
	private StorageManager storage;
	private List<Map<String, String>> routeList = new ArrayList<Map<String, String>>();
	//private Map<String, String> nameToStorage= new HashMap<String, String>();
	private SimpleAdapter simpleAdpt;
	static final String TAG = "Route Load Activity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_loader);
		SharedPreferences settings = this.getSharedPreferences("FPPrefsFile", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		storage = new StorageManager();
		if(storage.isFirstRun(this)){
			storage.transferDefaultRoute(this);
			storage.loadDownloadedZips(this);
			storage.setFirstRun(this);
		}
		List<String> routeNames = storage.getRouteNames(this);

		for(String s : routeNames){
			Route r = storage.buildRoute(this, s);
			routeList.add(createRouteItem("routeItem", r.getName()));
			storage.setReadName(this, s, r.getName());
			Log.d(TAG, "Found route " + s);
		}
	
		ListView lv = (ListView) findViewById(R.id.list);
		simpleAdpt = new SimpleAdapter(this, routeList, android.R.layout.simple_list_item_1, new String[]{"routeItem"}, new int[] {android.R.id.text1});

		//simpleAdpt = new SimpleAdapter(this, routeList, android.R.layout.activity_list_item, new String[]{"routeItem"}, new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
				Intent i = new Intent(RouteLoaderActivity.this, FPMapActivity.class);
				TextView clickedView = (TextView) view;
			//	String name = nameToStorage.get(clickedView.getText());
				String name = storage.getReadName(RouteLoaderActivity.this, clickedView.getText().toString());
				i.putExtra("com.jmie.fieldplay.routeName", name);
				startActivity(i);			
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_loader, menu);
		return true;
	}
	private HashMap<String, String> createRouteItem(String key, String name){
		HashMap<String, String> routeItem = new HashMap<String, String>();
		routeItem.put(key, name);
		return routeItem;
	}
}
