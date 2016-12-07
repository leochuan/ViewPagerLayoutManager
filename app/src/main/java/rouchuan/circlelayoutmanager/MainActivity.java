package rouchuan.circlelayoutmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private boolean isCircle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView =(RecyclerView)findViewById(R.id.recycler);
        final CircleLayoutManager circleLayoutManager = new CircleLayoutManager(this,true);
        final ScrollZoomLayoutManager scrollZoomLayoutManager = new ScrollZoomLayoutManager(this,Dp2px(10));
        recyclerView.addOnScrollListener(new CenterScrollListener());
        recyclerView.setLayoutManager(circleLayoutManager);
        recyclerView.setAdapter(new Adapter());
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCircle){
                    recyclerView.setLayoutManager(scrollZoomLayoutManager);
                }else{
                    recyclerView.setLayoutManager(circleLayoutManager);
                }
                isCircle = !isCircle;
            }
        });
    }

    public int Dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.my_image,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int index = (position+1)%6;
            int res = 0;
            switch (index){
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
            ((MyViewHolder)holder).imageView.setImageResource(res);
        }

        @Override
        public int getItemCount() {
            return 200;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            public MyViewHolder(View itemView){
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }
}
