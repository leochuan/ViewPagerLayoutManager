package rouchuan.viewpagerlayoutmanager.rotate;

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
import com.leochuan.RotateLayoutManager;
import com.leochuan.ViewPagerLayoutManager;

import rouchuan.viewpagerlayoutmanager.R;
import rouchuan.viewpagerlayoutmanager.SettingPopUpWindow;
import rouchuan.viewpagerlayoutmanager.Util;

/**
 * Created by Dajavu on 27/10/2017.
 */

@SuppressLint("InflateParams")
@SuppressWarnings("FieldCanBeLocal")
public class RotatePopUpWindow extends SettingPopUpWindow
        implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private RotateLayoutManager rotateLayoutManager;
    private RecyclerView recyclerView;
    private TextView itemSpaceValue;
    private TextView speedValue;
    private TextView angleValue;
    private SwitchCompat changeOrientation;
    private SwitchCompat autoCenter;
    private SwitchCompat infinite;
    private SwitchCompat reverseRotate;
    private SwitchCompat reverse;
    private CenterSnapHelper centerSnapHelper;

    RotatePopUpWindow(Context context, RotateLayoutManager rotateLayoutManager, RecyclerView recyclerView) {
        super(context);
        this.rotateLayoutManager = rotateLayoutManager;
        this.recyclerView = recyclerView;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rotate_setting, null);
        setContentView(view);

        centerSnapHelper = new CenterSnapHelper();

        SeekBar itemSpace = view.findViewById(R.id.sb_item_space);
        SeekBar speed = view.findViewById(R.id.sb_speed);
        SeekBar angle = view.findViewById(R.id.sb_angle);

        itemSpaceValue = view.findViewById(R.id.item_space);
        speedValue = view.findViewById(R.id.speed_value);
        angleValue = view.findViewById(R.id.angle_value);

        reverseRotate = view.findViewById(R.id.s_reverse_rotate);
        changeOrientation = view.findViewById(R.id.s_change_orientation);
        autoCenter = view.findViewById(R.id.s_auto_center);
        infinite = view.findViewById(R.id.s_infinite);
        reverse = view.findViewById(R.id.s_reverse);

        itemSpace.setOnSeekBarChangeListener(this);
        speed.setOnSeekBarChangeListener(this);
        angle.setOnSeekBarChangeListener(this);

        itemSpace.setProgress(rotateLayoutManager.getItemSpace() / 2);
        speed.setProgress(Math.round(rotateLayoutManager.getMoveSpeed() / 0.05f));
        angle.setProgress(Math.round(rotateLayoutManager.getAngle() / 360 * 100));

        itemSpaceValue.setText(String.valueOf(rotateLayoutManager.getItemSpace()));
        speedValue.setText(Util.formatFloat(rotateLayoutManager.getMoveSpeed()));
        angleValue.setText(Util.formatFloat(rotateLayoutManager.getAngle()));

        reverseRotate.setChecked(rotateLayoutManager.getEnableBringCenterToFront());
        changeOrientation.setChecked(rotateLayoutManager.getOrientation() == ViewPagerLayoutManager.VERTICAL);
        reverse.setChecked(rotateLayoutManager.getReverseLayout());
        infinite.setChecked(rotateLayoutManager.getInfinite());

        reverseRotate.setOnCheckedChangeListener(this);
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
                rotateLayoutManager.setItemSpace(itemSpace);
                itemSpaceValue.setText(String.valueOf(itemSpace));
                break;
            case R.id.sb_angle:
                final float angle = progress / 100f * 360;
                rotateLayoutManager.setAngle(angle);
                angleValue.setText(Util.formatFloat(angle));
                break;
            case R.id.sb_speed:
                final float speed = progress * 0.05f;
                rotateLayoutManager.setMoveSpeed(speed);
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
                rotateLayoutManager.setInfinite(isChecked);
                break;
            case R.id.s_change_orientation:
                rotateLayoutManager.scrollToPosition(0);
                rotateLayoutManager.setOrientation(isChecked ?
                        ViewPagerLayoutManager.VERTICAL : ViewPagerLayoutManager.HORIZONTAL);
                break;
            case R.id.s_auto_center:
                if (isChecked) {
                    centerSnapHelper.attachToRecyclerView(recyclerView);
                } else {
                    centerSnapHelper.attachToRecyclerView(null);
                }
                break;
            case R.id.s_reverse_rotate:
                rotateLayoutManager.setReverseRotate(isChecked);
                break;
            case R.id.s_reverse:
                rotateLayoutManager.scrollToPosition(0);
                rotateLayoutManager.setReverseLayout(isChecked);
                break;
        }
    }
}
