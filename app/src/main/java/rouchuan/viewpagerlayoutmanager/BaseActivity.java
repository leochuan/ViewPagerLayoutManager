package rouchuan.viewpagerlayoutmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.leochuan.ViewPagerLayoutManager;


/**
 * Created by Dajavu on 26/10/2017.
 */

public abstract class BaseActivity<V extends ViewPagerLayoutManager, S extends SettingPopUpWindow>
        extends AppCompatActivity {
    private RecyclerView recyclerView;
    private V viewPagerLayoutManager;
    private S settingPopUpWindow;

    protected abstract V createLayoutManager();

    protected abstract S createSettingPopUpWindow();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        setTitle(getIntent().getCharSequenceExtra(MainActivity.INTENT_TITLE));
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        viewPagerLayoutManager = createLayoutManager();
        recyclerView.setAdapter(new DataAdapter());
        recyclerView.setLayoutManager(viewPagerLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        MenuItem settings = menu.findItem(R.id.setting);
        VectorDrawableCompat settingIcon =
                VectorDrawableCompat.create(getResources(), R.drawable.ic_settings_white_48px, null);
        settings.setIcon(settingIcon);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                showDialog();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        if (settingPopUpWindow == null) {
            settingPopUpWindow = createSettingPopUpWindow();
        }
        settingPopUpWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }

    public V getViewPagerLayoutManager() {
        return viewPagerLayoutManager;
    }

    public S getSettingPopUpWindow() {
        return settingPopUpWindow;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (settingPopUpWindow != null && settingPopUpWindow.isShowing())
            settingPopUpWindow.dismiss();
    }
}
