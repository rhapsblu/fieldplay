package com.jmie.fieldplay.audioservice;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;


import com.jmie.fieldplay.R;
import com.jmie.fieldplay.route.FPAudio;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.storage.StorageManager;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

public class AudioService extends Service 
implements OnCompletionListener, OnPreparedListener{
	private Queue<FPAudio> audioQueue = new PriorityBlockingQueue<FPAudio>();
	private Route route;
   // private int mStartMode;       // indicates how to behave if the service is killed
   // private boolean mAllowRebind; // indicates whether onRebind should be used
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    //private Thread queueThread;
    
    private boolean paused = false;
    private boolean stop = false;
    private boolean preparing = false;
    private boolean prepared = false;
    
    private static int ONGOING_NOTIFICATION_ID = 1;
    public class LocalBinder extends Binder {
        public AudioService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AudioService.this;
        }
    }
    @Override
    public void onCreate(){
    	mediaPlayer = new MediaPlayer();
    	super.onCreate();
    	
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
    	super.onStartCommand(intent, flags, startId);
    	return START_STICKY;
    }
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	public void setRoute (Route r) {
		this.route = r;
		stopPlayer();
		audioQueue.clear();
	}
	public void stopPlayer(){
		if(mediaPlayer.isPlaying()) mediaPlayer.stop();
		stop = true;
		paused = false;
		this.stopForeground(true);

	}
	public void startPlayer(){
		stop = false;
		paused = false;
		runPlayer();
	}
	public void runPlayer(){
		if(mediaPlayer.isPlaying() || preparing || stop || paused)return;
		else if(prepared){
			prepared = false;
			mediaPlayer.start();
		}
		else{
			
			FPAudio audio = audioQueue.poll();
			 Notification notification = new Notification.Builder(this)
	         .setContentTitle("FP Player " )
	         .setContentText(audio.getName())
	         .setSmallIcon(R.drawable.av_play)
	         .build();
			startForeground(ONGOING_NOTIFICATION_ID, notification);
//			try {
//				mediaPlayer.setDataSource(StorageManager.getAudioPath(this, route.getName(), audio.getFilePath()));
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			preparing = true;
			mediaPlayer.prepareAsync();
		}
		
	}
	public void pausePlayer(){
		if(mediaPlayer.isPlaying()) mediaPlayer.pause();
		if(!stop) paused = true;
		this.stopForeground(false);


	}
	public void playLocation(String name){
		FPLocation l = route.getLocationByName(name);
		if(l instanceof InterestLocation){
			InterestLocation audioLocation = (InterestLocation)l;
			Iterator<FPAudio> audioIterator = audioLocation.getAudioIterator();
			while(audioIterator.hasNext()) audioQueue.offer(audioIterator.next());
		}
		runPlayer();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if(!audioQueue.isEmpty()){
			runPlayer();

		}
		else{
			this.stopForeground(true);
			stop = false;
			paused = false;
			preparing = false;
			prepared = false;
		}
		
	}
	@Override
	public void onPrepared(MediaPlayer arg0) {
		if((!paused)&&(!stop)){
			prepared = false;
			mediaPlayer.start();
		}
		preparing = false;
		prepared = true;
		
	}
	public void distory(){
		stopPlayer();
		mediaPlayer.release();
		this.stopSelf();
	}
}
