package com.leochuan;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * Class intended to support snapping for a {@link RecyclerView}
 * which use {@link ViewPagerLayoutManager} as its {@link LayoutManager}.
 * <p>
 * The implementation will snap the center of the target child view to the center of
 * the attached {@link RecyclerView}.
 */
public class CenterSnapHelper extends RecyclerView.OnFlingListener {

    RecyclerView mRecyclerView;
    Scroller mGravityScroller;

    // Handles the snap on scroll case.
    private final RecyclerView.OnScrollListener mScrollListener =
            new RecyclerView.OnScrollListener() {

                boolean mScrolled = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    final ViewPagerLayoutManager layoutManager =
                            (ViewPagerLayoutManager) recyclerView.getLayoutManager();
                    final ViewPagerLayoutManager.OnPageChangeListener onPageChangeListener =
                            layoutManager.onPageChangeListener;
                    if (onPageChangeListener != null) {
                        onPageChangeListener.onPageScrollStateChanged(newState);
                    }

                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                        mScrolled = false;
                        snapToCenterView(layoutManager, onPageChangeListener);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dx != 0 || dy != 0) {
                        mScrolled = true;
                    }
                }
            };

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        ViewPagerLayoutManager layoutManager = (ViewPagerLayoutManager) mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }

        final int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        mGravityScroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);

        if (layoutManager.mOrientation == ViewPagerLayoutManager.VERTICAL
                && Math.abs(velocityY) > minFlingVelocity) {
            final int currentPosition = layoutManager.getCurrentPosition();
            final int offsetPosition = (int) (mGravityScroller.getFinalY() /
                    layoutManager.mInterval / layoutManager.getDistanceRatio());
            mRecyclerView.smoothScrollToPosition(layoutManager.getReverseLayout() ?
                    currentPosition - offsetPosition : currentPosition + offsetPosition);
            return true;
        } else if (layoutManager.mOrientation == ViewPagerLayoutManager.HORIZONTAL
                && Math.abs(velocityX) > minFlingVelocity) {
            final int currentPosition = layoutManager.getCurrentPosition();
            final int offsetPosition = (int) (mGravityScroller.getFinalX() /
                    layoutManager.mInterval / layoutManager.getDistanceRatio());
            mRecyclerView.smoothScrollToPosition(layoutManager.getReverseLayout() ?
                    currentPosition - offsetPosition : currentPosition + offsetPosition);
            return true;
        }

        return true;
    }

    /**
     * Please attach after {{@link LayoutManager} is setting}
     * Attaches the {@link CenterSnapHelper} to the provided RecyclerView, by calling
     * {@link RecyclerView#setOnFlingListener(RecyclerView.OnFlingListener)}.
     * You can call this method with {@code null} to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     *                     {@code null} if you want to remove CenterSnapHelper from the current
     *                     RecyclerView.
     * @throws IllegalArgumentException if there is already a {@link RecyclerView.OnFlingListener}
     *                                  attached to the provided {@link RecyclerView}.
     */
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            final LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (!(layoutManager instanceof ViewPagerLayoutManager)) return;

            setupCallbacks();
            mGravityScroller = new Scroller(mRecyclerView.getContext(),
                    new DecelerateInterpolator());

            snapToCenterView((ViewPagerLayoutManager) layoutManager,
                    ((ViewPagerLayoutManager) layoutManager).onPageChangeListener);
        }
    }

    void snapToCenterView(ViewPagerLayoutManager layoutManager,
                          ViewPagerLayoutManager.OnPageChangeListener listener) {
        final int delta = layoutManager.getOffsetToCenter();
        if (delta != 0) {
            if (layoutManager.getOrientation()
                    == ViewPagerLayoutManager.VERTICAL)
                mRecyclerView.smoothScrollBy(0, delta);
            else
                mRecyclerView.smoothScrollBy(delta, 0);
        }
        if (listener != null)
            listener.onPageSelected(layoutManager.getCurrentPosition());
    }

    /**
     * Called when an instance of a {@link RecyclerView} is attached.
     */
    void setupCallbacks() throws IllegalStateException {
        if (mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(this);
    }

    /**
     * Called when the instance of a {@link RecyclerView} is detached.
     */
    void destroyCallbacks() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(null);
    }
}
