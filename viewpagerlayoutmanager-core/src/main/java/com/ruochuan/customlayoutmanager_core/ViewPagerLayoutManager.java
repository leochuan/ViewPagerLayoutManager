package com.ruochuan.customlayoutmanager_core;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Dajavu on 18/5/17.
 */

public abstract class ViewPagerLayoutManager extends RecyclerView.LayoutManager {
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;

    OrientationHelper mOrientationHelper;
    LayoutState mLayoutState;
    private boolean mReverseLayout = false;
    boolean mShouldReverseLayout = false;

    private boolean mRecycleChildrenOnDetach;
    private int mPendingScrollPosition = NO_POSITION;

    int mPendingScrollPositionOffset = INVALID_OFFSET;

    SavedState mPendingSavedState = null;

    /**
     * Works the same way as {@link android.widget.AbsListView#setStackFromBottom(boolean)} and
     * it supports both orientations.
     * see {@link android.widget.AbsListView#setStackFromBottom(boolean)}
     */
    private boolean mStackFromEnd = false;

    /**
     * Re-used variable to keep anchor information on re-layout.
     * Anchor position and coordinate defines the reference point for LLM while doing a layout.
     */
    final AnchorInfo mAnchorInfo = new AnchorInfo();

    public ViewPagerLayoutManager() {
        this(false);
    }

    public ViewPagerLayoutManager(boolean reverseLayout) {
        setReverseLayout(reverseLayout);
        setAutoMeasureEnabled(true);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Returns whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     *
     * @return true if LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     */
    public boolean getRecycleChildrenOnDetach() {
        return mRecycleChildrenOnDetach;
    }

    /**
     * Set whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     * <p>
     * If you are using a {@link RecyclerView.RecycledViewPool}, it might be a good idea to set
     * this flag to <code>true</code> so that views will be avilable to other RecyclerViews
     * immediately.
     * <p>
     * Note that, setting this flag will result in a performance drop if RecyclerView
     * is restored.
     *
     * @param recycleChildrenOnDetach Whether children should be recycled in detach or not.
     */
    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        mRecycleChildrenOnDetach = recycleChildrenOnDetach;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * Compatibility support for {@link android.widget.AbsListView#setStackFromBottom(boolean)}
     */
    public void setStackFromEnd(boolean stackFromEnd) {
        assertNotInLayoutOrScroll(null);
        if (mStackFromEnd == stackFromEnd) {
            return;
        }
        mStackFromEnd = stackFromEnd;
        requestLayout();
    }

    public boolean getStackFromEnd() {
        return mStackFromEnd;
    }

    protected boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    /**
     * Calculates the view layout order. (e.g. from end to start or start to end)
     * RTL layout support is applied automatically. So if layout is RTL and
     * {@link #getReverseLayout()} is {@code true}, elements will be laid out starting from left.
     */
    private void resolveShouldLayoutReverse() {
        // A == B is the same result, but we rather keep it readable
        if (!isLayoutRTL()) {
            mShouldReverseLayout = mReverseLayout;
        } else {
            mShouldReverseLayout = !mReverseLayout;
        }
    }

    /**
     * Returns if views are laid out from the opposite direction of the layout.
     *
     * @return If layout is reversed or not.
     * @see #setReverseLayout(boolean)
     */
    public boolean getReverseLayout() {
        return mReverseLayout;
    }

    /**
     * Used to reverse item traversal and layout order.
     * This behaves similar to the layout change for RTL views. When set to true, first item is
     * laid out at the end of the UI, second item is laid out before it etc.
     * <p>
     * When set to true, If {@link android.support.v7.widget.RecyclerView} is LTR, than it will
     * layout from RTL, if {@link android.support.v7.widget.RecyclerView}} is RTL, it will layout
     * from LTR.
     * <p>
     * If you are looking for the exact same behavior of
     * {@link android.widget.AbsListView#setStackFromBottom(boolean)}, use
     * {@link #setStackFromEnd(boolean)}
     */
    public void setReverseLayout(boolean reverseLayout) {
        assertNotInLayoutOrScroll(null);
        if (reverseLayout == mReverseLayout) {
            return;
        }
        mReverseLayout = reverseLayout;
        requestLayout();
    }

    void ensureLayoutState() {
        if (mLayoutState == null) {
            mLayoutState = new LayoutState();
        }
        if (mOrientationHelper == null) {
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, LinearLayout.HORIZONTAL);
        }
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

    /**
     * <p>Returns the amount of extra space that should be laid out by LayoutManager.
     * By default, {@link android.support.v7.widget.LinearLayoutManager} lays out 1 extra page of
     * items while smooth scrolling and 0 otherwise. You can override this method to implement your
     * custom layout pre-cache logic.</p>
     * <p>Laying out invisible elements will eventually come with performance cost. On the other
     * hand, in places like smooth scrolling to an unknown location, this extra content helps
     * LayoutManager to calculate a much smoother scrolling; which improves user experience.</p>
     * <p>You can also use this if you are trying to pre-layout your upcoming views.</p>
     *
     * @return The extra space that should be laid out (in pixels).
     */
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (state.hasTargetScrollPosition()) {
            return mOrientationHelper.getTotalSpace();
        } else {
            return 0;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mPendingSavedState != null || mPendingScrollPosition != NO_POSITION) {
            if (state.getItemCount() == 0) {
                removeAndRecycleAllViews(recycler);
                return;
            }
        }

        if (mPendingSavedState != null && mPendingSavedState.hasValidAnchor()) {
            mPendingScrollPosition = mPendingSavedState.mAnchorPosition;
        }

        ensureLayoutState();
        // resolve layout direction
        resolveShouldLayoutReverse();

        mAnchorInfo.reset();
        mAnchorInfo.mLayoutFromEnd = mShouldReverseLayout ^ mStackFromEnd;
        // calculate anchor position and coordinate
        updateAnchorInfoForLayout(recycler, state, mAnchorInfo);

    }

    private void updateAnchorInfoForLayout(RecyclerView.Recycler recycler, RecyclerView.State state,
                                           AnchorInfo anchorInfo) {
        if (updateAnchorFromPendingData(state, anchorInfo)) {
            return;
        }

        if (updateAnchorFromChildren(recycler, state, anchorInfo)) {
            return;
        }
        anchorInfo.assignCoordinateFromPadding();
        anchorInfo.mPosition = mStackFromEnd ? state.getItemCount() - 1 : 0;
    }

    private boolean updateAnchorFromPendingData(RecyclerView.State state, AnchorInfo anchorInfo) {
        if (state.isPreLayout() || mPendingScrollPosition == NO_POSITION) {
            return false;
        }

        // validate scroll position
        if (mPendingScrollPosition < 0 || mPendingScrollPosition >= state.getItemCount()) {
            mPendingScrollPosition = NO_POSITION;
            mPendingScrollPositionOffset = INVALID_OFFSET;
            return false;
        }

        // if child is visible, try to make it a reference child and ensure it is fully visible.
        // if child is not visible, align it depending on its virtual position.
        anchorInfo.mPosition = mPendingScrollPosition;
        if (mPendingSavedState != null && mPendingSavedState.hasValidAnchor()) {
            // Anchor offset depends on how that child was laid out. Here, we update it
            // according to our current view bounds
            anchorInfo.mLayoutFromEnd = mPendingSavedState.mAnchorLayoutFromEnd;
            if (anchorInfo.mLayoutFromEnd) {
                anchorInfo.mCoordinate = mOrientationHelper.getEndAfterPadding() -
                        mPendingSavedState.mAnchorOffset;
            } else {
                anchorInfo.mCoordinate = mOrientationHelper.getStartAfterPadding() +
                        mPendingSavedState.mAnchorOffset;
            }
            return true;
        }

        if (mPendingScrollPositionOffset == INVALID_OFFSET) {
            View child = findViewByPosition(mPendingScrollPosition);
            if (child != null) {
                final int childSize = mOrientationHelper.getDecoratedMeasurement(child);
                if (childSize > mOrientationHelper.getTotalSpace()) {
                    // item does not fit. fix depending on layout direction
                    anchorInfo.assignCoordinateFromPadding();
                    return true;
                }
                final int startGap = mOrientationHelper.getDecoratedStart(child)
                        - mOrientationHelper.getStartAfterPadding();
                if (startGap < 0) {
                    anchorInfo.mCoordinate = mOrientationHelper.getStartAfterPadding();
                    anchorInfo.mLayoutFromEnd = false;
                    return true;
                }
                final int endGap = mOrientationHelper.getEndAfterPadding() -
                        mOrientationHelper.getDecoratedEnd(child);
                if (endGap < 0) {
                    anchorInfo.mCoordinate = mOrientationHelper.getEndAfterPadding();
                    anchorInfo.mLayoutFromEnd = true;
                    return true;
                }
                anchorInfo.mCoordinate = anchorInfo.mLayoutFromEnd
                        ? (mOrientationHelper.getDecoratedEnd(child) + mOrientationHelper
                        .getTotalSpaceChange())
                        : mOrientationHelper.getDecoratedStart(child);
            } else { // item is not visible.
                if (getChildCount() > 0) {
                    // get position of any child, does not matter
                    int pos = getPosition(getChildAt(0));
                    anchorInfo.mLayoutFromEnd = mPendingScrollPosition < pos
                            == mShouldReverseLayout;
                }
                anchorInfo.assignCoordinateFromPadding();
            }
            return true;
        }

        // override layout from end values for consistency
        anchorInfo.mLayoutFromEnd = mShouldReverseLayout;
        // if this changes, we should update prepareForDrop as well
        if (mShouldReverseLayout) {
            anchorInfo.mCoordinate = mOrientationHelper.getEndAfterPadding() -
                    mPendingScrollPositionOffset;
        } else {
            anchorInfo.mCoordinate = mOrientationHelper.getStartAfterPadding() +
                    mPendingScrollPositionOffset;
        }

        return true;
    }

    /**
     * Finds an anchor child from existing Views. Most of the time, this is the view closest to
     * start or end that has a valid position (e.g. not removed).
     * <p>
     * If a child has focus, it is given priority.
     */
    private boolean updateAnchorFromChildren(RecyclerView.Recycler recycler,
                                             RecyclerView.State state, AnchorInfo anchorInfo) {
//        if (getChildCount() == 0) {
//            return false;
//        }
//        final View focused = getFocusedChild();
//        if (focused != null && anchorInfo.isViewValidAsAnchor(focused, state)) {
//            anchorInfo.assignFromViewAndKeepVisibleRect(focused);
//            return true;
//        }
//        if (mLastStackFromEnd != mStackFromEnd) {
//            return false;
//        }
//        View referenceChild = anchorInfo.mLayoutFromEnd
//                ? findReferenceChildClosestToEnd(recycler, state)
//                : findReferenceChildClosestToStart(recycler, state);
//        if (referenceChild != null) {
//            anchorInfo.assignFromView(referenceChild);
//            // If all visible views are removed in 1 pass, reference child might be out of bounds.
//            // If that is the case, offset it back to 0 so that we use these pre-layout children.
//            if (!state.isPreLayout() && supportsPredictiveItemAnimations()) {
//                // validate this child is at least partially visible. if not, offset it to start
//                final boolean notVisible =
//                        mOrientationHelper.getDecoratedStart(referenceChild) >= mOrientationHelper
//                                .getEndAfterPadding()
//                                || mOrientationHelper.getDecoratedEnd(referenceChild)
//                                < mOrientationHelper.getStartAfterPadding();
//                if (notVisible) {
//                    anchorInfo.mCoordinate = anchorInfo.mLayoutFromEnd
//                            ? mOrientationHelper.getEndAfterPadding()
//                            : mOrientationHelper.getStartAfterPadding();
//                }
//            }
//            return true;
//        }
        return false;
    }

    private class AnchorInfo {
        int mPosition;
        int mCoordinate;
        boolean mLayoutFromEnd;

        void reset() {
            mPosition = NO_POSITION;
            mCoordinate = INVALID_OFFSET;
            mLayoutFromEnd = false;
        }

        /**
         * assigns anchor coordinate from the RecyclerView's padding depending on current
         * layoutFromEnd value
         */
        void assignCoordinateFromPadding() {
            mCoordinate = mLayoutFromEnd
                    ? mOrientationHelper.getEndAfterPadding()
                    : mOrientationHelper.getStartAfterPadding();
        }
    }

    public static class SavedState implements Parcelable {
        int mAnchorPosition;

        int mAnchorOffset;

        boolean mAnchorLayoutFromEnd;

        public SavedState() {

        }

        SavedState(Parcel in) {
            mAnchorPosition = in.readInt();
            mAnchorOffset = in.readInt();
            mAnchorLayoutFromEnd = in.readInt() == 1;
        }

        public SavedState(SavedState other) {
            mAnchorPosition = other.mAnchorPosition;
            mAnchorOffset = other.mAnchorOffset;
            mAnchorLayoutFromEnd = other.mAnchorLayoutFromEnd;
        }

        boolean hasValidAnchor() {
            return mAnchorPosition >= 0;
        }

        void invalidateAnchor() {
            mAnchorPosition = NO_POSITION;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mAnchorPosition);
            dest.writeInt(mAnchorOffset);
            dest.writeInt(mAnchorLayoutFromEnd ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private static class LayoutState {

    }
}
