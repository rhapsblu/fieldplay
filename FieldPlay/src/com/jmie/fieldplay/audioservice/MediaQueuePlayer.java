package com.jmie.fieldplay.audioservice;

import java.io.IOException;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;



public class MediaQueuePlayer implements 
			MediaPlayer.OnPreparedListener, 
			MediaPlayer.OnErrorListener, 
			MediaPlayer.OnCompletionListener{
	
	private Queue<AudioBundle> bundleQueue;
	private MediaPlayer mediaPlayer;
	private Context context;
	private AudioBundle currentBundle;
	private boolean mute = false;
	
	public MediaQueuePlayer(Context c){
		bundleQueue = new ConcurrentLinkedQueue<AudioBundle>();
		context = c;
	}
	public void addAudio(List<String> audioPaths){
		AudioBundle audioBundle = new AudioBundle(audioPaths);
		bundleQueue.add(audioBundle);
	}
	public void startPlayer(){
		
		if(mediaPlayer != null) {
//			Log.d(TAG, "MediaPlayer already active");
			return;
		}
		if(!hasNextAudio()){
			releaseMediaPlayer();
			return;
		}
		mediaPlayer = new MediaPlayer();
		if(mute)mediaPlayer.setVolume(0, 0);
		mediaPlayer.setWakeMode(context.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		try {
			String audioPath = getNextAudio();
//			Log.d(TAG, "Prepping path: " + audioPath);
			mediaPlayer.setDataSource(audioPath);
			mediaPlayer.prepareAsync();
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
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(hasNextAudio()){
			try {
				mp.reset();
				mp.setDataSource(getNextAudio());
				mp.prepareAsync();
			} catch (IllegalArgumentException e) {
				releaseMediaPlayer();
				startPlayer();
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				releaseMediaPlayer();
				startPlayer();
				e.printStackTrace();
			} catch (IOException e) {
				releaseMediaPlayer();
				startPlayer();
				e.printStackTrace();
			}
		}
		else releaseMediaPlayer();	
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mp.reset();
		if(hasNextAudio()){
			try {
				mp.setDataSource(getNextAudio());
				mp.prepareAsync();
			} catch (IllegalArgumentException e) {
				releaseMediaPlayer();
				startPlayer();
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				releaseMediaPlayer();
				startPlayer();
				e.printStackTrace();
			} catch (IOException e) {
				releaseMediaPlayer();
				startPlayer();
				e.printStackTrace();
			}
		}
		else releaseMediaPlayer();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
//		Log.d(TAG, "Playing audio");
		mediaPlayer.start();
		
	}
	public void mute(boolean mute){
		this.mute = mute;
		if(mediaPlayer == null) return;
		if(mute)
			mediaPlayer.setVolume(0, 0);
		else
			mediaPlayer.setVolume(1, 1);
	}
	public void releaseMediaPlayer(){
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
//		Log.d(TAG, "Media Player released");
	}
	private String getNextAudio(){
		if((currentBundle==null)&&bundleQueue.isEmpty())return null;
		if(currentBundle == null) currentBundle = bundleQueue.poll();
		if(currentBundle.hasNext()) return currentBundle.getNext();
		if(bundleQueue.isEmpty()) return null;
		currentBundle = bundleQueue.poll();
		return getNextAudio();
	}
	private boolean hasNextAudio(){
		if((currentBundle==null)&&bundleQueue.isEmpty())return false;
		if(currentBundle == null) currentBundle = bundleQueue.poll();
		if(currentBundle.hasNext()) return true;
		if(bundleQueue.isEmpty()) return false;
		currentBundle = bundleQueue.poll();
		return hasNextAudio();
	}
	private class AudioBundle {
		private Queue<String> bundleQueue = new PriorityQueue<String>();
		private AudioBundle(List<String> audioPaths){
			for(String audioPath: audioPaths) bundleQueue.add(audioPath);
		}
		public boolean hasNext(){
			return !bundleQueue.isEmpty();
		}
		public String getNext(){
			return bundleQueue.poll();
		}
	}
}
