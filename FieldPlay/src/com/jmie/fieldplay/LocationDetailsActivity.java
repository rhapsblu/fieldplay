package com.jmie.fieldplay;

import java.util.ArrayList;

import com.jmie.fieldplay.binocular.testar.FPBinocularActivity;
import com.jmie.fieldplay.details.FPPicture;
import com.jmie.fieldplay.storage.StorageManager;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class LocationDetailsActivity extends FragmentActivity implements TabListener{

	private Route route;
	private FPLocation location;
	private String routeStorageName;
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
		for(String key: b.keySet()){
			Log.d("Bundle keys", key);
		}
		route = (Route)b.getParcelable("com.jmie.fieldplay.route");
		Log.d(TAG, "Route name: " +route.getName());
		String location_id = b.getString("com.jmie.fieldplay.location");
		Log.d(TAG, "location= " + location_id);
		location = route.getLocationByName(location_id);
        for(FPPicture pic: location.getImageList()){
        	imagePathList.add(StorageManager.getImagePath(this, route.getStorageName(), pic.getResource()));
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
			if(!stopLocation.getBinocularPointIterator().hasNext()) binocularActionButton.setVisible(false);
		}
		else binocularActionButton.setVisible(false);
		return true;
	}
	@Override
	protected void onResume(){
		super.onResume();
//		PhotoViewFragment photoFrag = (PhotoViewFragment)
//                getFragmentManager().findFragmentById(R.id.picture_nav);
//		
//		if(location instanceof StopLocation){
//			StopLocation stopLocation = (StopLocation)location;
//			for(FPPicture image: stopLocation.getImageList()){
//				File inputFile = this.getExternalFilesDir(StorageManager.getImagePath(this, routeStorageName, image.getResource()));
//				Bitmap bm = BitmapFactory.decodeFile(inputFile.getAbsolutePath());
//				photoFrag.addImage(bm);
//			}
//			
//		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.augmented_binoculars:
				Intent i = new Intent(LocationDetailsActivity.this, FPBinocularActivity.class);
				String[] routeLocPair = {route.getStorageName(), location.getName()};
				i.putExtra("com.jmie.fieldplay.locationID", routeLocPair);
				startActivity(i);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
