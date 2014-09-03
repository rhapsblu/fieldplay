package com.jmie.fieldplay.location;

import java.util.List;

import com.jmie.fieldplay.R;
import com.jmie.fieldplay.route.FPAudio;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AudioPlaylistAdapter extends BaseAdapter{
	private AudioPlaylistFragment playFragment;
	private List<FPAudio> audioList;
	private LayoutInflater inflater;
	
	public AudioPlaylistAdapter(AudioPlaylistFragment playFragment ){
		this.playFragment = playFragment;
		audioList = playFragment.getAudioList();
		inflater = LayoutInflater.from(playFragment.getActivity());
	}
	@Override
	public int getCount() {
		return audioList.size();
	}

	@Override
	public FPAudio getItem(int i) {
		return audioList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(contentView == null){
			view = inflater.inflate(R.layout.route_row, parent, false);
			holder = new ViewHolder();
			holder.audioName = (TextView)view.findViewById(R.id.routename);

			view.setTag(holder);
		}
		else{
			view = contentView;
			holder = (ViewHolder)view.getTag();
		}
		holder.audioName.setText(audioList.get(position).getName());
		return view;
	}
	private class ViewHolder {
		private TextView audioName;
	}
}
