package com.rouchuan;

import android.view.View;

import rouchuan.customlayoutmanager.ViewPagerLayoutManager;

/**
 * Created by zixintechno on 12/7/16.
 */

public class GalleryLayoutManager extends ViewPagerLayoutManager {
    private static float INTERVAL_ANGLE = 30f;
    private static float MIN_ALPHA = 0.5f;

    private int itemSpace = 0;

    public GalleryLayoutManager(int itemSpace) {
        super();
        this.itemSpace = itemSpace;
    }

    public GalleryLayoutManager(int itemSpace, boolean shouldReverseLayout) {
        super(shouldReverseLayout);
        this.itemSpace = itemSpace;
    }

    @Override
    protected float setInterval() {
        return mDecoratedChildWidth + itemSpace;
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        itemView.setRotationY(calRotationY(targetOffset));
        itemView.setAlpha(calAlpha(targetOffset));
    }

    private float calRotationY(float targetOffset) {
        return -INTERVAL_ANGLE / interval * targetOffset;
    }

    private float calAlpha(float targetOffset) {
        float alpha = (MIN_ALPHA - 1f) / interval * Math.abs(targetOffset) + 1f;
        if (alpha < MIN_ALPHA) alpha = MIN_ALPHA;
        return alpha;
    }
}
