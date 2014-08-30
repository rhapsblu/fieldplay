package com.jmie.fieldplay.location;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;

import android.view.View;
import android.view.View.OnClickListener;
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
 
        // get screen dimensions
//        Bitmap image = decodeFile(_activity, _filePaths.get(position), imageWidth,
//                imageWidth);
 
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        //imageView.setImageBitmap(image);
        imageView.setImageBitmap(null);
        
        BitmapWorkerTask handler = new BitmapWorkerTask(_activity, imageView, _filePaths.get(position), imageWidth, imageWidth);
        handler.execute(_filePaths.get(position));
        // image view click listener
        imageView.setOnClickListener(new OnImageClickListener(position));
        
        return imageView;
    }
 
    class OnImageClickListener implements OnClickListener {
 
        int _postion;
 
        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }
 
        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, FullScreenViewActivity.class);
            i.putExtra("com.jmie.fieldplay.position", _postion);
            i.putStringArrayListExtra("com.jmie.fieldplay.filepaths", _filePaths);
            
            _activity.startActivity(i);
        }
 
    }
//    public void loadBitmap(int resId, Context context, ImageView imageView, String path, int width, int heith) {
//        if (cancelPotentialWork(resId, imageView)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(context, imageView, path, width, heith);
//            final AsyncDrawable asyncDrawable =
//                    new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
//            imageView.setImageDrawable(asyncDrawable);
//            task.execute(path);
//        }
//    }
//    public static boolean cancelPotentialWork(int data, ImageView imageView) {
//        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//        if (bitmapWorkerTask != null) {
//            final int bitmapData = bitmapWorkerTask.data;
//            // If bitmapData is not yet set or it differs from the new data
//            if (bitmapData == 0 || bitmapData != data) {
//                // Cancel previous task
//                bitmapWorkerTask.cancel(true);
//            } else {
//                // The same work is already in progress
//                return false;
//            }
//        }
//        // No task associated with the ImageView, or an existing task was cancelled
//        return true;
//    }
//    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
//    	   if (imageView != null) {
//    	       final Drawable drawable = imageView.getDrawable();
//    	       if (drawable instanceof AsyncDrawable) {
//    	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
//    	           return asyncDrawable.getBitmapWorkerTask();
//    	       }
//    	    }
//    	    return null;
//    	}
    /*
     * Resizing image size
     */
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
 
//            if(imageViewReference!=null && result!=null) {
//            	final ImageView imageView = imageViewReference.get();
//            	if(imageView != null)
//            	imageView.setImageBitmap(result);
//            }
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
//    static class AsyncDrawable extends BitmapDrawable {
//        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
//
//        public AsyncDrawable(Resources res, Bitmap bitmap,
//                BitmapWorkerTask bitmapWorkerTask) {
//            super(res, bitmap);
//            bitmapWorkerTaskReference =
//                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
//        }
//
//        public BitmapWorkerTask getBitmapWorkerTask() {
//            return bitmapWorkerTaskReference.get();
//        }
//    }
}
