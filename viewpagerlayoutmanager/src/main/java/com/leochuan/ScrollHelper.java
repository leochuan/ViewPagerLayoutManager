package com.leochuan;

import android.support.v7.widget.RecyclerView;

class ScrollHelper {
    /* package */ static void smoothScrollToPosition(RecyclerView recyclerView, ViewPagerLayoutManager viewPagerLayoutManager, int targetPosition) {
        final int delta = viewPagerLayoutManager.getOffsetToPosition(targetPosition);
        if (viewPagerLayoutManager.getOrientation() == ViewPagerLayoutManager.VERTICAL) {
            recyclerView.smoothScrollBy(0, delta);
        } else {
            recyclerView.smoothScrollBy(delta, 0);
        }
    }
}
