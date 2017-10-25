package rouchuan.viewpagerlayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Dajavu on 25/10/2017.
 */

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int COUNT = 20;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_image, parent, false));
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
        ((ViewHolder) holder).imageView.setImageResource(res);
    }

    @Override
    public int getItemCount() {
        return COUNT;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
