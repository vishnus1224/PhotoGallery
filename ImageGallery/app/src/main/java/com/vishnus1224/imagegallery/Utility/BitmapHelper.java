package com.vishnus1224.imagegallery.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Helper class to handle proper decoding of bitmaps
 * Created by vishnu on 12/06/15.
 */
public class BitmapHelper {

    /**
     * Tells the decoder to sub sample the image, loading a smaller version into memory.
     *
     * @param path The path of the image to decode
     * @param reqWidth Required width of the final bitmap
     * @param reqHeight Required height of the final bitmap
     * @param memoryCache
     * @return Bitmap scaled to the required width and height
     */
    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight, MemoryCache memoryCache) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // Uncomment this if you want to reuse already existing bitmaps. Make sure that the images are of same size if you are using this method.
//        if (Utils.hasHoneycomb()) {
//            addInBitmapOptions(options, memoryCache);
//        }

        return BitmapFactory.decodeFile(path, options);
    }

    private static void addInBitmapOptions(BitmapFactory.Options options,
                                           MemoryCache cache) {
        // inBitmap only works with mutable bitmaps, so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try to find a bitmap to use for inBitmap.
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                // If a suitable bitmap has been found, set it as the value of
                // inBitmap.
                options.inBitmap = inBitmap;
            }
        }
    }


    /**
     * Tells the decoder to sub sample the image, loading a smaller version into memory.
     *
     * @param inputStream The stream containing the image to decode
     * @param reqWidth Required width of the final bitmap
     * @param reqHeight Required height of the final bitmap
     * @return Bitmap scaled to the required width and height
     */
    public static Bitmap decodeSampledBitmapFromStream(InputStream inputStream, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    /**
     * Calculates the sample size needed to scale the bitmap
     * For example, an image with resolution 2048x1536 that is decoded with an inSampleSize of 4 produces a bitmap of approximately 512x384.
     *
     * @param options Options object to get the width and height of bitmap.
     * @param reqWidth Required width of the final bitmap
     * @param reqHeight Required height of the final bitmap
     * @return The calculated sample size.
     */
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

}
