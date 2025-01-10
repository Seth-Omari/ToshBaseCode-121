/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.images.simplecrop.util;

@SuppressWarnings("unused")
public class Logger {
    private static final String TAG = "SimpleCropView";
    public static boolean enabled = true;

    public static void e(String msg) {
        if (!enabled) return;
        android.util.Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable e) {
        if (!enabled) return;
        android.util.Log.e(TAG, msg, e);
    }
}
