package com.jmie.fieldplay.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.route.FPAudio;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.RouteData;
import com.jmie.fieldplay.storage.StorageManager;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioPlaylistFragment extends Fragment {
	private RouteData routeData;
	private FPLocation location;
	private List<FPAudio> audioList;
	private ListView lv;
	private AudioPlaylistAdapter playlistAdapter;
	
	private SimpleAudioPlayer audioPlayer;
	private ImageButton playButton;
	private View playControls;
	private SeekBar seekBar;
	private boolean play = false;
	private Context c;

	
	public static AudioPlaylistFragment newInstance(Bundle b, Context c){
		AudioPlaylistFragment fragment = new AudioPlaylistFragment(c);
		fragment.setArguments(b);

		return fragment;
	}
	public AudioPlaylistFragment(Context c){
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
    	if(audioList.size()==0) return getDefaultView();
        View rootView = inflater.inflate(R.layout.audio_playlist_fragment, container, false);


        lv = (ListView) rootView.findViewById(R.id.audiolist);
		playlistAdapter = new AudioPlaylistAdapter(this);
	
		lv.setAdapter(playlistAdapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		playControls = (View)rootView.findViewById(R.id.controller);
    	playControls.setVisibility(View.INVISIBLE);


    	seekBar = (SeekBar)rootView.findViewById(R.id.mediaSeekBar);
    	seekBar.setMax(100);
    	seekBar.setOnSeekBarChangeListener(new OnSeekListener());
    	playButton = (ImageButton)rootView.findViewById(R.id.singlePlayButton);
    	playButton.setOnClickListener(new OnPlayButtonListener());
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
				playControls.setVisibility(View.VISIBLE);
	            for (int j = 0; j < parentAdapter.getChildCount(); j++)
	                parentAdapter.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

	            // change the background color of the selected element
	            view.setBackgroundColor(Color.LTGRAY);
	            seekBar.setProgress(0);
	            //				TextView clickedView = (TextView) view;
	            FPAudio audio = playlistAdapter.getItem(position);
	            audioPlayer.setPlayer(StorageManager.getAudioPath(getActivity(), routeData, audio.getFilePath()));
	
			}
		});
		audioPlayer = new SimpleAudioPlayer(this);
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
			while(audioIterator.hasNext()){
				FPAudio audio= audioIterator.next();
				if(audio.getPriority()>=c.getResources().getInteger(R.integer.alert_priority_threshold)){
					audioList.add(audio);
				}
			}
			Collections.sort(audioList);
		}
		
		
    }
    @Override
    public void onStart(){
    	super.onStart();
    	audioPlayer = new SimpleAudioPlayer(this);

    }
    @Override
    public void onDestroyView(){
    	audioPlayer.releaseMediaPlayer();
    	super.onDestroyView();
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
	public void mediaDone(){
		play = false;
		playButton.setImageDrawable(getResources().getDrawable(R.drawable.av_play));
	}
	public class OnPlayButtonListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			play = !play;
			if(play){
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.av_stop));
				audioPlayer.play(seekBar.getProgress());
				new SeekbarProgressHandler().execute();
			}
			else{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.av_play));
				audioPlayer.pause();
			}
			
		}
	}
	public class OnSeekListener implements SeekBar.OnSeekBarChangeListener{


		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(fromUser){

				if(play) audioPlayer.play(seekBar.getProgress());
			}
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
	}
	public class SeekbarProgressHandler extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			while(play){
				seekBar.setProgress(audioPlayer.getProgress());
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}


		
	}
}
