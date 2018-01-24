package com.leochuan;

import android.content.Context;
import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which rotates items
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class RotateLayoutManager extends ViewPagerLayoutManager {

    private int itemSpace;
    private float angle;
    private float moveSpeed;
    private boolean reverseRotate;

    public RotateLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public RotateLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public RotateLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public RotateLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.angle, builder.orientation, builder.moveSpeed,
                builder.reverseRotate, builder.maxVisibleItemCount, builder.distanceToBottom,
                builder.reverseLayout);
    }

    private RotateLayoutManager(Context context, int itemSpace, float angle, int orientation, float moveSpeed,
                                boolean reverseRotate, int maxVisibleItemCount, int distanceToBottom,
                                boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.angle = angle;
        this.moveSpeed = moveSpeed;
        this.reverseRotate = reverseRotate;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public float getAngle() {
        return angle;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public boolean getReverseRotate() {
        return reverseRotate;
    }

    public void setItemSpace(int itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) return;
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public void setAngle(float centerScale) {
        assertNotInLayoutOrScroll(null);
        if (this.angle == centerScale) return;
        this.angle = centerScale;
        requestLayout();
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) return;
        this.moveSpeed = moveSpeed;
    }

    public void setReverseRotate(boolean reverseRotate) {
        assertNotInLayoutOrScroll(null);
        if (this.reverseRotate == reverseRotate) return;
        this.reverseRotate = reverseRotate;
        requestLayout();
    }

    @Override
    protected float setInterval() {
        return mDecoratedMeasurement + itemSpace;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        itemView.setRotation(calRotation(targetOffset));
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) return Float.MAX_VALUE;
        return 1 / moveSpeed;
    }

    private float calRotation(float targetOffset) {
        final float realAngle = reverseRotate ? angle : -angle;
        return realAngle / mInterval * targetOffset;
    }

    public static class Builder {
        private static float INTERVAL_ANGLE = 360f;
        private static final float DEFAULT_SPEED = 1f;

        private int itemSpace;
        private int orientation;
        private float angle;
        private float moveSpeed;
        private boolean reverseRotate;
        private boolean reverseLayout;
        private Context context;
        private int maxVisibleItemCount;
        private int distanceToBottom;

        public Builder(Context context, int itemSpace) {
            this.context = context;
            this.itemSpace = itemSpace;
            orientation = HORIZONTAL;
            angle = INTERVAL_ANGLE;
            this.moveSpeed = DEFAULT_SPEED;
            reverseRotate = false;
            reverseLayout = false;
            distanceToBottom = ViewPagerLayoutManager.INVALID_SIZE;
            maxVisibleItemCount = ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setAngle(float angle) {
            this.angle = angle;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
            return this;
        }

        public Builder setMoveSpeed(float moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public Builder setReverseRotate(boolean reverseRotate) {
            this.reverseRotate = reverseRotate;
            return this;
        }

        public Builder setMaxVisibleItemCount(int maxVisibleItemCount) {
            this.maxVisibleItemCount = maxVisibleItemCount;
            return this;
        }

        public Builder setDistanceToBottom(int distanceToBottom) {
            this.distanceToBottom = distanceToBottom;
            return this;
        }

        public RotateLayoutManager build() {
            return new RotateLayoutManager(this);
        }
    }
}
