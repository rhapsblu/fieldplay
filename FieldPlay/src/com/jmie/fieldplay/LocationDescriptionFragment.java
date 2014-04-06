package com.jmie.fieldplay;



import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class LocationDescriptionFragment extends Fragment{
	public static String TAG = "Description Fragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.location_detail_fragment, container, false);
		Log.d(TAG, "Frament create view");
		
		String locationName = this.getArguments().getString("com.jmie.fieldplay.locationName");
		String locationDescription = this.getArguments().getString("com.jmie.fieldplay.locationDescription");
		TextView name = (TextView)rootView.findViewById(R.id.location_name);
		TextView description = (TextView)rootView.findViewById(R.id.location_description);
		description.setText(locationDescription);
		name.setText(locationName);
		Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "Roboto-Medium.ttf");
		name.setTypeface(font);
		return rootView;
	}
	

}
