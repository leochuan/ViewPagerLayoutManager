package rouchuan.circlelayoutmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.rouchuan.CircleLayoutManager;
import com.rouchuan.CircleScaleLayoutManager;
import com.rouchuan.ElevateScaleLayoutManager;
import com.rouchuan.GalleryLayoutManager;
import com.rouchuan.ScaleLayoutManager;

import rouchuan.customlayoutmanager.CenterScrollListener;

public class MainActivity extends AppCompatActivity {
    private final static int CIRCLE = 0;
    private final static int SCROLL_SCALE = 1;
    private final static int CIRCLE_SCALE = 2;
    private final static int GALLERY = 3;
    private final static int ElE_SCALE = 4;

    private int mode = -1;
    private RecyclerView recyclerView;
    private CircleLayoutManager circleLayoutManager;
    private CircleScaleLayoutManager circleScaleLayoutManager;
    private ScaleLayoutManager scaleLayoutManager;
    private GalleryLayoutManager galleryLayoutManager;
    private ElevateScaleLayoutManager elevateScaleLayoutManager;

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
        circleLayoutManager = new CircleLayoutManager();
        circleScaleLayoutManager = new CircleScaleLayoutManager();
        scaleLayoutManager = new ScaleLayoutManager(Dp2px(10));
        galleryLayoutManager = new GalleryLayoutManager(Dp2px(10));
        elevateScaleLayoutManager = new ElevateScaleLayoutManager(Dp2px(-100));
        recyclerView.addOnScrollListener(new CenterScrollListener());
        determineLayoutManager();
        recyclerView.setAdapter(new Adapter());
        circleLayoutManager.setEnableEndlessScroll(true);
        elevateScaleLayoutManager.setEnableEndlessScroll(true);
    }

    private void determineLayoutManager() {
        mode++;
        if (mode == 5) mode = 0;
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
        }
    }

    private void changeAndToast(RecyclerView.LayoutManager layoutManager, String toast) {
        recyclerView.setLayoutManager(layoutManager);
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    private int Dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.my_image, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int index = (position + 1) % 6;
            int res = 0;
            switch (index) {
                case 0:
                    res = R.mipmap.item1;
                    break;
                case 1:
                    res = R.mipmap.item2;
                    break;
                case 2:
                    res = R.mipmap.item3;
                    break;
                case 3:
                    res = R.mipmap.item4;
                    break;
                case 4:
                    res = R.mipmap.item5;
                    break;
                case 5:
                    res = R.mipmap.item6;
                    break;
            }
            ((MyViewHolder) holder).imageView.setImageResource(res);
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }
}
