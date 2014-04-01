package com.jmie.fieldplay;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.jmie.fieldplay.audioservice.FPGeofenceStore;
import com.jmie.fieldplay.audioservice.GeofenceRemover;
import com.jmie.fieldplay.audioservice.GeofenceRequester;
import com.jmie.fieldplay.audioservice.GeofenceUtils;
import com.jmie.fieldplay.audioservice.AudioService.LocalBinder;
import com.jmie.fieldplay.audioservice.GeofenceUtils.REMOVE_TYPE;
import com.jmie.fieldplay.audioservice.GeofenceUtils.REQUEST_TYPE;
import com.jmie.fieldplay.binocular.activity.SensorsActivity.ErrorDialogFragment;
import com.jmie.fieldplay.storage.StorageManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
								Activity 
							implements
								OnMarkerClickListener,        
								ConnectionCallbacks,
								OnConnectionFailedListener,
								LocationListener
								//ServiceConnection
{
	
//    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
//    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
//            GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;
//    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
   // private REMOVE_TYPE mRemoveType;

    // Persistent storage for geofences
//    private FPGeofenceStore mPrefs;
//    List<Geofence> mCurrentGeofences;
    // Add geofences handler
//    private GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
//    private GeofenceRemover mGeofenceRemover;
//    
//    private List<String> mGeofenceIdsToRemove;
    
	private Route route;
//	private AudioService audioService;
	public static String TAG = "FP Map";
	private GoogleMap mMap;
	private MenuItem toggle;
	private boolean bound = false;
	private List<Marker> markerList = new ArrayList<Marker>();
//	private List<FPGeofence> fenceList = new ArrayList<FPGeofence>();
    private Map<Marker, FPLocation> markerToLocation = new HashMap<Marker, FPLocation>();
    private PopupMenu layerMenu;
    private CustomLayerTileProvider tileProvider = new CustomLayerTileProvider();
    private TileOverlay tileOverlay;
    private Context c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		c = this;
		String routeName = b.getString("com.jmie.fieldplay.routeName");

//		mPrefs = new FPGeofenceStore(this);
        // Instantiate the current List of geofences
//        mCurrentGeofences = new ArrayList<Geofence>();

        // Instantiate a Geofence requester
//        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
//        mGeofenceRemover = new GeofenceRemover(this);
		Log.d(TAG, "Recieved Route " + routeName);
		route = StorageManager.getCachedRoute(c);
		if((route == null)||(route.getStorageName().compareTo(routeName)!=0)){
			route = StorageManager.buildRoute(this, routeName);
			StorageManager.cacheRoute(c, route);
		}
		setContentView(R.layout.activity_fpmap);
		setUpMapIfNeeded();
//		Intent intent = new Intent(this, AudioService.class);
//		startService(intent);
//        bindService(new Intent(this, AudioService.class), this,
//                Context.BIND_AUTO_CREATE);
     
        layerMenu = new PopupMenu(this, findViewById(R.id.popupAnchor));
        List<MapLayer> mapLayers = route.getMapLayers();
        layerMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "No layers");
        	
        for(int i = 2; i<=mapLayers.size()+1; i++){
        	layerMenu.getMenu().add(Menu.NONE, i, Menu.NONE, mapLayers.get(i-2).getName());
        }
        layerMenu.setOnMenuItemClickListener(new LayerMenuItemClickListener());

	}
	@Override
	protected void onResume(){
		super.onResume();
		setUpMapIfNeeded();
//		if(mPrefs.getFenceCount() >0){
//			for(int i = 0; i<mPrefs.getFenceCount(); i++) fenceList.add(mPrefs.getGeofence(String.valueOf(i)));
//		}

		//mLocationClient.connect();
	}
    @Override
    public void onPause() {
        super.onPause();
//        if (mLocationClient != null) {
//            mLocationClient.disconnect();

//        }
    }
//    @Override
//    public void onDestroy(){
//    	super.onDestroy();
//    	if(bound) unbindService(this);
//    	bound = false;
//    }
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
    private void setUpMapIfNeeded() {
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
        }
    }
//    private void setUpLocationClientIfNeeded() {
//        if (mLocationClient == null) {
//            mLocationClient = new LocationClient(
//                    getApplicationContext(),
//                    this,  // ConnectionCallbacks
//                    this); // OnConnectionFailedListener
//        }
//    }
//    public void showMyLocation(View view) {
//        if (mLocationClient != null && mLocationClient.isConnected()) {
//            String msg = "Location = " + mLocationClient.getLastLocation();
//            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//        }
//    }
    private void setUpMap() {
    	Log.d(TAG, "Setting up map");
        // Add lots of markers to the map.
        addMarkersToMap();
        addRouteLineToMap();
        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);

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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
    }
    private void addMarkersToMap() {
    	Log.d(TAG, "Adding markers to map");
    	IconGenerator iconFactory = new IconGenerator(this);
    	int locationCount = 1;
    	for(FPLocation location: route.getLocationList()){
    		Log.d(TAG, "Adding marker for " + location.getName() + " lat: " + location.getLatitude() + " long: " + location.getLongitude());
    		//addIcon(iconFactory, "Blue style", new LatLng(location.getLatitude(), location.getLongitude()));
    		String iconText = "";
    		if(location instanceof BinocularLocation){
    			iconFactory.setStyle(IconGenerator.STYLE_BLUE);
    			iconText = "B";
    		}
    		else{
    			iconText = Integer.toString(locationCount);
    			locationCount++;
    			if(location instanceof StopLocation) iconFactory.setStyle(IconGenerator.STYLE_GREEN);
    			else iconFactory.setStyle(IconGenerator.STYLE_RED);
    		}
            MarkerOptions markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(iconText))).
                    position(new LatLng(location.getLatitude(), location.getLongitude())).
                    anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

    		Marker marker = mMap.addMarker(markerOptions);
    		markerList.add(marker);
    		markerToLocation.put(marker, location);

    	}

    }

    private void addRouteLineToMap(){
    	Log.d(TAG, "Adding route lines to map");
    	PolylineOptions routeLine = new PolylineOptions();
    	for(FPLocation location: route.getLocationList()){
    		if(!(location instanceof BinocularLocation))
    			routeLine.add(new LatLng(location.getLatitude(), location.getLongitude()));
    	}
    
    	mMap.addPolyline(routeLine);
    }
	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent i = new Intent(FPMapActivity.this, LocationDetailsActivity.class);
		FPLocation location = markerToLocation.get(marker);
		i.putExtra("com.jmie.fieldplay.route", route);
		i.putExtra("com.jmie.fieldplay.locationID", location.getName());
		startActivity(i);	
		return false;
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnected(Bundle connectionHint) {
//        mLocationClient.requestLocationUpdates(
//                REQUEST,
//                this);
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
//	@Override
//	public void onServiceConnected(ComponentName name, IBinder service) {
//		LocalBinder localBinder = (LocalBinder)service;
//		audioService = localBinder.getService();
//		bound = true;
//		
//	}
//	@Override
//	public void onServiceDisconnected(ComponentName name) {
//		// TODO Auto-generated method stub
//		
//	}
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        // Choose what to do based on the request code
//        switch (requestCode) {
//
//            // If the request code matches the code sent in onConnectionFailed
//            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :
//
//                switch (resultCode) {
//                    // If Google Play services resolved the problem
//                    case Activity.RESULT_OK:
//
//                        // If the request was to add geofences
//                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {
//
//                            // Toggle the request flag and send a new request
//                            mGeofenceRequester.setInProgressFlag(false);
//
//                            // Restart the process of adding the current geofences
//                            mGeofenceRequester.addGeofences(mCurrentGeofences);
//
//                        // If the request was to remove geofences
//                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){
//
//                            // Toggle the removal flag and send a new removal request
//                            mGeofenceRemover.setInProgressFlag(false);
//
//                            // If the removal was by Intent
//                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {
//
//                                // Restart the removal of all geofences for the PendingIntent
//                                mGeofenceRemover.removeGeofencesByIntent(
//                                    mGeofenceRequester.getRequestPendingIntent());
//
//                            // If the removal was by a List of geofence IDs
//                            } else {
//
//                                // Restart the removal of the geofence list
//                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
//                            }
//                        }
//                    break;
//
//                    // If any other result was returned by Google Play services
//                    default:
//
//                        // Report that Google Play services was unable to resolve the problem.
//                        Log.d(GeofenceUtils.APPTAG, "google play services reports a problem");
//                }
//
//            // If any other request code was received
//            default:
//               // Report that this Activity received an unknown requestCode
//               Log.d(GeofenceUtils.APPTAG,
//                       "unknown code: ");
//
//               break;
//        }
//    }
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
//    private boolean servicesConnected() {
//
//        // Check that Google Play services is available
//        int resultCode =
//                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//
//        // If Google Play services is available
//        if (ConnectionResult.SUCCESS == resultCode) {
//
//            // In debug mode, log the status
//            Log.d(GeofenceUtils.APPTAG, "play services available");
//
//            registerGeofences();
//            return true;
//
//        // Google Play services was not available for some reason
//        } else {
//
//            // Display an error dialog
//            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
//            if (dialog != null) {
//                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//                errorFragment.setDialog(dialog);
//                errorFragment.show(getFragmentManager(), GeofenceUtils.APPTAG);
//            }
//            return false;
//        }
//    }
    
//    public void onUnregisterByPendingIntent() {
//        /*
//         * Remove all geofences set by this app. To do this, get the
//         * PendingIntent that was added when the geofences were added
//         * and use it as an argument to removeGeofences(). The removal
//         * happens asynchronously; Location Services calls
//         * onRemoveGeofencesByPendingIntentResult() (implemented in
//         * the current Activity) when the removal is done
//         */
//
//        /*
//         * Record the removal as remove by Intent. If a connection error occurs,
//         * the app can automatically restart the removal if Google Play services
//         * can fix the error
//         */
//        // Record the type of removal
//        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;
//
//        /*
//         * Check for Google Play services. Do this after
//         * setting the request type. If connecting to Google Play services
//         * fails, onActivityResult is eventually called, and it needs to
//         * know what type of request was in progress.
//         */
//        if (!servicesConnected()) {
//
//            return;
//        }
//
//        // Try to make a removal request
//        try {
//        /*
//         * Remove the geofences represented by the currently-active PendingIntent. If the
//         * PendingIntent was removed for some reason, re-create it; since it's always
//         * created with FLAG_UPDATE_CURRENT, an identical PendingIntent is always created.
//         */
//        mGeofenceRemover.removeGeofencesByIntent(mGeofenceRequester.getRequestPendingIntent());
//        
//
//        } catch (UnsupportedOperationException e) {
//            // Notify user that previous request hasn't finished.
//            Toast.makeText(this, "Remove fences already requested",
//                        Toast.LENGTH_LONG).show();
//        }
//
//    }
//    
//    public void registerGeofences() {
//
//        /*
//         * Record the request as an ADD. If a connection error occurs,
//         * the app can automatically restart the add request if Google Play services
//         * can fix the error
//         */
//        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
//
//        /*
//         * Check for Google Play services. Do this after
//         * setting the request type. If connecting to Google Play services
//         * fails, onActivityResult is eventually called, and it needs to
//         * know what type of request was in progress.
//         */
//        if (!servicesConnected()) {
//
//            return;
//        }
//
//        int id = 0;
//        for(FPLocation l : route.getLocationList()){
//        	if(l instanceof StopLocation){
//        		fenceList.add(new FPGeofence(Integer.toString(id), route.getName(), l.getName(), 
//        				l.getLatitude(), l.getLongitude(),(long) ((StopLocation) l).getAlertRadius(), 
//        				GEOFENCE_EXPIRATION_IN_MILLISECONDS,Geofence.GEOFENCE_TRANSITION_ENTER));
//        		id++;
//        		fenceList.add(new FPGeofence(Integer.toString(id), route.getName(), l.getName(), 
//        				l.getLatitude(), l.getLongitude(),(long) ((StopLocation) l).getContentRadius(), 
//        				GEOFENCE_EXPIRATION_IN_MILLISECONDS,Geofence.GEOFENCE_TRANSITION_ENTER));
//        		id++;
//        	}
//        	else if(l instanceof InterestLocation){
//        		fenceList.add(new FPGeofence(Integer.toString(id), route.getName(), l.getName(), 
//        				l.getLatitude(), l.getLongitude(),(long) ((InterestLocation) l).getContentRadius(), 
//        				GEOFENCE_EXPIRATION_IN_MILLISECONDS,Geofence.GEOFENCE_TRANSITION_ENTER));
//        		id++;
//        	}
//        }
//        for(FPGeofence fence: fenceList){
//        	mPrefs.setGeofence(fence.getId(), fence);
//        	mCurrentGeofences.add(fence.toGeofence());
//        }
        /*
         * Create a version of geofence 1 that is "flattened" into individual fields. This
         * allows it to be stored in SharedPreferences.
         */
//        mUIGeofence1 = new SimpleGeofence(
//            "1",
//            // Get latitude, longitude, and radius from the UI
//            Double.valueOf(mLatitude1.getText().toString()),
//            Double.valueOf(mLongitude1.getText().toString()),
//            Float.valueOf(mRadius1.getText().toString()),
//            // Set the expiration time
//            GEOFENCE_EXPIRATION_IN_MILLISECONDS,
//            // Only detect entry transitions
//            Geofence.GEOFENCE_TRANSITION_ENTER);
//
//        // Store this flat version in SharedPreferences
//        mPrefs.setGeofence("1", mUIGeofence1);
//
//        /*
//         * Create a version of geofence 2 that is "flattened" into individual fields. This
//         * allows it to be stored in SharedPreferences.
//         */
//        mUIGeofence2 = new SimpleGeofence(
//            "2",
//            // Get latitude, longitude, and radius from the UI
//            Double.valueOf(mLatitude2.getText().toString()),
//            Double.valueOf(mLongitude2.getText().toString()),
//            Float.valueOf(mRadius2.getText().toString()),
//            // Set the expiration time
//            GEOFENCE_EXPIRATION_IN_MILLISECONDS,
//            // Detect both entry and exit transitions
//            Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
//            );
//
//        // Store this flat version in SharedPreferences
//        mPrefs.setGeofence("2", mUIGeofence2);
//
//        /*
//         * Add Geofence objects to a List. toGeofence()
//         * creates a Location Services Geofence object from a
//         * flat object
//         */
//        mCurrentGeofences.add(mUIGeofence1.toGeofence());
//        mCurrentGeofences.add(mUIGeofence2.toGeofence());

        // Start the request. Fail if there's already a request in progress
//        try {
//            // Try to add geofences
//            mGeofenceRequester.addGeofences(mCurrentGeofences);
//        } catch (UnsupportedOperationException e) {
//            // Notify user that previous request hasn't finished.
//            Toast.makeText(this, "Adding geofence already requested",
//                        Toast.LENGTH_LONG).show();
//        }
//    }
    private class LayerMenuItemClickListener implements OnMenuItemClickListener{

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			List<MapLayer> layers = route.getMapLayers();
			
			int i = item.getItemId()-2;
			
			if(i == -1){
				if(tileOverlay != null)tileOverlay.remove();
				return false;
			}
			tileProvider.setMapLayer(c, route.getStorageName(), layers.get(i));
			tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			return false;
		}
    	
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.layer_options:
	        	layerMenu.show();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
