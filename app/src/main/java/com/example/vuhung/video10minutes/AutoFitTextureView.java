package com.example.vuhung.video10minutes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class AutoFitTextureView extends TextureView {

    int maxwidth = 0;
    int maxheight = 0;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    // private Size previewSize;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
        }
    }
}
