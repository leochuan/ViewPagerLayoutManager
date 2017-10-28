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

import com.leochuan.CenterScrollListener;
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
    private TextView centerScaleValue;
    private SwitchCompat changeOrientation;
    private SwitchCompat autoCenter;
    private SwitchCompat infinite;
    private SwitchCompat reverse;
    private CenterScrollListener scrollListener;

    ScalePopUpWindow(Context context, ScaleLayoutManager scaleLayoutManager, RecyclerView recyclerView) {
        super(context);
        this.scaleLayoutManager = scaleLayoutManager;
        this.recyclerView = recyclerView;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_scale_setting, null);
        setContentView(view);

        scrollListener = new CenterScrollListener();

        SeekBar itemSpace = (SeekBar) view.findViewById(R.id.sb_item_space);
        SeekBar speed = (SeekBar) view.findViewById(R.id.sb_speed);
        SeekBar centerScale = (SeekBar) view.findViewById(R.id.sb_center_scale);

        itemSpaceValue = (TextView) view.findViewById(R.id.item_space);
        speedValue = (TextView) view.findViewById(R.id.speed_value);
        centerScaleValue = (TextView) view.findViewById(R.id.center_scale_value);

        changeOrientation = (SwitchCompat) view.findViewById(R.id.s_change_orientation);
        autoCenter = (SwitchCompat) view.findViewById(R.id.s_auto_center);
        infinite = (SwitchCompat) view.findViewById(R.id.s_infinite);
        reverse = (SwitchCompat) view.findViewById(R.id.s_reverse);

        itemSpace.setOnSeekBarChangeListener(this);
        speed.setOnSeekBarChangeListener(this);
        centerScale.setOnSeekBarChangeListener(this);

        itemSpace.setProgress(scaleLayoutManager.getItemSpace() / 2);
        speed.setProgress((int) (scaleLayoutManager.getMoveSpeed() / 0.05f));
        centerScale.setProgress((int) (scaleLayoutManager.getCenterScale() * 200f / 3 - 100f / 3));

        itemSpaceValue.setText(String.valueOf(scaleLayoutManager.getItemSpace()));
        speedValue.setText(Util.formatFloat(scaleLayoutManager.getMoveSpeed()));
        centerScaleValue.setText(Util.formatFloat(scaleLayoutManager.getCenterScale()));

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
            case R.id.sb_center_scale:
                final float scale = (progress + 100f / 3) * 3 / 200;
                scaleLayoutManager.setCenterScale(scale);
                centerScaleValue.setText(Util.formatFloat(scale));
                break;
            case R.id.sb_speed:
                final float speed = progress * 0.05f;
                scaleLayoutManager.setMoveSpeed(speed);
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
                scaleLayoutManager.setInfinite(isChecked);
                break;
            case R.id.s_change_orientation:
                scaleLayoutManager.scrollToPosition(0);
                scaleLayoutManager.setOrientation(isChecked ?
                        ViewPagerLayoutManager.VERTICAL : ViewPagerLayoutManager.HORIZONTAL);
                break;
            case R.id.s_auto_center:
                if (isChecked) {
                    recyclerView.addOnScrollListener(scrollListener);
                } else {
                    recyclerView.removeOnScrollListener(scrollListener);
                }
                break;
            case R.id.s_reverse:
                scaleLayoutManager.scrollToPosition(0);
                scaleLayoutManager.setReverseLayout(isChecked);
                break;
        }
    }
}
