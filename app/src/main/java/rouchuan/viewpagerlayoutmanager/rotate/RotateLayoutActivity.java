package rouchuan.viewpagerlayoutmanager.rotate;

import com.leochuan.RotateLayoutManager;
import com.leochuan.ScaleLayoutManager;

import rouchuan.viewpagerlayoutmanager.BaseActivity;
import rouchuan.viewpagerlayoutmanager.Util;
import rouchuan.viewpagerlayoutmanager.scale.ScalePopUpWindow;

/**
 * Created by Dajavu on 27/10/2017.
 */

public class RotateLayoutActivity extends BaseActivity<RotateLayoutManager, RotatePopUpWindow> {

    @Override
    protected RotateLayoutManager createLayoutManager() {
        return new RotateLayoutManager(Util.Dp2px(this, 10));
    }

    @Override
    protected RotatePopUpWindow createSettingPopUpWindow() {
        return new RotatePopUpWindow(this, getViewPagerLayoutManager(), getRecyclerView());
    }
}
