package com.yuuki.flyu.ui.fragment.children;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.collapse.CollapsibleItem;

import java.util.ArrayList;
import java.util.List;

public final class LockScreenItems {

    private LockScreenItems() {}

    public static List<CollapsibleItem> build() {
        List<CollapsibleItem> items = new ArrayList<>();

        items.add(new CollapsibleItem(Strings.LS_HIDE_UDFPS_TITLE, Strings.LS_HIDE_UDFPS_SUBTITLE, PrefConst.KEY_HIDE_UDFPS));
        items.add(new CollapsibleItem(Strings.LS_HIDE_CARRIER_TITLE, Strings.LS_HIDE_CARRIER_SUBTITLE, PrefConst.KEY_HIDE_LOCKSCREEN_CARRIER));
        items.add(new CollapsibleItem(Strings.LS_HIDE_FLASHLIGHT_TITLE, Strings.LS_HIDE_FLASHLIGHT_SUBTITLE, PrefConst.KEY_HIDE_LOCKSCREEN_FLASHLIGHT));
        items.add(new CollapsibleItem(Strings.LS_HIDE_CAMERA_TITLE, Strings.LS_HIDE_CAMERA_SUBTITLE, PrefConst.KEY_HIDE_LOCKSCREEN_CAMERA));

        return items;
    }
}
