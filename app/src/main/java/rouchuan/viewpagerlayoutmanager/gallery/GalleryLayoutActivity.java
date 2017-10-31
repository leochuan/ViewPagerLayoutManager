package rouchuan.viewpagerlayoutmanager.gallery;

import com.leochuan.GalleryLayoutManager;
import com.leochuan.ScaleLayoutManager;

import rouchuan.viewpagerlayoutmanager.BaseActivity;
import rouchuan.viewpagerlayoutmanager.Util;
import rouchuan.viewpagerlayoutmanager.scale.ScalePopUpWindow;

/**
 * Created by Dajavu on 27/10/2017.
 */

public class GalleryLayoutActivity extends BaseActivity<GalleryLayoutManager, GalleryPopUpWindow> {

    @Override
    protected GalleryLayoutManager createLayoutManager() {
        return new GalleryLayoutManager(Util.Dp2px(this, 10));
    }

    @Override
    protected GalleryPopUpWindow createSettingPopUpWindow() {
        return new GalleryPopUpWindow(this, getViewPagerLayoutManager(), getRecyclerView());
    }
}
