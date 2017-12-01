package com.leochuan;

import android.content.Context;
import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which layouts item in a circle and will change the child's centerScale while scrolling
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class CircleScaleLayoutManager extends ViewPagerLayoutManager {
    public static final int LEFT_ON_TOP = 0;
    public static final int RIGHT_ON_TOP = 1;
    public static final int CENTER_ON_TOP = 2;

    private int radius;
    private int angleInterval;
    private float moveSpeed;
    private float centerScale;
    private float maxRemoveAngle;
    private float minRemoveAngle;
    private int zAlignment;

    public CircleScaleLayoutManager(Context context) {
        this(new Builder(context));
    }

    public CircleScaleLayoutManager(Context context, boolean reverseLayout) {
        this(new Builder(context).setReverseLayout(reverseLayout));
    }

    public CircleScaleLayoutManager(Builder builder) {
        this(builder.context, builder.radius, builder.angleInterval, builder.centerScale, builder.moveSpeed,
                builder.maxRemoveAngle, builder.minRemoveAngle, builder.zAlignment, builder.reverseLayout);
    }

    private CircleScaleLayoutManager(Context context, int radius, int angleInterval, float centerScale, float moveSpeed,
                                     float max, float min, int zAlignment, boolean reverseLayout) {
        super(context, HORIZONTAL, reverseLayout);
        setEnableBringCenterToFront(true);
        this.radius = radius;
        this.angleInterval = angleInterval;
        this.centerScale = centerScale;
        this.moveSpeed = moveSpeed;
        this.maxRemoveAngle = max;
        this.minRemoveAngle = min;
        this.zAlignment = zAlignment;
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

    public int getZAlignment() {
        return zAlignment;
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

    public void setZAlignment(int zAlignment) {
        assertNotInLayoutOrScroll(null);
        assertZAlignmentState(zAlignment);
        if (this.zAlignment == zAlignment) return;
        this.zAlignment = zAlignment;
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
    protected int calItemLeft(View itemView, float targetOffset) {
        return (int) (radius * Math.cos(Math.toRadians(90 - targetOffset)));
    }

    @Override
    protected int calItemTop(View itemView, float targetOffset) {
        return (int) (radius - radius * Math.sin(Math.toRadians(90 - targetOffset)));
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        itemView.setRotation(targetOffset);
        float scale = calculateScale(itemView, targetOffset);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        if (zAlignment == LEFT_ON_TOP)
            return (540 - targetOffset) / 72;
        else if (zAlignment == RIGHT_ON_TOP)
            return (targetOffset - 540) / 72;
        else
            return (360 - Math.abs(targetOffset)) / 72;
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

    static void assertZAlignmentState(int zAlignment) {
        if (zAlignment != LEFT_ON_TOP && zAlignment != RIGHT_ON_TOP && zAlignment != CENTER_ON_TOP) {
            throw new IllegalStateException("zAlignment must be one of LEFT_ON_TOP RIGHT_ON_TOP and CENTER_ON_TOP");
        }
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
        private Context context;
        private int zAlignment;

        public Builder(Context context) {
            this.context = context;
            radius = INVALID_VALUE;
            angleInterval = INTERVAL_ANGLE;
            centerScale = SCALE_RATE;
            moveSpeed = 1 / DISTANCE_RATIO;
            maxRemoveAngle = 90;
            minRemoveAngle = -90;
            reverseLayout = false;
            zAlignment = CENTER_ON_TOP;
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

        public Builder setZAlignment(int zAlignment) {
            assertZAlignmentState(zAlignment);
            this.zAlignment = zAlignment;
            return this;
        }

        public CircleScaleLayoutManager build() {
            return new CircleScaleLayoutManager(this);
        }
    }
}
