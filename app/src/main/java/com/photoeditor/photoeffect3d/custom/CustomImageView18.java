package com.photoeditor.photoeffect3d.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

public class CustomImageView18 extends AppCompatImageView {
    public static float radius = 9.0f;

    public CustomImageView18(Context context) {
        super(context);
    }

    public CustomImageView18(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView18(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        clipPath.addRoundRect(new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), radius, radius, Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
