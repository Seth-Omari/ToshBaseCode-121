/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.toshngure.basecode.R;
import ke.co.toshngure.basecode.utils.BaseUtils;


/**
 * Created by Anthony Ngure on 7/25/2016.
 * Email : anthonyngure25@gmail.com.
 */
public class BaseSimpleListItemView extends FrameLayout {

    private TextView mTitleTV;
    private TextView mSubTitleTV;
    private ImageView mDrawableIV;

    public BaseSimpleListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BaseSimpleListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BaseSimpleListItemView);
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_list_item, this, true);

        mTitleTV = findViewById(R.id.titleTV);
        mSubTitleTV = findViewById(R.id.subTitleTV);
        mDrawableIV = findViewById(R.id.drawableIV);

        setTitle(typedArray.getString(R.styleable.BaseSimpleListItemView_sli_Title));
        int titleColor = typedArray.getColor(R.styleable.BaseSimpleListItemView_sli_TitleColor, Color.BLACK);
        mSubTitleTV.setTextColor(titleColor);

        setSubTitle(typedArray.getString(R.styleable.BaseSimpleListItemView_sli_SubTitle));
        int subTitleColor = typedArray.getColor(R.styleable.BaseSimpleListItemView_sli_SubTitleColor, Color.DKGRAY);
        mSubTitleTV.setTextColor(subTitleColor);

        setItemDrawable(typedArray.getDrawable(R.styleable.BaseSimpleListItemView_sli_Drawable));
        int drawableTint = typedArray.getColor(R.styleable.BaseSimpleListItemView_sli_DrawableTint,
                BaseUtils.getColorAttr(getContext(), R.attr.colorPrimary));
        mDrawableIV.setColorFilter(drawableTint);

        boolean showDivider = typedArray.getBoolean(R.styleable.BaseSimpleListItemView_sli_DividerVisible, true);
        findViewById(R.id.separatorView).setVisibility(showDivider ? VISIBLE : GONE);

        typedArray.recycle();
    }

    public void setTitle(String title) {
        if ((title != null) && (!title.isEmpty())) {
            mTitleTV.setVisibility(VISIBLE);
            mTitleTV.setText(title);
        } else {
            mTitleTV.setVisibility(GONE);
        }
    }


    public void setSubTitle(String subTitle) {
        if (subTitle == null || subTitle.isEmpty()) {
            mSubTitleTV.setVisibility(GONE);
        } else {
            mSubTitleTV.setText(subTitle);
            mSubTitleTV.setVisibility(VISIBLE);
        }
    }

    public void setItemDrawable(Drawable itemDrawable) {
        if (itemDrawable == null) {
            mDrawableIV.setVisibility(GONE);
        } else {
            mDrawableIV.setImageDrawable(itemDrawable);
            mDrawableIV.setVisibility(VISIBLE);
        }
    }
}
