package com.vishnus1224.imagegallery.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by vishnu on 17/06/15.
 */
public class MemoryCache {

    private LruCache<String, Bitmap> memoryCache;

    //To store previously used bitmaps for reuse
    Set<SoftReference<Bitmap>> reusableBitmapSet;

    public MemoryCache(){
        init();
    }

    private void init() {

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        if(Utils.hasHoneycomb()){
            reusableBitmapSet = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }

        memoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // The cache size will be measured in kilobytes rather than number of items.
                return value.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if(Utils.hasHoneycomb()){
                    //add the bitmap to a SoftReference set for possible use with inBitmap later.
                    reusableBitmapSet.add(new SoftReference<Bitmap>(oldValue));
                }
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromMemoryCache(key) == null){
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key){
        return memoryCache.get(key);
    }

    // This method iterates through the reusable bitmaps, looking for one
    // to use for inBitmap:
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (reusableBitmapSet != null && !reusableBitmapSet.isEmpty()) {
            synchronized (reusableBitmapSet) {
                final Iterator<SoftReference<Bitmap>> iterator = reusableBitmapSet.iterator();

                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap.
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again.
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    private boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    /**
     * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
     */
    private int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

}
