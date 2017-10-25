package com.rouchuan;

import android.os.Build;
import android.view.View;

import rouchuan.customlayoutmanager.ViewPagerLayoutManager;

/**
 * Created by Dajavu on 12/7/16.
 * <p>
 * this layoutManager Require api21 to support elevation
 */

public class ElevateScaleLayoutManager extends ViewPagerLayoutManager {

    private static final float MIN_SCALE = 0.5f;

    private int itemSpace = 0;
    private float minScale;

    public ElevateScaleLayoutManager(int itemSpace) {
        this(itemSpace, MIN_SCALE, HORIZONTAL, false);
    }

    public ElevateScaleLayoutManager(int itemSpace, float minScale, int orientation, boolean reverseLayout) {
        super(orientation, reverseLayout);
        this.itemSpace = itemSpace;
        this.minScale = minScale;
        setEnableElevation(true);
    }

    @Override
    protected float setInterval() {
        return (mDecoratedMeasurement + itemSpace);
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale((int) targetOffset + mSpaceMain);
        float elevation = calculateElevation((int) targetOffset + mSpaceMain);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(elevation);
        }
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        return (float) calculateElevation((int) targetOffset + mSpaceMain);
    }

    /**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     */
    private float calculateScale(int x) {
        float deltaX = Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        return -minScale * deltaX / (mOrientationHelper.getTotalSpace() / 2f) + 1f;
    }

    private int calculateElevation(int x) {
        int deltaX = (int) Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        return Integer.MAX_VALUE - deltaX;
    }
}
