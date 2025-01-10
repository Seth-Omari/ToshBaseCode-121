/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.images;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.ref.WeakReference;

import ke.co.toshngure.basecode.R;
import ke.co.toshngure.basecode.view.CircleImageView;


/**
 * Created by Anthony Ngure on 20/02/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

public class BaseNetworkImage extends FrameLayout {

    private static final String TAG = BaseNetworkImage.class.getSimpleName();

    protected ImageView mImageView;
    protected ImageView mErrorButton;
    protected ProgressBar mProgressBar;
    private LoadingCallBack loadingCallBack;

    public BaseNetworkImage(Context context) {
        this(context, null);
    }

    public BaseNetworkImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseNetworkImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_network_image, this, true);
        mErrorButton = findViewById(R.id.errorButton);
        mProgressBar = findViewById(R.id.progressBar);
        ViewStub viewStub = findViewById(R.id.imageVS);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseNetworkImage);
        boolean circled = typedArray.getBoolean(R.styleable.BaseNetworkImage_circled, false);
        if (circled) {
            viewStub.setLayoutResource(R.layout.layout_circle_imageview);
        } else {
            viewStub.setLayoutResource(R.layout.layout_normal_imageview);
        }
        mImageView = (ImageView) viewStub.inflate();

        if (circled) {
            int imageSize = (int) typedArray.getDimension(R.styleable.BaseNetworkImage_circledImageSize, 48);
            mImageView.getLayoutParams().width = imageSize;
            mImageView.getLayoutParams().height = imageSize;
        }
        typedArray.recycle();

    }

    public void loadImageFromNetwork(final String networkPath) {

        mErrorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Retrying to loadFromNetwork image");
                mProgressBar.setVisibility(VISIBLE);
                mErrorButton.setVisibility(GONE);
                loadImageFromNetwork(networkPath);
            }
        });
        Glide.with(getContext().getApplicationContext())
                .load(networkPath)
                .dontAnimate()
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton))
                .into(mImageView);
    }

    public BaseNetworkImage setImageSize(int imageSize) {
        mImageView.getLayoutParams().width = imageSize;
        mImageView.getLayoutParams().height = imageSize;
        return this;
    }

    public void setProgressBarVisible(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(VISIBLE);
        } else {
            mProgressBar.setVisibility(GONE);
        }
    }

    public void loadImageFromMediaStore(String path) {
        File file = new File(path);
        Glide.with(getContext().getApplicationContext())
                .loadFromMediaStore(Uri.fromFile(file))
                .dontAnimate()
                .dontTransform()
                .listener(new Listener(mImageView, mProgressBar, mErrorButton))
                .into(mImageView);
    }


    public void loadImageFromMediaStore(Uri uri) {
        Glide.with(getContext().getApplicationContext())
                .loadFromMediaStore(uri)
                .dontAnimate()
                .dontTransform()
                .listener(new Listener(mImageView, mProgressBar, mErrorButton))
                .into(mImageView);
    }

    public BaseNetworkImage setLoadingCallBack(LoadingCallBack loadingCallBack) {
        this.loadingCallBack = loadingCallBack;
        return this;
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
        mImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mErrorButton.setVisibility(GONE);
    }

    public void setImageResource(@DrawableRes int resId) {
        mImageView.setImageResource(resId);
        mImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mErrorButton.setVisibility(GONE);
    }



    public interface LoadingCallBack {
        void onSuccess(GlideDrawable drawable);
    }



    /**
     * Glide Callback which clears the ImageView's background onSuccess. This is done to reduce
     * overdraw. A weak reference is used to avoid leaking the Activity context because the Callback
     * will be strongly referenced by Glide.
     */
    static class Listener implements RequestListener<Object, GlideDrawable> {

        final WeakReference<ImageView> imageViewWeakReference;
        final WeakReference<ProgressBar> progressBarWeakReference;
        final WeakReference<ImageView> errorImageViewWeakReference;

        public Listener(ImageView imageView, ProgressBar progressBar, ImageView errorImageView) {
            imageViewWeakReference = new WeakReference<>(imageView);
            progressBarWeakReference = new WeakReference<>(progressBar);
            errorImageViewWeakReference = new WeakReference<>(errorImageView);
        }


        @Override
        public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
            try {
                Log.e(TAG, e.toString());
            } catch (Exception e1) {
                Log.e(TAG, e1.toString());
            }

            ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(GONE);
            }

            ImageView errorImageView = errorImageViewWeakReference.get();
            if (errorImageView != null) {
                errorImageView.setVisibility(VISIBLE);
            }

            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            final ImageView imageView = imageViewWeakReference.get();
            if (imageView != null) {
                if (imageView instanceof CircleImageView){
                    imageView.setBackgroundResource(android.R.color.transparent);
                } else {
                    imageView.setBackgroundResource(android.R.color.white);
                }
            }
            ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(GONE);
            }

            ImageView errorImageView = errorImageViewWeakReference.get();
            if (errorImageView != null) {
                errorImageView.setVisibility(GONE);
            }
            return false;
        }
    }

}
