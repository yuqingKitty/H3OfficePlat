package com.aotuo.h3officeplat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;

/**
 * 自定义标题栏
 */
public class TitleView extends LinearLayout {

    private ImageView ivBack;
    private TextView mTitle, mRightTitle;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        {
            View view = LayoutInflater.from(context).inflate(R.layout.view_activity_title, null);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, (int)(context.getResources().getDisplayMetrics().density * 45 + 0.5F));
            addView(view, lp);
            ivBack = (ImageView) view.findViewById(R.id.iv_back);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
            mRightTitle = (TextView) view.findViewById(R.id.tv_right_title);

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppTitle);
            String title = typedArray.getString(R.styleable.AppTitle_centerTitle);
            String rightTitle = typedArray.getString(R.styleable.AppTitle_rightTitle);
            if (title != null) {
                mTitle.setText(title);
            }
            if (rightTitle != null) {
                mRightTitle.setText(rightTitle);
            }
            typedArray.recycle();
        }
    }

    public void setTitle(String title) {
        if (title == null)
            title = "";
        mTitle.setText(title);
    }

    public void setRightTitle(String rightTitle) {
        if (rightTitle == null)
            rightTitle = "";
        mRightTitle.setText(rightTitle);
    }

    public TextView getTitleTv(){
        if (mTitle == null) {
            mTitle = (TextView) findViewById(R.id.tv_title);
        }
        return mTitle;
    }

    public TextView getRightTitle(){
        if (mRightTitle == null) {
            mRightTitle = (TextView) findViewById(R.id.tv_right_title);
        }
        return mRightTitle;
    }

    public ImageView getBackBtn() {
        if (ivBack == null) {
            ivBack = (ImageView) findViewById(R.id.iv_back);
        }
        return ivBack;
    }

}

