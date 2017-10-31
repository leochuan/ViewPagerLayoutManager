package com.leochuan;

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

    public RotateLayoutManager(int itemSpace) {
        this(new Builder(itemSpace));
    }

    public RotateLayoutManager(int itemSpace, int orientation) {
        this(new Builder(itemSpace).setOrientation(orientation));
    }

    public RotateLayoutManager(int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public RotateLayoutManager(Builder builder) {
        this(builder.itemSpace, builder.angle,
                builder.orientation, builder.moveSpeed, builder.reverseRotate, builder.reverseLayout);
    }

    private RotateLayoutManager(int itemSpace, float angle, int orientation,
                                float moveSpeed, boolean reverseRotate, boolean reverseLayout) {
        super(orientation, reverseLayout);
        setIntegerDy(true);
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

    public int getOrientation() {
        return super.getOrientation();
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

    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
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

        public Builder(int itemSpace) {
            this.itemSpace = itemSpace;
            orientation = HORIZONTAL;
            angle = INTERVAL_ANGLE;
            this.moveSpeed = DEFAULT_SPEED;
            reverseRotate = false;
            reverseLayout = false;
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

        public RotateLayoutManager build() {
            return new RotateLayoutManager(this);
        }
    }
}
