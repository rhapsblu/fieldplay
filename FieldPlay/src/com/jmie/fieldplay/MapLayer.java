package com.jmie.fieldplay;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.jmie.fieldplay.storage.StorageManager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class MapLayer {
	private static final int TILE_WIDTH = 256;
	private static final int TILE_HEIGHT = 256;
	private static final int BUFFER_SIZE = 16 * 1024;
	private String layerPath;
	private String name;
	private String description;
	private String layerName;
	private Context c;

	
	static final String TAG = "MapLayer";

	
	public MapLayer(String name, String description, String layerName){
		this.layerName = layerName;
		this.name = name;
		this.description = description;

	}
	public void setUpRoute(Context c, String routeStorageName){
		layerPath = StorageManager.getTilePath(c, routeStorageName, layerName);
		this.c = c;
	}
	public String getName(){
		return name;
	}
	public String getDescription(){
		return description;
	}
	
    public Tile getTile(int x, int y, int zoom) {

        byte[] image = readTileImage(c, x, y, zoom);
        if(image== null) return TileProvider.NO_TILE;
        return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
    }
    
    public byte[] readTileImage(Context c, int x, int y, int zoom) {
		Log.d(TAG, "Reading tile file");
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {	
			Log.d(TAG, "Media Mounted");
			InputStream inputStream = null;
			ByteArrayOutputStream buffer = null;
			try {
				
				File inputFile = c.getExternalFilesDir(getTileFilename(x, y, zoom));
				if(inputFile.isDirectory()){
					//silly hack.  Why does getExternalFileDir create the directory?
					inputFile.delete();
					return null;
				}
				inputStream = new FileInputStream(inputFile);
				buffer = new ByteArrayOutputStream();

	            int nRead;
	            byte[] data = new byte[BUFFER_SIZE];

	            while ((nRead = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
	                buffer.write(data, 0, nRead);
	            }
	            buffer.flush();

	            return buffer.toByteArray();


			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
	            if (inputStream != null) try { inputStream.close(); } catch (Exception ignored) {}
	            if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
	        }
		} 
		else {
			Log.e(TAG, "External media not available ");
		}
    	return null;
    }

    private String getTileFilename(int x, int y, int zoom) {
        return layerPath + "/" + zoom + '/' + x + '/' + y + ".png";
    }
	
}
