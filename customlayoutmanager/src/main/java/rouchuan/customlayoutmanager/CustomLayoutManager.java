package rouchuan.customlayoutmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zixintechno on 12/7/16.
 */

public abstract class CustomLayoutManager extends RecyclerView.LayoutManager{

    //Flags of scroll dirction
    private static int SCROLL_LEFT = 1;
    private static int SCROLL_RIGHT = 2;

    private static int MAX_DISPLAY_ITEM_COUNT = 100;

    protected Context context;

    // Size of each items
    protected int mDecoratedChildWidth;
    protected int mDecoratedChildHeight;

    //Properties
    protected int startLeft;
    protected int startTop;
    protected float offset; //The delta of property which will change when scroll

    private SparseBooleanArray itemAttached = new SparseBooleanArray();
    private SparseArray<Float> itemsOffset = new SparseArray<>();

    protected boolean isClockWise;

    protected float interval; //the interval of each item's offset

    protected abstract float setInterval();

    /**
     * You can set up your own properties here or change the exist properties like startLeft and startTop
     */
    protected abstract void setUp();

    /**
     *
     * @return the max offset value of which the view should be removed
     */
    protected abstract float maxRemoveOffset();

    /**
     *
     * @return the min offset value of which the view should be removed
     */
    protected abstract float minRemoveOffset();

    protected abstract int calItemLeftPosition(float targetOffset);

    protected abstract int calItemTopPosition(float targetOffset);

    protected abstract void setItemViewProperty(View itemView,float targetOffset);

    protected abstract float propertyChangeWhenScroll(View itemView);

    public CustomLayoutManager(Context context){
        this(context,true);
    }

    public CustomLayoutManager(Context context, boolean isClockWise) {
        this.context = context;
        this.isClockWise = isClockWise;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getItemCount() == 0){
            detachAndScrapAttachedViews(recycler);
            offset = 0;
            return;
        }

        if(getChildCount() == 0){
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);
            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
            startLeft = (getHorizontalSpace() - mDecoratedChildWidth) / 2;
            startTop = (getVerticalSpace()-mDecoratedChildHeight) / 2;
            interval = setInterval();
            setUp();
            detachAndScrapView(scrap, recycler);
        }

        //init the state of each items
        float property = 0;
        for(int i = 0;i<getItemCount();i++){
            itemAttached.put(i,false);
            itemsOffset.put(i,property);
            property=isClockWise?property+interval:property-interval;
        }

        detachAndScrapAttachedViews(recycler);
        handleOutOfRange();
        layoutItems(recycler,state);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
        offset = 0;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void scrollToPosition(int position) {
        if(position < 0 || position > getItemCount()-1) return;
        float targetRotate = isClockWise?position * interval:-position * interval;
        if(targetRotate == offset) return;
        offset = targetRotate;
        handleOutOfRange();
        requestLayout();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int willScroll = dx;

        float realDx = dx / getDistanceRatio();
        float targetOffset = offset + realDx;

        //handle the boundary
        if (targetOffset < getMinOffset()) {
            willScroll = (int) (-offset*getDistanceRatio());
        }
        else if (targetOffset > getMaxOffset()) {
            willScroll = (int) ((getMaxOffset() - offset)*getDistanceRatio());
        }
        realDx = willScroll/getDistanceRatio();

        offset+=realDx;

        //re-calculate the rotate x,y of each items
        for(int i=0;i<getChildCount();i++){
            View scrap = getChildAt(i);
            float delta = propertyChangeWhenScroll(scrap) - realDx;
            layoutScrap(scrap,delta);
        }

        //different direction child will overlap different way
        if (dx < 0)
            layoutItems(recycler, state,SCROLL_LEFT);
        else
            layoutItems(recycler,state,SCROLL_RIGHT);
        return willScroll;
    }

    private void layoutItems(RecyclerView.Recycler recycler,RecyclerView.State state){
        int layoutDire = isClockWise?SCROLL_RIGHT:SCROLL_LEFT;
        layoutItems(recycler,state,layoutDire);
    }

    private void layoutItems(RecyclerView.Recycler recycler,
                             RecyclerView.State state,int oritention){
        if(state.isPreLayout()) return;

        //remove the views which out of range
        for(int i = 0;i<getChildCount();i++){
            View view =  getChildAt(i);
            int position = getPosition(view);
            if(removeCondition(itemsOffset.get(position)-offset)){
                itemAttached.put(position,false);
                removeAndRecycleView(view,recycler);
            }
        }

        //add the views which do not attached and in the range
        int begin = getCurrentPosition() - MAX_DISPLAY_ITEM_COUNT / 2;
        int end = getCurrentPosition() + MAX_DISPLAY_ITEM_COUNT / 2;
        if(begin<0) begin = 0;
        if(end > getItemCount()) end = getItemCount();
        for(int i=begin;i<end;i++){
            if(!removeCondition(itemsOffset.get(i)-offset)){
                if(!itemAttached.get(i)){
                    View scrap = recycler.getViewForPosition(i);
                    measureChildWithMargins(scrap, 0, 0);
                    if(oritention == SCROLL_LEFT)
                        addView(scrap,0);
                    else
                        addView(scrap);
                    resetViewProperty(scrap);
                    float targetOffset = itemsOffset.get(i) - offset;
                    layoutScrap(scrap,targetOffset);
                    itemAttached.put(i,true);
                }
            }
        }
    }

    private boolean removeCondition(float targetOffset){
        return targetOffset > maxRemoveOffset() || targetOffset < minRemoveOffset();
    }

    private void handleOutOfRange(){
        if(offset < getMinOffset()){
            offset = getMinOffset();
        }
        if(offset > getMaxOffset()){
            offset = getMaxOffset();
        }
    }

    private void resetViewProperty(View v) {
        v.setRotation(0);
        v.setRotationY(0);
        v.setRotationY(0);
        v.setAlpha(1f);
    }

    private float getMaxOffset(){
        return isClockWise?(getItemCount()-1)* interval:0;
    }

    private float getMinOffset() {
        return isClockWise?0:-(getItemCount()-1)* interval;
    }

    private void layoutScrap(View scrap,float targetOffset){
        int left = calItemLeftPosition(targetOffset);
        int top = calItemTopPosition(targetOffset);
        layoutDecorated(scrap, startLeft + left, startTop + top,
                startLeft + left + mDecoratedChildWidth, startTop + top + mDecoratedChildHeight);
        setItemViewProperty(scrap,targetOffset);
    }

    protected int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    protected int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    protected float getDistanceRatio(){
        return 1f;
    }

    public int getCurrentPosition(){
        return Math.round(Math.abs(offset)/interval);
    }

    public int getOffsetCenterView(){
        return (int) ((getCurrentPosition()*(isClockWise?interval:-interval)-offset)*getDistanceRatio());
    }
}
