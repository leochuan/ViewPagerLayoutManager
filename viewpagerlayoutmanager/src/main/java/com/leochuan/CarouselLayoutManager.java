package com.leochuan;

import android.content.Context;
import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which layouts items like carousel
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class CarouselLayoutManager extends ViewPagerLayoutManager {

    private int itemSpace;
    private float minScale;
    private float moveSpeed;

    public CarouselLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public CarouselLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public CarouselLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public CarouselLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.minScale, builder.orientation,
                builder.maxVisibleItemCount, builder.moveSpeed, builder.distanceToBottom,
                builder.reverseLayout);
    }

    private CarouselLayoutManager(Context context, int itemSpace, float minScale, int orientation,
                                  int maxVisibleItemCount, float moveSpeed, int distanceToBottom,
                                  boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setEnableBringCenterToFront(true);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.minScale = minScale;
        this.moveSpeed = moveSpeed;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setItemSpace(int itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) return;
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public void setMinScale(float minScale) {
        assertNotInLayoutOrScroll(null);
        if (minScale > 1f) minScale = 1f;
        if (this.minScale == minScale) return;
        this.minScale = minScale;
        requestLayout();
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) return;
        this.moveSpeed = moveSpeed;
    }

    @Override
    protected float setInterval() {
        return (mDecoratedMeasurement - itemSpace);
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale(targetOffset + mSpaceMain);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) return Float.MAX_VALUE;
        return 1 / moveSpeed;
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        return itemView.getScaleX() * 5;
    }

    private float calculateScale(float x) {
        float deltaX = Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        return (minScale - 1) * deltaX / (mOrientationHelper.getTotalSpace() / 2f) + 1f;
    }

    public static class Builder {
        private static final float DEFAULT_SPEED = 1f;
        private static final float MIN_SCALE = 0.5f;

        private Context context;
        private int itemSpace;
        private int orientation;
        private float minScale;
        private float moveSpeed;
        private int maxVisibleItemCount;
        private boolean reverseLayout;
        private int distanceToBottom;

        public Builder(Context context, int itemSpace) {
            this.itemSpace = itemSpace;
            this.context = context;
            orientation = HORIZONTAL;
            minScale = MIN_SCALE;
            this.moveSpeed = DEFAULT_SPEED;
            reverseLayout = false;
            maxVisibleItemCount = ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN;
            distanceToBottom = ViewPagerLayoutManager.INVALID_SIZE;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setMinScale(float minScale) {
            this.minScale = minScale;
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

        public Builder setMaxVisibleItemCount(int maxVisibleItemCount) {
            this.maxVisibleItemCount = maxVisibleItemCount;
            return this;
        }

        public Builder setDistanceToBottom(int distanceToBottom) {
            this.distanceToBottom = distanceToBottom;
            return this;
        }

        public CarouselLayoutManager build() {
            return new CarouselLayoutManager(this);
        }
    }
}
