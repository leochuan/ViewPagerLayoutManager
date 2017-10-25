package rouchuan.viewpagerlayoutmanager;

import android.content.Context;

/**
 * Created by Dajavu on 25/10/2017.
 */

public class Util {
    public static int Dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
