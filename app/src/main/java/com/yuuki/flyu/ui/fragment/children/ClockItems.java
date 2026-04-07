package com.yuuki.flyu.ui.fragment.children;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.collapse.CollapsibleItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ClockItems {

    private ClockItems() {}

    public static List<CollapsibleItem> build() {
        List<CollapsibleItem> items = new ArrayList<>();

        items.add(new CollapsibleItem(Strings.CLK_BOLD_TITLE, Strings.CLK_BOLD_SUBTITLE, PrefConst.KEY_BOLD_CLOCK));
        items.add(new CollapsibleItem(Strings.CLK_SECONDS_TITLE, Strings.CLK_SECONDS_SUBTITLE, PrefConst.KEY_SHOW_SECONDS));

        items.add(new CollapsibleItem(Strings.CLK_12HOUR_TITLE, Strings.CLK_12HOUR_SUBTITLE,
                Arrays.asList(
                        new CollapsibleItem(Strings.CLK_12HOUR_CN_TITLE, Strings.CLK_12HOUR_CN_SUBTITLE, PrefConst.KEY_12HOUR_CHINESE)
                ), PrefConst.KEY_12HOUR_FORMAT));

        items.add(new CollapsibleItem(Strings.CLK_DAY_OF_WEEK_TITLE, Strings.CLK_DAY_OF_WEEK_SUBTITLE,
                Arrays.asList(
                        new CollapsibleItem(Strings.CLK_DAY_OF_WEEK_CN_TITLE, Strings.CLK_DAY_OF_WEEK_CN_SUBTITLE, PrefConst.KEY_DAY_OF_WEEK_CHINESE)
                ), PrefConst.KEY_SHOW_DAY_OF_WEEK));

        items.add(new CollapsibleItem(Strings.CLK_SHICHEN_TITLE, Strings.CLK_SHICHEN_SUBTITLE, PrefConst.KEY_SHICHEN_MODE));
        items.add(new CollapsibleItem(Strings.CLK_QS_SECONDS_TITLE, Strings.CLK_QS_SECONDS_SUBTITLE, PrefConst.KEY_QS_CLOCK_SECONDS));

        return items;
    }
}
