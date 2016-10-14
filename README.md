# SegmentedControl
SegmentedControl
![效果图](https://github.com/danledian/SegmentedControl/blob/master/gif/seg.gif)
使用如下：

    <com.dld.view.SegmentedControlView
        android:layout_width="300dp"
        android:layout_height="40dp"
        android:layout_marginTop="10dp"
        app:segMarginLeft="3dp"
        app:segMarginTop="3dp"
        app:segMode="Circle"
        app:segSelectedItem="1"
        android:id="@+id/scv"/>
        
 添加Item:
 
      List<SegmentedControlItem> items = new ArrayList<>();
      items.add(new SegmentedControlItem("Yesterday"));
      items.add(new SegmentedControlItem("Today"));
      items.add(new SegmentedControlItem("Tomorrow"));
      mScv1.addItems(items);       
      
## License

MIT
