package com.vishnus1224.imagegallery.Task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.vishnus1224.imagegallery.Utility.BitmapHelper;
import com.vishnus1224.imagegallery.Utility.Constant;
import com.vishnus1224.imagegallery.Utility.ImageLoader;
import com.vishnus1224.imagegallery.Utility.MemoryCache;

import java.lang.ref.WeakReference;

/**
 * Created by vishnu on 12/06/15.
 */
public class DecodeImageTask extends AsyncTask<Void, Void, Bitmap> {

    private Context context;
    private String path;
    private final WeakReference<ImageView> imageViewWeakReference;
    private MemoryCache memoryCache;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DecodeImageTask(Context context, String path, ImageView imageView, MemoryCache memoryCache){
        this.context = context;
        this.path = path;
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.memoryCache = memoryCache;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromPath(path, Constant.THUMBNAIL_IMAGE_WIDTH, Constant.THUMBNAIL_IMAGE_HEIGHT, memoryCache);
        addBitmapToCache(bitmap);
        return bitmap;
    }

    private void addBitmapToCache(Bitmap bitmap) {

        if(memoryCache.getBitmapFromMemoryCache(path) == null && bitmap != null){
            memoryCache.addBitmapToMemoryCache(path, bitmap);
        }

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()){
            bitmap = null;
        }

        //check to see if the weak reference is still around. It will be null if it is garbage collected.
        if(imageViewWeakReference != null && bitmap != null){
            ImageView imageView = imageViewWeakReference.get();
            DecodeImageTask decodeImageTask = ImageLoader.getDecodeImageTask(imageView);
            if(this == decodeImageTask && imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }

    }


}
