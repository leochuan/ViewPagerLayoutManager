package com.leochuan;

import android.os.Build;
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

    public CarouselLayoutManager(int itemSpace) {
        this(new Builder(itemSpace));
    }

    public CarouselLayoutManager(int itemSpace, int orientation) {
        this(new Builder(itemSpace).setOrientation(orientation));
    }

    public CarouselLayoutManager(int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public CarouselLayoutManager(Builder builder) {
        this(builder.itemSpace, builder.minScale,
                builder.orientation, builder.moveSpeed, builder.reverseLayout);
    }

    private CarouselLayoutManager(int itemSpace, float minScale, int orientation,
                                  float moveSpeed, boolean reverseLayout) {
        super(orientation, reverseLayout);
        setEnableBringCenterToFront(true);
        setIntegerDy(true);
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

    public int getOrientation() {
        return super.getOrientation();
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

    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(scale);
        }
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) return Float.MAX_VALUE;
        return 1 / moveSpeed;
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        return itemView.getScaleX();
    }

    private float calculateScale(float x) {
        float deltaX = Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        return (minScale - 1) * deltaX / (mOrientationHelper.getTotalSpace() / 2f) + 1f;
    }

    public static class Builder {
        private static final float DEFAULT_SPEED = 1f;
        private static final float MIN_SCALE = 0.5f;

        private int itemSpace;
        private int orientation;
        private float minScale;
        private float moveSpeed;
        private boolean reverseLayout;

        public Builder(int itemSpace) {
            this.itemSpace = itemSpace;
            orientation = HORIZONTAL;
            minScale = MIN_SCALE;
            this.moveSpeed = DEFAULT_SPEED;
            reverseLayout = false;
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

        public CarouselLayoutManager build() {
            return new CarouselLayoutManager(this);
        }
    }
}
