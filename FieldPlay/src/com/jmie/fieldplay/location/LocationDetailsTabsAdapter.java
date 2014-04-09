package com.jmie.fieldplay.location;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LocationDetailsTabsAdapter extends FragmentPagerAdapter{
	private LocationDetailsActivity activity;
	public static String TAG = "Tab adapter";
	public List<Fragment> fragments = new ArrayList<Fragment>();
	public LocationDetailsTabsAdapter(FragmentManager fm, LocationDetailsActivity activity) {
		super(fm);
		this.activity = activity;
		//setUpFragmentList();
		
	}
//	private void setUpFragmentList(){
//		Fragment descriptionFrag = new LocationDescriptionFragment();
//		Bundle detailArgs = new Bundle();
//		detailArgs.putString("com.jmie.fieldplay.locationName", activity.getLocation().getName());
//		detailArgs.putString("com.jmie.fieldplay.locationDescription", activity.getLocation().getDescription());
//		descriptionFrag.setArguments(detailArgs);
//		
//		Fragment viewGridFrag = new ImageViewGridFragment();
//		Bundle gridArgs = new Bundle();
//		gridArgs.putParcelable("com.jmie.fieldplay.location", activity.getLocation());
//		gridArgs.putString("com.jmie.fieldplay.routeStorageName", activity.getRoute().getStorageName());
//		viewGridFrag.setArguments(gridArgs);
//
//		
//		Fragment audioFrag = new AudioPlaylistFragment();
//		Bundle audioArgs = new Bundle();
//		audioFrag.setArguments(audioArgs);
//		
//		Fragment vidioFrag = new AudioPlaylistFragment();
//		Bundle vidioArgs = new Bundle();
//		vidioFrag.setArguments(vidioArgs);
//		
//		fragments.add(descriptionFrag);
//		fragments.add(viewGridFrag);
//		fragments.add(audioFrag);
//		fragments.add(vidioFrag);
//		
//		
//		
//	}
	@Override
	public Fragment getItem(int index) {
		switch(index){
		case 0:
			Bundle detailArgs = new Bundle();
			detailArgs.putString("com.jmie.fieldplay.locationName", activity.getLocation().getName());
			detailArgs.putString("com.jmie.fieldplay.locationDescription", activity.getLocation().getDescription());
			return LocationDescriptionFragment.newInstance(detailArgs);
		case 1:
			Bundle gridArgs = new Bundle();
			gridArgs.putParcelable("com.jmie.fieldplay.location", activity.getLocation());
			gridArgs.putString("com.jmie.fieldplay.routeStorageName", activity.getRoute().getStorageName());
			return ImageViewGridFragment.newInstance(gridArgs);
		case 2:
			Bundle audioArgs = new Bundle();
			return AudioPlaylistFragment.newInstance(audioArgs);
		case 3:
			Bundle videoArgs = new Bundle();
			return AudioPlaylistFragment.newInstance(videoArgs);
		}
		return fragments.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}

}
