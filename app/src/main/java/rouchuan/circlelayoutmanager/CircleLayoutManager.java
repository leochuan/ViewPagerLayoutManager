package rouchuan.circlelayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by Dajavu on 16/8/8.
 */
public class CircleLayoutManager extends RecyclerView.LayoutManager{
    private Context context;

    private static int OFFSET_DEGREE = 30; //偏移的角度
    private static float DISTANCE_RATIO = 10f;
    private static int SCROLL_LEFT = 1;
    private static int SCROLL_RIGHT = 2;

    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;
    private int startLeft;
    private int startTop;
    private int mRadius;
    private float offsetRotate = 0;
    private SparseBooleanArray itemAttached = new SparseBooleanArray();
    private SparseArray<Float> itemsRotate = new SparseArray<>();

    public CircleLayoutManager(Context context) {
        this.context = context;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            offsetRotate = 0;
            return;
        }

        //计算Child的长宽
        if (getChildCount() == 0) {
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);
            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
            startLeft = (getHorizontalSpace() - mDecoratedChildWidth)/2;
            //startTop = (getVerticalSpace() - mDecoratedChildHeight*3/4);
            startTop = 0;
            mRadius = mDecoratedChildHeight + 200;
            detachAndScrapView(scrap, recycler);
        }

        float rotate = 0;

        for (int i = 0; i < getItemCount(); i++) {
            itemsRotate.put(i,rotate);
            itemAttached.put(i,false);
            rotate+=OFFSET_DEGREE;
        }

        detachAndScrapAttachedViews(recycler);

        fixRotateOffset();

        layoutItems(recycler,state);
    }

    private int calLeftPosition(float rotate){
        return (int) (mRadius * Math.cos(Math.toRadians(90 - rotate)));
    }

    private int calTopPosition(float rotate){
        return (int) (mRadius - mRadius * Math.sin(Math.toRadians(90 - rotate)));
    }

    private float getMaxOffsetDegree(){
        return (getItemCount()-1)*OFFSET_DEGREE;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int willScroll = dx;

        float theta = dx/DISTANCE_RATIO;

        float targetRotate = offsetRotate + theta;

        if (targetRotate < 0) {
            willScroll = (int) (-offsetRotate*DISTANCE_RATIO);
        }
        else if (targetRotate > getMaxOffsetDegree()) {
            willScroll = (int) ((getMaxOffsetDegree() - offsetRotate)*DISTANCE_RATIO);
        }

        theta = willScroll/DISTANCE_RATIO;

        offsetRotate+=theta;

        for(int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            float newRotate = view.getRotation() - theta;
            int offsetX = calLeftPosition(newRotate);
            int offsetY = calTopPosition(newRotate);
            layoutDecorated(view, startLeft + offsetX, startTop + offsetY,
                    startLeft + offsetX + mDecoratedChildWidth, startTop + offsetY + mDecoratedChildHeight);
            view.setRotation(newRotate);
        }

        if (dx < 0)
            layoutItems(recycler, state,SCROLL_LEFT);
        else
            layoutItems(recycler,state,SCROLL_RIGHT);
        return willScroll;
    }

    private void layoutItems(RecyclerView.Recycler recycler,RecyclerView.State state){
        layoutItems(recycler,state,SCROLL_RIGHT);
    }

    private void layoutItems(RecyclerView.Recycler recycler,
                             RecyclerView.State state,int oritention){
        if(state.isPreLayout()) return;

        for(int i = 0;i<getChildCount();i++){
            View view =  getChildAt(i);
            int position = getPosition(view);
            if(itemsRotate.get(position) - offsetRotate>90
                    || itemsRotate.get(position) - offsetRotate< -90){
                itemAttached.put(position,false);
                removeAndRecycleView(view,recycler);
            }
        }

        for(int i=0;i<getItemCount();i++){
            if(itemsRotate.get(i) - offsetRotate< 90
                    && itemsRotate.get(i) - offsetRotate> -90){
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
    public void scrollToPosition(int position) {
        if(position < 0 || position > getItemCount()-1) return;
        offsetRotate = position * OFFSET_DEGREE;
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

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;
        return new PointF(direction, 0);
    }

    public int getCurrentPosition(){
        return Math.round(offsetRotate / OFFSET_DEGREE);
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

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private void fixRotateOffset() {
        if(offsetRotate < 0){
            offsetRotate = 0;
        }

        if(offsetRotate > getMaxOffsetDegree()){
            offsetRotate = getMaxOffsetDegree();
        }
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public int getmRadius() {
        return mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
    }
}

