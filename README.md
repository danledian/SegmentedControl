# SegmentedControlView

自定义View实现的分段选择控件

![效果图](https://github.com/danledian/SegmentedControl/blob/master/gif/seg.gif)

##Download

####Gradle:
~~~groovy
dependencies {
    compile 'com.dld.seg:SegControlView:1.0.0'
}
~~~

####Maven:
~~~xml
<dependency>
  <groupId>com.dld.seg</groupId>
  <artifactId>SegControlView</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
~~~

xml使用如下：

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

## 联系我
QQ:474608897, Email: iamdanledian@gmail.com
      
## License

MIT
