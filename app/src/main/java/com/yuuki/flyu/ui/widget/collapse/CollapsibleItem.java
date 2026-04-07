package com.yuuki.flyu.ui.widget.collapse;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CollapsibleItem {

    private final String title;
    private final String subtitle;
    private boolean checked;
    private final List<CollapsibleItem> children;
    private final View customView;
    private final String prefKey;
    private final boolean defaultChecked;
    private OnCheckedListener onCheckedListener;

    public interface OnCheckedListener {
        void onCheckedChanged(boolean isChecked);
    }

    /**
     * 创建一个没有子项的开关项
     */
    public CollapsibleItem(String title, String subtitle) {
        this(title, subtitle, null, null, null);
    }

    /**
     * 创建一个没有子项的开关项（带 pref key）
     */
    public CollapsibleItem(String title, String subtitle, String prefKey) {
        this(title, subtitle, null, null, prefKey);
    }

    /**
     * 创建一个带子项的开关项（父开关）
     */
    public CollapsibleItem(String title, String subtitle, List<CollapsibleItem> children) {
        this(title, subtitle, children, null, null);
    }

    /**
     * 创建一个带子项的开关项（父开关，带 pref key）
     */
    public CollapsibleItem(String title, String subtitle, List<CollapsibleItem> children, String prefKey) {
        this(title, subtitle, children, null, prefKey);
    }

    /**
     * 创建一个展开后显示自定义 View 的开关项
     */
    public CollapsibleItem(String title, String subtitle, View customView) {
        this(title, subtitle, null, customView, null);
    }

    /**
     * 创建一个展开后显示自定义 View 的开关项（带 pref key）
     */
    public CollapsibleItem(String title, String subtitle, View customView, String prefKey) {
        this(title, subtitle, null, customView, prefKey);
    }

    private CollapsibleItem(String title, String subtitle, List<CollapsibleItem> children, View customView, String prefKey) {
        this(title, subtitle, children, customView, prefKey, false);
    }

    private CollapsibleItem(String title, String subtitle, List<CollapsibleItem> children, View customView, String prefKey, boolean defaultChecked) {
        this.title = title;
        this.subtitle = subtitle;
        this.checked = defaultChecked;
        this.children = children != null ? children : new ArrayList<>();
        this.customView = customView;
        this.prefKey = prefKey;
        this.defaultChecked = defaultChecked;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
    public List<CollapsibleItem> getChildren() { return children; }
    public boolean hasChildren() { return !children.isEmpty(); }
    public View getCustomView() { return customView; }
    public boolean hasCustomView() { return customView != null; }
    public String getPrefKey() { return prefKey; }
    public boolean hasPrefKey() { return prefKey != null; }
    public boolean getDefaultChecked() { return defaultChecked; }

    public CollapsibleItem withDefaultChecked(boolean defaultChecked) {
        return new CollapsibleItem(title, subtitle, children.isEmpty() ? null : children, customView, prefKey, defaultChecked);
    }
    public void setOnCheckedListener(OnCheckedListener listener) { this.onCheckedListener = listener; }
    public OnCheckedListener getOnCheckedListener() { return onCheckedListener; }
}
