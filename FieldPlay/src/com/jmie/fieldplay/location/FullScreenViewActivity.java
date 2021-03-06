package com.jmie.fieldplay.location;
import java.util.ArrayList;

import com.jmie.fieldplay.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class FullScreenViewActivity extends Activity{


	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image_view);

		viewPager = (ViewPager) findViewById(R.id.pager);



		Intent i = getIntent();
		int position = i.getIntExtra("com.jmie.fieldplay.position", 0);
		ArrayList<String> paths = i.getStringArrayListExtra("com.jmie.fieldplay.filepaths");
		ArrayList<String> descriptions = i.getStringArrayListExtra("com.jmie.fieldplay.descriptionlist");
		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
				paths, descriptions);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
}