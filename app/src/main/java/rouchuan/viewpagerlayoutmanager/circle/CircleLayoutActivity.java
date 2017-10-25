package rouchuan.viewpagerlayoutmanager.circle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.leochuan.CircleLayoutManager;

import rouchuan.viewpagerlayoutmanager.DataAdapter;
import rouchuan.viewpagerlayoutmanager.R;

/**
 * Created by Dajavu on 25/10/2017.
 */

public class CircleLayoutActivity extends AppCompatActivity {
    private static final String TAG = "CircleLayoutManager";

    private RecyclerView recyclerView;
    private CircleLayoutManager circleLayoutManager;
    private CirclePopUpWindow circlePopUpWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        setTitle(TAG);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        circleLayoutManager = new CircleLayoutManager();
        recyclerView.setAdapter(new DataAdapter());
        recyclerView.setLayoutManager(circleLayoutManager);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        if (circlePopUpWindow == null) {
            circlePopUpWindow = new CirclePopUpWindow(this, circleLayoutManager, recyclerView);
        }
        circlePopUpWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }
}
