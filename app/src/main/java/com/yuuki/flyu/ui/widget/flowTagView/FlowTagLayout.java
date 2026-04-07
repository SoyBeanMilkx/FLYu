package com.yuuki.flyu.ui.widget.flowTagView;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlowTagLayout extends ViewGroup {

    private static final String TAG = FlowTagLayout.class.getSimpleName();

    public static final int FLOW_TAG_CHECKED_NONE = 0;
    public static final int FLOW_TAG_CHECKED_SINGLE = 1;
    public static final int FLOW_TAG_CHECKED_MULTI = 2;

    private int mHorizontalSpacing = 10;
    private int mVerticalSpacing = 10;

    AdapterDataSetObserver mDataSetObserver;
    ListAdapter mAdapter;
    OnTagClickListener mOnTagClickListener;
    OnTagSelectListener mOnTagSelectListener;

    private int mTagCheckMode = FLOW_TAG_CHECKED_NONE;
    private SparseBooleanArray mCheckedTagArray = new SparseBooleanArray();

    public FlowTagLayout(Context context) {
        super(context);
        init();
    }

    public FlowTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlowTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getContext().getResources().getDisplayMetrics().density;
        mHorizontalSpacing = (int) (mHorizontalSpacing * density);
        mVerticalSpacing = (int) (mVerticalSpacing * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int availableWidth = sizeWidth - getPaddingLeft() - getPaddingRight();

        int resultWidth = 0;
        int resultHeight = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            int realChildWidth = childWidth + mlp.leftMargin + mlp.rightMargin;
            int realChildHeight = childHeight + mlp.topMargin + mlp.bottomMargin;

            int widthWithSpacing = realChildWidth;
            if (lineWidth > 0) {
                widthWithSpacing += mHorizontalSpacing;
            }

            if (lineWidth > 0 && (lineWidth + widthWithSpacing) > availableWidth) {
                resultWidth = Math.max(lineWidth, resultWidth);
                resultHeight += lineHeight;

                if (resultHeight > 0) {
                    resultHeight += mVerticalSpacing;
                }

                lineWidth = realChildWidth;
                lineHeight = realChildHeight;
            } else {
                if (lineWidth > 0) {
                    lineWidth += mHorizontalSpacing;
                }
                lineWidth += realChildWidth;
                lineHeight = Math.max(lineHeight, realChildHeight);
            }

            if (i == childCount - 1) {
                resultWidth = Math.max(lineWidth, resultWidth);
                resultHeight += lineHeight;
            }
        }

        resultWidth += getPaddingLeft() + getPaddingRight();
        resultHeight += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : resultWidth,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : resultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int flowWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int currentLineHeight = 0;

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);

            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();

            int totalChildWidth = mlp.leftMargin + childWidth + mlp.rightMargin;
            int totalChildHeight = mlp.topMargin + childHeight + mlp.bottomMargin;
            int spacingToAdd = (childLeft > getPaddingLeft()) ? mHorizontalSpacing : 0;

            if (childLeft > getPaddingLeft() && (childLeft + spacingToAdd + totalChildWidth) > (getPaddingLeft() + flowWidth)) {
                childTop += (currentLineHeight + mVerticalSpacing);
                childLeft = getPaddingLeft();
                spacingToAdd = 0;
                currentLineHeight = 0;
            }

            childLeft += spacingToAdd;

            currentLineHeight = Math.max(currentLineHeight, totalChildHeight);

            int left = childLeft + mlp.leftMargin;
            int top = childTop + mlp.topMargin;
            int right = left + childWidth;
            int bottom = top + childHeight;
            childView.layout(left, top, right, bottom);

            childLeft += totalChildWidth;
        }
    }

    private int lineHeight = 0;

    private void updateLineHeight(int childTop) {
        lineHeight = 0;
        int currentTop = childTop;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;

            if (child.getTop() == currentTop) {
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                int childHeight = child.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public ListAdapter getAdapter() {
        return mAdapter;
    }

    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            reloadData();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    private void reloadData() {
        removeAllViews();

        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }

        boolean isSetted = false;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final int j = i;

            mCheckedTagArray.put(i, false);
            final View childView = mAdapter.getView(i, null, this);

            MarginLayoutParams layoutParams = new MarginLayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
            );

            addView(childView, layoutParams);

            if (mAdapter instanceof OnInitSelectedPosition) {
                boolean isSelected = ((OnInitSelectedPosition) mAdapter).isSelectedPosition(i);
                if (mTagCheckMode == FLOW_TAG_CHECKED_SINGLE) {
                    if (isSelected && !isSetted) {
                        mCheckedTagArray.put(i, true);
                        childView.setSelected(true);
                        isSetted = true;
                    }
                } else if (mTagCheckMode == FLOW_TAG_CHECKED_MULTI) {
                    if (isSelected) {
                        mCheckedTagArray.put(i, true);
                        childView.setSelected(true);
                    }
                }
            }

            childView.setOnClickListener(v -> {
                String clickedText = "";
                if (childView instanceof SelectableView) {
                    clickedText = ((SelectableView) childView).getText().toString();
                }

                if (mTagCheckMode == FLOW_TAG_CHECKED_NONE) {
                    if (mOnTagClickListener != null) {
                        mOnTagClickListener.onItemClick(FlowTagLayout.this, childView, j);
                    }
                    // 即使在NONE模式下也调用新的回调
                    if (mOnTagSelectListener != null) {
                        mOnTagSelectListener.onTagClicked(FlowTagLayout.this, j, false, clickedText);
                    }
                } else if (mTagCheckMode == FLOW_TAG_CHECKED_SINGLE) {
                    if (mCheckedTagArray.get(j)) {
                        mCheckedTagArray.put(j, false);
                        childView.setSelected(false);
                        if (mOnTagSelectListener != null) {
                            mOnTagSelectListener.onItemSelect(FlowTagLayout.this, new ArrayList<Integer>());
                            mOnTagSelectListener.onTagClicked(FlowTagLayout.this, j, false, clickedText);
                        }
                        return;
                    }

                    for (int k = 0; k < mAdapter.getCount(); k++) {
                        mCheckedTagArray.put(k, false);
                    }
                    for (int k = 0; k < getChildCount(); k++) {
                        getChildAt(k).setSelected(false);
                    }
                    mCheckedTagArray.put(j, true);
                    childView.setSelected(true);

                    if (mOnTagSelectListener != null) {
                        mOnTagSelectListener.onItemSelect(FlowTagLayout.this, Arrays.asList(j));
                        mOnTagSelectListener.onTagClicked(FlowTagLayout.this, j, true, clickedText);
                    }
                } else if (mTagCheckMode == FLOW_TAG_CHECKED_MULTI) {
                    boolean willBeSelected;
                    if (mCheckedTagArray.get(j)) {
                        mCheckedTagArray.put(j, false);
                        childView.setSelected(false);
                        willBeSelected = false;
                    } else {
                        mCheckedTagArray.put(j, true);
                        childView.setSelected(true);
                        willBeSelected = true;
                    }

                    if (mOnTagSelectListener != null) {
                        List<Integer> list = new ArrayList<Integer>();
                        for (int k = 0; k < mAdapter.getCount(); k++) {
                            if (mCheckedTagArray.get(k)) {
                                list.add(k);
                            }
                        }
                        mOnTagSelectListener.onItemSelect(FlowTagLayout.this, list);
                        mOnTagSelectListener.onTagClicked(FlowTagLayout.this, j, willBeSelected, clickedText);
                    }
                }
            });
        }
    }

    private static class ItemInfo {
        int index;
        int width;
    }

    public void clearAllOption(){
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (mCheckedTagArray.get(i)) {
                getChildAt(i).setSelected(false);
            }
        }
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.mOnTagClickListener = onTagClickListener;
    }

    public void setOnTagSelectListener(OnTagSelectListener onTagSelectListener) {
        this.mOnTagSelectListener = onTagSelectListener;
    }

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        removeAllViews();
        mAdapter = adapter;

        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
            reloadData();
        }
    }

    public int getmTagCheckMode() {
        return mTagCheckMode;
    }

    public void setTagCheckedMode(int tagMode) {
        this.mTagCheckMode = tagMode;
    }

    public void setTagSelected(int position, boolean selected) {
        if (position >= 0 && position < mAdapter.getCount()) {
            mCheckedTagArray.put(position, selected);
            getChildAt(position).setSelected(selected);

            if (mOnTagSelectListener != null) {
                List<Integer> selectedList = new ArrayList<>();
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (mCheckedTagArray.get(i)) {
                        selectedList.add(i);
                    }
                }
                mOnTagSelectListener.onItemSelect(this, selectedList);
            }
        }
    }

    public void setHorizontalSpacing(int spacingDp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        this.mHorizontalSpacing = (int) (spacingDp * density);
        if (mAdapter != null) {
            reloadData();
        }
    }

    public void setVerticalSpacing(int spacingDp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        this.mVerticalSpacing = (int) (spacingDp * density);
        if (mAdapter != null) {
            reloadData();
        }
    }

    public void setSpacing(int spacingDp) {
        setHorizontalSpacing(spacingDp);
        setVerticalSpacing(spacingDp);
    }

}