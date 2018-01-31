package com.leochuan;

import android.support.v7.widget.RecyclerView;

/**
 * Used by {@link AutoPlayRecyclerView} to implement auto play effect
 */

public class PageSnapHelper extends CenterSnapHelper {

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
            final int offsetPosition = mGravityScroller.getFinalY() * layoutManager.getDistanceRatio() > layoutManager.mInterval ? 1 : 0;
            mRecyclerView.smoothScrollToPosition(layoutManager.getReverseLayout() ?
                    currentPosition - offsetPosition : currentPosition + offsetPosition);
            return true;
        } else if (layoutManager.mOrientation == ViewPagerLayoutManager.HORIZONTAL
                && Math.abs(velocityX) > minFlingVelocity) {
            final int currentPosition = layoutManager.getCurrentPosition();
            final int offsetPosition = mGravityScroller.getFinalX() * layoutManager.getDistanceRatio() > layoutManager.mInterval ? 1 : 0;
            mRecyclerView.smoothScrollToPosition(layoutManager.getReverseLayout() ?
                    currentPosition - offsetPosition : currentPosition + offsetPosition);
            return true;
        }

        return true;
    }
}
