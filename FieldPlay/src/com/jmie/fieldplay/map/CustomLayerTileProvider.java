package com.jmie.fieldplay.map;

import android.content.Context;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.jmie.fieldplay.route.RouteData;

public class CustomLayerTileProvider implements TileProvider {
	private MapLayer mapLayer;
	public CustomLayerTileProvider(){
	}
	public void setMapLayer(Context c, RouteData routeData, MapLayer ml){
		mapLayer = ml;
		mapLayer.setUpRoute(c, routeData);
	}
	@Override
	public Tile getTile(int x, int y, int zoom) {
		Tile t = mapLayer.getTile(x, y, zoom);
		return t==null?CustomLayerTileProvider.NO_TILE:t;
	}

}
