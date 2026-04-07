package com.yuuki.flyu.ui.widget.flowTagView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yuuki.flyu.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagAdapter extends BaseAdapter implements OnInitSelectedPosition {

    private final Context mContext;
    private final List<String> mDataList;
    private final Set<String> mSelectedItems;

    public TagAdapter(Context context) {
        this(context, null);
    }

    public TagAdapter(Context context, Set<String> initialSelected) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
        this.mSelectedItems = initialSelected != null ? new HashSet<>(initialSelected) : new HashSet<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public String getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectableView view;
        if (convertView == null) {
            view = new SelectableView(mContext);
        } else {
            view = (SelectableView) convertView;
        }
        view.setColors(
                mContext.getResources().getColor(R.color.tag_selected),
                mContext.getResources().getColor(R.color.tag_normal),
                mContext.getResources().getColor(R.color.tag_text_selected),
                mContext.getResources().getColor(R.color.tag_text_normal)
        );
        view.setTextSize(13);
        view.setText(mDataList.get(position));
        return view;
    }

    public void onlyAddAll(List<String> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<String> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }

    public Set<String> getSelectedItems() {
        return mSelectedItems;
    }

    public void toggleSelection(int position) {
        String item = mDataList.get(position);
        if (mSelectedItems.contains(item)) {
            mSelectedItems.remove(item);
        } else {
            mSelectedItems.add(item);
        }
    }

    @Override
    public boolean isSelectedPosition(int position) {
        return mSelectedItems.contains(mDataList.get(position));
    }
}