package com.jmie.fieldplay.route;

import com.jmie.fieldplay.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RouteDetailsFragment extends Fragment{
	OnRouteSelectedListener mCallback;
	View view;
	Button button;
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.route_detail_fragment, container, false);
    	button = (Button)view.findViewById(R.id.route_select_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCallback.onRouteSelected();
            }
        });
        button.setEnabled(false);
        // Inflate the layout for this fragment         
        return view;
    }
    
    public interface OnRouteSelectedListener {
    	public void onRouteSelected();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRouteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRouteSelectedListener");
        }
    }
    public void displayRouteDetails(String details){
    	TextView description = (TextView)getView().findViewById(R.id.route_details);
    	description.setText(details);
    	button.setEnabled(true);
    }
    public void clear(){
    	TextView description = (TextView)getView().findViewById(R.id.route_details);
    	description.setText(getString(R.string.no_route_selected));
    	button.setEnabled(false);
    }
}
