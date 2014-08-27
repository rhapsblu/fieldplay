package com.jmie.fieldplay.audioservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;


import com.google.android.gms.location.Geofence;
import com.jmie.fieldplay.R;
import com.jmie.fieldplay.audioservice.GeofenceUtils.REMOVE_TYPE;
import com.jmie.fieldplay.audioservice.GeofenceUtils.REQUEST_TYPE;
import com.jmie.fieldplay.location.LocationDetailsActivity;
import com.jmie.fieldplay.map.FPMapActivity;
import com.jmie.fieldplay.route.FPAudio;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.RouteLoaderActivity;
import com.jmie.fieldplay.storage.StorageManager;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

public class AudioService extends Service {
	

    
    /*Media player vars*/
	private Looper serviceLooper;
	private ServiceHandler serviceHandler;
	private Route route;
	private ArrayList<FPGeofence> geoFenceList;
	private Map<String, InterestLocation> fenceIdToLocation;
	private String TAG = "AudioService";
	private final class ServiceHandler extends Handler {
		public ServiceHandler (Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg){
			String id = msg.getData().getString("com.jmie.fieldplay.fence_id");
			Log.d(TAG, "Handler recieved id: " + id);
			sendNotification(id);
			playAudio(id);
		}
	}
	
	@Override
	public void onCreate(){
	
		fenceIdToLocation = new HashMap<String, InterestLocation>();
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_FOREGROUND);
		thread.start();
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);

        
		Toast.makeText(this, "Audio service ON", Toast.LENGTH_SHORT).show();

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(intent.getAction() == "com.jmie.fieldplay.start_audio_service"){
			route = intent.getExtras().getParcelable("com.jmie.fieldplay.route");
			geoFenceList = intent.getExtras().getParcelableArrayList("com.jmie.fieldplay.geofence_list");
			for(FPGeofence fpgeoFence : geoFenceList){
				fenceIdToLocation.put(fpgeoFence.getAlertId(), fpgeoFence.getInterestLocation());
				fenceIdToLocation.put(fpgeoFence.getContentId(), fpgeoFence.getInterestLocation());
			}
			NotificationCompat.Builder builder = new NotificationCompat
					.Builder(this)
					.setSmallIcon(R.drawable.ic_action_boot)
					.setContentTitle(getText(R.string.audio_ticker))
					.setContentText(route.getName());
			
			Intent mapIntent = new Intent(this, FPMapActivity.class);
			mapIntent.putExtra("com.jmie.fieldplay.routeData", route.getRouteData());
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(FPMapActivity.class);
			stackBuilder.addNextIntent(mapIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			startForeground(1, builder.build());
		}
		else if(intent.getAction()=="com.jmie.fieldplay.play_location"){
			String transitionType = intent.getStringExtra("com.jmie.fieldplay.transition_type");
			String[] ids = TextUtils.split(intent.getStringExtra("com.jmie.fieldplay.fence_ids"), GeofenceUtils.GEOFENCE_ID_DELIMITER.toString());
			for(String id: ids){
				if(id.startsWith("!")){
					Message msg = serviceHandler.obtainMessage();
					Bundle data = new Bundle();
					data.putString("com.jmie.fieldplay.fence_id", id);
				    msg.setData(data);
				    serviceHandler.sendMessage(msg);
				}
			}
			for(String id: ids){
				if(!id.startsWith("!")){
					Message msg = serviceHandler.obtainMessage();
					Bundle data = new Bundle();
					data.putString("com.jmie.fieldplay.fence_id", id);
				    msg.setData(data);
				    serviceHandler.sendMessage(msg);
				}
			}
		      
		}
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public void onDestroy(){
		Toast.makeText(this,  "Audio service OFF", Toast.LENGTH_SHORT).show();

	}
	private void sendNotification(String geoFenceID){
		if(geoFenceID.startsWith("!"))return;
		InterestLocation location = fenceIdToLocation.get(geoFenceID);
		NotificationCompat.Builder builder = new NotificationCompat
				.Builder(this)
				.setSmallIcon(R.drawable.ic_action_boot)
				.setContentTitle(getText(R.string.audio_ticker))
				.setContentText(location.getName());
		
		Intent locationIntent = new Intent(this, FPMapActivity.class);
		locationIntent.putExtra("com.jmie.fieldplay.route", route);
		locationIntent.putExtra("com.jmie.fieldplay.location", location.getName());
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(LocationDetailsActivity.class);
		stackBuilder.addNextIntent(locationIntent);
		Intent mapIntent = new Intent(this, FPMapActivity.class);
		mapIntent.putExtra("com.jmie.fieldplay.routeData", route.getRouteData());
		stackBuilder.addNextIntent(mapIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		 NotificationManager mNotificationManager =
		            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 mNotificationManager.notify(geoFenceID.hashCode(), builder.build());
	}
	private void playAudio(String geoFenceID){
		
	}
//	private Queue<FPAudio> audioQueue = new PriorityBlockingQueue<FPAudio>();
//	private Route route;
//   // private int mStartMode;       // indicates how to behave if the service is killed
//   // private boolean mAllowRebind; // indicates whether onRebind should be used
//    private final IBinder mBinder = new LocalBinder();
//    private MediaPlayer mediaPlayer;
//    //private Thread queueThread;
//    
//    private boolean paused = false;
//    private boolean stop = false;
//    private boolean preparing = false;
//    private boolean prepared = false;
//    
//    private static int ONGOING_NOTIFICATION_ID = 1;
//    public class LocalBinder extends Binder {
//        public AudioService getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return AudioService.this;
//        }
//    }
//    @Override
//    public void onCreate(){
//    	mediaPlayer = new MediaPlayer();
//    	super.onCreate();
//    	
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId){
//    	super.onStartCommand(intent, flags, startId);
//    	return START_STICKY;
//    }
//	@Override
//	public IBinder onBind(Intent arg0) {
//		return mBinder;
//	}
//	public void setRoute (Route r) {
//		this.route = r;
//		stopPlayer();
//		audioQueue.clear();
//	}
//	public void stopPlayer(){
//		if(mediaPlayer.isPlaying()) mediaPlayer.stop();
//		stop = true;
//		paused = false;
//		this.stopForeground(true);
//
//	}
//	public void startPlayer(){
//		stop = false;
//		paused = false;
//		runPlayer();
//	}
//	public void runPlayer(){
//		if(mediaPlayer.isPlaying() || preparing || stop || paused)return;
//		else if(prepared){
//			prepared = false;
//			mediaPlayer.start();
//		}
//		else{
//			
//			FPAudio audio = audioQueue.poll();
//			 Notification notification = new Notification.Builder(this)
//	         .setContentTitle("FP Player " )
//	         .setContentText(audio.getName())
//	         .setSmallIcon(R.drawable.av_play)
//	         .build();
//			startForeground(ONGOING_NOTIFICATION_ID, notification);
////			try {
////				mediaPlayer.setDataSource(StorageManager.getAudioPath(this, route.getName(), audio.getFilePath()));
////			} catch (IllegalArgumentException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (SecurityException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (IllegalStateException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
//			preparing = true;
//			mediaPlayer.prepareAsync();
//		}
//		
//	}
//	public void pausePlayer(){
//		if(mediaPlayer.isPlaying()) mediaPlayer.pause();
//		if(!stop) paused = true;
//		this.stopForeground(false);
//
//
//	}
//	public void playLocation(String name){
//		FPLocation l = route.getLocationByName(name);
//		if(l instanceof InterestLocation){
//			InterestLocation audioLocation = (InterestLocation)l;
//			Iterator<FPAudio> audioIterator = audioLocation.getAudioIterator();
//			while(audioIterator.hasNext()) audioQueue.offer(audioIterator.next());
//		}
//		runPlayer();
//	}
//
//	@Override
//	public void onCompletion(MediaPlayer arg0) {
//		if(!audioQueue.isEmpty()){
//			runPlayer();
//
//		}
//		else{
//			this.stopForeground(true);
//			stop = false;
//			paused = false;
//			preparing = false;
//			prepared = false;
//		}
//		
//	}
//	@Override
//	public void onPrepared(MediaPlayer arg0) {
//		if((!paused)&&(!stop)){
//			prepared = false;
//			mediaPlayer.start();
//		}
//		preparing = false;
//		prepared = true;
//		
//	}
//	public void distory(){
//		stopPlayer();
//		mediaPlayer.release();
//		this.stopSelf();
//	}
}
