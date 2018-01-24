package rouchuan.viewpagerlayoutmanager.scale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.leochuan.CenterSnapHelper;
import com.leochuan.ScaleLayoutManager;
import com.leochuan.ViewPagerLayoutManager;

import rouchuan.viewpagerlayoutmanager.R;
import rouchuan.viewpagerlayoutmanager.SettingPopUpWindow;
import rouchuan.viewpagerlayoutmanager.Util;

/**
 * Created by Dajavu on 27/10/2017.
 */

@SuppressLint("InflateParams")
@SuppressWarnings("FieldCanBeLocal")
public class ScalePopUpWindow extends SettingPopUpWindow
        implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private ScaleLayoutManager scaleLayoutManager;
    private RecyclerView recyclerView;
    private TextView itemSpaceValue;
    private TextView speedValue;
    private TextView minScaleValue;
    private TextView minAlphaValue;
    private TextView maxAlphaValue;
    private SwitchCompat changeOrientation;
    private SwitchCompat autoCenter;
    private SwitchCompat infinite;
    private SwitchCompat reverse;
    private CenterSnapHelper centerSnapHelper;

    ScalePopUpWindow(Context context, ScaleLayoutManager scaleLayoutManager, RecyclerView recyclerView) {
        super(context);
        this.scaleLayoutManager = scaleLayoutManager;
        this.recyclerView = recyclerView;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_scale_setting, null);
        setContentView(view);

        centerSnapHelper = new CenterSnapHelper();

        SeekBar itemSpace = view.findViewById(R.id.sb_item_space);
        SeekBar speed = view.findViewById(R.id.sb_speed);
        SeekBar minScale = view.findViewById(R.id.sb_min_scale);
        SeekBar minAlpha = view.findViewById(R.id.sb_min_alpha);
        SeekBar maxAlpha = view.findViewById(R.id.sb_max_alpha);

        itemSpaceValue = view.findViewById(R.id.item_space);
        speedValue = view.findViewById(R.id.speed_value);
        minScaleValue = view.findViewById(R.id.min_scale_value);
        minAlphaValue = view.findViewById(R.id.min_alpha_value);
        maxAlphaValue = view.findViewById(R.id.max_alpha_value);

        changeOrientation = view.findViewById(R.id.s_change_orientation);
        autoCenter = view.findViewById(R.id.s_auto_center);
        infinite = view.findViewById(R.id.s_infinite);
        reverse = view.findViewById(R.id.s_reverse);

        itemSpace.setOnSeekBarChangeListener(this);
        speed.setOnSeekBarChangeListener(this);
        minScale.setOnSeekBarChangeListener(this);
        minAlpha.setOnSeekBarChangeListener(this);
        maxAlpha.setOnSeekBarChangeListener(this);

        itemSpace.setProgress(scaleLayoutManager.getItemSpace() / 2);
        speed.setProgress(Math.round(scaleLayoutManager.getMoveSpeed() / 0.05f));
        minScale.setProgress(Math.round((scaleLayoutManager.getMinScale() - 0.5f) * 200));
        maxAlpha.setProgress(Math.round(scaleLayoutManager.getMaxAlpha() * 100));
        minAlpha.setProgress(Math.round(scaleLayoutManager.getMinAlpha() * 100));

        itemSpaceValue.setText(String.valueOf(scaleLayoutManager.getItemSpace()));
        speedValue.setText(Util.formatFloat(scaleLayoutManager.getMoveSpeed()));
        minScaleValue.setText(Util.formatFloat(scaleLayoutManager.getMinScale()));
        minAlphaValue.setText(Util.formatFloat(scaleLayoutManager.getMinAlpha()));
        maxAlphaValue.setText(Util.formatFloat(scaleLayoutManager.getMaxAlpha()));

        changeOrientation.setChecked(scaleLayoutManager.getOrientation() == ViewPagerLayoutManager.VERTICAL);
        reverse.setChecked(scaleLayoutManager.getReverseLayout());
        infinite.setChecked(scaleLayoutManager.getInfinite());

        changeOrientation.setOnCheckedChangeListener(this);
        autoCenter.setOnCheckedChangeListener(this);
        reverse.setOnCheckedChangeListener(this);
        infinite.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_item_space:
                int itemSpace = progress * 2;
                scaleLayoutManager.setItemSpace(itemSpace);
                itemSpaceValue.setText(String.valueOf(itemSpace));
                break;
            case R.id.sb_min_scale:
                final float scale = 0.5f + (progress / 200f);
                scaleLayoutManager.setMinScale(scale);
                minScaleValue.setText(Util.formatFloat(scale));
                break;
            case R.id.sb_speed:
                final float speed = progress * 0.05f;
                scaleLayoutManager.setMoveSpeed(speed);
                speedValue.setText(Util.formatFloat(speed));
                break;
            case R.id.sb_max_alpha:
                final float maxAlpha = progress / 100f;
                scaleLayoutManager.setMaxAlpha(maxAlpha);
                maxAlphaValue.setText(Util.formatFloat(maxAlpha));
                break;
            case R.id.sb_min_alpha:
                final float minAlpha = progress / 100f;
                scaleLayoutManager.setMinAlpha(minAlpha);
                minAlphaValue.setText(Util.formatFloat(minAlpha));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.s_infinite:
                recyclerView.scrollToPosition(0);
                scaleLayoutManager.setInfinite(isChecked);
                break;
            case R.id.s_change_orientation:
                scaleLayoutManager.scrollToPosition(0);
                scaleLayoutManager.setOrientation(isChecked ?
                        ViewPagerLayoutManager.VERTICAL : ViewPagerLayoutManager.HORIZONTAL);
                break;
            case R.id.s_auto_center:
                if (isChecked) {
                    centerSnapHelper.attachToRecyclerView(recyclerView);
                } else {
                    centerSnapHelper.attachToRecyclerView(null);
                }
                break;
            case R.id.s_reverse:
                scaleLayoutManager.scrollToPosition(0);
                scaleLayoutManager.setReverseLayout(isChecked);
                break;
        }
    }
}
