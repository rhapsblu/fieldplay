package com.jmie.fieldplay.location;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.PowerManager;


public class SimpleAudioPlayer 	
								implements MediaPlayer.OnPreparedListener, 
										   MediaPlayer.OnErrorListener, 
										   MediaPlayer.OnCompletionListener{

	private MediaPlayer mp;
	public static String TAG = "SimpleAudioPlayer";
	private boolean loaded = false;
	private boolean paused = true;
	private AudioPlaylistFragment fragment;
	

	public SimpleAudioPlayer (AudioPlaylistFragment fragment){
		this.fragment = fragment;
	}
	public boolean setPlayer(String audioPath){
		loaded = false;
		if(mp==null){
			mp = new MediaPlayer();

			mp.setOnPreparedListener(this);
			mp.setOnErrorListener(this);
			mp.setOnCompletionListener(this);
		}
		else mp.reset();
		try {
			mp.setWakeMode(fragment.getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
			mp.setDataSource(audioPath);
			mp.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		fragment.mediaDone();
		paused = true;
		
	}
	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		loaded = false;
		releaseMediaPlayer();
		return false;
	}
	@Override
	public void onPrepared(MediaPlayer arg0) {
		loaded = true;
		if(!paused) mp.start();
	}
	public void releaseMediaPlayer(){
		loaded = false;
		if (mp != null) {
			mp.reset();
			mp.release();
			mp = null;
		}
	}
	public int getProgress(){
		if(mp==null) return 0;
		if(!loaded) return 0;
		int progress = (mp.getCurrentPosition()*100)/mp.getDuration();
		return progress;
	}
	public void pause(){
		paused = !paused;
		if((mp!=null) && mp.isPlaying()){
			mp.pause();
		}
	}
	public boolean play(int location){
		paused = false;
		if(mp==null) return false;
		if(!loaded)return false;
		mp.seekTo((mp.getDuration()*location)/100);
		mp.start();
		
		return true;
	}

}
