package com.jmie.fieldplay.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.route.FPAudio;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.RouteData;

import android.graphics.Color;
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

public class AudioPlaylistFragment extends Fragment{
	private RouteData routeData;
	private FPLocation location;
	private List<FPAudio> audioList;
	private ListView lv;
	private AudioPlaylistAdapter playlistAdapter;
	private String TAG = "Playlist Fragment";
	
	public static AudioPlaylistFragment newInstance(Bundle b){
		AudioPlaylistFragment fragment = new AudioPlaylistFragment();
		fragment.setArguments(b);
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedinstance){
		super.onCreate(savedinstance);
		setRetainInstance(true);

	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	if(audioList.size()==0) return getDefaultView();
        View rootView = inflater.inflate(R.layout.audio_playlist_fragment, container, false);

       // lv = (ListView) this.getActivity().findViewById(R.id.audiolist);
        lv = (ListView) rootView.findViewById(R.id.audiolist);
		playlistAdapter = new AudioPlaylistAdapter(this);
//		
		lv.setAdapter(playlistAdapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    	
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
	            for (int j = 0; j < parentAdapter.getChildCount(); j++)
	                parentAdapter.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

	            // change the background color of the selected element
	            view.setBackgroundColor(Color.LTGRAY);
//				TextView clickedView = (TextView) view;

	            Log.d(TAG, "Item clicked " + position );
	
			}
		});

        return rootView;
    }
    @Override
    public void setArguments(Bundle b){
    	super.setArguments(b);
		audioList = new ArrayList<FPAudio>();
		
		location = b.getParcelable("com.jmie.fieldplay.location");
		routeData = b.getParcelable("com.jmie.fieldplay.routeData");
		if(location instanceof InterestLocation){
			InterestLocation interestLocation = (InterestLocation)location;
			Iterator<FPAudio> audioIterator = interestLocation.getAudioIterator();
			while(audioIterator.hasNext()) audioList.add(audioIterator.next());
			Collections.sort(audioList);
		}
		
		
    }
    @Override
    public void onStart(){
    	super.onStart();


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);

		
    }
    private View getDefaultView(){
    	TextView tv = new TextView(getActivity());
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
    	tv.setText("No audio content for this location");
    	return tv;
    }
    public List<FPAudio> getAudioList(){
    	return audioList;
    }
}
