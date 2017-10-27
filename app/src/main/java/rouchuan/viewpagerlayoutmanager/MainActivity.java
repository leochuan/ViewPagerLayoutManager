package rouchuan.viewpagerlayoutmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import rouchuan.viewpagerlayoutmanager.circle.CircleLayoutActivity;
import rouchuan.viewpagerlayoutmanager.circlescale.CircleScaleLayoutActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String INTENT_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_circle).setOnClickListener(this);
        findViewById(R.id.bt_circle_scale).setOnClickListener(this);
        findViewById(R.id.bt_elevate_scale).setOnClickListener(this);
        findViewById(R.id.bt_gallery).setOnClickListener(this);
        findViewById(R.id.bt_rotate).setOnClickListener(this);
        findViewById(R.id.bt_scale).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_circle:
                startActivity(CircleLayoutActivity.class, ((AppCompatButton)v).getText());
                break;
            case R.id.bt_circle_scale:
                startActivity(CircleScaleLayoutActivity.class, ((AppCompatButton)v).getText());
                break;
            case R.id.bt_elevate_scale:
                break;
            case R.id.bt_gallery:
                break;
            case R.id.bt_rotate:
                break;
            case R.id.bt_scale:
                break;
        }
    }

    private void startActivity(Class clz, CharSequence title) {
        Intent intent = new Intent(this, clz);
        intent.putExtra(INTENT_TITLE, title);
        startActivity(intent);
    }
}
