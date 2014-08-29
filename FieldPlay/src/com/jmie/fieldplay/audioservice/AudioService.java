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
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
	private MediaQueuePlayer player;
	private boolean muted = false;
	private final class ServiceHandler extends Handler {
		public ServiceHandler (Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg){
			String id = msg.getData().getString("com.jmie.fieldplay.fence_id");
			Log.d(TAG, "Handler recieved id: " + id);
			sendNotification(id);
			if(muted) return;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		player = new MediaQueuePlayer(this);
        
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
			Intent muteIntent = new Intent(this, AudioService.class);
			muteIntent.setAction("com.jmie.fieldplay.mute_audio");
			PendingIntent mutePending = PendingIntent.getService(this, 0, muteIntent, 0);
			NotificationCompat.Builder builder = new NotificationCompat
					.Builder(this)
					.setSmallIcon(R.drawable.ic_action_boot)
					.setLargeIcon(((BitmapDrawable)this.getResources().getDrawable(R.drawable.fp_logo_notification)).getBitmap())
					.setContentTitle(getText(R.string.audio_ticker))
					.setContentText(route.getName())
					.setPriority(NotificationCompat.PRIORITY_MAX)
					.addAction(R.drawable.ic_action_volume_on, "Mute Audio", mutePending);
			
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
			Log.d(TAG, "recieved fence notification: " + intent.getStringExtra("com.jmie.fieldplay.fence_ids"));
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
		else if(intent.getAction()=="com.jmie.fieldplay.mute_audio"){
			muted = !muted;
			Log.d(TAG, "Audio mute: " + muted);
			Intent muteIntent = new Intent(this, AudioService.class);
			muteIntent.setAction("com.jmie.fieldplay.mute_audio");
			PendingIntent mutePending = PendingIntent.getService(this, 0, muteIntent, 0);
			NotificationCompat.Builder builder = new NotificationCompat
					.Builder(this)
					.setSmallIcon(R.drawable.ic_action_boot)
					.setLargeIcon(((BitmapDrawable)this.getResources().getDrawable(R.drawable.fp_logo_notification)).getBitmap())
					.setContentTitle(getText(R.string.audio_ticker))
					.setPriority(NotificationCompat.PRIORITY_MAX)
					.setContentText(route.getName());
			
					if(muted)builder.addAction(R.drawable.ic_action_volume_muted, "Unmute Audio", mutePending);
					else builder.addAction(R.drawable.ic_action_volume_on, "Mute Audio", mutePending);
			
			Intent mapIntent = new Intent(this, FPMapActivity.class);
			mapIntent.putExtra("com.jmie.fieldplay.routeData", route.getRouteData());
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(FPMapActivity.class);
			stackBuilder.addNextIntent(mapIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			startForeground(1, builder.build());
		}
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public void onDestroy(){
		player.releaseMediaPlayer();
		Toast.makeText(this,  "Audio service OFF", Toast.LENGTH_SHORT).show();

	}
	private void sendNotification(String geoFenceID){
		if(geoFenceID.startsWith("!"))return;
		InterestLocation location = fenceIdToLocation.get(geoFenceID);

		NotificationCompat.Builder builder = new NotificationCompat
				.Builder(this)
				.setSmallIcon(R.drawable.ic_action_boot)
				.setLargeIcon(((BitmapDrawable)this.getResources().getDrawable(R.drawable.fp_logo_notification)).getBitmap())
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
				.setContentTitle(location.getName())
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setLights(0xFF00FF00, 300, 100)
				.setContentText(location.getDescription());

		Intent locationIntent = new Intent(this, LocationDetailsActivity.class);
		locationIntent.putExtra("com.jmie.fieldplay.route", route);
		locationIntent.putExtra("com.jmie.fieldplay.location", location.getName());
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(LocationDetailsActivity.class);
		Intent mapIntent = new Intent(this, FPMapActivity.class);
		mapIntent.putExtra("com.jmie.fieldplay.routeData", route.getRouteData());
		mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		stackBuilder.addNextIntent(mapIntent);
		stackBuilder.addNextIntent(locationIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		 NotificationManager mNotificationManager =
		            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 
		 mNotificationManager.notify(geoFenceID.hashCode(), builder.build());
	}
	private void playAudio(String geoFenceID){
		InterestLocation location = fenceIdToLocation.get(geoFenceID);
		Iterator<FPAudio> iterator = location.getAudioIterator();
		if(!iterator.hasNext()) return;
		List<String> pathList = new ArrayList<String>();
		while(iterator.hasNext()){
			FPAudio fpAudio = iterator.next();
			if(geoFenceID.startsWith("!")&&(fpAudio.getPriority()<50))continue;
			String localPath = StorageManager.getAudioPath(this, route.getRouteData(), fpAudio.getFilePath());
			Log.d(TAG, "Path to add is " + localPath);
			pathList.add(localPath);
		}
		
		player.addAudio(pathList);
		Log.d(TAG, "Audio added, Starting player");
		player.startPlayer();
	}

}
