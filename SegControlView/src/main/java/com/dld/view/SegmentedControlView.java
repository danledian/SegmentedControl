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
import android.view.animation.AccelerateInterpolator;
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

    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_OUTER_COLOR = Color.parseColor("#12B7F5");
    private static final int DEFAULT_ITEM_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_SELECTED_TEXT_COLOR = Color.parseColor("#00A5E0");

    private static final int ANIMATION_DURATION = 300;

    /**
     * mode is Round
     */
    private static final int Round = 0;

    /**
     * mode is Circle;
     */
    private static final int Circle = 1;

    @IntDef({Round, Circle})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode{}

    /**
     * The radius
     */
    private int mRadius;

    /**
     * The whole background's color
     */
    private int mOuterColor;
    /**
     *  Item outer left or right margin
     */
    private int mItemMarginLeft;

    /**
     * Item outer top or bottom margin
     */
    private int mItemMarginTop;

    /**
     * The item background's color
     */
    private int mItemColor;

    /**
     * Item's size
     */
    private float mTextSize;

    /**
     * The unselected font color
     */
    private int mTextColor;

    /**
     * The selected font color
     */
    private int mSelectedTextColor;

    /**
     * Selected item position
     */
    private int mSelectedItem;

    /**
     * The mode(Circle or Round)
     */
    private int mMode = Round;

    private boolean mScrollEnable = true;

    private int mStart;
    private int mEnd;
    private int mHeight;
    private int mItemWidth;
    private int mMaximumFlingVelocity;
    private RectF mRectF;
    private Paint mPaint;
    private Paint mTextPaint;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private List<SegmentedControlItem> mSegmentedControlItems  = new ArrayList<>();
    private OnSegItemClickListener listener;

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
        if(ta == null)return;
        mRadius = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segRadius, DEFAULT_RADIUS);
        mOuterColor = ta.getColor(R.styleable.SegmentedControlView_segOuterColor, DEFAULT_OUTER_COLOR);
        mItemColor = ta.getColor(R.styleable.SegmentedControlView_segItemColor, DEFAULT_ITEM_COLOR);
        mTextColor = ta.getColor(R.styleable.SegmentedControlView_segTextColor, DEFAULT_TEXT_COLOR);
        mSelectedTextColor = ta.getColor(R.styleable.SegmentedControlView_segSelectedTextColor, DEFAULT_SELECTED_TEXT_COLOR);
        mItemMarginLeft = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segMarginLeft, 0);
        mItemMarginTop = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segMarginTop, 0);
        mSelectedItem = ta.getInteger(R.styleable.SegmentedControlView_segSelectedItem, 0);
        mTextSize = ta.getDimensionPixelSize(R.styleable.SegmentedControlView_segTextSize, (int) getResources().getDimension(R.dimen.seg_textSize));
        mMode = ta.getInt(R.styleable.SegmentedControlView_segMode, Round);
        mScrollEnable = ta.getBoolean(R.styleable.SegmentedControlView_segScrollEnable, true);
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
        mPaint.setColor(mOuterColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

    }

    public void setMode(@Mode int mode){
        mMode = mode;
        invalidate();
    }

    public void setItemColor(int color) {
        this.mItemColor = color;
        invalidate();
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        invalidate();
    }

    public void setSelectedTextColor(int color) {
        this.mSelectedTextColor = color;
        invalidate();
    }

    public void setSelectedItem(int position) {
        this.mSelectedItem = position;
        mSelectedItem = position < getCount()?position: getCount() - 1;
        invalidate();
    }

    public void setOnSegItemClickListener(OnSegItemClickListener listener){
        this.listener = listener;
    }

    public void onItemClick(SegmentedControlItem item, int position){
        if(null != listener){
            listener.onItemClick(item, position);
        }
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

        if(checkCount())
            return;

        drawBackground(canvas);

        drawText(canvas);

        drawItem(canvas);

        drawOuterText(canvas);

    }

    private void drawOuterText(Canvas canvas) {
        canvas.saveLayer(mStart, 0, mStart + mItemWidth, getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mTextPaint.setColor(mSelectedTextColor);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        int begin = mStart/mItemWidth;
        int end = (begin + 2) < getCount()?begin+2:getCount();

        for (int i = begin; i< end; i++){
            int start = mItemMarginLeft + i * mItemWidth;
            float x = start + mItemWidth/2 - mTextPaint.measureText(getName(i))/2;
            float y = getHeight()/2 - (mTextPaint.ascent() + mTextPaint.descent())/2;
            canvas.drawText(getName(i), x, y, mTextPaint);
        }
        canvas.restore();
    }

    float x = 0;
    int movePosition = -1;

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
            if(!mScroller.isFinished()){
                mScroller.abortAnimation();
            }
            x = event.getX();
            movePosition = -1;
            final float y = event.getY();
            if(isItemInside(x, y)){
                if(!mScrollEnable){
                    return false;
                }
                return true;
            }else if(isItemOutside(x, y)){
                movePosition = (int) ((x - mItemMarginLeft)/ mItemWidth);
                startScroll(positionStart(x));
                if(!mScrollEnable){
                    onStateChange(movePosition);
                    return false;
                }
                return true;
            }
            return false;
        }else if(action == MotionEvent.ACTION_MOVE){
            if(!mScroller.isFinished()){
                return true;
            }
            float dx = event.getX() - x;
            if(Math.abs(dx) > 5f){
                mStart = (int) (mStart + dx);
                mStart = Math.min(Math.max(mStart, mItemMarginLeft), mEnd);
                postInvalidate();
                x = event.getX();
            }
            return true;
        }else if(action == MotionEvent.ACTION_UP){
            int newSelectedItem;
            float offset = (mStart - mItemMarginLeft)%mItemWidth;
            int pos = (mStart - mItemMarginLeft) / mItemWidth;
            if(!mScroller.isFinished() && movePosition != -1){
                newSelectedItem = movePosition;
            }else{
                if(offset == 0f){
                    newSelectedItem = pos;
                }else {
                    VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);

                    int initialVelocity = (int) velocityTracker.getXVelocity();
                    if (Math.abs(initialVelocity) > 1500) {
                        newSelectedItem = initialVelocity>0?pos+1:pos-1;
                    }else {
                        newSelectedItem = Math.round(offset/mItemWidth) + pos;
                    }
                    newSelectedItem = Math.max(Math.min(newSelectedItem, getCount() - 1), 0);
                    startScroll(getXByPosition(newSelectedItem));
                }
            }
            onStateChange(newSelectedItem);
            mVelocityTracker = null;
            movePosition = -1;
            return true;
        }
        return super.onTouchEvent(event);
    }

    private int getXByPosition(int item){
        return item * mItemWidth + mItemMarginLeft;
    }

    private void onStateChange(int selectedItem){
        if(selectedItem != mSelectedItem){
            mSelectedItem = selectedItem;
            onItemClick(getItem(mSelectedItem), mSelectedItem);
        }
    }

    private void startScroll(int dx){
        mScroller.startScroll(mStart, 0, dx - mStart , 0, ANIMATION_DURATION);
        postInvalidate();
    }

    private int positionStart(float x){
        return mItemMarginLeft + (int) ((x - mItemMarginLeft)/ mItemWidth) * mItemWidth;
    }

    private boolean isItemInside(float x, float y){
        return x >= mStart && x <= mStart + mItemWidth && y > mItemMarginTop && y < mHeight - mItemMarginTop;
    }

    private boolean isItemOutside(float x, float y){

        return  !isItemInside(x, y) && y > mItemMarginTop && y < mHeight - mItemMarginTop && x < mEnd + mItemWidth;
    }

    private void drawItem(Canvas canvas) {
        float r = mMode == Round?mRadius:mHeight/2 - mItemMarginTop;
        mPaint.setColor(mItemColor);
        mRectF.set(mStart, mItemMarginTop, mStart + mItemWidth, getHeight() - mItemMarginTop);
        canvas.drawRoundRect(mRectF, r, r, mPaint);
    }

    private void drawBackground(Canvas canvas) {
        float r = mMode == Round?mRadius:mHeight/2;
        mPaint.setXfermode(null);
        mPaint.setColor(mOuterColor);
        mRectF.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(mRectF, r, r, mPaint);
    }

    private void drawText(Canvas canvas) {
        mTextPaint.setColor(mTextColor);
        mTextPaint.setXfermode(null);
        for (int i = 0; i< getCount(); i++){
            int start = mItemMarginLeft + i * mItemWidth;
            float x = start + mItemWidth/2 - mTextPaint.measureText(getName(i))/2;
            float y = getHeight()/2 - (mTextPaint.ascent() + mTextPaint.descent())/2;
            canvas.drawText(getName(i), x, y, mTextPaint);
        }
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

        if(checkCount() || getMeasuredWidth() == 0)
            return;

        mHeight = getMeasuredHeight();
        int width = getMeasuredWidth();
        mItemWidth = (width - 2 * mItemMarginLeft)/getCount();
        mStart = mItemMarginLeft + mItemWidth * mSelectedItem;
        mEnd = width - mItemMarginLeft - mItemWidth;

    }

    private boolean checkCount(){
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
        PullToLoadState pullToLoadState = new PullToLoadState(parcelable);
        pullToLoadState.selectedItem = mSelectedItem;
        return pullToLoadState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof PullToLoadState))
            return;
        PullToLoadState pullToLoadState = ((PullToLoadState)state);
        super.onRestoreInstanceState(pullToLoadState.getSuperState());
        mSelectedItem = pullToLoadState.selectedItem;
        invalidate();
    }


    private static class PullToLoadState extends BaseSavedState{

        private int selectedItem;

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator<PullToLoadState>(){

            @Override
            public PullToLoadState createFromParcel(Parcel source) {
                return new PullToLoadState(source);
            }

            @Override
            public PullToLoadState[] newArray(int size) {
                return new PullToLoadState[size];
            }
        };

        PullToLoadState(Parcel superState) {
            super(superState);
            selectedItem = superState.readInt();
        }

        PullToLoadState(Parcelable source) {
            super(source);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selectedItem);
        }

    }

}
