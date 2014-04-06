package com.jmie.fieldplay.location;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VideoPlaylistFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.video_playlist_fragment, container, false);
         
        return rootView;
    }
}
