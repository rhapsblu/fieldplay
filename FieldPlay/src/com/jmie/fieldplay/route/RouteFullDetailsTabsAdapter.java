package com.jmie.fieldplay.route;

import java.util.ArrayList;
import java.util.List;




import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RouteFullDetailsTabsAdapter extends FragmentPagerAdapter{
	private RouteFullDetailsActivity activity;

	
	private int defaultRefSelect = 0;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	public RouteFullDetailsTabsAdapter(FragmentManager fm, RouteFullDetailsActivity activity) {
		super(fm);
		this.activity = activity;
		//setUpFragmentList();
		
	}

	@Override
	public Fragment getItem(int index) {
		switch(index){
		case 0:
			Bundle detailArgs = new Bundle();
			detailArgs.putParcelable("com.jmie.fieldplay.route", activity.getRoute());
//			detailArgs.putString("com.jmie.fieldplay.locationDescription", activity.getLocation().getDescription());
			return RouteDescriptionFragment.newInstance(detailArgs, activity);
		case 1:
			Bundle locArgs = new Bundle();
			locArgs.putParcelable("com.jmie.fieldplay.route", activity.getRoute());
			return RouteLocationsFragment.newInstance(locArgs, activity);
		case 2:
			Bundle referenceArgs = new Bundle();
			referenceArgs.putParcelable("com.jmie.fieldplay.route", activity.getRoute());
			referenceArgs.putInt("com.jmie.fieldplay.refselect", defaultRefSelect);
//			audioArgs.putParcelable("com.jmie.fieldplay.location", activity.getLocation());
			return ReferenceFragment.newInstance(referenceArgs, activity);
		}
		return fragments.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}
	public void setDefaultRefSelect(int i){
		defaultRefSelect = i;
	}
}
