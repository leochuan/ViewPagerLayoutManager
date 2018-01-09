package rouchuan.viewpagerlayoutmanager.circle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.leochuan.CenterSnapHelper;
import com.leochuan.CircleLayoutManager;

import rouchuan.viewpagerlayoutmanager.R;
import rouchuan.viewpagerlayoutmanager.SettingPopUpWindow;
import rouchuan.viewpagerlayoutmanager.Util;

/**
 * Created by Dajavu on 25/10/2017.
 */

@SuppressLint("InflateParams")
@SuppressWarnings("FieldCanBeLocal")
class CirclePopUpWindow extends SettingPopUpWindow
        implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener {

    private CircleLayoutManager circleLayoutManager;
    private RecyclerView recyclerView;
    private TextView radiusValue;
    private TextView intervalValue;
    private TextView speedValue;
    private TextView distanceToBottomValue;
    private SwitchCompat infinite;
    private SwitchCompat autoCenter;
    private SwitchCompat reverse;
    private SwitchCompat flipRotate;
    private CenterSnapHelper centerSnapHelper;
    private RadioGroup gravity;
    private RadioGroup zAlignment;

    CirclePopUpWindow(Context context, CircleLayoutManager circleLayoutManager, RecyclerView recyclerView) {
        super(context);
        this.circleLayoutManager = circleLayoutManager;
        this.recyclerView = recyclerView;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_circle_setting, null);
        setContentView(view);

        centerSnapHelper = new CenterSnapHelper();

        SeekBar radius = view.findViewById(R.id.sb_radius);
        SeekBar interval = view.findViewById(R.id.sb_interval);
        SeekBar speed = view.findViewById(R.id.sb_speed);
        SeekBar distanceToBottom = view.findViewById(R.id.sb_distance_to_bottom);

        radiusValue = view.findViewById(R.id.radius_value);
        intervalValue = view.findViewById(R.id.interval_value);
        speedValue = view.findViewById(R.id.speed_value);
        distanceToBottomValue = view.findViewById(R.id.distance_to_bottom_value);

        infinite = view.findViewById(R.id.s_infinite);
        autoCenter = view.findViewById(R.id.s_auto_center);
        reverse = view.findViewById(R.id.s_reverse);
        flipRotate = view.findViewById(R.id.s_flip);

        gravity = view.findViewById(R.id.rg_gravity);
        zAlignment = view.findViewById(R.id.rg_z_alignment);

        radius.setOnSeekBarChangeListener(this);
        interval.setOnSeekBarChangeListener(this);
        speed.setOnSeekBarChangeListener(this);
        distanceToBottom.setOnSeekBarChangeListener(this);

        final int maxRadius = Util.Dp2px(radius.getContext(), 400);
        radius.setProgress(Math.round(circleLayoutManager.getRadius() * 1f / maxRadius * 100));
        interval.setProgress(Math.round(circleLayoutManager.getAngleInterval() / 0.9f));
        speed.setProgress(Math.round(circleLayoutManager.getMoveSpeed() / 0.005f));
        distanceToBottom.setProgress(circleLayoutManager.getDistanceToBottom() / 10);

        radiusValue.setText(String.valueOf(circleLayoutManager.getRadius()));
        intervalValue.setText(String.valueOf(circleLayoutManager.getAngleInterval()));
        speedValue.setText(Util.formatFloat(circleLayoutManager.getMoveSpeed()));
        distanceToBottomValue.setText(String.valueOf(circleLayoutManager.getDistanceToBottom()));

        infinite.setChecked(circleLayoutManager.getInfinite());
        reverse.setChecked(circleLayoutManager.getReverseLayout());
        flipRotate.setChecked(circleLayoutManager.getFlipRotate());

        infinite.setOnCheckedChangeListener(this);
        autoCenter.setOnCheckedChangeListener(this);
        reverse.setOnCheckedChangeListener(this);
        flipRotate.setOnCheckedChangeListener(this);

        switch (circleLayoutManager.getGravity()) {
            case CircleLayoutManager.LEFT:
                gravity.check(R.id.rb_left);
                break;
            case CircleLayoutManager.RIGHT:
                gravity.check(R.id.rb_right);
                break;
            case CircleLayoutManager.TOP:
                gravity.check(R.id.rb_top);
                break;
            case CircleLayoutManager.BOTTOM:
                gravity.check(R.id.rb_bottom);
                break;
        }

        switch (circleLayoutManager.getZAlignment()) {
            case CircleLayoutManager.LEFT_ON_TOP:
                zAlignment.check(R.id.rb_left_on_top);
                break;
            case CircleLayoutManager.RIGHT_ON_TOP:
                zAlignment.check(R.id.rb_right_on_top);
                break;
            case CircleLayoutManager.CENTER_ON_TOP:
                zAlignment.check(R.id.rb_center_on_top);
                break;
        }

        gravity.setOnCheckedChangeListener(this);
        zAlignment.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_radius:
                final int maxRadius = Util.Dp2px(seekBar.getContext(), 400);
                final int radius = Math.round(progress / 100f * maxRadius);
                circleLayoutManager.setRadius(radius);
                radiusValue.setText(String.valueOf(radius));
                break;
            case R.id.sb_interval:
                final int interval = Math.round(progress * 0.9f);
                circleLayoutManager.setAngleInterval(interval);
                intervalValue.setText(String.valueOf(interval));
                break;
            case R.id.sb_speed:
                final float speed = progress * 0.005f;
                circleLayoutManager.setMoveSpeed(speed);
                speedValue.setText(Util.formatFloat(speed));
                break;
            case R.id.sb_distance_to_bottom:
                final int distance = progress * 10;
                circleLayoutManager.setDistanceToBottom(distance);
                distanceToBottomValue.setText(String.valueOf(distance));
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
                circleLayoutManager.setInfinite(isChecked);
                break;
            case R.id.s_auto_center:
                if (isChecked) {
                    centerSnapHelper.attachToRecyclerView(recyclerView);
                } else {
                    centerSnapHelper.attachToRecyclerView(null);
                }
                break;
            case R.id.s_reverse:
                circleLayoutManager.scrollToPosition(0);
                circleLayoutManager.setReverseLayout(isChecked);
                break;
            case R.id.s_flip:
                circleLayoutManager.setFlipRotate(isChecked);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_left:
                circleLayoutManager.setGravity(CircleLayoutManager.LEFT);
                break;
            case R.id.rb_right:
                circleLayoutManager.setGravity(CircleLayoutManager.RIGHT);
                break;
            case R.id.rb_top:
                circleLayoutManager.setGravity(CircleLayoutManager.TOP);
                break;
            case R.id.rb_bottom:
                circleLayoutManager.setGravity(CircleLayoutManager.BOTTOM);
                break;
            case R.id.rb_left_on_top:
                circleLayoutManager.setZAlignment(CircleLayoutManager.LEFT_ON_TOP);
                break;
            case R.id.rb_right_on_top:
                circleLayoutManager.setZAlignment(CircleLayoutManager.RIGHT_ON_TOP);
                break;
            case R.id.rb_center_on_top:
                circleLayoutManager.setZAlignment(CircleLayoutManager.CENTER_ON_TOP);
                break;
        }
    }
}
