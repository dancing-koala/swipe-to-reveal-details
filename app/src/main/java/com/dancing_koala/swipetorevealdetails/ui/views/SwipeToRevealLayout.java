package com.dancing_koala.swipetorevealdetails.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dancing_koala.swipetorevealdetails.R;
import com.dancing_koala.swipetorevealdetails.ui.listeners.FrontViewTouchListener;

public class SwipeToRevealLayout extends FrameLayout {

    private static final int DEFAULT_BACKVIEW_MARGIN_H = 32;
    private static final int DEFAULT_BACKVIEW_MARGIN_V = 32;
    private static final int DEFAULT_CORNER_RADIUS = 24;

    private boolean inLayout;
    private FrontViewTouchListener frontViewTouchListener;
    private int backviewInnerMarginH;
    private int backviewInnerMarginV;
    private int frontViewHeight;
    private int frontViewWidth;
    private int heightMeasureSpec;
    private int widthMeasureSpec;
    private int height;
    private int width;
    private View backView;
    private View frontView;

    /**
     * @see FrameLayout#FrameLayout(Context)
     */
    public SwipeToRevealLayout(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * @see FrameLayout#FrameLayout(Context, AttributeSet)
     */
    public SwipeToRevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @see FrameLayout#FrameLayout(Context, AttributeSet, int)
     */
    public SwipeToRevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * @see FrameLayout#FrameLayout(Context, AttributeSet, int, int)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeToRevealLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipeToRevealLayout);

        int backViewID = ta.getResourceId(R.styleable.SwipeToRevealLayout_layout_backview, -1);
        int frontViewID = ta.getResourceId(R.styleable.SwipeToRevealLayout_layout_frontview, -1);
        int cornerRadius = ta.getDimensionPixelSize(R.styleable.SwipeToRevealLayout_corner_radius, DEFAULT_CORNER_RADIUS);

        frontViewHeight = ta.getDimensionPixelSize(R.styleable.SwipeToRevealLayout_frontview_height, -1);
        frontViewWidth = ta.getDimensionPixelSize(R.styleable.SwipeToRevealLayout_frontview_width, -1);

        backviewInnerMarginH = ta.getDimensionPixelSize(R.styleable.SwipeToRevealLayout_backview_inner_margin_h, DEFAULT_BACKVIEW_MARGIN_H);
        backviewInnerMarginV = ta.getDimensionPixelSize(R.styleable.SwipeToRevealLayout_backview_inner_margin_v, DEFAULT_BACKVIEW_MARGIN_V);

        ta.recycle();

        if (backViewID == -1 || frontViewID == -1)
            throw new InflateException("You have to provide a frontview layout and a backview layout.");

        // Putting the frontview into a CardView
        CardView frontCardView = new CardView(context);
        frontCardView.setRadius(cornerRadius);
        frontCardView.addView(View.inflate(context, frontViewID, null), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Setting the LayoutParams for the content of the backview
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.rightMargin = backviewInnerMarginH;
        params.leftMargin = backviewInnerMarginH;
        params.bottomMargin = backviewInnerMarginV;
        params.topMargin = backviewInnerMarginV;
        // Putting the backview into a CardView
        View backViewContent = View.inflate(context, backViewID, null);
        CardView backCardView = new CardView(context);
        backCardView.setRadius(cornerRadius);
        backCardView.setAlpha(0f);
        backCardView.addView(backViewContent, params);

        // Assigning cardviews as frontview and backview
        frontView = frontCardView;
        backView = backCardView;

        addView(backView);
        addView(frontView);

        inLayout = false;
        frontViewTouchListener = new FrontViewTouchListener();
        frontViewTouchListener.setBackviewContentID(backViewContent.getId());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        if (!frontViewTouchListener.hasBackView()) {
            frontViewTouchListener.setBackView(backView);
            frontView.setOnTouchListener(frontViewTouchListener);
        }
    }

    @Override
    public void requestLayout() {
        if (!inLayout) super.requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        inLayout = true;
        addAndPositionChild(frontView, true);
        addAndPositionChild(backView, false);
        inLayout = false;
    }

    private void addAndPositionChild(View child, boolean isFrontView) {
        FrameLayout.LayoutParams params;

        if (child.getLayoutParams() == null) {
            params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            child.setLayoutParams(params);
        } else {
            params = (FrameLayout.LayoutParams) child.getLayoutParams();
        }

        if (!isFrontView) {
            params.width = frontViewWidth + 2 * backviewInnerMarginH;
            params.height = frontViewHeight + 2 * backviewInnerMarginV;

            child.setScaleX((float) frontViewWidth / params.width);
            child.setScaleY((float) frontViewHeight / params.height);

            frontViewTouchListener.setMinScaleX((float) frontViewWidth / params.width);
            frontViewTouchListener.setMinScaleY((float) frontViewHeight / params.height);

        } else {
            params.width = frontViewWidth;
            params.height = frontViewHeight;
        }

        final boolean needToMeasure = child.isLayoutRequested();

        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(
                    widthMeasureSpec,
                    getPaddingLeft() + getPaddingRight() + params.leftMargin + params.rightMargin,
                    params.width
            );

            int childHeightSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    getPaddingTop() + getPaddingBottom() + params.topMargin + params.bottomMargin,
                    params.height
            );

            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int left = width / 2 - w / 2;
        int top = height / 2 - h / 2;
        int right = left + w;
        int bottom = top + h;

        child.layout(left, top, right, bottom);
    }
}
