package com.jmie.fieldplay;

import java.io.File;

import com.jmie.fieldplay.binocular.testar.FPBinocularActivity;
import com.jmie.fieldplay.details.FPPicture;
import com.jmie.fieldplay.storage.StorageManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class LocationDetailsActivity extends Activity{

	private Route route;
	private FPLocation location;
	private String routeStorageName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		String location_id = b.getString("com.jmie.fieldplay.locationID");
		route = b.getParcelable("com.jmie.fieldplay.route");
		location = route.getLocationByName(location_id);
		setContentView(R.layout.location_detail);
		TextView name = (TextView)findViewById(R.id.location_name);

		name.setText(location.getName());
		
		TextView description = (TextView)findViewById(R.id.location_description);

		description.setText(location.getDescription());
		Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
		name.setTypeface(font);



		

	}	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_detail, menu);
		MenuItem binocularActionButton = menu.findItem(R.id.augmented_binoculars);
		if(location instanceof StopLocation){
			StopLocation stopLocation = (StopLocation)location;
			if(!stopLocation.getBinocularPointIterator().hasNext()) binocularActionButton.setVisible(false);
		}
		else binocularActionButton.setVisible(false);
		return true;
	}
	@Override
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
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.photo_viewer:
	            //load photo viewer activity here
	            return true;
	        case R.id.augmented_binoculars:
				Intent i = new Intent(LocationDetailsActivity.this, FPBinocularActivity.class);
				String[] routeLocPair = {route.getStorageName(), location.getName()};
				i.putExtra("com.jmie.fieldplay.locationID", routeLocPair);
				startActivity(i);
	            return true;
	        case R.id.video_viewer:
	        	//load video viewer activity here
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
