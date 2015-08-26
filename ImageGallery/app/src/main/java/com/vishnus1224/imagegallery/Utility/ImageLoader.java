package com.vishnus1224.imagegallery.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.vishnus1224.imagegallery.Task.DecodeImageTask;

import java.lang.ref.WeakReference;

/**
 * Created by vishnu on 12/06/15.
 */
public class ImageLoader {

    private MemoryCache memoryCache;

    //Use the disk cache when the application needs to download images from the internet.
    //private DiskLruImageCache diskLruImageCache;

    public ImageLoader(Context context){
        memoryCache = new MemoryCache();
        //diskLruImageCache = new DiskLruImageCache(context, "thumbnails");
    }

    /**
     * Starts an async task to fetch the image
     * @param context The current context
     * @param path  The path of the image
     * @param imageView The imageview to pass to the async task
     */
    public void fetchImage(Context context, String path, ImageView imageView){

        //check if image is available in memory cache
        Bitmap bitmap = memoryCache.getBitmapFromMemoryCache(path);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            bitmap = null;
        }else {
            if (cancelPotentialDownload(path, imageView)) {
                DecodeImageTask decodeImageTask = new DecodeImageTask(context, path, imageView, memoryCache);
                AsyncDrawable asyncDrawable = new AsyncDrawable(decodeImageTask);
                imageView.setImageDrawable(asyncDrawable);
                decodeImageTask.execute();
            }
        }
    }

    /**
     * Stops the possible download in progress on this ImageView since a new one is about to start
     * @param path The path of the image
     * @param imageView The associated image view
     * @return true if the task is cancelled, false otherwise.
     */
    private boolean cancelPotentialDownload(String path, ImageView imageView){
        DecodeImageTask decodeImageTask = getDecodeImageTask(imageView);
        if(decodeImageTask != null){
            String imagePath = decodeImageTask.getPath();
            //If imagePath set in the task differs from the new path
            if(imagePath == null || (!imagePath.equals(path))){
                decodeImageTask.cancel(true);
            }else {
                // The same URL is already being downloaded.
                return false;
            }

        }
        return true;
    }


    //returns the task associated with the image view
    public static DecodeImageTask getDecodeImageTask(ImageView imageView){
        if(imageView != null){
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncDrawable){
                AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
                return asyncDrawable.getDecodeImageTask();
            }
        }
        return null;
    }


    static class AsyncDrawable extends ColorDrawable {

        private final WeakReference<DecodeImageTask> decodeImageTaskWeakReference;

        public AsyncDrawable( DecodeImageTask decodeImageTask){
            super(Color.BLACK);
            decodeImageTaskWeakReference = new WeakReference<DecodeImageTask>(decodeImageTask);
        }

        public DecodeImageTask getDecodeImageTask(){
            return decodeImageTaskWeakReference.get();
        }
    }

}
