package com.jmie.fieldplay.route;



import com.jmie.fieldplay.R;


import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;

public class RouteDescriptionFragment extends Fragment {
	private Route route;
	private Context c;

	
	public static RouteDescriptionFragment newInstance(Bundle b, Context c){
		RouteDescriptionFragment fragment = new RouteDescriptionFragment(c);
		fragment.setArguments(b);

		return fragment;
	}
	public RouteDescriptionFragment(Context c){
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
        View rootView = inflater.inflate(R.layout.route_full_detail_fragment, container, false);


        TextView name = (TextView) rootView.findViewById(R.id.route_name);
        name.setText(route.getName());
        TextView length = (TextView) rootView.findViewById(R.id.route_milage);
        length.setText("Route Length: " + Double.toString(route.getLength()));
        TextView description = (TextView) rootView.findViewById(R.id.route_description);
        description.setText(route.getDescription());

        return rootView;
    }
    @Override
    public void setArguments(Bundle b){
    	super.setArguments(b);
		
		route = b.getParcelable("com.jmie.fieldplay.route");
		
    }
    @Override
    public void onStart(){
    	super.onStart();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);

		
    }

}
