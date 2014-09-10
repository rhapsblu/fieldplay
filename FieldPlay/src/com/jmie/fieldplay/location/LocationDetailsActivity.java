package com.jmie.fieldplay.location;

import java.util.ArrayList;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.binocular.activity.FPBinocularActivity;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.FPPicture;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.StopLocation;

import com.jmie.fieldplay.storage.StorageManager;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;



public class LocationDetailsActivity extends FragmentActivity implements TabListener{

	private Route route;
	private FPLocation location;

	private String[] tabs = {"Description", "Photos", "Audio", "Video"};
	private ViewPager viewPager;
	private LocationDetailsTabsAdapter mAdapter;
	private ActionBar actionBar;
	private ArrayList<String> imagePathList = new ArrayList<String>();
	public static String TAG = "LocationDetails";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();

		route = (Route)b.getParcelable("com.jmie.fieldplay.route");
		Log.d(TAG, "Route name: " +route.getName());
		String location_id = b.getString("com.jmie.fieldplay.location");
		Log.d(TAG, "location= " + location_id);
		location = route.getLocationByName(location_id);
		
		
        for(FPPicture pic: location.getImageList()){
        	imagePathList.add(StorageManager.getImagePath(this, route.getRouteData(), pic.getResource()));
        }
		
		setContentView(R.layout.activity_location_detail);
		mAdapter = new LocationDetailsTabsAdapter(getSupportFragmentManager(), this);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(mAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position){
				getActionBar().setSelectedNavigationItem(position);
			}
		});

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for(String t_name : tabs){
			actionBar.addTab(actionBar.newTab().setText(t_name).setTabListener(this));
		}

	}	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_detail, menu);
		MenuItem binocularActionButton = menu.findItem(R.id.augmented_binoculars);
		if(location instanceof StopLocation){
			StopLocation stopLocation = (StopLocation)location;
			if(stopLocation.getBinocPointCount()<1)  binocularActionButton.setVisible(false);	
		}
		else binocularActionButton.setVisible(false);
		return true;
	}
	@Override
	protected void onResume(){
		super.onResume();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		
		if(item.getItemId()==R.id.augmented_binoculars){
        	//Log.d(TAG, route.debugPrintMap());
			Intent i = new Intent(LocationDetailsActivity.this, FPBinocularActivity.class);
			i.putExtra("com.jmie.fieldplay.route", route);
			i.putExtra("com.jmie.fieldplay.locationName", location.getName());
			startActivity(i);
            return true;
		}

		return super.onOptionsItemSelected(item);

	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	public ArrayList<String> getImagePaths(){
		return imagePathList;
	}
	public FPLocation getLocation(){
		return location;
	}
	public Route getRoute(){
		return route;
	}

}
