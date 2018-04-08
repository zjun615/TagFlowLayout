package com.zjun.widget.tagflowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * TagFlowLayout
 *
 * @author Ralap
 * @version v1
 * @description 标签流式布局
 * @date 2018-03-30
 */
public class TagFlowLayout extends ViewGroup implements OnDataChangedListener {

    private static final String TAG = "TagFlowLayout";
    /**
     * 子View之间水平、垂直的间距
     */
    private int horizontalInterval;
    private int verticalInterval;

    /**
     * 数据适配器
     */
    private Adapter mAdapter;

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);

        horizontalInterval = ta.getDimensionPixelSize(R.styleable.TagFlowLayout_horizontalInterval, dp2px(10));
        verticalInterval = ta.getDimensionPixelSize(R.styleable.TagFlowLayout_verticalInterval, horizontalInterval);
        ta.recycle();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 父ViewGroup推荐的宽高与测量模式
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int paddingVertical = getPaddingTop() + getPaddingBottom();
        final int paddingHorizontal = getPaddingLeft() + getPaddingRight();

        Log.d(TAG, "onMeasure: childCount=" + getChildCount());
        if (getChildCount() == 0) {
            setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : paddingHorizontal
                    , heightMode == MeasureSpec.EXACTLY ? heightSize : paddingVertical);
            return;
        }

        // 1. 测量所有的childView，并确定最大的宽高
        int maxWidth = 0;
        int maxHeight = paddingVertical;

        // 行的宽度
        // 每行的宽度与高度
        int lineWidth;
        int lineHeight;

        // childView的宽高
        int cWidth;
        int cHeight;

        View child;

        // 1. 第一个View，无论是否超过宽度，都要摆放
        child = getChildAt(0);
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        cWidth = child.getMeasuredWidth();
        cHeight = child.getMeasuredHeight();
        lineWidth = paddingHorizontal + cWidth;
        lineHeight = cHeight;

        // 2. 是否每行的第一个childView。此childView不需要水平间距
        boolean isRowFirst = false;
        for (int i = 1; i < getChildCount(); i++) {
            child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            cWidth = child.getMeasuredWidth();
            cHeight = child.getMeasuredHeight();

            if (lineWidth + horizontalInterval + cWidth > widthSize) {
                // 超过了一行的宽度，则换行
                maxWidth = Math.max(maxWidth, lineWidth);
                maxHeight += lineHeight + verticalInterval;
                lineWidth = paddingHorizontal;
                lineHeight = 0;
                isRowFirst = true;
            }

            lineWidth += (isRowFirst ? 0 : horizontalInterval) + cWidth;
            lineHeight = Math.max(lineHeight, cHeight);
            isRowFirst = false;
        }
        maxWidth = Math.max(maxWidth, lineWidth);
        // 最后一个不再需要垂直间距
        maxHeight += lineHeight;

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : maxWidth
                , heightMode == MeasureSpec.EXACTLY ? heightSize : maxHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int paddingLeft = getPaddingLeft();
        final int valuableWidth = r - l - getPaddingLeft() - getPaddingRight();
        final int paddingTop = getPaddingTop();

        int width;
        int height;

        int widthOffset;
        int heightOffset = paddingTop;

        int left, top, right, bottom;

        final int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        /*
         先把第一个childView放置好。为了性能，不用在for内每次都判断
         宽度偏移量 = 控件宽度
          */
        View child0 = getChildAt(0);
        width = child0.getMeasuredWidth();
        height = child0.getMeasuredHeight();
        left = paddingLeft;
        top = heightOffset;
        right = left + width;
        bottom = top + height;
        widthOffset = width;
        child0.layout(left, top, right, bottom);

        /*
         第二个childView开始：
         宽度偏移量 = 【水平间距】（换行后的第一个，不需要） + 控件宽度
         高度偏移量 = 控件高度 + 垂直间距
          */
        for (int i = 1; i < childCount; i++) {
            View child = getChildAt(i);
            width = child.getMeasuredWidth();
            height = child.getMeasuredHeight();

            if (widthOffset + horizontalInterval + width > valuableWidth) {
                // 换行
                left = paddingLeft;
                heightOffset += height + verticalInterval;
                top = heightOffset;
                widthOffset = width;
            } else {
                left = paddingLeft + widthOffset + horizontalInterval;
                top = heightOffset;
                widthOffset += horizontalInterval + width;
            }

            right = left + width;
            bottom = top + height;

            child.layout(left, top, right, bottom);
        }
    }

    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setOnDataChangedListener(this);

        updateViews();
    }

    private void updateViews() {
        removeAllViews();
        if (mAdapter.getViewCount() <= 0) {
            return;
        }
        for (int i = 0; i < mAdapter.getViewCount(); i++) {
            View child = mAdapter.onCreateView(mAdapter.mLayoutInflater, this);
            mAdapter.onBindView(child, i);
            addView(child);
        }
    }

    @Override
    public void onChanged() {
        updateViews();
    }

    /**
     * Adapter 适配器
     */
    public static abstract class Adapter {
        
        protected Context mContext;
        private LayoutInflater mLayoutInflater;
        private OnDataChangedListener mOnDataChangedListener;

        public Adapter(@NonNull Context context) {
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        /**
         * Set the count of tags by implement class
         * @return the count of tags
         */
        protected abstract int getViewCount();

        /**
         * You can create the tag view here
         *
         * @param inflater LayoutInflater
         * @param parent This TagFlowLayout
         * @return
         */
        protected abstract View onCreateView(LayoutInflater inflater, ViewGroup parent);

        /**
         * set the content for tag, eg. setText
         * @param view
         * @param position
         */
        protected abstract void onBindView(View view, int position);

        private void setOnDataChangedListener(OnDataChangedListener listener) {
            this.mOnDataChangedListener = listener;
        }

        public void notifyDataSetChanged() {
            if (mOnDataChangedListener != null) {
                mOnDataChangedListener.onChanged();
            }
        }
    }
}
