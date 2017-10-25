package com.leochuan;

import android.os.Build;
import android.view.View;

/**
 * A layoutManager which layouts item in a circle
 */

@SuppressWarnings("WeakerAccess")
public class CircleLayoutManager extends ViewPagerLayoutManager {

    private int radius;
    private int angleInterval;
    private float rotateSpeed;
    private float maxRemoveAngle;
    private float minRemoveAngle;

    public CircleLayoutManager() {
        this(new Builder());
    }

    public CircleLayoutManager(boolean reverseLayout) {
        this(new Builder().setReverseLayout(reverseLayout));
    }

    public CircleLayoutManager(Builder builder) {
        this(builder.radius, builder.angleInterval, builder.rotateSpeed, builder.maxRemoveAngle,
                builder.minRemoveAngle, builder.reverseLayout);
    }

    private CircleLayoutManager(int radius, int angleInterval, float rotateSpeed,
                                float max, float min, boolean reverseLayout) {
        super(HORIZONTAL, reverseLayout);
        this.radius = radius;
        this.angleInterval = angleInterval;
        this.rotateSpeed = rotateSpeed;
        this.maxRemoveAngle = max;
        this.minRemoveAngle = min;
    }

    public int getRadius() {
        return radius;
    }

    public int getAngleInterval() {
        return angleInterval;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public float getMaxRemoveAngle() {
        return maxRemoveAngle;
    }

    public float getMinRemoveAngle() {
        return minRemoveAngle;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        removeAllViews();
        requestLayout();
    }

    public void setAngleInterval(int angleInterval) {
        this.angleInterval = angleInterval;
        removeAllViews();
        requestLayout();
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
        requestLayout();
    }

    public void setMaxRemoveAngle(float maxRemoveAngle) {
        this.maxRemoveAngle = maxRemoveAngle;
        requestLayout();
    }

    public void setMinRemoveAngle(float minRemoveAngle) {
        this.minRemoveAngle = minRemoveAngle;
        requestLayout();
    }

    @Override
    protected float setInterval() {
        return angleInterval;
    }

    @Override
    protected void setUp() {
        radius = radius == Integer.MIN_VALUE ? mDecoratedMeasurementInOther : radius;
    }

    @Override
    protected float maxRemoveOffset() {
        return maxRemoveAngle;
    }

    @Override
    protected float minRemoveOffset() {
        return minRemoveAngle;
    }

    @Override
    protected int calMainDirection(float targetOffset) {
        return (int) (radius * Math.cos(Math.toRadians(90 - targetOffset)));
    }

    @Override
    protected int calOtherDirection(float targetOffset) {
        return (int) (radius - radius * Math.sin(Math.toRadians(90 - targetOffset)));
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        itemView.setRotation(targetOffset);
        if (getEnableBringCenterToFront() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(calculateElevation(targetOffset));
        }
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        if (getEnableBringCenterToFront())
            return calculateElevation(targetOffset);
        else return super.setViewElevation(itemView, targetOffset);
    }

    @Override
    protected float propertyChangeWhenScroll(View itemView) {
        return itemView.getRotation();
    }

    @Override
    protected float getDistanceRatio() {
        return 1 / rotateSpeed;
    }

    private float calculateElevation(float targetOffset) {
        return 360 - Math.abs(targetOffset);
    }

    public static class Builder {
        private static int INTERVAL_ANGLE = 30;// The default mInterval angle between each items
        private static float DISTANCE_RATIO = 10f; // Finger swipe distance divide item rotate angle
        private static int INVALID_VALUE = Integer.MIN_VALUE;

        private int radius;
        private int angleInterval;
        private float rotateSpeed;
        private float maxRemoveAngle;
        private float minRemoveAngle;
        private boolean reverseLayout;

        public Builder() {
            radius = INVALID_VALUE;
            angleInterval = INTERVAL_ANGLE;
            rotateSpeed = 1 / DISTANCE_RATIO;
            maxRemoveAngle = 90;
            minRemoveAngle = -90;
            reverseLayout = false;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public Builder setAngleInterval(int angleInterval) {
            this.angleInterval = angleInterval;
            return this;
        }

        public Builder setRotateSpeed(int rotateSpeed) {
            this.rotateSpeed = rotateSpeed;
            return this;
        }

        public Builder setMaxRemoveAngle(float maxRemoveAngle) {
            this.maxRemoveAngle = maxRemoveAngle;
            return this;
        }

        public Builder setMinRemoveAngle(float minRemoveAngle) {
            this.minRemoveAngle = minRemoveAngle;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
            return this;
        }

        public CircleLayoutManager build() {
            return new CircleLayoutManager(radius, angleInterval,
                    rotateSpeed, maxRemoveAngle, minRemoveAngle, reverseLayout);
        }
    }
}
