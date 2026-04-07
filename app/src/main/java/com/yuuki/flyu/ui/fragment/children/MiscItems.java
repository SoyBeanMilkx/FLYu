package com.yuuki.flyu.ui.fragment.children;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.collapse.CollapsibleItem;

import java.util.ArrayList;
import java.util.List;

public final class MiscItems {

    private MiscItems() {}

    public static List<CollapsibleItem> build() {
        List<CollapsibleItem> items = new ArrayList<>();

        items.add(new CollapsibleItem(Strings.MISC_HIDE_LOW_SPEED_TITLE, Strings.MISC_HIDE_LOW_SPEED_SUBTITLE, PrefConst.KEY_HIDE_LOW_SPEED));
        items.add(new CollapsibleItem(Strings.MISC_BRIGHTNESS_TITLE, Strings.MISC_BRIGHTNESS_SUBTITLE, PrefConst.KEY_STATUSBAR_BRIGHTNESS));
        items.add(new CollapsibleItem(Strings.MISC_DOUBLE_TAP_TITLE, Strings.MISC_DOUBLE_TAP_SUBTITLE, PrefConst.KEY_DOUBLE_TAP_LOCK));

        return items;
    }
}
