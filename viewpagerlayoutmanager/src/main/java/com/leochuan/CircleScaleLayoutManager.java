package com.leochuan;

import android.os.Build;
import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which layouts item in a circle and will change the child's centerScale while scrolling
 */

@SuppressWarnings("WeakerAccess")
public class CircleScaleLayoutManager extends ViewPagerLayoutManager {

    private int radius;
    private int angleInterval;
    private float moveSpeed;
    private float centerScale;
    private float maxRemoveAngle;
    private float minRemoveAngle;

    public CircleScaleLayoutManager() {
        this(new Builder());
    }

    public CircleScaleLayoutManager(boolean reverseLayout) {
        this(new Builder().setReverseLayout(reverseLayout));
    }

    public CircleScaleLayoutManager(Builder builder) {
        this(builder.radius, builder.angleInterval, builder.centerScale, builder.moveSpeed,
                builder.maxRemoveAngle, builder.minRemoveAngle, builder.reverseLayout);
    }

    private CircleScaleLayoutManager(int radius, int angleInterval, float centerScale, float moveSpeed,
                                     float max, float min, boolean reverseLayout) {
        super(HORIZONTAL, reverseLayout);
        setEnableBringCenterToFront(true);
        this.radius = radius;
        this.angleInterval = angleInterval;
        this.centerScale = centerScale;
        this.moveSpeed = moveSpeed;
        this.maxRemoveAngle = max;
        this.minRemoveAngle = min;
    }

    public int getRadius() {
        return radius;
    }

    public int getAngleInterval() {
        return angleInterval;
    }

    public float getCenterScale() {
        return centerScale;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getMaxRemoveAngle() {
        return maxRemoveAngle;
    }

    public float getMinRemoveAngle() {
        return minRemoveAngle;
    }

    public void setRadius(int radius) {
        assertNotInLayoutOrScroll(null);
        if (this.radius == radius) return;
        this.radius = radius;
        removeAllViews();
    }

    public void setAngleInterval(int angleInterval) {
        assertNotInLayoutOrScroll(null);
        if (this.angleInterval == angleInterval) return;
        this.angleInterval = angleInterval;
        removeAllViews();
    }

    public void setCenterScale(float centerScale) {
        assertNotInLayoutOrScroll(null);
        if (this.centerScale == centerScale) return;
        this.centerScale = centerScale;
        requestLayout();
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) return;
        this.moveSpeed = moveSpeed;
    }

    public void setMaxRemoveAngle(float maxRemoveAngle) {
        assertNotInLayoutOrScroll(null);
        if (this.maxRemoveAngle == maxRemoveAngle) return;
        this.maxRemoveAngle = maxRemoveAngle;
        requestLayout();
    }

    public void setMinRemoveAngle(float minRemoveAngle) {
        assertNotInLayoutOrScroll(null);
        if (this.minRemoveAngle == minRemoveAngle) return;
        this.minRemoveAngle = minRemoveAngle;
        requestLayout();
    }

    @Override
    protected float setInterval() {
        return angleInterval;
    }

    @Override
    protected void setUp() {
        radius = radius == Builder.INVALID_VALUE ? mDecoratedMeasurementInOther : radius;
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
        float scale = calculateScale(itemView, targetOffset);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(calculateElevation(targetOffset));
        }
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return calculateElevation(targetOffset);
        }
        return super.setViewElevation(itemView, targetOffset);
    }

    @Override
    protected float propertyChangeWhenScroll(View itemView) {
        return itemView.getRotation();
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) return Float.MAX_VALUE;
        return 1 / moveSpeed;
    }

    private float calculateScale(View itemView, float targetOffset) {
        if (targetOffset >= angleInterval || targetOffset <= -angleInterval) return 1f;
        float diff = Math.abs(Math.abs(itemView.getRotation() - angleInterval) - angleInterval);
        return (centerScale - 1f) / -angleInterval * diff + centerScale;
    }

    private float calculateElevation(float targetOffset) {
        return 360 - Math.abs(targetOffset);
    }

    public static class Builder {
        private static int INTERVAL_ANGLE = 30;// The default mInterval angle between each items
        private static float DISTANCE_RATIO = 10f; // Finger swipe distance divide item rotate angle
        private static final float SCALE_RATE = 1.2f;
        private static int INVALID_VALUE = Integer.MIN_VALUE;

        private int radius;
        private int angleInterval;
        private float centerScale;
        private float moveSpeed;
        private float maxRemoveAngle;
        private float minRemoveAngle;
        private boolean reverseLayout;

        public Builder() {
            radius = INVALID_VALUE;
            angleInterval = INTERVAL_ANGLE;
            centerScale = SCALE_RATE;
            moveSpeed = 1 / DISTANCE_RATIO;
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

        public Builder setCenterScale(float centerScale) {
            this.centerScale = centerScale;
            return this;
        }

        public Builder setMoveSpeed(int moveSpeed) {
            this.moveSpeed = moveSpeed;
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

        public CircleScaleLayoutManager build() {
            return new CircleScaleLayoutManager(this);
        }
    }
}
