package com.jmie.fieldplay;

import java.io.File;

import com.jmie.fieldplay.binocular.testar.FPBinocularActivity;
import com.jmie.fieldplay.details.FPPicture;
import com.jmie.fieldplay.storage.StorageManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LocationDetailsActivity extends Activity{
	private StorageManager storage = new StorageManager();
	private Route route;
	private FPLocation location;
	private String routeStorageName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		String[] routeAndLocation = b.getStringArray("com.jmie.fieldplay.locationID");
		routeStorageName = routeAndLocation[0];
		route = storage.buildRouteByName(this, routeAndLocation[0]);
		location = route.getLocationByName(routeAndLocation[1]);
		setContentView(R.layout.location_detail);
		TextView name = (TextView)findViewById(R.id.location_name);

		name.setText(location.getName());
		
		TextView description = (TextView)findViewById(R.id.location_description);

		description.setText(location.getDescription());



		

	}
	protected void onResume(){
		super.onResume();
		PhotoViewFragment photoFrag = (PhotoViewFragment)
                getFragmentManager().findFragmentById(R.id.picture_nav);
		if(location instanceof StopLocation){
			StopLocation stopLocation = (StopLocation)location;
			for(FPPicture image: stopLocation.getImageList()){
				File inputFile = this.getExternalFilesDir(StorageManager.getImagePath(this, routeStorageName, image.getResource()));
				Bitmap bm = BitmapFactory.decodeFile(inputFile.getAbsolutePath());
				photoFrag.addImage(bm);
			}
		}
		
		Button button = (Button) findViewById(R.id.binocular_button);
		button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
				Intent i = new Intent(LocationDetailsActivity.this, FPBinocularActivity.class);
				//FPLocation location = markerToLocation.get(marker);
				String[] routeLocPair = {route.getName(), location.getName()};
				i.putExtra("com.jmie.fieldplay.locationID", routeLocPair);
				startActivity(i);
		    }
		});
	}

}
