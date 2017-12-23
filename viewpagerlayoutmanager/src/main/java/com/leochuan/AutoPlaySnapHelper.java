package com.leochuan;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;


/**
 * Created by Dajavu on 24/12/2017.
 */

public class AutoPlaySnapHelper extends CenterSnapHelper {
    private final static int INTERVAL = 2000;

    private Handler handler;
    private int timeInterval;
    private Runnable autoPlayRunnable;
    private boolean runnableAdded;

    public AutoPlaySnapHelper() {
        handler = new Handler(Looper.getMainLooper());
        this.timeInterval = INTERVAL;
    }

    public AutoPlaySnapHelper(int timeInterval) {
        handler = new Handler(Looper.getMainLooper());
        this.timeInterval = timeInterval;
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

            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            if (runnableAdded) {
                                handler.removeCallbacks(autoPlayRunnable);
                                runnableAdded = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            handler.postDelayed(autoPlayRunnable, timeInterval);
                            runnableAdded = true;
                            break;
                    }
                    return false;
                }
            });
            autoPlayRunnable = new Runnable() {
                @Override
                public void run() {
                    final int currentPosition =
                            ((ViewPagerLayoutManager) layoutManager).getCurrentPosition();
                    mRecyclerView.smoothScrollToPosition(currentPosition + 1);
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
}
