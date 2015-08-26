package com.vishnus1224.imagegallery.Utility;

import android.os.Build;

/**
 * Created by vishnu on 19/06/15.
 */
public class Utils {

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
