package com.dancing_koala.swipetorevealdetails.ui.listeners;

import android.view.MotionEvent;
import android.view.View;

import com.dancing_koala.swipetorevealdetails.R;

/**
 * Listener dedicated to the front view's vertical swiping and to views animations
 */
public class FrontViewTouchListener implements View.OnTouchListener {

    private static final float
            MAX_SWIPE_PERCENT = 1.0f,
            MIN_SWIPE_PERCENT = 0.0f,
            MAX_SCALE = 1.0f,
            MIN_SCALE = 0.8f;

    private static final long DEFAULT_ANIM_DURATION = 150;

    private boolean swiping;
    private float originY;
    private float downY;
    private float verticalThreshold;
    private int backviewContentID;
    private int backViewHeight;
    private float minScaleX;
    private float minScaleY;

    /**
     * Self-explanatory
     */
    private View backView;

    /**
     * Constructor
     */
    public FrontViewTouchListener() {
        backViewHeight = -1;
        originY = -1;
        verticalThreshold = -1;
        downY = -1;
        swiping = false;
        minScaleX = MIN_SCALE;
        minScaleY = MIN_SCALE;
    }

    /**
     * @see View.OnTouchListener#onTouch(View, MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                if (backViewHeight < 1) {
                    // Initializing the back view content height
                    backViewHeight = backView.findViewById(backviewContentID).getHeight();
                    // Initializing the vertical threshold being a third of the back view content height
                    verticalThreshold = backViewHeight / 3;
                }

                // Initialiazing the original Y coordinate of the front view
                if (originY < 0) originY = v.getY();

                downY = event.getY();
                swiping = true;

                return true;

            case MotionEvent.ACTION_MOVE:
                if (swiping) {
                    float newY = v.getY() - (downY - event.getY());

                    // Swiping lower than the original Y or oo high is useless
                    // so we check that newY is between the minimum and maximum values.
                    if (newY < originY && newY > (originY - backViewHeight - verticalThreshold)) {
                        // move the front view to the new Y coordinate
                        v.setY(newY);

                        // Swiping percent calculated with the difference of the front view divided
                        // by the height of the back view content and then clamp to make sure the
                        // the value is between 0 and 1 included.
                        float swipePercent = clamp((originY - v.getY()) / backViewHeight, MIN_SWIPE_PERCENT, MAX_SWIPE_PERCENT);

                        // Scale calculated with the swipe percent and then clamp to make sure its
                        // value is between MIN_SCALE and MAX_SCALE.
                        backView.setScaleX(clamp(swipePercent * (MAX_SCALE - minScaleX) + minScaleX, minScaleX, MAX_SCALE));
                        backView.setScaleY(clamp(swipePercent * (MAX_SCALE - minScaleY) + minScaleY, minScaleY, MAX_SCALE));

                        // Alpha value is the same as the swiping percent.
                        backView.setAlpha(swipePercent);

                    } else if (v.getY() != originY) {
                        // The Y coodrinate of the front is out of the authorized bounds
                        swiping = false;
                        checkPosition(v);
                    }
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (swiping) {
                    // If still swiping when released, the frontview is repositionned
                    checkPosition(v);
                    swiping = false;
                }

                return true;
        }

        return false;
    }

    /**
     * Reposition the front view
     *
     * @param frontView Self-explanatory
     */
    private void checkPosition(View frontView) {

        final float frontY = frontView.getY();

        if (frontY != originY) {
            // The current front view's Y coordinate is different from the original Y so
            // we reposition the view accordingly.
            float targetY = (originY - frontY > verticalThreshold) ? originY - backViewHeight : originY;
            float targetAlpha = (originY - frontY > verticalThreshold) ? MAX_SCALE : 0f;
            float targetScaleX = (originY - frontY > verticalThreshold) ? MAX_SCALE : minScaleX;
            float targetScaleY = (originY - frontY > verticalThreshold) ? MAX_SCALE : minScaleY;

            //TODO animate the views using an AnimatorSet.

            // Animating the front view.
            frontView.animate()
                    .y(targetY)
                    .setDuration(DEFAULT_ANIM_DURATION)
                    .start();

            // Animating the back view.
            backView.animate()
                    .scaleX(targetScaleX)
                    .scaleY(targetScaleY)
                    .alpha(targetAlpha)
                    .setDuration(DEFAULT_ANIM_DURATION)
                    .start();
        }
    }

    /**
     * Clamps a float value
     *
     * @param val value to clamp
     * @param min minimum value to use for clamping
     * @param max maximum value to use for clamping
     * @return the value clamped
     */
    private float clamp(float val, float min, float max) {
        return Math.min(Math.max(val, min), max);
    }

    public boolean hasBackView() {
        return backView != null;
    }

    public void setBackView(View backView) {
        this.backView = backView;
    }

    public void setMinScaleX(float minScaleX) {
        this.minScaleX = minScaleX;
    }

    public void setMinScaleY(float minScaleY) {
        this.minScaleY = minScaleY;
    }

    public void setBackviewContentID(int backviewContentID) {
        this.backviewContentID = backviewContentID;
    }
}
