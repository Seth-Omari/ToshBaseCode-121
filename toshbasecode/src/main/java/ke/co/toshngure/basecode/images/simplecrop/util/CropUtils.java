/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.images.simplecrop.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import ke.co.toshngure.basecode.R;


/**
 * Builder for crop Intents and utils for handling result
 */
public class CropUtils {

    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;

    /**
     * Retrieve error that caused crop to fail
     *
     * @param result Result Intent
     * @return Throwable handled in CropImageActivity
     */
    public static Throwable getError(Intent result) {
        return (Throwable) result.getSerializableExtra(Extra.ERROR);
    }

    /**
     * Pick image from an Activity
     *
     * @param activity Activity to receive result
     */
    public static void pickImage(Activity activity) {
        try {
            activity.startActivityForResult(getImagePicker(), REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(activity);
        }
    }

    /**
     * Pick image from a support library Fragment
     *
     * @param context  Context
     * @param fragment Fragment to receive result
     */
    public static void pickImage(Context context, Fragment fragment) {
        try {
            fragment.startActivityForResult(getImagePicker(), REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(context);
        }
    }


    private static Intent getImagePicker() {
        return new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
    }

    private static void showImagePickerError(Context context) {
        Toast.makeText(context.getApplicationContext(), R.string.crop__pick_error, Toast.LENGTH_SHORT).show();
    }


    private static class Extra {
        public static final String ERROR = "error";
    }
}
