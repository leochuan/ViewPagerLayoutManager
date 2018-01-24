package com.leochuan;

import android.content.Context;
import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which will change rotate x or rotate y
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class GalleryLayoutManager extends ViewPagerLayoutManager {
    private final float MAX_ELEVATION = 5F;

    private int itemSpace;
    private float moveSpeed;
    private float maxAlpha;
    private float minAlpha;
    private float angle;
    private boolean flipRotate;
    private boolean rotateFromEdge;

    public GalleryLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public GalleryLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public GalleryLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public GalleryLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.angle, builder.maxAlpha, builder.minAlpha,
                builder.orientation, builder.moveSpeed, builder.flipRotate, builder.rotateFromEdge,
                builder.maxVisibleItemCount, builder.distanceToBottom, builder.reverseLayout);
    }

    private GalleryLayoutManager(Context context, int itemSpace, float angle, float maxAlpha, float minAlpha,
                                 int orientation, float moveSpeed, boolean flipRotate, boolean rotateFromEdge,
                                 int maxVisibleItemCount, int distanceToBottom, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.moveSpeed = moveSpeed;
        this.angle = angle;
        this.maxAlpha = maxAlpha;
        this.minAlpha = minAlpha;
        this.flipRotate = flipRotate;
        this.rotateFromEdge = rotateFromEdge;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public float getMaxAlpha() {
        return maxAlpha;
    }

    public float getMinAlpha() {
        return minAlpha;
    }

    public float getAngle() {
        return angle;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public boolean getFlipRotate() {
        return flipRotate;
    }

    public boolean getRotateFromEdge() {
        return rotateFromEdge;
    }

    public void setItemSpace(int itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) return;
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) return;
        this.moveSpeed = moveSpeed;
    }

    public void setMaxAlpha(float maxAlpha) {
        assertNotInLayoutOrScroll(null);
        if (maxAlpha > 1) maxAlpha = 1;
        if (this.maxAlpha == maxAlpha) return;
        this.maxAlpha = maxAlpha;
        requestLayout();
    }

    public void setMinAlpha(float minAlpha) {
        assertNotInLayoutOrScroll(null);
        if (minAlpha < 0) minAlpha = 0;
        if (this.minAlpha == minAlpha) return;
        this.minAlpha = minAlpha;
        requestLayout();
    }

    public void setAngle(float angle) {
        assertNotInLayoutOrScroll(null);
        if (this.angle == angle) return;
        this.angle = angle;
        requestLayout();
    }

    public void setFlipRotate(boolean flipRotate) {
        assertNotInLayoutOrScroll(null);
        if (this.flipRotate == flipRotate) return;
        this.flipRotate = flipRotate;
        requestLayout();
    }

    public void setRotateFromEdge(boolean rotateFromEdge) {
        assertNotInLayoutOrScroll(null);
        if (this.rotateFromEdge == rotateFromEdge) return;
        this.rotateFromEdge = rotateFromEdge;
        removeAllViews();
    }

    @Override
    protected float setInterval() {
        return mDecoratedMeasurement + itemSpace;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        final float rotation = calRotation(targetOffset);
        if (getOrientation() == HORIZONTAL) {
            if (rotateFromEdge) {
                itemView.setPivotX(rotation > 0 ? 0 : mDecoratedMeasurement);
                itemView.setPivotY(mDecoratedMeasurementInOther * 0.5f);
            }
            if (flipRotate) {
                itemView.setRotationX(rotation);
            } else {
                itemView.setRotationY(rotation);
            }
        } else {
            if (rotateFromEdge) {
                itemView.setPivotY(rotation > 0 ? 0 : mDecoratedMeasurement);
                itemView.setPivotX(mDecoratedMeasurementInOther * 0.5f);

            }
            if (flipRotate) {
                itemView.setRotationY(-rotation);
            } else {
                itemView.setRotationX(-rotation);
            }
        }
        final float alpha = calAlpha(targetOffset);
        itemView.setAlpha(alpha);
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        final float ele = Math.max(Math.abs(itemView.getRotationX()), Math.abs(itemView.getRotationY())) * MAX_ELEVATION / 360;
        return MAX_ELEVATION - ele;
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) return Float.MAX_VALUE;
        return 1 / moveSpeed;
    }

    private float calRotation(float targetOffset) {
        return -angle / mInterval * targetOffset;
    }

    private float calAlpha(float targetOffset) {
        final float offset = Math.abs(targetOffset);
        float alpha = (minAlpha - maxAlpha) / mInterval * offset + maxAlpha;
        if (offset >= mInterval) alpha = minAlpha;
        return alpha;
    }

    public static class Builder {
        private static float INTERVAL_ANGLE = 30f;
        private static final float DEFAULT_SPEED = 1f;
        private static float MIN_ALPHA = 0.5f;
        private static float MAX_ALPHA = 1f;

        private int itemSpace;
        private float moveSpeed;
        private int orientation;
        private float maxAlpha;
        private float minAlpha;
        private float angle;
        private boolean flipRotate;
        private boolean reverseLayout;
        private Context context;
        private int maxVisibleItemCount;
        private int distanceToBottom;
        private boolean rotateFromEdge;

        public Builder(Context context, int itemSpace) {
            this.itemSpace = itemSpace;
            this.context = context;
            orientation = HORIZONTAL;
            angle = INTERVAL_ANGLE;
            maxAlpha = MAX_ALPHA;
            minAlpha = MIN_ALPHA;
            this.moveSpeed = DEFAULT_SPEED;
            reverseLayout = false;
            flipRotate = false;
            rotateFromEdge = false;
            distanceToBottom = ViewPagerLayoutManager.INVALID_SIZE;
            maxVisibleItemCount = ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN;
        }

        public Builder setItemSpace(int itemSpace) {
            this.itemSpace = itemSpace;
            return this;
        }

        public Builder setMoveSpeed(float moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setMaxAlpha(float maxAlpha) {
            if (maxAlpha > 1) maxAlpha = 1;
            this.maxAlpha = maxAlpha;
            return this;
        }

        public Builder setMinAlpha(float minAlpha) {
            if (minAlpha < 0) minAlpha = 0;
            this.minAlpha = minAlpha;
            return this;
        }

        public Builder setAngle(float angle) {
            this.angle = angle;
            return this;
        }

        public Builder setFlipRotate(boolean flipRotate) {
            this.flipRotate = flipRotate;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
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

        public Builder setRotateFromEdge(boolean rotateFromEdge) {
            this.rotateFromEdge = rotateFromEdge;
            return this;
        }

        public GalleryLayoutManager build() {
            return new GalleryLayoutManager(this);
        }
    }
}
