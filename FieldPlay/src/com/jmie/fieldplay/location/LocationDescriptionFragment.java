package com.jmie.fieldplay.location;



import com.jmie.fieldplay.R;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class LocationDescriptionFragment extends Fragment{
	public static String TAG = "Description Fragment";
	private String locationName;
	private String locationDescription;
	public static LocationDescriptionFragment newInstance(Bundle b){
		LocationDescriptionFragment fragment = new LocationDescriptionFragment();
		fragment.setArguments(b);
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedinstance){
		super.onCreate(savedinstance);
		locationName = this.getArguments().getString("com.jmie.fieldplay.locationName");
		locationDescription = this.getArguments().getString("com.jmie.fieldplay.locationDescription");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.location_detail_fragment, container, false);
		if(locationDescription.length()==0) return getDefaultView();
		

		TextView name = (TextView)rootView.findViewById(R.id.location_name);
		TextView description = (TextView)rootView.findViewById(R.id.location_description);
		description.setText(locationDescription);
		description.setMovementMethod(new ScrollingMovementMethod());
		name.setText(locationName);
		Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "Roboto-Medium.ttf");
		name.setTypeface(font);
		return rootView;
	}
    private View getDefaultView(){
    	TextView tv = new TextView(getActivity());
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
    	tv.setText("No description for this location");
    	return tv;
    }

}
