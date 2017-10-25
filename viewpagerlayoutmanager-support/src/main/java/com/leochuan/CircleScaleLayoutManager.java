package com.leochuan;

import android.os.Build;
import android.view.View;

/**
 * Created by Dajavu on 12/7/16.
 */

public class CircleScaleLayoutManager extends ViewPagerLayoutManager {

    private static int INTERVAL_ANGLE = 30;// The default mInterval angle between each items
    private static float DISTANCE_RATIO = 10f; // Finger swipe distance divide item rotate angle
    private static final float SCALE_RATE = 1.2f;

    private int mRadius;

    public CircleScaleLayoutManager() {
        this(false);
    }

    public CircleScaleLayoutManager(boolean reverseLayout) {
        super(HORIZONTAL, reverseLayout);
        setEnableBringCenterToFront(true);
    }

    @Override
    protected float setInterval() {
        return INTERVAL_ANGLE;
    }

    @Override
    protected void setUp() {
        mRadius = mDecoratedMeasurementInOther;
    }

    @Override
    protected float maxRemoveOffset() {
        return 90;
    }

    @Override
    protected float minRemoveOffset() {
        return -90;
    }

    @Override
    protected int calMainDirection(float targetOffset) {
        return (int) (mRadius * Math.cos(Math.toRadians(90 - targetOffset)));
    }

    @Override
    protected int calOtherDirection(float targetOffset) {
        return (int) (mRadius - mRadius * Math.sin(Math.toRadians(90 - targetOffset)));
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        itemView.setRotation(targetOffset);
        float scale = calculateScale(itemView, targetOffset);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(scale);
        }
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        return calculateScale(itemView, targetOffset);
    }

    @Override
    protected float propertyChangeWhenScroll(View itemView) {
        return itemView.getRotation();
    }

    @Override
    protected float getDistanceRatio() {
        return DISTANCE_RATIO;
    }

    private float calculateScale(View itemView, float targetOffset) {
        if (targetOffset >= INTERVAL_ANGLE || targetOffset <= -INTERVAL_ANGLE) return 1f;
        float diff = Math.abs(Math.abs(itemView.getRotation() - INTERVAL_ANGLE) - INTERVAL_ANGLE);
        return (SCALE_RATE - 1f) / -INTERVAL_ANGLE * diff + SCALE_RATE;
    }
}
