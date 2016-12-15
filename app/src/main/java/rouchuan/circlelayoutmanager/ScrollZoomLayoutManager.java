package rouchuan.circlelayoutmanager;

import android.content.Context;
import android.view.View;

import rouchuan.customlayoutmanager.CustomLayoutManager;

/**
 * Created by zixintechno on 12/7/16.
 */

public class ScrollZoomLayoutManager extends CustomLayoutManager {

    private static final float SCALE_RATE = 1.2f;
    private int itemSpace = 0;

    public ScrollZoomLayoutManager(Context context, int itemSpace) {
        super(context);
        this.itemSpace = itemSpace;
    }

    public ScrollZoomLayoutManager(Context context, int itemSpace, boolean isClockWise) {
        super(context,isClockWise);
        this.itemSpace = itemSpace;
    }

    @Override
    protected float setInterval() {
        return (int) (mDecoratedChildWidth*((SCALE_RATE-1f)/2f+1)+itemSpace);
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
        float scale = calculateScale((int) targetOffset + startLeft);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
    }

    @Override
    protected float propertyChangeWhenScroll(View itemView) {
        return itemView.getLeft()-startLeft;
    }

    /**
     *
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll offset
     */
    private float calculateScale(int x){
        int deltaX = Math.abs(x-(getHorizontalSpace() - mDecoratedChildWidth) / 2);
        float diff = 0f;
        if((mDecoratedChildWidth-deltaX)>0) diff = mDecoratedChildWidth-deltaX;
        return (SCALE_RATE-1f)/mDecoratedChildWidth * diff + 1;
    }
}
