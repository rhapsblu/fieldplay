package com.jmie.fieldplay;

import android.content.Context;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

public class CustomLayerTileProvider implements TileProvider {
	private MapLayer mapLayer;
	public CustomLayerTileProvider(Context c, String routeName, MapLayer mapLayer){
		this.mapLayer = mapLayer;
		this.mapLayer.setUpRoute(c, routeName);
	}
	public void setMapLayer(Context c, String routeName, MapLayer ml){
		mapLayer = ml;
		mapLayer.setUpRoute(c, routeName);
	}
	@Override
	public Tile getTile(int x, int y, int zoom) {
		return mapLayer.getTile(x, y, zoom);
	}

}
