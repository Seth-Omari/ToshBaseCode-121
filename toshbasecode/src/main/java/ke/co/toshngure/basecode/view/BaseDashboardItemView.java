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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.toshngure.basecode.R;
import ke.co.toshngure.basecode.utils.BaseUtils;


/**
 * Created by Anthony Ngure on 7/25/2016.
 * Email : anthonyngure25@gmail.com.
 *
 */
public class BaseDashboardItemView extends FrameLayout {

    private static final int DEFAULT_ICON_SIZE = 64;
    private TextView mTitleTV;
    private ImageView mIconIV;

    public BaseDashboardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BaseDashboardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BaseDashboardItemView);

        View mContentView = LayoutInflater.from(getContext()).inflate(R.layout.view_dashboard_item, this, true);

        mTitleTV =  mContentView.findViewById(R.id.titleTV);
        mIconIV =  mContentView.findViewById(R.id.iconIV);

        setItemTitle(typedArray.getString(R.styleable.BaseDashboardItemView_itemTitle));
        setItemIcon(typedArray.getDrawable(R.styleable.BaseDashboardItemView_itemIcon));

        int titleColor = typedArray.getColor(R.styleable.BaseDashboardItemView_itemTitleColor, Color.WHITE);
        mTitleTV.setTextColor(titleColor);

        int iconTint = typedArray.getColor(R.styleable.BaseDashboardItemView_itemIconTint, Color.WHITE);
        mIconIV.setColorFilter(iconTint);

        int iconBackground = typedArray.getColor(R.styleable.BaseDashboardItemView_itemIconBackground,
                BaseUtils.getColorAttr(getContext(), R.attr.colorPrimaryDark));

        int itemIconSize = typedArray.getDimensionPixelSize(R.styleable.BaseDashboardItemView_itemIconSize, DEFAULT_ICON_SIZE);

        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(itemIconSize);
        shapeDrawable.setIntrinsicWidth(itemIconSize);
        shapeDrawable.getPaint().setColor(iconBackground);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mIconIV.setBackground(shapeDrawable);
        } else {
            mIconIV.setBackgroundDrawable(shapeDrawable);
        }

        if (isInEditMode()){
            setBadge(5);
        }

        typedArray.recycle();
    }

    public void setItemTitle(String itemTitle) {
        if (!TextUtils.isEmpty(itemTitle)) {
            mTitleTV.setVisibility(VISIBLE);
            mTitleTV.setText(itemTitle);
        } else {
            mTitleTV.setVisibility(GONE);
        }
    }

    public void setItemIcon(Drawable itemIcon) {
        if (itemIcon == null) {
            mIconIV.setVisibility(GONE);
        } else {
            mIconIV.setImageDrawable(itemIcon);
            mIconIV.setVisibility(VISIBLE);
        }
    }

    public void setBadge(int count){

        BaseBadgeView badge = new BaseBadgeView(getContext(), mIconIV);
        badge.setBadgePosition(BaseBadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(Color.RED);
        badge.setTextColor(Color.WHITE);
        if (count == 0){
            badge.setText("");
            badge.show();
            badge.hide(true);

        } else {
            badge.setText(String.valueOf(count));
            badge.show();
        }

    }

}
