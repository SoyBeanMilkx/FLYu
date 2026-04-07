package com.yuuki.flyu.ui.fragment.manager;

import android.content.Context;

import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.BottomNavigation;

public class BottomNavigationConfig {

    private static final NavigationItemConfig[] ITEMS = {
            new NavigationItemConfig(Strings.NAV_HOME, R.drawable.home),
            new NavigationItemConfig(Strings.NAV_CONFIG, R.drawable.config)
    };

    public static void applyStyle(BottomNavigation nav) {
        nav.setSelectedColor(Colors.NAV_SELECTED);
        nav.setUnselectedColor(Colors.NAV_UNSELECTED);
        nav.setIndicatorColor(Colors.NAV_INDICATOR);
        nav.setSurfaceColor(Colors.NAV_SURFACE);
        nav.setGlassOverlayColor(Colors.NAV_GLASS_OVERLAY);
        nav.setShadowSize(16f);
        nav.setShowLabels(false);
        nav.setIndicatorWrapText(false);
        nav.setIndicatorPadding(4, 4);
    }

    public static void addItems(BottomNavigation nav, Context context) {
        for (NavigationItemConfig item : ITEMS) {
            nav.addItem(new BottomNavigation.NavigationItem(
                    item.title,
                    context.getDrawable(item.iconResId)
            ));
        }
    }

    private static class NavigationItemConfig {
        final String title;
        final int iconResId;

        NavigationItemConfig(String title, int iconResId) {
            this.title = title;
            this.iconResId = iconResId;
        }
    }
}
