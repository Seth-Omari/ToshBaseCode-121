/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.validation.METValidator;

import ke.co.toshngure.basecode.annotations.GsonAvoid;


/**
 * Created by Anthony Ngure on 17/02/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

public class BaseUtils {

    public static void cacheInput(@NonNull EditText editText, @StringRes final int key, final PrefUtilsImpl prefUtils) {
        String currentInput = prefUtils.getString(key);
        editText.setText(currentInput);
        editText.setSelection(currentInput.length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prefUtils.writeString(key, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static boolean canConnect(@NonNull Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static int generateColor(Object object) {
        return ColorGenerator.MATERIAL.getColor(object);
    }

    public static TextDrawable getTextDrawableAvatar(String text, @Nullable Object object) {
        if (object == null) {
            object = text;
        }
        return TextDrawable.builder().round().build(text, generateColor(object));
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Gson getSafeGson() {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return ((fieldAttributes.getAnnotation(GsonAvoid.class) != null));
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();
    }

    public static Gson getSafeGson(final String... avoidNames) {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                for (String avoidName : avoidNames) {
                    if (fieldAttributes.getName().equals(avoidName)){
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();
    }

    public static METValidator createEmailValidator(String errorMsg) {
        return new METValidator(errorMsg) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && Patterns.EMAIL_ADDRESS.matcher(text).matches();
            }
        };
    }

    public static METValidator createPhoneValidator(String errorMsg) {
        return new METValidator(errorMsg) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && Patterns.PHONE.matcher(text).matches();
            }
        };
    }

    public static METValidator createRequiredValidator(String errorMsg) {
        return new METValidator(errorMsg) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty;
            }
        };
    }

    public static METValidator createLengthValidator(final int length) {
        return new METValidator("You must input " + length + " characters") {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && (text.length() == length);
            }
        };
    }

    public static void setButtonBackgroundTintList(AppCompatButton button, int color) {
        ColorStateList colorStateList = new ColorStateList(new int[][]{new int[0]}, new int[]{color});
        button.setSupportBackgroundTintList(colorStateList);
    }

    /**
     * Get the color value for give attribute
     */
    @ColorInt
    public static int getColorAttr(Context ctx, @AttrRes int colorAttrId){
        int[] attrs = new int[] { colorAttrId /* index 0 */};
        TypedArray ta = ctx.obtainStyledAttributes(attrs);
        int colorFromTheme = ta.getColor(0, 0);
        ta.recycle();
        return colorFromTheme;
    }

    public static void tintMenu(Activity activity, Menu menu, @ColorInt int color){
        if(menu != null && menu.size() > 0){
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                Drawable icon = item.getIcon();
                if(icon != null){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        icon.setTint(color);
                    }else{
                        icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }
        }
    }
}
