package com.jmie.fieldplay.route;


import java.util.List;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.location.LocationDetailsActivity;

import com.jmie.fieldplay.route.FPLocation;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;

import android.widget.TextView;

public class RouteLocationsFragment extends Fragment {
	private Route route;
	private List<FPLocation> locationList;
	private ListView lv;
	private LocationsListAdapter locationsListAdapter;
	private String TAG = "Playlist Fragment";
	
	private Context c;

	public static RouteLocationsFragment newInstance(Bundle b, Context c){
		RouteLocationsFragment fragment = new RouteLocationsFragment(c);
		fragment.setArguments(b);

		return fragment;
	}
	public RouteLocationsFragment(Context c){
		this.c = c;
	}
	@Override
	public void onCreate(Bundle savedinstance){
		super.onCreate(savedinstance);
		setRetainInstance(true);

	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	if(locationList.size()==0) return getDefaultView();
        View rootView = inflater.inflate(R.layout.location_list_fragment, container, false);


        lv = (ListView) rootView.findViewById(R.id.locationlist);
		locationsListAdapter = new LocationsListAdapter(this);
	
		lv.setAdapter(locationsListAdapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
	            FPLocation location = locationsListAdapter.getItem(position);
	
	            Log.d(TAG, "Item clicked " + position );
				Intent i = new Intent(RouteLocationsFragment.this.getActivity(), LocationDetailsActivity.class);
				i.putExtra("com.jmie.fieldplay.route", route);
				i.putExtra("com.jmie.fieldplay.location", location.getName());
				
				startActivity(i);	
			}
		});

        return rootView;
    }
    @Override
    public void setArguments(Bundle b){
    	super.setArguments(b);
		route = b.getParcelable("com.jmie.fieldplay.route");
		locationList = route.getLocationList();	
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);

		
    }
    private View getDefaultView(){
    	TextView tv = new TextView(getActivity());
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
    	tv.setText("No locations found");
    	return tv;
    }
    public List<FPLocation> getLocationList(){
    	return locationList;
    }



}
