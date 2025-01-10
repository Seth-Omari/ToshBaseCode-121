/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.images;

import android.annotation.SuppressLint;
import android.content.Context;


/**
 * Created by Tosh on 08.12.16.
 */

@SuppressLint("ViewConstructor")
class CollageViewNetworkImage extends BaseNetworkImage {

    private BaseCollageView.ImageForm imageForm = BaseCollageView.ImageForm.IMAGE_FORM_SQUARE;

    public CollageViewNetworkImage(Context context, BaseCollageView.ImageForm imageForm) {
        super(context);
        this.imageForm = imageForm;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getParent() != null) {
            getLayoutParams().height = widthMeasureSpec / imageForm.getDivider();
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec / imageForm.getDivider());
        }
    }
}