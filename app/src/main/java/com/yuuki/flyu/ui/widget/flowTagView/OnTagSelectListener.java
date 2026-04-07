package com.yuuki.flyu.ui.widget.flowTagView;

import java.util.List;

public interface OnTagSelectListener {
    void onItemSelect(FlowTagLayout parent, List<Integer> selectedList);

    // 新增方法：当某个tag被点击时调用，传递点击位置和当前选中状态
    default void onTagClicked(FlowTagLayout parent, int clickedPosition, boolean isSelected, String text) {
        // 默认实现为空，保持向后兼容
    }
}