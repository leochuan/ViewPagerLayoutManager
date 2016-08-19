package rouchuan.circlelayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dajavu on 16/4/22.
 */
public class CircleLayoutManager extends RecyclerView.LayoutManager{
    
    private static int INTERVAL_ANGLE = 30;// The default interval angle between each items
    private static float DISTANCE_RATIO = 10f; // Finger swipe distance divide item rotate angle

    //Flags of scroll dirction
    private static int SCROLL_LEFT = 1;
    private static int SCROLL_RIGHT = 2;

    private Context context;

    // Size of each items
    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;

    //Property
    private int startLeft;
    private int startTop;
    private int mRadius;
    private int intervalAngle;
    private float offsetRotate; // The offset angle for each items which will change according to the scroll offset

    //the range of remove from parent
    private int minRemoveDegree;
    private int maxRemoveDegree;

    //initial position of content
    private int contentOffsetX = -1;
    private int contentOffsetY = -1;

    private int firstChildRotate = 0;

    //Sparse array for recording the attachment and rotate angle of each items
    private SparseBooleanArray itemAttached = new SparseBooleanArray();
    private SparseArray<Float> itemsRotate = new SparseArray<>();


    public CircleLayoutManager(Context context) {
        this.context = context;
        intervalAngle = INTERVAL_ANGLE;
        offsetRotate = 0;
        minRemoveDegree = -90;
        maxRemoveDegree = 90;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            offsetRotate = 0;
            return;
        }

        //calculate the size of child
        if (getChildCount() == 0) {
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);
            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
            startLeft = contentOffsetX == -1?(getHorizontalSpace() - mDecoratedChildWidth)/2: contentOffsetX;
            startTop = contentOffsetY ==-1?0: contentOffsetY;
            mRadius = mDecoratedChildHeight;
            detachAndScrapView(scrap, recycler);
        }

        //record the state of each items
        float rotate = firstChildRotate;
        for (int i = 0; i < getItemCount(); i++) {
            itemsRotate.put(i,rotate);
            itemAttached.put(i,false);
            rotate+= intervalAngle;
        }

        detachAndScrapAttachedViews(recycler);
        fixRotateOffset();
        layoutItems(recycler,state);
    }

    private void layoutItems(RecyclerView.Recycler recycler,RecyclerView.State state){
        layoutItems(recycler,state,SCROLL_RIGHT);
    }

    private void layoutItems(RecyclerView.Recycler recycler,
                             RecyclerView.State state,int oritention){
        if(state.isPreLayout()) return;

        //remove the views which out of range
        for(int i = 0;i<getChildCount();i++){
            View view =  getChildAt(i);
            int position = getPosition(view);
            if(itemsRotate.get(position) - offsetRotate>maxRemoveDegree
                    || itemsRotate.get(position) - offsetRotate< minRemoveDegree){
                itemAttached.put(position,false);
                removeAndRecycleView(view,recycler);
            }
        }

        //add the views which do not attached and in the range
        for(int i=0;i<getItemCount();i++){
            if(itemsRotate.get(i) - offsetRotate<= maxRemoveDegree
                    && itemsRotate.get(i) - offsetRotate>= minRemoveDegree){
                if(!itemAttached.get(i)){
                    View scrap = recycler.getViewForPosition(i);
                    measureChildWithMargins(scrap, 0, 0);
                    if(oritention == SCROLL_LEFT)
                        addView(scrap,0);
                    else
                        addView(scrap);
                    float rotate = itemsRotate.get(i) - offsetRotate;
                    int left = calLeftPosition(rotate);
                    int top = calTopPosition(rotate);
                    scrap.setRotation(rotate);
                    layoutDecorated(scrap, startLeft + left, startTop + top,
                            startLeft + left + mDecoratedChildWidth, startTop + top + mDecoratedChildHeight);
                    itemAttached.put(i,true);
                }
            }
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int willScroll = dx;

        float theta = dx/DISTANCE_RATIO; // the angle every item will rotate for each dx
        float targetRotate = offsetRotate + theta;

        //handle the boundary
        if (targetRotate < 0) {
            willScroll = (int) (-offsetRotate*DISTANCE_RATIO);
        }
        else if (targetRotate > getMaxOffsetDegree()) {
            willScroll = (int) ((getMaxOffsetDegree() - offsetRotate)*DISTANCE_RATIO);
        }
        theta = willScroll/DISTANCE_RATIO;

        offsetRotate+=theta; //increase the offset rotate so when re-layout it can recycle the right views

        //re-calculate the rotate x,y of each items
        for(int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            float newRotate = view.getRotation() - theta;
            int offsetX = calLeftPosition(newRotate);
            int offsetY = calTopPosition(newRotate);
            layoutDecorated(view, startLeft + offsetX, startTop + offsetY,
                    startLeft + offsetX + mDecoratedChildWidth, startTop + offsetY + mDecoratedChildHeight);
            view.setRotation(newRotate);
        }

        //different direction child will overlap different way
        if (dx < 0)
            layoutItems(recycler, state,SCROLL_LEFT);
        else
            layoutItems(recycler,state,SCROLL_RIGHT);
        return willScroll;
    }

    /**
     *
     * @param rotate the current rotate of view
     * @return the x of view
     */
    private int calLeftPosition(float rotate){
        return (int) (mRadius * Math.cos(Math.toRadians(90 - rotate)));
    }

    /**
     *
     * @param rotate the current rotate of view
     * @return the y of view
     */
    private int calTopPosition(float rotate){
        return (int) (mRadius - mRadius * Math.sin(Math.toRadians(90 - rotate)));
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     *  fix the offset rotate angle in case item out of boundary
     **/
    private void fixRotateOffset() {
        if(offsetRotate < 0){
            offsetRotate = 0;
        }
        if(offsetRotate > getMaxOffsetDegree()){
            offsetRotate = getMaxOffsetDegree();
        }
    }

    /**
     *
     * @return the max degrees according to current number of views and interval angle
     */
    private float getMaxOffsetDegree(){
       return (getItemCount()-1)* intervalAngle;
    }

    private PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;
        return new PointF(direction, 0);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void scrollToPosition(int position) {
        if(position < 0 || position > getItemCount()-1) return;
        float targetRotate = position * intervalAngle;
        if(targetRotate == offsetRotate) return;
        offsetRotate = targetRotate;
        fixRotateOffset();
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return CircleLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }
        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
        offsetRotate = 0;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     *
     * @return Get the current positon of views
     */
    public int getCurrentPosition(){
        return Math.round(offsetRotate / intervalAngle);
    }

    /**
     *
     * @return Get the dx should be scrolled to the center
     */
    public int getOffsetCenterView(){
        return (int) ((getCurrentPosition()*intervalAngle-offsetRotate)*DISTANCE_RATIO);
    }

    /**
     *
     * @return Get the radius of the circle
     */
    public int getRadius() {
        return mRadius;
    }

    /**
     *
     * @param mRadius the radius of the circle,default will be item's height
     */
    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    /**
     *
     * @return the interval angle between each items
     */
    public int getIntervalAngle() {
        return intervalAngle;
    }

    /**
     * Default angle is 30
     * @param intervalAngle the interval angle between each items
     */
    public void setIntervalAngle(int intervalAngle) {
        this.intervalAngle = intervalAngle;
    }

    /**
     * Default is center in parent
     * @return the content offset of x
     */
    public int getContentOffsetX() {
        return contentOffsetX;
    }

    /**
     * Default is center in parent
     * @param contentOffsetX the content offset of x
     */
    public void setContentOffsetX(int contentOffsetX) {
        this.contentOffsetX = contentOffsetX;
    }

    /**
     * Default is top in parent
     * @return the content offset of y
     */
    public int getContentOffsetY() {
        return contentOffsetY;
    }

    /**
     * Default is top in parent
     * @param contentOffsetY the content offset of y
     */
    public void setContentOffsetY(int contentOffsetY) {
        this.contentOffsetY = contentOffsetY;
    }

    /**
     * Default is 0
     * @return the rotate of first child
     */
    public int getFirstChildRotate() {
        return firstChildRotate;
    }

    /**
     * Default is 0
     * @param firstChildRotate the rotate of first child
     */
    public void setFirstChildRotate(int firstChildRotate) {
        this.firstChildRotate = firstChildRotate;
    }

    /**
     * The rotate of child view in range[min,max] will be shown,default will be [-90,90]
     * @param min min rotate that will be show
     * @param max max rotate that will be show
     */
    public void setDegreeRangeWillShow(int min,int max){
        if(min > max) return;
        minRemoveDegree = min;
        maxRemoveDegree = max;
    }
}
