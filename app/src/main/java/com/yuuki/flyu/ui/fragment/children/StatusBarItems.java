package com.yuuki.flyu.ui.fragment.children;

import android.content.Context;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.collapse.CollapsibleItem;
import com.yuuki.flyu.ui.widget.flowTagView.FlowTagLayout;
import com.yuuki.flyu.ui.widget.flowTagView.TagAdapter;
import com.yuuki.flyu.utils.PrefHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class StatusBarItems {

    private StatusBarItems() {}

    private static final List<String> RIGHT_ICON_SLOTS = Arrays.asList(
            "android/", "com.android.",
            "screen_record", "connected_display", "satellite", "alarm_clock",
            "rotate", "data_saver", "nfc", "cast", "vpn", "bluetooth",
            "camera", "microphone", "location", "mute", "volume",
            "zen", "wifi", "hotspot", "mobile", "no_sims",
            "sensors_off", "volte_or_vowifi"
    );

    public static List<CollapsibleItem> build(Context context) {
        List<CollapsibleItem> items = new ArrayList<>();

        // 隐藏 WiFi 图标
        items.add(new CollapsibleItem(Strings.SB_HIDE_WIFI_TITLE, Strings.SB_HIDE_WIFI_SUBTITLE, PrefConst.KEY_HIDE_WIFI));

        // 隐藏 SIM 卡图标（含子项：SIM1 / SIM2）
        List<CollapsibleItem> simChildren = new ArrayList<>();
        simChildren.add(new CollapsibleItem(Strings.SB_HIDE_SIM1_TITLE, Strings.SB_HIDE_SIM1_SUBTITLE, PrefConst.KEY_HIDE_SIM1));
        simChildren.add(new CollapsibleItem(Strings.SB_HIDE_SIM2_TITLE, Strings.SB_HIDE_SIM2_SUBTITLE, PrefConst.KEY_HIDE_SIM2));
        items.add(new CollapsibleItem(Strings.SB_HIDE_SIM_TITLE, Strings.SB_HIDE_SIM_SUBTITLE, simChildren));

        // 隐藏充电指示器
        items.add(new CollapsibleItem(Strings.SB_HIDE_CHARGE_TITLE, Strings.SB_HIDE_CHARGE_SUBTITLE, PrefConst.KEY_HIDE_CHARGE_INDICATOR));

        // 计算有效选中集合；android/ 和 com.android. 默认选中
        Set<String> effective = new HashSet<>();
        Set<String> saved = PrefHelper.readStringSet(context, PrefConst.KEY_HIDDEN_RIGHT_SLOTS, null);
        if (saved != null) effective.addAll(saved);
        boolean changed = false;
        if (!effective.contains("android/")) { effective.add("android/"); changed = true; }
        if (!effective.contains("com.android.")) { effective.add("com.android."); changed = true; }
        if (changed) {
            PrefHelper.writeStringSet(context, PrefConst.KEY_HIDDEN_RIGHT_SLOTS, effective);
        }

        FlowTagLayout flowLayout = new FlowTagLayout(context);
        flowLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);

        TagAdapter adapter = new TagAdapter(context, effective);
        adapter.onlyAddAll(RIGHT_ICON_SLOTS);
        flowLayout.setAdapter(adapter);

        flowLayout.setOnTagSelectListener((parent, selectedList) -> {
            Set<String> selected = new HashSet<>();
            for (int idx : selectedList) {
                selected.add(RIGHT_ICON_SLOTS.get(idx));
            }
            PrefHelper.writeStringSet(context, PrefConst.KEY_HIDDEN_RIGHT_SLOTS, selected);
        });

        items.add(new CollapsibleItem(Strings.SB_HIDE_ICONS_TITLE, Strings.SB_HIDE_ICONS_SUBTITLE, flowLayout, PrefConst.KEY_HIDE_RIGHT_ICONS));

        return items;
    }

}
