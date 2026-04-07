package com.yuuki.flyu.ui.fragment.children;

import android.content.Context;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.utils.HwMonitorManager;
import com.yuuki.flyu.ui.widget.collapse.CollapsibleItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HwMonitorItems {

    private HwMonitorItems() {}

    public static List<CollapsibleItem> build(Context context) {
        List<CollapsibleItem> items = new ArrayList<>();

        CollapsibleItem root = new CollapsibleItem(Strings.HW_MONITOR_TITLE, Strings.HW_MONITOR_SUBTITLE,
                Arrays.asList(
                        new CollapsibleItem(Strings.HW_CPU_TEMP_TITLE, Strings.HW_CPU_TEMP_SUBTITLE, PrefConst.KEY_HW_CPU_TEMP),
                        new CollapsibleItem(Strings.HW_BAT_TEMP_TITLE, Strings.HW_BAT_TEMP_SUBTITLE, PrefConst.KEY_HW_BAT_TEMP),
                        new CollapsibleItem(Strings.HW_BAT_POWER_TITLE, Strings.HW_BAT_POWER_SUBTITLE, PrefConst.KEY_HW_BAT_POWER),
                        new CollapsibleItem(Strings.HW_MEM_USAGE_TITLE, Strings.HW_MEM_USAGE_SUBTITLE, PrefConst.KEY_HW_MEM_USAGE),
                        new CollapsibleItem(Strings.HW_SHOW_LEFT_TITLE, Strings.HW_SHOW_LEFT_SUBTITLE, PrefConst.KEY_HW_SHOW_LEFT).withDefaultChecked(true)
                ), PrefConst.KEY_HW_MONITOR);

        HwMonitorManager manager = new HwMonitorManager(context);
        root.setOnCheckedListener(isChecked -> {
            new Thread(() -> {
                if (isChecked) {
                    manager.start();
                } else {
                    manager.stop();
                }
            }).start();
        });

        items.add(root);
        return items;
    }
}
