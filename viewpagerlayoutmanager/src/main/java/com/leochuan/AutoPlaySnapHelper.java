package com.leochuan;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;


/**
 * Used by {@link AutoPlayRecyclerView} to implement auto play effect
 */

class AutoPlaySnapHelper extends CenterSnapHelper {
    final static int TIME_INTERVAL = 2000;

    final static int LEFT = 1;
    final static int RIGHT = 2;

    private Handler handler;
    private int timeInterval;
    private Runnable autoPlayRunnable;
    private boolean runnableAdded;
    private int direction;

    AutoPlaySnapHelper(int timeInterval, int direction) {
        checkTimeInterval(timeInterval);
        checkDirection(direction);
        handler = new Handler(Looper.getMainLooper());
        this.timeInterval = timeInterval;
        this.direction = direction;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (!(layoutManager instanceof ViewPagerLayoutManager)) return;

            setupCallbacks();
            mGravityScroller = new Scroller(mRecyclerView.getContext(),
                    new DecelerateInterpolator());

            snapToCenterView((ViewPagerLayoutManager) layoutManager,
                    ((ViewPagerLayoutManager) layoutManager).onPageChangeListener);

            ((ViewPagerLayoutManager) layoutManager).setInfinite(true);

            autoPlayRunnable = new Runnable() {
                @Override
                public void run() {
                    final int currentPosition =
                            ((ViewPagerLayoutManager) layoutManager).getCurrentPosition();
                    mRecyclerView.smoothScrollToPosition(direction == RIGHT ? currentPosition + 1 : currentPosition - 1);
                    handler.postDelayed(autoPlayRunnable, timeInterval);
                }
            };
            handler.postDelayed(autoPlayRunnable, timeInterval);
            runnableAdded = true;
        }
    }

    @Override
    void destroyCallbacks() {
        super.destroyCallbacks();
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable);
            runnableAdded = false;
        }
    }

    void pause() {
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable);
            runnableAdded = false;
        }
    }

    void start() {
        if (!runnableAdded) {
            handler.postDelayed(autoPlayRunnable, timeInterval);
            runnableAdded = true;
        }
    }

    void setTimeInterval(int timeInterval) {
        checkTimeInterval(timeInterval);
        this.timeInterval = timeInterval;
    }

    void setDirection(int direction) {
        checkDirection(direction);
        this.direction = direction;
    }

    private void checkDirection(int direction) {
        if (direction != LEFT && direction != RIGHT)
            throw new IllegalArgumentException("direction should be one of left or right");
    }

    private void checkTimeInterval(int timeInterval) {
        if (timeInterval <= 0)
            throw new IllegalArgumentException("time interval should greater than 0");
    }
}
