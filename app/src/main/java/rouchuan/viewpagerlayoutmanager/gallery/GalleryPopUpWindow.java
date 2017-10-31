package rouchuan.viewpagerlayoutmanager.gallery;

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
import com.leochuan.GalleryLayoutManager;
import com.leochuan.ViewPagerLayoutManager;

import rouchuan.viewpagerlayoutmanager.R;
import rouchuan.viewpagerlayoutmanager.SettingPopUpWindow;
import rouchuan.viewpagerlayoutmanager.Util;

/**
 * Created by Dajavu on 27/10/2017.
 */

@SuppressLint("InflateParams")
@SuppressWarnings("FieldCanBeLocal")
public class GalleryPopUpWindow extends SettingPopUpWindow
        implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private GalleryLayoutManager galleryLayoutManager;
    private RecyclerView recyclerView;
    private TextView itemSpaceValue;
    private TextView speedValue;
    private TextView minAlphaValue;
    private TextView maxAlphaValue;
    private TextView angleValue;
    private SwitchCompat changeOrientation;
    private SwitchCompat autoCenter;
    private SwitchCompat infinite;
    private SwitchCompat reverse;
    private SwitchCompat flipRotate;
    private CenterScrollListener scrollListener;

    GalleryPopUpWindow(Context context, GalleryLayoutManager galleryLayoutManager, RecyclerView recyclerView) {
        super(context);
        this.galleryLayoutManager = galleryLayoutManager;
        this.recyclerView = recyclerView;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_gallery_setting, null);
        setContentView(view);

        scrollListener = new CenterScrollListener();

        SeekBar itemSpace = (SeekBar) view.findViewById(R.id.sb_item_space);
        SeekBar speed = (SeekBar) view.findViewById(R.id.sb_speed);
        SeekBar minAlpha = (SeekBar) view.findViewById(R.id.sb_min_alpha);
        SeekBar maxAlpha = (SeekBar) view.findViewById(R.id.sb_max_alpha);
        SeekBar angle = (SeekBar) view.findViewById(R.id.sb_interval);

        itemSpaceValue = (TextView) view.findViewById(R.id.item_space);
        speedValue = (TextView) view.findViewById(R.id.speed_value);
        minAlphaValue = (TextView) view.findViewById(R.id.min_alpha_value);
        maxAlphaValue = (TextView) view.findViewById(R.id.max_alpha_value);
        angleValue = (TextView) view.findViewById(R.id.angle_value);

        changeOrientation = (SwitchCompat) view.findViewById(R.id.s_change_orientation);
        autoCenter = (SwitchCompat) view.findViewById(R.id.s_auto_center);
        infinite = (SwitchCompat) view.findViewById(R.id.s_infinite);
        reverse = (SwitchCompat) view.findViewById(R.id.s_reverse);
        flipRotate = (SwitchCompat) view.findViewById(R.id.s_flip);

        itemSpace.setOnSeekBarChangeListener(this);
        speed.setOnSeekBarChangeListener(this);
        minAlpha.setOnSeekBarChangeListener(this);
        maxAlpha.setOnSeekBarChangeListener(this);
        angle.setOnSeekBarChangeListener(this);

        itemSpace.setProgress(galleryLayoutManager.getItemSpace() / 2);
        speed.setProgress(Math.round(galleryLayoutManager.getMoveSpeed() / 0.05f));
        maxAlpha.setProgress(Math.round(galleryLayoutManager.getMaxAlpha() * 100));
        minAlpha.setProgress(Math.round(galleryLayoutManager.getMinAlpha() * 100));
        angle.setProgress(Math.round(galleryLayoutManager.getAngle() / 0.9f));

        itemSpaceValue.setText(String.valueOf(galleryLayoutManager.getItemSpace()));
        speedValue.setText(Util.formatFloat(galleryLayoutManager.getMoveSpeed()));
        minAlphaValue.setText(Util.formatFloat(galleryLayoutManager.getMinAlpha()));
        maxAlphaValue.setText(Util.formatFloat(galleryLayoutManager.getMaxAlpha()));
        angleValue.setText(Util.formatFloat(galleryLayoutManager.getAngle()));

        changeOrientation.setChecked(galleryLayoutManager.getOrientation() == ViewPagerLayoutManager.VERTICAL);
        reverse.setChecked(galleryLayoutManager.getReverseLayout());
        flipRotate.setChecked(galleryLayoutManager.getFlipRotate());
        infinite.setChecked(galleryLayoutManager.getInfinite());

        changeOrientation.setOnCheckedChangeListener(this);
        autoCenter.setOnCheckedChangeListener(this);
        reverse.setOnCheckedChangeListener(this);
        flipRotate.setOnCheckedChangeListener(this);
        infinite.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_item_space:
                int itemSpace = progress * 2;
                galleryLayoutManager.setItemSpace(itemSpace);
                itemSpaceValue.setText(String.valueOf(itemSpace));
                break;
            case R.id.sb_speed:
                final float speed = progress * 0.05f;
                galleryLayoutManager.setMoveSpeed(speed);
                speedValue.setText(Util.formatFloat(speed));
                break;
            case R.id.sb_interval:
                final int angle = Math.round(progress * 0.9f);
                galleryLayoutManager.setAngle(angle);
                angleValue.setText(String.valueOf(angle));
                break;
            case R.id.sb_max_alpha:
                final float maxAlpha = progress / 100f;
                galleryLayoutManager.setMaxAlpha(maxAlpha);
                maxAlphaValue.setText(Util.formatFloat(maxAlpha));
                break;
            case R.id.sb_min_alpha:
                final float minAlpha = progress / 100f;
                galleryLayoutManager.setMinAlpha(minAlpha);
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
                galleryLayoutManager.setInfinite(isChecked);
                break;
            case R.id.s_change_orientation:
                galleryLayoutManager.scrollToPosition(0);
                galleryLayoutManager.setOrientation(isChecked ?
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
                galleryLayoutManager.scrollToPosition(0);
                galleryLayoutManager.setReverseLayout(isChecked);
                break;
            case R.id.s_flip:
                galleryLayoutManager.setFlipRotate(isChecked);
        }
    }
}
