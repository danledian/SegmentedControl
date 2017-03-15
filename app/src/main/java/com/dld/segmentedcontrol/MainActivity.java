package com.dld.segmentedcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.dld.view.SegmentedControlItem;
import com.dld.view.SegmentedControlView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author husong
 * github: https://github.com/danledian/SegmentedControl
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SegmentedControlView mScv1;
    private SegmentedControlView mScv2;
    private SegmentedControlView mScv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScv1 = (SegmentedControlView)findViewById(R.id.scv1);
        mScv2 = (SegmentedControlView)findViewById(R.id.scv2);
        mScv3 = (SegmentedControlView)findViewById(R.id.scv3);

        List<SegmentedControlItem> items = new ArrayList<>();
        items.add(new SegmentedControlItem("Yesterday"));
        items.add(new SegmentedControlItem("Today"));
        items.add(new SegmentedControlItem("Tomorrow"));
        mScv1.addItems(items);

        mScv2.addItems(items);
        mScv3.addItems(items);

        mScv1.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "selected:%d", position);
                Log.d(TAG, msg);
            }
        });

        mScv3.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "selected:%d", position);
                Log.d(TAG, msg);
            }
        });
    }
}
