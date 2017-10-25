package rouchuan.viewpagerlayoutmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.leochuan.CenterScrollListener;
import com.leochuan.CircleLayoutManager;
import com.leochuan.CircleScaleLayoutManager;
import com.leochuan.ElevateScaleLayoutManager;
import com.leochuan.GalleryLayoutManager;
import com.leochuan.RotateLayoutManager;
import com.leochuan.ScaleLayoutManager;
import com.leochuan.ViewPagerLayoutManager;

public class MainActivity extends AppCompatActivity {
    private final static int CIRCLE = 0;
    private final static int SCROLL_SCALE = 1;
    private final static int CIRCLE_SCALE = 2;
    private final static int GALLERY = 3;
    private final static int ElE_SCALE = 4;
    private final static int ROTATE = 5;

    private int mode = -1;
    private RecyclerView recyclerView;
    private CircleLayoutManager circleLayoutManager;
    private CircleScaleLayoutManager circleScaleLayoutManager;
    private ScaleLayoutManager scaleLayoutManager;
    private GalleryLayoutManager galleryLayoutManager;
    private ElevateScaleLayoutManager elevateScaleLayoutManager;
    private RotateLayoutManager rotateLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureRecyclerView();
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determineLayoutManager();
            }
        });
        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab2);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(5);
            }
        });
    }

    private void configureRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        circleLayoutManager = new CircleLayoutManager.Builder()
                .setReverseLayout(true)
                .build();
        circleScaleLayoutManager = new CircleScaleLayoutManager();
        scaleLayoutManager = new ScaleLayoutManager(Util.Dp2px(this, 10), ViewPagerLayoutManager.VERTICAL, false);
        galleryLayoutManager = new GalleryLayoutManager(Util.Dp2px(this, 10), ViewPagerLayoutManager.VERTICAL, false);
        elevateScaleLayoutManager = new ElevateScaleLayoutManager(Util.Dp2px(this, -100), 0.5f,
                ViewPagerLayoutManager.HORIZONTAL, false);
        rotateLayoutManager = new RotateLayoutManager(Util.Dp2px(this, 50), 180, ViewPagerLayoutManager.VERTICAL, false);
        recyclerView.addOnScrollListener(new CenterScrollListener());
        determineLayoutManager();
        recyclerView.setAdapter(new DataAdapter());
        elevateScaleLayoutManager.setInfinite(true);
    }

    private void determineLayoutManager() {
        mode++;
        if (mode == 6) mode = 0;
        switch (mode) {
            case CIRCLE:
                changeAndToast(circleLayoutManager, "CircleLayoutManager");
                break;
            case SCROLL_SCALE:
                changeAndToast(scaleLayoutManager, "ScaleLayoutManager");
                break;
            case CIRCLE_SCALE:
                changeAndToast(circleScaleLayoutManager, "CircleScaleLayoutManager");
                break;
            case GALLERY:
                changeAndToast(galleryLayoutManager, "GalleryLayoutManager");
                break;
            case ElE_SCALE:
                changeAndToast(elevateScaleLayoutManager, "ElevateScaleLayoutManager");
                break;
            case ROTATE:
                changeAndToast(rotateLayoutManager, "RotateLayoutManager");
                break;
        }
    }

    private void changeAndToast(RecyclerView.LayoutManager layoutManager, String toast) {
        recyclerView.setLayoutManager(layoutManager);
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }


}
