package com.jmie.fieldplay.location;

import java.util.ArrayList;


import com.jmie.fieldplay.R;

import com.jmie.fieldplay.route.FPPicture;
import com.jmie.fieldplay.route.RouteData;
import com.jmie.fieldplay.storage.StorageManager;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
 
public class ImageViewGridFragment extends Fragment {
 
	public static final int GRID_PADDING = 8;
	public static final int NUM_OF_COLUMNS = 3;


    private ArrayList<String> pathList;
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private ArrayList<String> descriptionList;

    private RouteData routeData;
    
    public static final ImageViewGridFragment newInstance(Bundle b){
    	ImageViewGridFragment f = new ImageViewGridFragment();
    	f.setArguments(b);
    	return f;
    };
    @Override
    public void onCreate(Bundle savedinstance){
    	super.onCreate(savedinstance);
		routeData =  this.getArguments().getParcelable("com.jmie.fieldplay.routeData");
		
        pathList = new ArrayList<String>();
		descriptionList = new ArrayList<String>();
		ArrayList<FPPicture>pictures = this.getArguments().getParcelableArrayList("com.jmie.fieldplay.locations");
		for(FPPicture pic : pictures){
			pathList.add(getActivity().getExternalFilesDir(StorageManager.getImagePath(this.getActivity(), routeData, pic.getResource())).getPath());
			descriptionList.add(pic.getDescription());
		}

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	if(pathList.size()==0) return getDefaultView();
        View rootView = inflater.inflate(R.layout.activity_image_grid, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        InitilizeGridLayout();
        
        // Gridview adapter

        adapter = new GridViewImageAdapter(getActivity(),  pathList,
                columnWidth);
        
        // setting grid view adapter
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnImageClickListener());
        return rootView;
    }
 
 
    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                GRID_PADDING, r.getDisplayMetrics());
 
        columnWidth = (int) ((getScreenWidth() - ((NUM_OF_COLUMNS + 1) * padding)) / NUM_OF_COLUMNS);
 
        gridView.setNumColumns(NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }
    public int getScreenWidth() {
        int columnWidth;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
 
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
        	display.getSize(point);

        }
        columnWidth = point.x;
        return columnWidth;
    }
    private View getDefaultView(){
    	TextView tv = new TextView(getActivity());
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
    	tv.setText("No images for this location");
    	return tv;
    }
  class OnImageClickListener implements OnItemClickListener {


      @Override
      public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

          // on selecting grid view image
          // launch full screen activity
          Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
          i.putExtra("com.jmie.fieldplay.position", position);
          i.putStringArrayListExtra("com.jmie.fieldplay.filepaths", pathList);
          i.putStringArrayListExtra("com.jmie.fieldplay.descriptionlist", descriptionList);
          
          getActivity().startActivity(i);
      }

  }
}