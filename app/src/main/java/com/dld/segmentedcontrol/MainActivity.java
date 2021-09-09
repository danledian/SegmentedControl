package com.dld.segmentedcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

        List<SegmentedControlItem> days = createDays();
        List<SegmentedControlItem> filesType = createFilesType();

        mScv1.addItems(days);
        mScv2.addItems(days);
        mScv3.addItems(filesType);

        mScv1.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "click scv1 selected:%d", position);
                Log.d(TAG, msg);
            }
        });

        mScv2.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "click scv2 selected:%d", position);
                Log.d(TAG, msg);
            }
        });

        mScv3.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "click scv3 selected:%d", position);
                Log.d(TAG, msg);
            }
        });
    }


    private List<SegmentedControlItem> createDays(){
        List<SegmentedControlItem> items = new ArrayList<>();
        items.add(new SegmentedControlItem(getResources().getString(R.string.yesterday)));
        items.add(new SegmentedControlItem(getResources().getString(R.string.today)));
        items.add(new SegmentedControlItem(getResources().getString(R.string.tomorrow)));
        return items;
    }

    private List<SegmentedControlItem> createFilesType(){
        List<SegmentedControlItem> items = new ArrayList<>();
        items.add(new SegmentedControlItem(getResources().getString(R.string.photo)));
        items.add(new SegmentedControlItem(getResources().getString(R.string.video)));
        return items;
    }
}
