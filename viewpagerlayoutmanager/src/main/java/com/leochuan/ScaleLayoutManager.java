package com.leochuan;

import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which zooms the center item
 */

@SuppressWarnings("WeakerAccess")
public class ScaleLayoutManager extends ViewPagerLayoutManager {

    private int itemSpace;
    private float centerScale;
    private float moveSpeed;

    public ScaleLayoutManager(int itemSpace) {
        this(new Builder(itemSpace));
    }

    public ScaleLayoutManager(int itemSpace, int orientation) {
        this(new Builder(itemSpace).setOrientation(orientation));
    }

    public ScaleLayoutManager(int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public ScaleLayoutManager(Builder builder) {
        this(builder.itemSpace, builder.centerScale,
                builder.orientation, builder.moveSpeed, builder.reverseLayout);
    }

    private ScaleLayoutManager(int itemSpace, float centerScale, int orientation,
                               float moveSpeed, boolean reverseLayout) {
        super(orientation, reverseLayout);
        setIntegerDy(true);
        this.itemSpace = itemSpace;
        this.centerScale = centerScale;
        this.moveSpeed = moveSpeed;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public float getCenterScale() {
        return centerScale;
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

    public void setCenterScale(float centerScale) {
        assertNotInLayoutOrScroll(null);
        if (this.centerScale == centerScale) return;
        this.centerScale = centerScale;
        removeAllViews();
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
        return mDecoratedMeasurement * ((centerScale - 1) / 2 + 1) + itemSpace;
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

    /**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     */
    private float calculateScale(float x) {
        float deltaX = Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        float diff = 0f;
        if ((mDecoratedMeasurement - deltaX) > 0) diff = mDecoratedMeasurement - deltaX;
        return (centerScale - 1f) / mDecoratedMeasurement * diff + 1;
    }

    public static class Builder {
        private static final float SCALE_RATE = 1.2f;
        private static final float DEFAULT_SPEED = 1f;
        private int itemSpace;
        private int orientation;
        private float centerScale;
        private float moveSpeed;
        private boolean reverseLayout;

        public Builder(int itemSpace) {
            this.itemSpace = itemSpace;
            orientation = HORIZONTAL;
            centerScale = SCALE_RATE;
            this.moveSpeed = DEFAULT_SPEED;
            reverseLayout = false;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setCenterScale(float centerScale) {
            this.centerScale = centerScale;
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

        public ScaleLayoutManager build() {
            return new ScaleLayoutManager(this);
        }
    }
}

