package com.dancing_koala.swipetorevealdetails.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * View only displaying a white stroked circle
 */
public class CircleView extends View {

    private static final int DEFAULT_STROKE_WIDTH = 4;
    /**
     * Default color for the circle
     */
    private static final String DEFAULT_BG_COLOR = "#FF4081";

    /**
     * halfHeight   : half of the view's height
     * halfWidth    : half of the view's width
     */
    private int halfHeight, halfWidth;

    /**
     * circlePaint  :   Paint used to draw the circle
     * strokePaint  :   Paint used to draw the circle's stroke
     */
    private Paint circlePaint, strokePaint;

    /**
     * @see View#View(Context)
     */
    public CircleView(Context context) {
        super(context);
    }

    /**
     * @see View#View(Context, AttributeSet)
     */
    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @see View#View(Context, AttributeSet, int)
     */
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * @see View#View(Context, AttributeSet, int, int)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initializes the view fields
     *
     * @param context Instanciation context
     * @param attrs   Style attribtutes
     */
    private void init(Context context, AttributeSet attrs) {
        // Retrieving the background attribute
        int[] attrsToGet = new int[]{android.R.attr.background};
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsToGet);
        Drawable bgDrawable = ta.getDrawable(0);
        ta.recycle();

        int color;

        if (bgDrawable != null && bgDrawable instanceof ColorDrawable) {
            color = ((ColorDrawable) bgDrawable).getColor();
        } else {
            color = Color.parseColor(DEFAULT_BG_COLOR);
        }

        setBackgroundColor(Color.TRANSPARENT);

        circlePaint = new Paint();
        circlePaint.setStrokeWidth(4);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(color);

        strokePaint = new Paint(circlePaint);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(
                halfWidth,
                halfHeight,
                Math.min(halfWidth, halfHeight),
                circlePaint
        );

        canvas.drawCircle(
                halfWidth,
                halfHeight,
                Math.min(halfWidth, halfHeight) - DEFAULT_STROKE_WIDTH,
                strokePaint
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        halfHeight = getMeasuredHeight() / 2;
        halfWidth = getMeasuredWidth() / 2;
    }
}
