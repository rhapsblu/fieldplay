package com.jmie.fieldplay;



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
        
        ImageGridHandler handler = new ImageGridHandler(_activity, imageView, _filePaths.get(position), imageWidth, imageWidth);
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
    public class ImageGridHandler extends AsyncTask<String, Void, Bitmap>{
        private final WeakReference<ImageView> imageViewReference;
        private Context context;
        private String path;
        int WIDTH;
        int HEITH;

        public ImageGridHandler(Context context, ImageView img, String path, int width, int heith){
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
            final ImageView imageView = imageViewReference.get();
            imageView.setImageBitmap(result);
            //notify
        }
    }
}
