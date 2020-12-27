# SegmentedControlView

自定义View实现的分段选择控件

![效果图](https://github.com/danledian/SegmentedControl/blob/master/gif/seg.gif)

[github地址](https://github.com/danledian/SegmentedControl)

## Download

#### Gradle:
~~~groovy
dependencies {
    compile 'com.dld.seg:SegControlView:1.0.0'
}
~~~

#### Maven:
~~~xml
<dependency>
  <groupId>com.dld.seg</groupId>
  <artifactId>SegControlView</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
~~~

## xml使用如下：

    <com.dld.view.SegmentedControlView
        android:layout_width="300dp"
        android:layout_height="40dp"
        app:segItemHorizontalMargin="3dp"
        app:segItemVerticalMargin="3dp"
        app:segCornersMode="Circle"
        app:segSelectedItem="1"
        android:id="@+id/scv"/>

### 添加Item:

      List<SegmentedControlItem> items = new ArrayList<>();
      items.add(new SegmentedControlItem("Yesterday"));
      items.add(new SegmentedControlItem("Today"));
      items.add(new SegmentedControlItem("Tomorrow"));
      mScv1.addItems(items);

### Attributes属性
|Attributes|format|describe
|---|---|---|
|segCornersRadius|dimension|内外边框的弧度|
|segBackgroundColor|color|背景颜色|
|segSelectedItemBackgroundColor|color|移动Item的颜色|
|segTextColor|color|背景中字体颜色|
|segSelectedItemTextColor|color|移动Item的字体颜色|
|segItemHorizontalMargin|dimension|移动Item距离边框的左外间距|
|segItemVerticalMargin|dimension|移动Item距离边框的上外间距|
|segSelectedItem|integer|设置选中位置|
|segTextSize|reference|设置字体大小|
|segScrollSelectEnabled|boolean|设置是否支持滑动选择|
|segCornersMode|enum|设置模式|
      
## License

MIT
