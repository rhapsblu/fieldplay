package com.jmie.fieldplay.map;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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
import com.jmie.fieldplay.location.LocationDetailsActivity;
import com.jmie.fieldplay.route.BinocularLocation;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.StopLocation;
import com.jmie.fieldplay.storage.StorageManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class FPMapActivity extends 
								FragmentActivity 
							implements
								OnMarkerClickListener       

{
	

    
	private Route route;
	public static String TAG = "FP Map";
	private GoogleMap mMap;
	private MenuItem toggle;
	private List<Marker> markerList = new ArrayList<Marker>();

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
		//route = b.getParcelable("com.jmie.fieldplay.route");
			route = StorageManager.buildRoute(this, routeName);

		setContentView(R.layout.activity_fpmap);
		setUpMapIfNeeded();

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
            mMap.setOnInfoWindowClickListener(new InfoWindowClickListener());
        }
    }

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
    	//Log.d(TAG, "Adding markers to map");
    	IconGenerator iconFactory = new IconGenerator(this);
    	int locationCount = 1;
    	for(FPLocation location: route.getLocationList()){
    		//Log.d(TAG, "Adding marker for " + location.getName() + " lat: " + location.getLatitude() + " long: " + location.getLongitude());
    		String iconText = "";
    		if(location instanceof BinocularLocation){
    			iconFactory.setStyle(IconGenerator.STYLE_BLUE);
    			iconText = "B";
    		}
    		else{
    			iconText = Integer.toString(locationCount);
    			locationCount++;
    			if(location instanceof StopLocation) iconFactory.setStyle(IconGenerator.STYLE_RED);
    			else iconFactory.setStyle(IconGenerator.STYLE_GREEN);
    		}
            MarkerOptions markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(iconText))).
                    position(new LatLng(location.getLatitude(), location.getLongitude())).
                    anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

    		Marker marker = mMap.addMarker(markerOptions);
    		marker.setTitle(location.getName());
    		marker.setSnippet("Latitude: " + location.getLatitude() + " Longitude: "+location.getLongitude());
    		markerList.add(marker);
    		markerToLocation.put(marker, location);

    	}

    }

    private void addRouteLineToMap(){
    	//Log.d(TAG, "Adding route lines to map");
    	PolylineOptions routeLine = new PolylineOptions();
    	for(FPLocation location: route.getLocationList()){
    		if(!(location instanceof BinocularLocation))
    			routeLine.add(new LatLng(location.getLatitude(), location.getLongitude()));
    	}
    
    	mMap.addPolyline(routeLine);
    }
	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
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
				tileProvider.setMapLayer(c, route.getStorageName(), layers.get(i));
				tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			}
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
    public class InfoWindowClickListener implements OnInfoWindowClickListener{

		@Override
		public void onInfoWindowClick(Marker marker) {
			Intent i = new Intent(FPMapActivity.this, LocationDetailsActivity.class);
			FPLocation location = markerToLocation.get(marker);
			i.putExtra("com.jmie.fieldplay.route", route);
			i.putExtra("com.jmie.fieldplay.location", location.getName());

			for(String key: i.getExtras().keySet()){
				Log.d(TAG, "Keys before: "+ key);
			}
			
			startActivity(i);	
			
		}
    	
    }
    
    
}
