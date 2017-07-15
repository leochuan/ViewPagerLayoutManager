package com.rouchuan;

import android.view.View;

import rouchuan.customlayoutmanager.ViewPagerLayoutManager;

/**
 * Created by Dajavu on 12/7/16.
 */

public class RotateLayoutManager extends ViewPagerLayoutManager {
    private static float INTERVAL_ANGLE = 360f;

    private int itemSpace = 0;

    private float angle;

    public RotateLayoutManager(int itemSpace) {
        this(itemSpace, INTERVAL_ANGLE, false);
    }

    public RotateLayoutManager(int itemSpace, float angle) {
        this(itemSpace, angle, false);
    }

    public RotateLayoutManager(int itemSpace, float angle, boolean shouldReverseLayout) {
        super(shouldReverseLayout);
        this.angle = angle;
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
        itemView.setRotation(calRotation(targetOffset));
    }

    private float calRotation(float targetOffset) {
        return -angle / interval * targetOffset;
    }
}
