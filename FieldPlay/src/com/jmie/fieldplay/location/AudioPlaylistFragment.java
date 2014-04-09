package com.jmie.fieldplay.location;

import com.jmie.fieldplay.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AudioPlaylistFragment extends Fragment{
	public static AudioPlaylistFragment newInstance(Bundle b){
		AudioPlaylistFragment fragment = new AudioPlaylistFragment();
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
 
        View rootView = inflater.inflate(R.layout.audio_playlist_fragment, container, false);
         
        return rootView;
    }
}
