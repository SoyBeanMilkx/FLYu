package com.yuuki.flyu;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.fragment.manager.BottomNavigationConfig;
import com.yuuki.flyu.ui.fragment.manager.FragmentNavigator;
import com.yuuki.flyu.ui.widget.BottomNavigation;
import com.yuuki.flyu.ui.widget.FloatButton;
import com.yuuki.flyu.ui.widget.blur.dialog.BlurDialog;
import com.yuuki.flyu.utils.FontHelper;
import com.yuuki.flyu.utils.Miscellaneous;

public class MainActivity extends Activity {

    private FragmentNavigator navigator;
    private BottomNavigation bottomNav;
    private FloatButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontHelper.applySystemFont();
        setContentView(R.layout.activity_main);
        Miscellaneous.initWindow(findViewById(R.id.content_root), this);

        initNavigator(savedInstanceState == null);
        initBottomNav();
        initFab();

        if (savedInstanceState != null) {
            bottomNav.setSelectedIndex(navigator.getCurrentIndex());
        }
    }

    private void initNavigator(boolean firstLaunch) {
        navigator = new FragmentNavigator(getFragmentManager(), R.id.fragment_container);
        if (firstLaunch) {
            navigator.init();
        } else {
            navigator.restore();
        }
    }

    private void initBottomNav() {
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.bindContentSource(findViewById(R.id.content_source));
        BottomNavigationConfig.applyStyle(bottomNav);
        BottomNavigationConfig.addItems(bottomNav, this);
        bottomNav.setOnItemSelectedListener((index, item) -> navigator.switchTo(index));
    }

    private void initFab() {
        fab = findViewById(R.id.fab_button);
        fab.setIcon(R.drawable.refresh);
        fab.setIconColor(Colors.FAB_ICON);
        fab.setClickable(true);

        // 与主标题高度对齐：状态栏 + fragment paddingTop(24dp)
        int statusBarH = Miscellaneous.getStatusBarHeight(this);
        int titleTop = statusBarH + Miscellaneous.dp2px(this, 24);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
        lp.topMargin = titleTop;
        fab.setLayoutParams(lp);

        fab.setOnLongClickListener(v -> {
            showRestartSystemUiDialog();
            return true;
        });

        fab.post(() -> {
            ViewGroup source = findViewById(R.id.content_source);
            fab.bindGlassSource(source);
        });
    }

    private void showRestartSystemUiDialog() {
        BlurDialog.with(this)
                .setTitle(Strings.RESTART_SYSTEMUI_TITLE)
                .setMessage(Strings.RESTART_SYSTEMUI_MSG)
                .setPositiveButton(Strings.RESTART_CONFIRM, dialog -> {
                    dialog.dismiss();
                    restartSystemUi();
                })
                .setNegativeButton(Strings.RESTART_CANCEL, Dialog::dismiss)
                .show();
    }

    private void restartSystemUi() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            java.io.OutputStream os = process.getOutputStream();
            os.write("pkill -f com.android.systemui\n".getBytes());
            os.write("exit\n".getBytes());
            os.flush();
            os.close();
            process.waitFor();
        } catch (Exception e) {
            Toast.makeText(this, Strings.RESTART_FAIL_TOAST, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        int index = navigator.handleBackPress();
        if (index >= 0) {
            bottomNav.setSelectedIndex(index);
        } else {
            super.onBackPressed();
        }
    }
}