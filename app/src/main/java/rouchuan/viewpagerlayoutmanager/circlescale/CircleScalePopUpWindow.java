package rouchuan.viewpagerlayoutmanager.circlescale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.leochuan.CenterScrollListener;
import com.leochuan.CircleScaleLayoutManager;

import rouchuan.viewpagerlayoutmanager.R;
import rouchuan.viewpagerlayoutmanager.SettingPopUpWindow;
import rouchuan.viewpagerlayoutmanager.Util;

/**
 * Created by Dajavu on 27/10/2017.
 */

@SuppressLint("InflateParams")
@SuppressWarnings("FieldCanBeLocal")
public class CircleScalePopUpWindow extends SettingPopUpWindow
        implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private CircleScaleLayoutManager circleScaleLayoutManager;
    private RecyclerView recyclerView;
    private TextView radiusValue;
    private TextView intervalValue;
    private TextView speedValue;
    private TextView centerScaleValue;
    private SwitchCompat infinite;
    private SwitchCompat autoCenter;
    private SwitchCompat reverse;
    private CenterScrollListener scrollListener;

    CircleScalePopUpWindow(Context context, CircleScaleLayoutManager circleScaleLayoutManager, RecyclerView recyclerView) {
        super(context);
        this.circleScaleLayoutManager = circleScaleLayoutManager;
        this.recyclerView = recyclerView;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_circle_scale_setting, null);
        setContentView(view);

        scrollListener = new CenterScrollListener();

        SeekBar radius = (SeekBar) view.findViewById(R.id.sb_radius);
        SeekBar interval = (SeekBar) view.findViewById(R.id.sb_interval);
        SeekBar speed = (SeekBar) view.findViewById(R.id.sb_speed);
        SeekBar centerScale = (SeekBar) view.findViewById(R.id.sb_center_scale);

        radiusValue = (TextView) view.findViewById(R.id.radius_value);
        intervalValue = (TextView) view.findViewById(R.id.interval_value);
        speedValue = (TextView) view.findViewById(R.id.speed_value);
        centerScaleValue = (TextView) view.findViewById(R.id.center_scale_value);

        infinite = (SwitchCompat) view.findViewById(R.id.s_infinite);
        autoCenter = (SwitchCompat) view.findViewById(R.id.s_auto_center);
        reverse = (SwitchCompat) view.findViewById(R.id.s_reverse);

        radius.setOnSeekBarChangeListener(this);
        interval.setOnSeekBarChangeListener(this);
        speed.setOnSeekBarChangeListener(this);
        centerScale.setOnSeekBarChangeListener(this);

        final int maxRadius = Util.Dp2px(radius.getContext(), 400);
        radius.setProgress(Math.round(circleScaleLayoutManager.getRadius() * 1f / maxRadius * 100));
        interval.setProgress(Math.round(circleScaleLayoutManager.getAngleInterval() / 0.9f));
        speed.setProgress(Math.round(circleScaleLayoutManager.getMoveSpeed() / 0.005f));
        centerScale.setProgress(Math.round(circleScaleLayoutManager.getCenterScale() * 200f / 3 - 100f / 3));

        radiusValue.setText(String.valueOf(circleScaleLayoutManager.getRadius()));
        intervalValue.setText(String.valueOf(circleScaleLayoutManager.getAngleInterval()));
        speedValue.setText(Util.formatFloat(circleScaleLayoutManager.getMoveSpeed()));
        centerScaleValue.setText(Util.formatFloat(circleScaleLayoutManager.getCenterScale()));

        infinite.setChecked(circleScaleLayoutManager.getInfinite());
        reverse.setChecked(circleScaleLayoutManager.getReverseLayout());

        infinite.setOnCheckedChangeListener(this);
        autoCenter.setOnCheckedChangeListener(this);
        reverse.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_radius:
                final int maxRadius = Util.Dp2px(seekBar.getContext(), 400);
                final int radius = Math.round(progress / 100f * maxRadius);
                circleScaleLayoutManager.setRadius(radius);
                radiusValue.setText(String.valueOf(radius));
                break;
            case R.id.sb_interval:
                final int interval = Math.round(progress * 0.9f);
                circleScaleLayoutManager.setAngleInterval(interval);
                intervalValue.setText(String.valueOf(interval));
                break;
            case R.id.sb_center_scale:
                final float scale = (progress + 100f / 3) * 3 / 200;
                circleScaleLayoutManager.setCenterScale(scale);
                centerScaleValue.setText(Util.formatFloat(scale));
                break;
            case R.id.sb_speed:
                final float speed = progress * 0.005f;
                circleScaleLayoutManager.setMoveSpeed(speed);
                speedValue.setText(Util.formatFloat(speed));
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
                circleScaleLayoutManager.setInfinite(isChecked);
                break;
            case R.id.s_auto_center:
                if (isChecked) {
                    recyclerView.addOnScrollListener(scrollListener);
                } else {
                    recyclerView.removeOnScrollListener(scrollListener);
                }
                break;
            case R.id.s_reverse:
                circleScaleLayoutManager.scrollToPosition(0);
                circleScaleLayoutManager.setReverseLayout(isChecked);
        }
    }
}
