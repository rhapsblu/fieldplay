package com.jmie.fieldplay.location;

import com.jmie.fieldplay.R;
 
import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
public class FullScreenImageAdapter extends PagerAdapter {
 
    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private ArrayList<String> descriptions;
    private LayoutInflater inflater;
 
    // constructor
    public FullScreenImageAdapter(Activity activity,
            ArrayList<String> imagePaths, ArrayList<String> descriptions) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.descriptions = descriptions;
    }
 
    @Override
    public int getCount() {
        return this._imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
     
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;
        TextView description;
        
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fullscreen_image_layout, container,
                false);
  
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        description = (TextView)viewLayout.findViewById(R.id.photo_description);
        description.setText(descriptions.get(position));
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
         
        DisplayMetrics metrics = new DisplayMetrics();
        this._activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imgDisplay.setImageBitmap(decodeSampledBitmapFromFile(_imagePaths.get(position), metrics.widthPixels, metrics.heightPixels));
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });
  
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
    public static Bitmap decodeSampledBitmapFromFile(String filePath,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}