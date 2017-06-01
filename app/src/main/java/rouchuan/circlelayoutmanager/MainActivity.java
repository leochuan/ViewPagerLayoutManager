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
import com.rouchuan.CircleZoomLayoutManager;
import com.rouchuan.GalleryLayoutManager;
import com.rouchuan.ScrollZoomLayoutManager;

import rouchuan.customlayoutmanager.CenterScrollListener;

public class MainActivity extends AppCompatActivity {
    private final static int CIRCLE = 0;
    private final static int SCROLL_ZOOM = 1;
    private final static int CIRCLE_ZOOM = 2;
    private final static int GALLERY = 3;

    private int mode = -1;
    private RecyclerView recyclerView;
    private CircleLayoutManager circleLayoutManager;
    private CircleZoomLayoutManager circleZoomLayoutManager;
    private ScrollZoomLayoutManager scrollZoomLayoutManager;
    private GalleryLayoutManager galleryLayoutManager;

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
        circleZoomLayoutManager = new CircleZoomLayoutManager();
        scrollZoomLayoutManager = new ScrollZoomLayoutManager(Dp2px(10));
        galleryLayoutManager = new GalleryLayoutManager(Dp2px(10));
        recyclerView.addOnScrollListener(new CenterScrollListener());
        determineLayoutManager();
        recyclerView.setAdapter(new Adapter());
    }

    private void determineLayoutManager() {
        mode++;
        if (mode == 4) mode = 0;
        switch (mode) {
            case CIRCLE:
                changeAndToast(circleLayoutManager, "Now in circleLayoutManager");
                break;
            case SCROLL_ZOOM:
                changeAndToast(scrollZoomLayoutManager, "Now in scrollZoomLayoutManager");
                break;
            case CIRCLE_ZOOM:
                changeAndToast(circleZoomLayoutManager, "Now in circleZoomLayoutManager");
                break;
            case GALLERY:
                changeAndToast(galleryLayoutManager, "Now in galleryLayoutManager");
                break;
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
