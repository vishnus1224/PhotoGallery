package com.vishnus1224.imagegallery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.vishnus1224.imagegallery.R;
import com.vishnus1224.imagegallery.Utility.ImageLoader;

import java.util.List;

/**
 * Created by vishnu on 12/06/15.
 */
public class LazyImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> imagePaths;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;

    public LazyImageAdapter(Context context, List<String> imagePaths){
        this.context = context;
        this.imagePaths = imagePaths;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.gridview_image_gallery, parent, false);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.gridview_row_imageview);
        String path = imagePaths.get(position);

        imageLoader.fetchImage(context, path, imageView);

        return convertView;
    }
}
