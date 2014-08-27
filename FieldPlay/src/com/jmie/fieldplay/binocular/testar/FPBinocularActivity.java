package com.jmie.fieldplay.binocular.testar;


import com.jmie.fieldplay.R;
import com.jmie.fieldplay.binocular.activity.AugmentedReality;
import com.jmie.fieldplay.binocular.data.ARData;
import com.jmie.fieldplay.binocular.ui.Marker;
import com.jmie.fieldplay.binocular.widget.VerticalTextView;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.Route;

import android.os.Bundle;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

public class FPBinocularActivity extends AugmentedReality {
    private static final String TAG = "BinocularActivity";
	private Route route;
	private FPLocation location;
//	private String routeStorageName;
    private static Toast myToast = null;
    private static VerticalTextView text = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(TAG, "create");
		Bundle b = getIntent().getExtras();
		
		//routeStorageName = routeAndLocation[0];
		route = b.getParcelable("com.jmie.fieldplay.route");
		String locationName = b.getString("com.jmie.fieldplay.locationName");
		Log.d(TAG,  "Route= " + route.getName());
		Log.d(TAG, "Location= " + locationName);
		//Log.d(TAG, route.debugPrintMap());
		location = route.getLocationByName(locationName);

		//setContentView(R.layout.activity_main);
        // Create toast
        myToast = new Toast(getApplicationContext());
        myToast.setGravity(Gravity.CENTER, 0, 0);
        myToast.setDuration(Toast.LENGTH_LONG);
        // Creating our custom text view, and setting text/rotation
        text = new VerticalTextView(getApplicationContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(params);
        text.setBackgroundResource(android.R.drawable.toast_frame);
        text.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
        text.setShadowLayer(2.75f, 0f, 0f, Color.parseColor("#BB000000"));
        myToast.setView(text);
        // Setting duration and displaying the toast
        myToast.setDuration(Toast.LENGTH_SHORT);

        // Local
        FPBinocularSource source = new FPBinocularSource(route, location);
        ARData.clearMarkers();
        ARData.addMarkers(source.getMarkers());
  

	
	}


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "start");
    }
    @Override
    public void onResume(){
    	super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fp_binocular, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected() item=" + item);
        
        if(item.getItemId() == R.id.showRadar){
            showRadar = !showRadar;
            item.setTitle(((showRadar) ? "Hide" : "Show") + " Radar");
        }
        else if(item.getItemId() == R.id.exit){
            finish();
        }
//        switch (item.getItemId()) {
//            case R.id.showRadar:
//                showRadar = !showRadar;
//                item.setTitle(((showRadar) ? "Hide" : "Show") + " Radar");
//                break;
//            case R.id.exit:
//                finish();
//                break;
//        }
        return true;
    }

    @Override
    protected void markerTouched(Marker marker) {
        text.setText(marker.getDescription());
        myToast.show();
    }

}
