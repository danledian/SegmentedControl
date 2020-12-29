package com.dld.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 2016/10/12.
 * github: https://github.com/danledian/SegmentedControl
 *
 */
public class SegmentedControlView extends View implements ISegmentedControl{

    private static final int VELOCITY_UNITS = 1000;
    private static final int ANIMATION_DURATION = 300;
    private static final int MOVE_ITEM_MIN_VELOCITY = 1500;//移动Item的最小速度
    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_OUTER_COLOR = Color.parseColor("#12B7F5");
    private static final int DEFAULT_ITEM_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_SELECTED_TEXT_COLOR = Color.parseColor("#00A5E0");

    private static final float MIN_MOVE_X = 5f;
    private static final float VELOCITY_CHANGE_POSITION_THRESHOLD = 0.25f;//速度改变位置阈值,范围:[0-1)

    /**
     * 圆角
     */
    public static final int Round = 0;

    /**
     * 圆形
     */
    public static final int Circle = 1;

    @IntDef({Round, Circle})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mode{}

    /**
     * 边角半径
     */
    private int cornersRadius;

    /**
     * 背景颜色
     */
    private int backgroundColor;

    /**
     *  Item水平方向外边距
     */
    private int itemHorizontalMargin;

    /**
     * Item垂直方向边距
     */
    private int itemVerticalMargin;

    /**
     * 选中项背景颜色
     */
    private int selectedItemBackgroundColor;

    /**
     * 文本尺寸
     */
    private int textSize;

    /**
     * 默认文本颜色
     */
    private int textColor;

    /**
     * 选中文本颜色
     */
    private int selectedItemTextColor;

    /**
     * 选中位置
     */
    private int selectedItem;

    /**
     * 边角模式
     */
    private int cornersMode = Round;

    /**
     * 滑动选择是否可用
     */
    private boolean scrollSelectEnabled = true;

    private OnSegItemClickListener onSegItemClickListener;


    private int mStart;
    private int mEnd;
    private int mHeight;
    private int mItemWidth;
    private int onClickDownPosition = -1;//点击事件down选中的位置
    private int mMaximumFlingVelocity;
    private float x = 0;

    private RectF mRectF;
    private Paint mPaint;
    private Paint mTextPaint;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private List<SegmentedControlItem> mSegmentedControlItems  = new ArrayList<>();


    public interface OnSegItemClickListener{
        void onItemClick(SegmentedControlItem item, int position);
    }

    public SegmentedControlView(Context context) {
        this(context, null);
    }

    public SegmentedControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentedControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if(isInEditMode())
            return;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SegmentedControlView);
        
        cornersRadius = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segCornersRadius, DEFAULT_RADIUS);
        backgroundColor = ta.getColor(R.styleable.SegmentedControlView_segBackgroundColor, DEFAULT_OUTER_COLOR);
        selectedItemBackgroundColor = ta.getColor(R.styleable.SegmentedControlView_segSelectedItemBackgroundColor, DEFAULT_ITEM_COLOR);
        textColor = ta.getColor(R.styleable.SegmentedControlView_segTextColor, DEFAULT_TEXT_COLOR);
        selectedItemTextColor = ta.getColor(R.styleable.SegmentedControlView_segSelectedItemTextColor, DEFAULT_SELECTED_TEXT_COLOR);
        itemHorizontalMargin = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segItemHorizontalMargin, 0);
        itemVerticalMargin = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segItemVerticalMargin, 0);
        selectedItem = ta.getInteger(R.styleable.SegmentedControlView_segSelectedItem, 0);
        textSize = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segTextSize, (int) getResources().getDimension(R.dimen.seg_textSize));
        cornersMode = ta.getInt(R.styleable.SegmentedControlView_segCornersMode, Round);
        scrollSelectEnabled = ta.getBoolean(R.styleable.SegmentedControlView_segScrollSelectEnabled, true);
        ta.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }

        mScroller = new Scroller(context, new FastOutSlowInInterpolator());
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        mRectF = new RectF();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(backgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
    }

    public void setCornersMode(@Mode int mode){
        cornersMode = mode;
        invalidate();
    }

    public void setTextColor(int color) {
        this.textColor = color;
        invalidate();
    }

    public void setSelectedItemTextColor(int color) {
        this.selectedItemTextColor = color;
        invalidate();
    }

    public void setSelectedItem(int position) {
        if(position < 0 || position >= getCount()){
            throw new IllegalArgumentException("position error");
        }
        this.selectedItem = position;
        invalidate();
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public int getCornersMode() {
        return cornersMode;
    }

    public boolean isScrollSelectEnabled() {
        return scrollSelectEnabled;
    }

    public void setOnSegItemClickListener(OnSegItemClickListener onSegItemClickListener){
        this.onSegItemClickListener = onSegItemClickListener;
    }

    public void addItems(List<SegmentedControlItem> list){
        if(list == null)
            throw new IllegalArgumentException("list is null");
        mSegmentedControlItems.addAll(list);
        requestLayout();
        invalidate();
    }

    public void addItem(SegmentedControlItem item){
        if(item == null)
            throw new IllegalArgumentException("item is null");
        mSegmentedControlItems.add(item);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isItemZero())
            return;

        drawBackgroundRect(canvas);

        drawUnselectedItemsText(canvas);

        drawSelectedItem(canvas);

        drawSelectedItemsText(canvas);
    }


    /**
     * 画选中项
     * @param canvas
     */
    private void drawSelectedItem(Canvas canvas) {
        float r = cornersMode == Round?cornersRadius: (mHeight >> 1) - itemVerticalMargin;
        mPaint.setColor(selectedItemBackgroundColor);
        mRectF.set(mStart, itemVerticalMargin, mStart + mItemWidth, getHeight() - itemVerticalMargin);
        canvas.drawRoundRect(mRectF, r, r, mPaint);
    }

    /**
     * 画背景区域
     * @param canvas
     */
    private void drawBackgroundRect(Canvas canvas) {
        float r = cornersMode == Round?cornersRadius: mHeight >> 1;
        mPaint.setXfermode(null);
        mPaint.setColor(backgroundColor);
        mRectF.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(mRectF, r, r, mPaint);
    }

    /**
     * 画所有未选中Item的文字
     * @param canvas
     */
    private void drawUnselectedItemsText(Canvas canvas) {
        mTextPaint.setColor(textColor);
        mTextPaint.setXfermode(null);
        for (int i = 0; i< getCount(); i++){
            int start = itemHorizontalMargin + i * mItemWidth;
            float x = start + (mItemWidth >> 1) - mTextPaint.measureText(getName(i))/2;
            float y = (getHeight() >> 1) - (mTextPaint.ascent() + mTextPaint.descent())/2;
            canvas.drawText(getName(i), x, y, mTextPaint);
        }
    }

    /**
     * 画所有选中Item的文字
     * @param canvas
     */
    private void drawSelectedItemsText(Canvas canvas) {
        canvas.saveLayer(mStart, 0, mStart + mItemWidth, getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mTextPaint.setColor(selectedItemTextColor);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        int begin = mStart/mItemWidth;
        int end = (begin + 2) < getCount()?begin+2:getCount();

        for (int i = begin; i< end; i++){
            int start = itemHorizontalMargin + i * mItemWidth;
            float x = start + (mItemWidth >> 1) - mTextPaint.measureText(getName(i))/2;
            float y = (getHeight() >> 1) - (mTextPaint.ascent() + mTextPaint.descent())/2;
            canvas.drawText(getName(i), x, y, mTextPaint);
        }
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!isEnabled() || !isInTouchMode() || getCount() == 0)
            return false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_DOWN){
            x = event.getX();
            onClickDownPosition = -1;
            final float y = event.getY();
            if(isItemInside(x, y)){
                return scrollSelectEnabled;
            }else if(isItemOutside(x, y)){
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                onClickDownPosition = (int) ((x - itemHorizontalMargin)/ mItemWidth);
                startScroll(positionStart(x));
                return true;
            }
            return false;
        }else if(action == MotionEvent.ACTION_MOVE){
            if(!mScroller.isFinished() || !scrollSelectEnabled){
                return true;
            }
            float dx = event.getX() - x;
            if(Math.abs(dx) > MIN_MOVE_X){
                mStart = (int) (mStart + dx);
                mStart = Math.min(Math.max(mStart, itemHorizontalMargin), mEnd);
                postInvalidate();
                x = event.getX();
            }
            return true;
        }else if(action == MotionEvent.ACTION_UP){

            int newSelectedItem;
            float offset = (mStart - itemHorizontalMargin)%mItemWidth;
            float itemStartPosition = (mStart - itemHorizontalMargin) * 1.0f/ mItemWidth;

            if(!mScroller.isFinished() && onClickDownPosition != -1){
                newSelectedItem = onClickDownPosition;
            }else{
                if(offset == 0f){
                    newSelectedItem = (int)itemStartPosition;
                }else {
                    VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(VELOCITY_UNITS, mMaximumFlingVelocity);
                    int initialVelocity = (int) velocityTracker.getXVelocity();

                    float itemRate = offset/mItemWidth;
                    if (isXVelocityCanMoveNextItem(initialVelocity, itemRate)){
                        newSelectedItem = initialVelocity > 0?(int)itemStartPosition+1:(int)itemStartPosition;
                    }else {
                        newSelectedItem = Math.round(itemStartPosition);
                    }
                    newSelectedItem = Math.max(Math.min(newSelectedItem, getCount() - 1), 0);
                    startScroll(getXByPosition(newSelectedItem));
                }
            }
            onStateChange(newSelectedItem);
            mVelocityTracker = null;
            onClickDownPosition = -1;
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void startScroll(int dx){
        mScroller.startScroll(mStart, 0, dx - mStart , 0, ANIMATION_DURATION);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            mStart = mScroller.getCurrX();
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(isItemZero() || getMeasuredWidth() == 0)
            return;

        mHeight = getMeasuredHeight();
        int width = getMeasuredWidth();
        mItemWidth = (width - 2 * itemHorizontalMargin)/getCount();
        mStart = itemHorizontalMargin + mItemWidth * selectedItem;
        mEnd = width - itemHorizontalMargin - mItemWidth;
    }

    private void onStateChange(int selectedItem){
        if(this.selectedItem != selectedItem){
            this.selectedItem = selectedItem;
            onItemClick(getItem(selectedItem), selectedItem);
        }
    }

    private void onItemClick(SegmentedControlItem item, int position){
        if(null != onSegItemClickListener){
            onSegItemClickListener.onItemClick(item, position);
        }
    }

    private int getXByPosition(int item){
        return item * mItemWidth + itemHorizontalMargin;
    }

    private int positionStart(float x){
        return itemHorizontalMargin + (int) ((x - itemHorizontalMargin)/ mItemWidth) * mItemWidth;
    }

    private boolean isItemInside(float x, float y){
        return x >= mStart && x <= mStart + mItemWidth && y > itemVerticalMargin && y < mHeight - itemVerticalMargin;
    }

    private boolean isItemOutside(float x, float y){
        return  !isItemInside(x, y) && y > itemVerticalMargin && y < mHeight - itemVerticalMargin && x < mEnd + mItemWidth;
    }

    /**
     * 根据速度和位置判断是否能移动下一个item
     * @return 否能移动item
     */
    private boolean isXVelocityCanMoveNextItem(int xVelocity, float dxItemRate){
        return Math.abs(xVelocity) > MOVE_ITEM_MIN_VELOCITY && ((xVelocity > 0 && dxItemRate >= VELOCITY_CHANGE_POSITION_THRESHOLD) ||
                (xVelocity < 0 && dxItemRate < (1 - VELOCITY_CHANGE_POSITION_THRESHOLD)));
    }

    private boolean isItemZero(){
        return getCount() == 0;
    }

    @Override
    public int getCount() {
        return mSegmentedControlItems.size();
    }

    @Override
    public SegmentedControlItem getItem(int position) {
        return mSegmentedControlItems.get(position);
    }

    @Override
    public String getName(int position) {
        return getItem(position).getName();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SelectedItemState pullToLoadState = new SelectedItemState(parcelable);
        pullToLoadState.setSelectedItem(selectedItem);
        return pullToLoadState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SelectedItemState))
            return;
        SelectedItemState pullToLoadState = ((SelectedItemState)state);
        super.onRestoreInstanceState(pullToLoadState.getSuperState());
        selectedItem = pullToLoadState.getSelectedItem();
        invalidate();
    }

}
