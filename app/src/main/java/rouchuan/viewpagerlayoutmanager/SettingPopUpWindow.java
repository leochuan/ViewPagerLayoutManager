package rouchuan.viewpagerlayoutmanager;

import android.content.Context;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Dajavu on 26/10/2017.
 */

public abstract class SettingPopUpWindow extends PopupWindow {
    public SettingPopUpWindow(Context context) {
        super(context);
        setOutsideTouchable(true);
        setWidth(Util.Dp2px(context, 320));
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
