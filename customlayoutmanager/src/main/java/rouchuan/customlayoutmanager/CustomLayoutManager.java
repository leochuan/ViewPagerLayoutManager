package rouchuan.customlayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.os.Parcelable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zixintechno on 12/7/16.
 */

public abstract class CustomLayoutManager extends RecyclerView.LayoutManager {

    private static int MAX_DISPLAY_ITEM_COUNT = 50;

    protected Context context;

    // Size of each items
    protected int mDecoratedChildWidth;
    protected int mDecoratedChildHeight;

    //Properties
    protected int startLeft;
    protected int startTop;
    protected float offset; //The delta of property which will change when scroll

    private boolean isClockWise;

    protected float interval; //the interval of each item's offset

    private int targetPosition = -1;

    private int scrollToPosition;

    /**
     * Works the same way as {@link android.widget.AbsListView#setSmoothScrollbarEnabled(boolean)}.
     * see {@link android.widget.AbsListView#setSmoothScrollbarEnabled(boolean)}
     */
    private boolean mSmoothScrollbarEnabled = true;

    protected abstract float setInterval();

    /**
     * You can set up your own properties here or change the exist properties like startLeft and startTop
     */
    protected abstract void setUp();

    protected abstract void setItemViewProperty(View itemView, float targetOffset);


    public CustomLayoutManager(Context context) {
        this(context, true);
    }

    public CustomLayoutManager(Context context, boolean shouldReverseLayout) {
        this.context = context;
        this.isClockWise = shouldReverseLayout;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            offset = 0;
            return;
        }

        if (getChildCount() == 0) {
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);
            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
            startLeft = (getHorizontalSpace() - mDecoratedChildWidth) / 2;
            startTop = (getVerticalSpace() - mDecoratedChildHeight) / 2;
            interval = setInterval();
            setUp();
            detachAndScrapView(scrap, recycler);
        }

        offset = isClockWise ? scrollToPosition * interval : -scrollToPosition * interval;
        detachAndScrapAttachedViews(recycler);
        handleOutOfRange();
        layoutItems(recycler, state);
    }

    private float getProperty(int position) {
        return isClockWise ? position * interval : position * -interval;
    }

    @Override
    public View findViewByPosition(int position) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        final int firstChild = getPosition(getChildAt(0));
        final int viewPosition = position - firstChild;
        if (viewPosition >= 0 && viewPosition < childCount) {
            final View child = getChildAt(viewPosition);
            if (getPosition(child) == position) {
                return child; // in pre-layout, this may not match
            }
        }
        // fallback to traversal. This might be necessary in pre-layout.
        return super.findViewByPosition(position);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
        offset = 0;
        scrollToPosition = 0;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPosition = position;
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return CustomLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }
        };
        targetPosition = position;
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final float direction = targetPosition < firstChildPos == isClockWise ? -1 / getDistanceRatio() : 1 / getDistanceRatio();
        return new PointF(direction, 0);
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }

        if (!mSmoothScrollbarEnabled) {
            return isClockWise ? getCurrentPosition() : getItemCount() - getCurrentPosition() - 1;
        }

        return isClockWise ? (int) offset : (int) (getMaxOffset() - offset);
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }

        if (!mSmoothScrollbarEnabled) {
            return 1;
        }

        return (int) (getMaxOffset() / getItemCount());
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }

        if (!mSmoothScrollbarEnabled) {
            return getItemCount();
        }

        return (int) getMaxOffset();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int willScroll = dx;

        float realDx = dx / getDistanceRatio();
        float targetOffset = offset + realDx;

        //handle the boundary
        if (targetOffset < getMinOffset()) {
            willScroll = isClockWise ? (int) (offset * getDistanceRatio()) : 0;
        } else if (targetOffset > getMaxOffset()) {
            willScroll = (int) ((getMaxOffset() - offset) * getDistanceRatio());
        }
        realDx = willScroll / getDistanceRatio();

        offset += realDx;

        //re-calculate the rotate x,y of each items
        for (int i = 0; i < getChildCount(); i++) {
            View scrap = getChildAt(i);
            float delta = propertyChangeWhenScroll(scrap) - realDx;
            layoutScrap(scrap, delta);
        }

        layoutItems(recycler, state);

        return willScroll;
    }

    private void layoutItems(RecyclerView.Recycler recycler,
                             RecyclerView.State state) {
        if (state.isPreLayout()) return;

        //remove the views which out of range
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            if (removeCondition(getProperty(position) - offset)) {
                removeAndRecycleView(view, recycler);
            }
        }

        //add the views which do not attached and in the range
        int begin = getCurrentPosition() - MAX_DISPLAY_ITEM_COUNT / 2;
        int end = getCurrentPosition() + MAX_DISPLAY_ITEM_COUNT / 2;
        if (begin < 0) begin = 0;
        if (end > getItemCount()) end = getItemCount();

        for (int i = begin; i < end; i++) {
            if (!removeCondition(getProperty(i) - offset)) {
                if (findViewByPosition(i) == null) {
                    View scrap = recycler.getViewForPosition(i);
                    measureChildWithMargins(scrap, 0, 0);
                    addView(scrap);
                    resetViewProperty(scrap);
                    float targetOffset = getProperty(i) - offset;
                    layoutScrap(scrap, targetOffset);
                }
            }
        }
    }

    private boolean removeCondition(float targetOffset) {
        return targetOffset > maxRemoveOffset() || targetOffset < minRemoveOffset();
    }

    private void handleOutOfRange() {
        if (offset < getMinOffset()) {
            offset = getMinOffset();
        }
        if (offset > getMaxOffset()) {
            offset = getMaxOffset();
        }
    }

    private void resetViewProperty(View v) {
        v.setRotation(0);
        v.setRotationY(0);
        v.setRotationX(0);
        v.setAlpha(1f);
    }

    private float getMaxOffset() {
        return isClockWise ? (getItemCount() - 1) * interval : 0;
    }

    private float getMinOffset() {
        return isClockWise ? 0 : -(getItemCount() - 1) * interval;
    }

    private void layoutScrap(View scrap, float targetOffset) {
        int left = calItemLeftPosition(targetOffset);
        int top = calItemTopPosition(targetOffset);
        layoutDecorated(scrap, startLeft + left, startTop + top,
                startLeft + left + mDecoratedChildWidth, startTop + top + mDecoratedChildHeight);
        setItemViewProperty(scrap, targetOffset);
    }

    protected int calItemLeftPosition(float targetOffset) {
        return (int) targetOffset;
    }

    protected int calItemTopPosition(float targetOffset) {
        return 0;
    }

    protected int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    protected int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    protected float maxRemoveOffset() {
        return getHorizontalSpace() - startLeft;
    }

    protected float minRemoveOffset() {
        return -mDecoratedChildWidth - getPaddingLeft() - startLeft;
    }

    protected float propertyChangeWhenScroll(View itemView) {
        return itemView.getLeft() - startLeft;
    }

    protected float getDistanceRatio() {
        return 1f;
    }

    public int getCurrentPosition() {
        if (targetPosition != -1) return targetPosition;
        return Math.round(Math.abs(offset) / interval);
    }

    public void resetTargetPosition() {
        targetPosition = -1;
    }

    public int getOffsetCenterView() {
        return (int) ((getCurrentPosition() * (isClockWise ? interval : -interval) - offset) * getDistanceRatio());
    }

    /**
     * When smooth scrollbar is enabled, the position and size of the scrollbar thumb is computed
     * based on the number of visible pixels in the visible items. This however assumes that all
     * list items have similar or equal widths or heights (depending on list orientation).
     * If you use a list in which items have different dimensions, the scrollbar will change
     * appearance as the user scrolls through the list. To avoid this issue,  you need to disable
     * this property.
     * <p>
     * When smooth scrollbar is disabled, the position and size of the scrollbar thumb is based
     * solely on the number of items in the adapter and the position of the visible items inside
     * the adapter. This provides a stable scrollbar as the user navigates through a list of items
     * with varying widths / heights.
     *
     * @param enabled Whether or not to enable smooth scrollbar.
     * @see #setSmoothScrollbarEnabled(boolean)
     */
    public void setSmoothScrollbarEnabled(boolean enabled) {
        mSmoothScrollbarEnabled = enabled;
    }

    /**
     * Returns the current state of the smooth scrollbar feature. It is enabled by default.
     *
     * @return True if smooth scrollbar is enabled, false otherwise.
     * @see #setSmoothScrollbarEnabled(boolean)
     */
    public boolean isSmoothScrollbarEnabled() {
        return mSmoothScrollbarEnabled;
    }
}
