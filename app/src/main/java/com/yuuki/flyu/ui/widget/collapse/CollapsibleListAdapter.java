package com.yuuki.flyu.ui.widget.collapse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.widget.SwitchButtonPro;
import com.yuuki.flyu.utils.Miscellaneous;
import com.yuuki.flyu.utils.PrefHelper;
import java.util.List;

public class CollapsibleListAdapter {

    private final Context context;

    public CollapsibleListAdapter(Context context) {
        this.context = context;
    }

    public void populate(ViewGroup container, List<CollapsibleItem> items) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (int i = 0; i < items.size(); i++) {
            View itemView = createItemView(inflater, container, items.get(i));
            applyOuterSpacing(itemView, i, items.size());
            container.addView(itemView);
        }
    }

    private void applyOuterSpacing(View itemView, int index, int itemCount) {
        int left = itemView.getPaddingLeft();
        int right = itemView.getPaddingRight();
        int top = Miscellaneous.dp2px(context, 5);
        int bottom = Miscellaneous.dp2px(context, 5);
        if (index == 0) top = Miscellaneous.dp2px(context, 14);
        if (index == itemCount - 1) bottom = Miscellaneous.dp2px(context, 14);
        itemView.setPadding(left, top, right, bottom);
    }

    private View createItemView(LayoutInflater inflater, ViewGroup parent, CollapsibleItem item) {
        View convertView = inflater.inflate(R.layout.item_collapsible, parent, false);
        ItemViews itemViews = new ItemViews(convertView);

        bindBasicInfo(itemViews, item);

        // 从本地 Preferences 恢复开关状态
        restoreTopLevelCheckedState(item);
        itemViews.switchBtn.setCheckedImmediately(item.isChecked());

        if (item.hasCustomView()) {
            bindCustomViewItem(itemViews, item);
        }
        else if (item.hasChildren()) {
            bindParentItem(itemViews, item);
        } else {
            bindLeafItem(itemViews, item);
        }

        return convertView;
    }

    private void bindBasicInfo(ItemViews itemViews, CollapsibleItem item) {
        itemViews.title.setText(item.getTitle());

        applySubtitle(itemViews.subtitle, item.getSubtitle());
    }

    private void applySubtitle(TextView subtitleView, String subtitleText) {
        if (subtitleText != null && !subtitleText.isEmpty()) {
            subtitleView.setText(subtitleText);
            subtitleView.setVisibility(View.VISIBLE);
        } else {
            subtitleView.setVisibility(View.GONE);
        }
    }

    private void restoreTopLevelCheckedState(CollapsibleItem item) {
        if (item.hasPrefKey()) {
            boolean saved = PrefHelper.readBoolean(context, item.getPrefKey(), false);
            item.setChecked(saved);
        }
    }

    private void bindCustomViewItem(ItemViews itemViews, CollapsibleItem item) {
        itemViews.childrenContainer.removeAllViews();
        itemViews.childrenContainer.setPadding(0, itemViews.childrenContainer.getPaddingTop(), 0, itemViews.childrenContainer.getPaddingBottom());
        itemViews.childrenContainer.addView(item.getCustomView());
        itemViews.childrenContainer.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);

        itemViews.switchBtn.setOnCheckedChangeListener((view, isChecked) -> {
            item.setChecked(isChecked);
            writeCheckedState(item, isChecked);
            if (isChecked) {
                ExpandCollapseAnimator.expand(itemViews.childrenContainer);
            } else {
                ExpandCollapseAnimator.collapse(itemViews.childrenContainer);
            }
        });
    }

    private void bindParentItem(ItemViews itemViews, CollapsibleItem item) {
        buildChildSwitches(itemViews.childrenContainer, item.getChildren());

        if (!item.hasPrefKey()) {
            boolean anyChildChecked = hasCheckedChildren(item.getChildren());
            item.setChecked(anyChildChecked);
            itemViews.switchBtn.setCheckedImmediately(anyChildChecked);
        }

        itemViews.childrenContainer.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
        setChildrenEnabled(itemViews.childrenContainer, item.isChecked());

        itemViews.switchBtn.setOnCheckedChangeListener((view, isChecked) -> {
            item.setChecked(isChecked);
            writeCheckedStateIfPresent(item, isChecked);
            notifyCheckedChanged(item, isChecked);
            if (isChecked) {
                ExpandCollapseAnimator.expand(itemViews.childrenContainer);
                setChildrenEnabled(itemViews.childrenContainer, true);
            } else {
                ExpandCollapseAnimator.collapse(itemViews.childrenContainer);
                resetChildren(itemViews.childrenContainer, item.getChildren());
            }
        });
    }

    private void bindLeafItem(ItemViews itemViews, CollapsibleItem item) {
        itemViews.childrenContainer.setVisibility(View.GONE);
        itemViews.switchBtn.setOnCheckedChangeListener((view, isChecked) -> {
            item.setChecked(isChecked);
            writeCheckedState(item, isChecked);
            notifyCheckedChanged(item, isChecked);
        });
    }

    private boolean hasCheckedChildren(List<CollapsibleItem> children) {
        for (CollapsibleItem child : children) {
            if (child.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void writeCheckedState(CollapsibleItem item, boolean isChecked) {
        PrefHelper.writeBoolean(context, item.getPrefKey(), isChecked);
    }

    private void writeCheckedStateIfPresent(CollapsibleItem item, boolean isChecked) {
        if (item.hasPrefKey()) {
            writeCheckedState(item, isChecked);
        }
    }

    private void notifyCheckedChanged(CollapsibleItem item, boolean isChecked) {
        if (item.getOnCheckedListener() != null) {
            item.getOnCheckedListener().onCheckedChanged(isChecked);
        }
    }


    private void buildChildSwitches(LinearLayout container, List<CollapsibleItem> children) {
        container.removeAllViews();
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(Miscellaneous.dp2px(context, 8), Miscellaneous.dp2px(context, 10), 0, Miscellaneous.dp2px(context, 4));

        IndentGuideView guideLine = new IndentGuideView(context);
        container.addView(guideLine, new LinearLayout.LayoutParams(
                Miscellaneous.dp2px(context, 12), LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout childColumn = new LinearLayout(context);
        childColumn.setOrientation(LinearLayout.VERTICAL);
        childColumn.setTag("child_column");
        LinearLayout.LayoutParams colLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colLp.setMarginStart(Miscellaneous.dp2px(context, 8));
        container.addView(childColumn, colLp);

        LayoutInflater inflater = LayoutInflater.from(context);

        for (CollapsibleItem child : children) {
            addChildSwitch(inflater, childColumn, child);
        }
    }

    private void addChildSwitch(LayoutInflater inflater, LinearLayout childColumn, CollapsibleItem child) {
        View childRow = inflater.inflate(R.layout.item_collapsible, childColumn, false);
        ItemViews childViews = new ItemViews(childRow);

        childViews.title.setText(child.getTitle());
        childViews.title.setTextSize(14);
        childViews.title.setTextColor(0xFF888888);

        applySubtitle(childViews.subtitle, child.getSubtitle());

        restoreChildCheckedState(child);
        childViews.switchBtn.setCheckedImmediately(child.isChecked());
        childViews.switchBtn.setOnCheckedChangeListener((view, isChecked) -> {
            child.setChecked(isChecked);
            writeCheckedState(child, isChecked);
        });

        childViews.childrenContainer.setVisibility(View.GONE);

        childRow.setPadding(0, Miscellaneous.dp2px(context, 2), Miscellaneous.dp2px(context, 10), Miscellaneous.dp2px(context, 2));

        childColumn.addView(childRow);
    }

    private void restoreChildCheckedState(CollapsibleItem child) {
        if (child.hasPrefKey()) {
            boolean saved = PrefHelper.readBoolean(context, child.getPrefKey(), child.getDefaultChecked());
            child.setChecked(saved);
        }
    }


    private LinearLayout getChildColumn(LinearLayout container) {
        View v = container.findViewWithTag("child_column");
        return v instanceof LinearLayout ? (LinearLayout) v : container;
    }

    private void resetChildren(LinearLayout container, List<CollapsibleItem> children) {
        LinearLayout col = getChildColumn(container);
        setChildrenEnabled(container, false);
        for (int i = 0; i < col.getChildCount() && i < children.size(); i++) {
            CollapsibleItem child = children.get(i);
            child.setChecked(false);
            writeCheckedState(child, false);
            View childRow = col.getChildAt(i);
            SwitchButtonPro childSwitch = childRow.findViewById(R.id.item_switch);
            childSwitch.setCheckedImmediately(false);
        }
    }

    private void setChildrenEnabled(ViewGroup container, boolean enabled) {
        LinearLayout col = getChildColumn((LinearLayout) container);
        for (int i = 0; i < col.getChildCount(); i++) {
            View child = col.getChildAt(i);
            SwitchButtonPro sw = child.findViewById(R.id.item_switch);
            if (sw != null) {
                sw.setEnabled(enabled);
                sw.setAlpha(enabled ? 1f : 0.4f);
            }
        }
    }

    private static class ItemViews {
        private final TextView title;
        private final TextView subtitle;
        private final SwitchButtonPro switchBtn;
        private final LinearLayout childrenContainer;

        private ItemViews(View root) {
            this.title = root.findViewById(R.id.item_title);
            this.subtitle = root.findViewById(R.id.item_subtitle);
            this.switchBtn = root.findViewById(R.id.item_switch);
            this.childrenContainer = root.findViewById(R.id.item_children);
        }
    }
}
