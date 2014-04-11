package com.jmie.fieldplay.location;

import java.util.ArrayList;
import java.util.List;

import com.jmie.fieldplay.R;

import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.FPPicture;
import com.jmie.fieldplay.storage.StorageManager;


import android.content.res.Resources;
import android.graphics.Point;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.GridView;
 
public class ImageViewGridFragment extends Fragment {
 
	public static final int GRID_PADDING = 8;
	public static final int NUM_OF_COLUMNS = 3;
	
    private List<FPPicture> imageList;
    private ArrayList<String> pathList;
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private FPLocation location;

    private String routeStorageName;
    
    public static final ImageViewGridFragment newInstance(Bundle b){
    	ImageViewGridFragment f = new ImageViewGridFragment();
    	f.setArguments(b);
    	return f;
    };
    @Override
    public void onCreate(Bundle savedinstance){
    	super.onCreate(savedinstance);
		//location = this.getArguments().getParcelable("com.jmie.fieldplay.location");
		routeStorageName =  this.getArguments().getString("com.jmie.fieldplay.routeStorageName");

        //imageList = location.getImageList();
        pathList = new ArrayList<String>();
		
		ArrayList<FPPicture> pictures = this.getArguments().getParcelableArrayList("com.jmie.fieldplay.locations");
		for(FPPicture pic : pictures){
			pathList.add(getActivity().getExternalFilesDir(StorageManager.getImagePath(this.getActivity(), routeStorageName, pic.getResource())).getPath());
		}
//        for(FPPicture pic: imageList){
//        	pathList.add(getActivity().getExternalFilesDir(StorageManager.getImagePath(this.getActivity(), routeStorageName, pic.getResource())).getPath());
//        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_image_grid, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        InitilizeGridLayout();
        
        // Gridview adapter

        adapter = new GridViewImageAdapter(getActivity(),  pathList,
                columnWidth);
        
        // setting grid view adapter
        gridView.setAdapter(adapter);

        return rootView;
    }
 

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_grid);

		//String[] routeAndLocation = b.getStringArray("com.jmie.fieldplay.locationID");
		
		
		//Route route = StorageManager.getCachedRoute(this);
//		if((route == null)||(route.getStorageName().compareTo(routeAndLocation[0])!=0)){
//			route = StorageManager.buildRoute(this, routeAndLocation[0]);
//			StorageManager.cacheRoute(this, route);
//		}
//		location = route.getLocationByName(routeAndLocation[1]);
		
        
 
 
        // Initilizing Grid View
        
        
        // loading all image paths from SD card

//    }
 
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
 
}