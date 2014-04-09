package com.jmie.fieldplay.binocular.testar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import android.graphics.Color;
import android.util.Log;

import com.jmie.fieldplay.binocular.data.DataSource;
import com.jmie.fieldplay.binocular.ui.Marker;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.StopLocation;

public class FPBinocularSource extends DataSource{
	protected List<Marker> markersCache = null;
    private static final String TAG = "FPBinocularSource";
	
	public FPBinocularSource(Route r, FPLocation l){
		Log.d(TAG, "Creating markers");
		markersCache = new ArrayList<Marker>();
		if(l instanceof StopLocation){
			StopLocation stopLocation = (StopLocation)l;
			Iterator <String> iterator = stopLocation.getBinocularPointIterator();
			while(iterator.hasNext()){
				String binocPointName = iterator.next();
				FPLocation binocPoint = r.getLocationByName(binocPointName);
				Log.d(TAG, "Adding marker " + binocPoint.getName());
				Marker m = new Marker(binocPoint.getName(), 
									  binocPoint.getDescription(),
						              binocPoint.getLatitude(), 
						              binocPoint.getLongitude(), 
						              binocPoint.getElevation(), 
						              Color.RED);
				markersCache.add(m);
			}

		}


	}
	@Override
	public List<Marker> getMarkers() {

		return markersCache;
	}

}
