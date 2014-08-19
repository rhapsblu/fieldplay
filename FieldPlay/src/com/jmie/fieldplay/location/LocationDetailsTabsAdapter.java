package com.jmie.fieldplay.location;

import java.util.ArrayList;
import java.util.List;

import com.jmie.fieldplay.route.FPPicture;


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
		//	gridArgs.putParcelable("com.jmie.fieldplay.location", activity.getLocation());
			gridArgs.putParcelableArrayList("com.jmie.fieldplay.locations", (ArrayList<FPPicture>)activity.getLocation().getImageList());
			
			gridArgs.putParcelable("com.jmie.fieldplay.routeData", activity.getRoute().getRouteData());
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
