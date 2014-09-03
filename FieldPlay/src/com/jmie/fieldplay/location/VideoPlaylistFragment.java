package com.jmie.fieldplay.location;

import com.jmie.fieldplay.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VideoPlaylistFragment extends Fragment{
	public static VideoPlaylistFragment newInstance(Bundle b){
		VideoPlaylistFragment fragment = new VideoPlaylistFragment();
		fragment.setArguments(b);
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedinstance){
		super.onCreate(savedinstance);
		//do something with passed values here
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	if(true) return getDefaultView();
        View rootView = inflater.inflate(R.layout.video_playlist_fragment, container, false);
         
        return rootView;
    }
    
    private View getDefaultView(){
    	TextView tv = new TextView(getActivity());
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
    	tv.setText("No video content for this location");
    	return tv;
    }
}
