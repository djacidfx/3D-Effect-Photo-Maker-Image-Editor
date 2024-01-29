package com.photoeditor.photoeffect3d.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class CustomTexture extends TextureView {
    public CustomTexture(Context context) {
        super(context);
    }

    public CustomTexture(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTexture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = widthSize;
        int height = heightSize;
        if (widthMode == 1073741824) {
            width = widthSize;
        } else if (widthMode == Integer.MIN_VALUE) {
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        } else if (heightMode == Integer.MIN_VALUE) {
        }
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) Math.round(((double) height) * 0.8d), height);
    }
}
