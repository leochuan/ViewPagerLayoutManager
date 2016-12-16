package rouchuan.circlelayoutmanager;

import android.content.Context;
import android.view.View;

import rouchuan.customlayoutmanager.CustomLayoutManager;

/**
 * Created by zixintechno on 12/7/16.
 */

public class GalleryLayoutManager extends CustomLayoutManager {
    private static float INTERVAL_ANGLE = 30f;
    private static float MIN_ALPHA = 0.5f;

    private int itemSpace = 0;

    public GalleryLayoutManager(Context context, int itemSpace) {
        super(context);
        this.itemSpace = itemSpace;
    }

    public GalleryLayoutManager(Context context, int itemSpace, boolean isClockWise) {
        super(context,isClockWise);
        this.itemSpace = itemSpace;
    }

    @Override
    protected float setInterval() {
        return mDecoratedChildWidth+itemSpace;
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected float maxRemoveOffset() {
        return getHorizontalSpace() - startLeft;
    }

    @Override
    protected float minRemoveOffset() {
        return -mDecoratedChildWidth-getPaddingLeft() - startLeft;
    }

    @Override
    protected int calItemLeftPosition(float targetOffset) {
        return (int) targetOffset;
    }

    @Override
    protected int calItemTopPosition(float targetOffset) {
        return 0;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        itemView.setRotationY(calRotationY(targetOffset));
        itemView.setAlpha(calAlpha(targetOffset));
    }

    @Override
    protected float propertyChangeWhenScroll(View itemView) {
        return itemView.getLeft()-startLeft;
    }

    private float calRotationY(float targetOffset){
        return -INTERVAL_ANGLE / interval * targetOffset;
    }

    private float calAlpha(float targetOffset){
        float alpha = (MIN_ALPHA - 1f)/interval * Math.abs(targetOffset) + 1f;
        if(alpha < MIN_ALPHA) alpha = MIN_ALPHA;
        return alpha;
    }
}
