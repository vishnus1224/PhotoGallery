package com.vishnus1224.imagegallery;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridView;

import com.vishnus1224.imagegallery.Adapter.LazyImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private GridView imageGalleryGridView;
    private LazyImageAdapter lazyImageAdapter;
    private List<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageGalleryGridView = (GridView)findViewById(R.id.gridview_image_gallery);

        imagePaths = getCameraImagePaths(getApplicationContext());
        lazyImageAdapter = new LazyImageAdapter(getApplicationContext(), imagePaths);
        imageGalleryGridView.setAdapter(lazyImageAdapter);
    }

    /**
     * Gets the paths of all images from the phone
     * @param context The current context
     * @return List of all image paths
     */
    public List<String> getCameraImagePaths(Context context) {
        final String[] projection = { MediaStore.Images.Media.DATA };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        List<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

}
