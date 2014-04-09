package com.jmie.fieldplay.location;

import com.jmie.fieldplay.R;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;

import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class PhotoViewFragment extends Fragment{
    GridView gridView;
    RelativeLayout r;
    LinearLayout l;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	HorizontalScrollView v = (HorizontalScrollView) inflater.inflate(R.layout.photo_scroller, container, false);
    	l = (LinearLayout) v.findViewById(R.id.photo_scroll);
    	l.setOrientation(LinearLayout.HORIZONTAL);

    	return v;
    }
    public void addImage(Bitmap bm){
    	
		Display display = this.getActivity().getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		int width = point.x/3;
    	Bitmap thumb = ThumbnailUtils.extractThumbnail(bm, width, width);
    	ImageView imgView = new ImageView(this.getActivity());
    	imgView.setPadding(2, 2,2, 2);
    	imgView.setImageBitmap(thumb);
    	l.addView(imgView);
    	imgView.setOnClickListener(new PictureTouchListener());
    	imgView.setOnLongClickListener(new PictureLongTouchListener());
    }
    class PictureTouchListener implements OnClickListener{

    	@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
		}
    }
    class PictureLongTouchListener implements OnLongClickListener{

    	@Override
		public boolean onLongClick(View arg0) {
    		
    		
			return false;
		}
    }
}