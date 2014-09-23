package com.jmie.fieldplay.route;



import com.jmie.fieldplay.R;

import com.jmie.fieldplay.route.Route;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;





public class RouteFullDetailsActivity extends FragmentActivity implements TabListener{

	private Route route;

	private String[] tabs = {"Description", "Locations", "References"};
	private ViewPager viewPager;
	private RouteFullDetailsTabsAdapter mAdapter;
	private ActionBar actionBar;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();

		route = (Route)b.getParcelable("com.jmie.fieldplay.route");

		
		
		setContentView(R.layout.activity_route_full_detail);
		mAdapter = new RouteFullDetailsTabsAdapter(getSupportFragmentManager(), this);
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
		if(b.containsKey("com.jmie.fieldplay.reference")){
			mAdapter.setDefaultRefSelect(b.getInt("com.jmie.fieldplay.reference"));
			getActionBar().setSelectedNavigationItem(2);
		}
	}	

	@Override
	protected void onResume(){
		super.onResume();
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

	public Route getRoute(){
		return route;
	}

}
