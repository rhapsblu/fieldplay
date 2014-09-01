package com.jmie.fieldplay.map;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.ui.IconGenerator;
import com.jmie.fieldplay.R;
import com.jmie.fieldplay.audioservice.AudioService;
import com.jmie.fieldplay.audioservice.FPGeofence;
import com.jmie.fieldplay.audioservice.GeofenceRemover;
import com.jmie.fieldplay.audioservice.GeofenceRequester;
import com.jmie.fieldplay.audioservice.GeofenceUtils;
import com.jmie.fieldplay.audioservice.GeofenceUtils.REMOVE_TYPE;
import com.jmie.fieldplay.audioservice.GeofenceUtils.REQUEST_TYPE;
import com.jmie.fieldplay.location.LocationDetailsActivity;
import com.jmie.fieldplay.route.BinocularLocation;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.RouteData;
import com.jmie.fieldplay.route.StopLocation;
import com.jmie.fieldplay.storage.StorageManager;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class FPMapActivity extends 
								FragmentActivity 
							implements
								OnMarkerClickListener,
								OnCameraChangeListener

{
	

    /*UI vars*/
	private MenuItem toggle;
    private PopupMenu layerMenu;
    
	/*Map vars*/
	private GoogleMap mMap;
	private List<Marker> markerList = new ArrayList<Marker>();
	private Map<Marker, FPLocation> markerToLocation = new HashMap<Marker, FPLocation>();
	private CustomLayerTileProvider tileProvider = new CustomLayerTileProvider();
    private TileOverlay tileOverlay;
	private List<Circle> circleList;
	private Route route;
	public static String TAG = "FP Mapp";
	private CameraPosition savedCamera;

    /*Geofence vars*/
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;

    // Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;
    List<Geofence> mCurrentGeofences;
    ArrayList<FPGeofence> currentFPGeofences;
    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    
    private FPGeofenceReceiver mBroadcastReceiver;
    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;

    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		//c = this;
		RouteData routeData = b.getParcelable("com.jmie.fieldplay.routeData");
        circleList = new ArrayList<Circle>();
		//route = b.getParcelable("com.jmie.fieldplay.route");
		route = StorageManager.buildRoute(this, routeData);

		setContentView(R.layout.activity_fpmap);
		setUpMapIfNeeded();

        layerMenu = new PopupMenu(this, findViewById(R.id.popupAnchor));
        List<MapLayer> mapLayers = route.getMapLayers();

        layerMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "No layers");
        	
        for(int i = 2; i<=mapLayers.size()+1; i++){
        	layerMenu.getMenu().add(Menu.NONE, i, Menu.NONE, mapLayers.get(i-2).getName());
        }
        layerMenu.setOnMenuItemClickListener(new LayerMenuItemClickListener());

        mBroadcastReceiver = new FPGeofenceReceiver();
        mIntentFilter = new IntentFilter();
        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
        mCurrentGeofences = new ArrayList<Geofence>();
        currentFPGeofences = new ArrayList<FPGeofence>();
        mGeofenceRequester = new GeofenceRequester(this);
        mGeofenceRemover = new GeofenceRemover(this);
	}
	@Override
	protected void onResume(){
		super.onResume();

			setUpMapIfNeeded();

		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
	}
    @Override
    public void onPause() {
        super.onPause();

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.fp_map, menu);
		toggle = menu.findItem(R.id.service_control);
		if(StorageManager.getAudioTourStatus(this)){toggle.setIcon(R.drawable.av_stop);}
		else toggle.setIcon(R.drawable.av_play);

		return true;
	}
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelable("com.jmie.fieldplay.map_camera", savedCamera);
	}
	@Override 
	public void onRestoreInstanceState(Bundle inState){
		super.onRestoreInstanceState(inState);
		savedCamera = inState.getParcelable("com.jmie.fieldplay.map_camera");
	}
    private void setUpMapIfNeeded(){
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.setMyLocationEnabled(true);
                
            }
            mMap.setOnInfoWindowClickListener(new InfoWindowClickListener());
        }
    }

    private void setUpMap(){
    	Log.d(TAG, "Setting up map");
        // Add lots of markers to the map.
        addMarkersToMap();
        generateGeofenceLines(mMap);
        //addRouteLineToMap();
        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraChangeListener(this);
        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                	LatLngBounds.Builder  builder = new LatLngBounds.Builder();
                	for(Marker m: markerList){
                		builder.include(m.getPosition());
                	}
                    LatLngBounds bounds = builder.build();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                      mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                      mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    Log.d(TAG, "Moving camera to bounds");
                    
                    
                    if(savedCamera==null){
                    	Log.d(TAG, "Saved camera is null");
                    	mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    }
                    else
                    	mMap.moveCamera(CameraUpdateFactory.newCameraPosition(savedCamera));
                }
            });
        }
    }
    private void addMarkersToMap() {
    	//Log.d(TAG, "Adding markers to map");
    	IconGenerator iconFactory = new IconGenerator(this);
    	int locationCount = 1;
    	for(FPLocation location: route.getLocationList()){
    		//Log.d(TAG, "Adding marker for " + location.getName() + " lat: " + location.getLatitude() + " long: " + location.getLongitude());
    		String iconText = "";
    		Bitmap bitmap;
    		if(location instanceof BinocularLocation){
    			iconFactory.setStyle(IconGenerator.STYLE_BLUE);
    			iconText = "B";
    			Bitmap oldBitmap = iconFactory.makeIcon(iconText);
    			bitmap = Bitmap.createScaledBitmap(oldBitmap, oldBitmap.getWidth()/2, oldBitmap.getHeight()/2, false);
    		}
    		else{
    			iconText = Integer.toString(locationCount);
    			locationCount++;
    			if(location instanceof StopLocation) iconFactory.setStyle(IconGenerator.STYLE_RED);
    			else iconFactory.setStyle(IconGenerator.STYLE_GREEN);
    			bitmap = iconFactory.makeIcon(iconText);
    		}
            MarkerOptions markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

    		Marker marker = mMap.addMarker(markerOptions);
    		marker.setTitle(location.getName());
    		marker.setSnippet("Latitude: " + location.getLatitude() + " Longitude: "+location.getLongitude());
    		markerList.add(marker);
    		markerToLocation.put(marker, location);

    	}

    }
    @Deprecated
    private void addRouteLineToMap(){
    	//Log.d(TAG, "Adding route lines to map");
    	PolylineOptions routeLine = new PolylineOptions();
    	for(FPLocation location: route.getLocationList()){
    		if(!(location instanceof BinocularLocation))
    			routeLine.add(new LatLng(location.getLatitude(), location.getLongitude()));
    	}
    
    	mMap.addPolyline(routeLine);
    }
    private void generateGeofenceLines(GoogleMap map){
    	for(FPLocation location: route.getLocationList()){
    		if(location instanceof StopLocation){
    			StopLocation stop = (StopLocation)location;
    			CircleOptions alertCircle = new CircleOptions()
    			.center(new LatLng(stop.getLatitude(), stop.getLongitude()))
    			.radius(stop.getAlertRadius())
    			.strokeWidth(3f)
    			.fillColor(0x11FF0000)
    			.strokeColor(0x88FF0000);
    			circleList.add(map.addCircle(alertCircle));
    			
    			CircleOptions contentCircle = new CircleOptions()
    			.center(new LatLng(stop.getLatitude(), stop.getLongitude()))
    			.radius(stop.getContentRadius())
    			.strokeWidth(3f)
    			.fillColor(0x1100FF00)
    			.strokeColor(0x8800FF00);
    			circleList.add(map.addCircle(contentCircle));
    			
    		}
    		else if(location instanceof InterestLocation){
    			InterestLocation interest = (InterestLocation)location;
    			CircleOptions contentCircle = new CircleOptions()
    			.center(new LatLng(interest.getLatitude(), interest.getLongitude()))
    			.radius(interest.getContentRadius())
    			.strokeWidth(3f)
    			.fillColor(0x1100FF00)
    			.strokeColor(0x8800FF00);
    			circleList.add(map.addCircle(contentCircle));
    		}	
    	}	
    	for(Circle c: circleList) c.setVisible(StorageManager.getAudioTourStatus(this));
    }
    private void toggleCircles(boolean on){
    	for(Circle c: circleList){
    		c.setVisible(on);
    		Log.d(TAG, "Turning on circle: " + c.isVisible());

    	}
    }
    private void toggleAudioService(){
    	boolean audioStatus = StorageManager.getAudioTourStatus(this);
    	toggleCircles(!audioStatus);
    	if(!audioStatus){
    		Intent intent = new Intent(this, AudioService.class);
    		intent.setAction("com.jmie.fieldplay.start_audio_service");
    		intent.putExtra("com.jmie.fieldplay.route", route);
    		intent.putParcelableArrayListExtra("com.jmie.fieldplay.geofence_list", currentFPGeofences);
    		registerFences();
    		startService(intent);
    		StorageManager.saveAudioTourStatus(this, !audioStatus);
    		toggle.setIcon(R.drawable.av_stop);
    	}
    	else if(audioStatus){
    		stopService(new Intent(this, AudioService.class));
    		unregisterByPendingIntent();
    		StorageManager.saveAudioTourStatus(this, !audioStatus);
    		toggle.setIcon(R.drawable.av_play);
    	}
    }
	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode) {

        // If the request code matches the code sent in onConnectionFailed
	        case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :
	
	            switch (resultCode) {
	                // If Google Play services resolved the problem
	                case Activity.RESULT_OK:
	
	                    // If the request was to add geofences
	                    if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {
	
	                        // Toggle the request flag and send a new request
	                        mGeofenceRequester.setInProgressFlag(false);
	
	                        // Restart the process of adding the current geofences
	                        mGeofenceRequester.addGeofences(mCurrentGeofences);
	
	                    // If the request was to remove geofences
	                    } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){
	
	                        // Toggle the removal flag and send a new removal request
	                        mGeofenceRemover.setInProgressFlag(false);
	
	                        // If the removal was by Intent
	                        if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {
	
	                            // Restart the removal of all geofences for the PendingIntent
	                            mGeofenceRemover.removeGeofencesByIntent(
	                                mGeofenceRequester.getRequestPendingIntent());
	
	                        // If the removal was by a List of geofence IDs
	                        } else {
	
	                            // Restart the removal of the geofence list
	                            mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
	                        }
	                    }
	                break;
	
	                // If any other result was returned by Google Play services
	                default:
	
	                    // Report that Google Play services was unable to resolve the problem.
	                    Log.d(GeofenceUtils.APPTAG, "No resolution");
	            }
	
	        // If any other request code was received
	        default:
	           // Report that this Activity received an unknown requestCode
	           Log.d(GeofenceUtils.APPTAG,
	                   "Unknown activity result: " + requestCode);
	
	           break;
	    }
	}
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(GeofenceUtils.APPTAG, "Play services available");

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
 
                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }

    private class LayerMenuItemClickListener implements OnMenuItemClickListener{

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			List<MapLayer> layers = route.getMapLayers();
			
			int i = item.getItemId()-2;
			
//			if(i == -1){
//				if(tileOverlay != null)tileOverlay.remove();
//				return false;
//			}
			if(tileOverlay != null)tileOverlay.remove();
			if(i!= -1){
				tileProvider.setMapLayer(FPMapActivity.this, route.getRouteData(), layers.get(i));
				tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			}
			return false;
		}
    	
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		
		if(item.getItemId()==R.id.layer_options){
			layerMenu.show();
		}
		else if(item.getItemId() == R.id.service_control){
			toggleAudioService();
		}
		else if(item.getItemId() == R.id.replay_last){
			boolean audioStatus = StorageManager.getAudioTourStatus(this);
			if(audioStatus){
		        Intent notifyAudioServiceIntent =
		                new Intent(getApplicationContext(),AudioService.class);
		        notifyAudioServiceIntent.setAction("com.jmie.fieldplay.replay");
		        startService(notifyAudioServiceIntent);
		        Toast.makeText(this, "Last location replay", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(this, "Can't replay with audio service off", Toast.LENGTH_SHORT).show();
			}
		}

		return super.onOptionsItemSelected(item);

//	    switch (item.getItemId()) {
//	        case R.id.layer_options:
//	        	layerMenu.show();
//	        case R.id.service_control:
//	        	toggleAudioService();
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }

	}
    private void unregisterByPendingIntent() {
        /*
         * Remove all geofences set by this app. To do this, get the
         * PendingIntent that was added when the geofences were added
         * and use it as an argument to removeGeofences(). The removal
         * happens asynchronously; Location Services calls
         * onRemoveGeofencesByPendingIntentResult() (implemented in
         * the current Activity) when the removal is done
         */

        /*
         * Record the removal as remove by Intent. If a connection error occurs,
         * the app can automatically restart the removal if Google Play services
         * can fix the error
         */
        // Record the type of removal
        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        // Try to make a removal request
        try {
        /*
         * Remove the geofences represented by the currently-active PendingIntent. If the
         * PendingIntent was removed for some reason, re-create it; since it's always
         * created with FLAG_UPDATE_CURRENT, an identical PendingIntent is always created.
         */
        mGeofenceRemover.removeGeofencesByIntent(mGeofenceRequester.getRequestPendingIntent());

        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, "Remove geofence already requested",
                        Toast.LENGTH_LONG).show();
        }

    }
    private void registerFences() {
    	//Log.d(TAG, "Call to register fences!");
        /*
         * Record the request as an ADD. If a connection error occurs,
         * the app can automatically restart the add request if Google Play services
         * can fix the error
         */
        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {
        	Log.d(TAG, "services not connected");
            return;
        }
        int id = 0;
        for(FPLocation location: route.getLocationList()){
        	FPGeofence fpGeofence = location.getGeofence(Integer.toString(id++), GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER);
        	if(fpGeofence==null)continue;
        	currentFPGeofences.add(fpGeofence);
        	Geofence content = fpGeofence.toContentGeofence();
        	Geofence alert = fpGeofence.toAlertGeofence();
        	if(content!=null)mCurrentGeofences.add(content);
        	if(alert!=null) mCurrentGeofences.add(alert);
        }
        for(FPGeofence fpGeofence:currentFPGeofences){
        	Log.d(TAG, "FP geofence: " + fpGeofence.getInterestLocation().getName());
        }

        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
            Toast.makeText(this, "Geofence already created",
                        Toast.LENGTH_LONG).show();
        }
    }
    
    public class InfoWindowClickListener implements OnInfoWindowClickListener{

		@Override
		public void onInfoWindowClick(Marker marker) {
			Intent i = new Intent(FPMapActivity.this, LocationDetailsActivity.class);
			FPLocation location = markerToLocation.get(marker);
			i.putExtra("com.jmie.fieldplay.route", route);
			i.putExtra("com.jmie.fieldplay.location", location.getName());
			
			startActivity(i);	
			
		}
    	
    }
    
    public class FPGeofenceReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

            // Intent contains information about successful addition or removal of geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    ||
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {
            	Log.d(TAG, action);
                handleGeofenceStatus(context, intent);

            // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

            // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, "Invalid action: " + action);
                Toast.makeText(context, "Invalid Action", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {
        	String action = intent.getAction();
        	if(TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED) ){
        		mCurrentGeofences.clear();
        		mGeofenceRequester = new GeofenceRequester(FPMapActivity.this);
        		currentFPGeofences.clear();
        	}
        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		Log.d(TAG, "saving map camera");
		savedCamera = cameraPosition;
	}
    
}
