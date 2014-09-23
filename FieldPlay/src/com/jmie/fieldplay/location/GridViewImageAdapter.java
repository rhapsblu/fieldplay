package com.jmie.fieldplay.location;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

 
import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

 
public class GridViewImageAdapter extends BaseAdapter {
    private Activity _activity;

    private ArrayList<String> _filePaths = new ArrayList<String>();
    private int imageWidth;
    public GridViewImageAdapter(Activity activity, ArrayList<String> _filePaths,
            int imageWidth) {
        this._activity = activity;
        this._filePaths = _filePaths;

        this.imageWidth = imageWidth;
    }
 
    @Override
    public int getCount() {
        return this._filePaths.size();
    }
 
    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }
 
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        
        
        BitmapWorkerTask handler = new BitmapWorkerTask(_activity, imageView, _filePaths.get(position), imageWidth, imageWidth);
        handler.execute(_filePaths.get(position));
        
        return imageView;
    }
 

    public static Bitmap decodeFile(Context c, String filePath, int WIDTH, int HIGHT) {
        try {
        	//File f = c.getExternalFilesDir(filePath);
            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
 
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
 
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
        private final WeakReference<ImageView> imageViewReference;
        private Context context;
        private String path;
        int WIDTH;
        int HEITH;

        public BitmapWorkerTask(Context context, ImageView img, String path, int width, int heith){
            imageViewReference = new WeakReference<ImageView>(img);
            this.WIDTH = width;
            this.HEITH = heith;
            this.path = path;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return GridViewImageAdapter.decodeFile(this.context, path, WIDTH, HEITH);
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (isCancelled()) {
                result = null;
            }

            if (imageViewReference != null && result != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(result);
                }
            }
        }
    }

}
