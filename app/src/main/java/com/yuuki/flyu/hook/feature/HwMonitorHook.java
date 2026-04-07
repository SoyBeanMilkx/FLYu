package com.yuuki.flyu.hook.feature;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.hook.BaseHook;
import com.yuuki.flyu.hook.HookConst;
import com.yuuki.flyu.hook.utils.HookLog;
import com.yuuki.flyu.hook.utils.HwMonitorServer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.libxposed.api.XposedModule;

public class HwMonitorHook extends BaseHook {

    private static final String P = "[HwMonitor] ";

    // 数据项索引
    private static final int IDX_CPU_TEMP   = 0;
    private static final int IDX_BAT_TEMP   = 1;
    private static final int IDX_BAT_POWER  = 2;
    private static final int IDX_MEM_USAGE  = 3;

    private final boolean[] enabledItems = new boolean[4];
    private final List<TextView> dataViews = new ArrayList<>();
    private boolean showLeft;

    public HwMonitorHook(XposedModule module) {
        super(module);
    }

    @Override
    public String name() {
        return "HwMonitor";
    }

    @Override
    public void hook(XposedModule.PackageReadyParam param) throws Throwable {
        SharedPreferences prefs = module.getRemotePreferences(PrefConst.PREF_NAME);
        if (!prefs.getBoolean(PrefConst.KEY_HW_MONITOR, false)) return;

        enabledItems[IDX_CPU_TEMP]  = prefs.getBoolean(PrefConst.KEY_HW_CPU_TEMP, true);
        enabledItems[IDX_BAT_TEMP]  = prefs.getBoolean(PrefConst.KEY_HW_BAT_TEMP, true);
        enabledItems[IDX_BAT_POWER] = prefs.getBoolean(PrefConst.KEY_HW_BAT_POWER, true);
        enabledItems[IDX_MEM_USAGE] = prefs.getBoolean(PrefConst.KEY_HW_MEM_USAGE, true);
        showLeft = prefs.getBoolean(PrefConst.KEY_HW_SHOW_LEFT, true);

        ClassLoader cl = param.getClassLoader();
        hookInjectViews(cl);
        hookDarkChanged(cl);
    }

    private void hookInjectViews(ClassLoader cl) throws Throwable {
        Class<?> barClass = Class.forName(HookConst.PHONE_STATUS_BAR_VIEW_CLASS, true, cl);
        Method onFinishInflate = barClass.getMethod("onFinishInflate");

        module.hook(onFinishInflate).intercept(chain -> {
            chain.proceed();
            buildAndAttach((ViewGroup) chain.getThisObject());
            return null;
        });
    }

    private void hookDarkChanged(ClassLoader cl) throws Throwable {
        Class<?> clockClass = Class.forName(HookConst.CLOCK_CLASS, true, cl);
        Method onDarkChanged = clockClass.getMethod("onDarkChanged",
                ArrayList.class, float.class, int.class);

        module.hook(onDarkChanged).intercept(chain -> {
            chain.proceed();
            int newColor = ((TextView) chain.getThisObject()).getCurrentTextColor();
            for (TextView tv : dataViews) {
                tv.setTextColor(newColor);
            }
            return null;
        });
    }

    // ── View 构建 & 挂载 ──

    private void buildAndAttach(ViewGroup statusBar) {
        List<Integer> active = new ArrayList<>();
        for (int i = 0; i < enabledItems.length; i++) {
            if (enabledItems[i]) active.add(i);
        }
        if (active.isEmpty()) return;

        HookLog.i(P + "injecting views, active items: " + active.size());

        View clockView = findViewByClass(statusBar, "Clock");
        int textColor = (clockView instanceof TextView)
                ? ((TextView) clockView).getCurrentTextColor() : 0xFFFFFFFF;

        LinearLayout container = new LinearLayout(statusBar.getContext());
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        container.setPadding(dp(statusBar, 1), 0, 0, 0);

        dataViews.clear();

        // 先构建所有 view，记录孤立项位置
        List<View> pairViews = new ArrayList<>();
        View singleView = null;

        int i = 0;
        while (i < active.size()) {
            if (i + 1 < active.size()) {
                LinearLayout pair = new LinearLayout(statusBar.getContext());
                pair.setOrientation(LinearLayout.VERTICAL);
                pair.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                int pad = dp(statusBar, 1);
                pair.setPadding(pad, 0, pad, 0);

                TextView top = createTextView(statusBar, textColor, 7);
                top.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                applyMinWidth(top, active.get(i));
                TextView bottom = createTextView(statusBar, textColor, 7);
                bottom.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                applyMinWidth(bottom, active.get(i + 1));
                pair.addView(top);
                pair.addView(bottom);
                dataViews.add(top);
                dataViews.add(bottom);
                pairViews.add(pair);
                i += 2;
            } else {
                float clockSizeSp = (clockView instanceof TextView)
                        ? ((TextView) clockView).getTextSize() / statusBar.getContext()
                            .getResources().getDisplayMetrics().scaledDensity
                        : 11;
                TextView single = createTextView(statusBar, textColor, (int) clockSizeSp);
                single.setGravity(Gravity.CENTER_VERTICAL);
                applyMinWidth(single, active.get(i));
                int pad = dp(statusBar, 1);
                single.setPadding(pad, 0, pad, 0);
                dataViews.add(single);
                singleView = single;
                i++;
            }
        }

        // 孤立项靠里：左侧模式放最前，右侧模式放最后
        if (showLeft && singleView != null) {
            container.addView(singleView);
        }
        for (View pv : pairViews) {
            container.addView(pv);
        }
        if (!showLeft && singleView != null) {
            container.addView(singleView);
        }

        View anchorView;
        if (showLeft) {
            anchorView = clockView;
        } else {
            anchorView = findViewByClass(statusBar, "ConnectionRateView");
        }

        ViewGroup target = statusBar;
        int insertIndex = 0;
        if (anchorView != null && anchorView.getParent() instanceof ViewGroup) {
            target = (ViewGroup) anchorView.getParent();
            for (int c = 0; c < target.getChildCount(); c++) {
                if (target.getChildAt(c) == anchorView) {
                    insertIndex = c + 1;
                    break;
                }
            }
        }
        target.addView(container, insertIndex,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        HookLog.i(P + "position: " + (showLeft ? "left (Clock)" : "right (ConnectionRate)"));

        HookLog.i(P + "views injected, starting server");
        startServer(active);
    }

    private void startServer(List<Integer> active) {
        HwMonitorServer server = new HwMonitorServer(data -> {
            int viewIdx = 0;
            for (int idx : active) {
                if (viewIdx >= dataViews.size()) break;
                dataViews.get(viewIdx).setText(formatData(idx, data));
                viewIdx++;
            }
        });
        server.start();
    }

    // ── 工具方法 ──

    private String formatData(int idx, HwMonitorServer.HwData data) {
        switch (idx) {
            case IDX_CPU_TEMP:
                return String.format(Locale.US, "CPU %.0f°", data.cpuTemp);
            case IDX_BAT_TEMP:
                return String.format(Locale.US, "BAT %.0f°", data.batTemp);
            case IDX_BAT_POWER:
                return String.format(Locale.US, "%.1fW%s", data.batPower,
                        data.batCharging ? "↑" : "↓");
            case IDX_MEM_USAGE:
                return String.format(Locale.US, "%.1f/%.0fG", data.memUsed, data.memTotal);
            default:
                return "";
        }
    }

    private TextView createTextView(ViewGroup parent, int color, int sizeSp) {
        TextView tv = new TextView(parent.getContext());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTextColor(color);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.getPaint().setFakeBoldText(true);
        tv.getPaint().setStrokeWidth(0.5f);
        tv.setSingleLine(true);
        tv.setEllipsize(null);
        tv.setFontFeatureSettings("tnum");
        tv.setGravity(Gravity.CENTER);
        tv.setIncludeFontPadding(false);
        return tv;
    }

    private void applyMinWidth(TextView tv, int idx) {
        String sample = maxSampleText(idx);
        int width = (int) Math.ceil(tv.getPaint().measureText(sample));
        tv.setMinWidth(width);
    }

    private String maxSampleText(int idx) {
        switch (idx) {
            case IDX_CPU_TEMP:  return "CPU 00°";
            case IDX_BAT_TEMP:  return "BAT 00°";
            case IDX_BAT_POWER: return "10.0W↑";
            case IDX_MEM_USAGE: return "15.8/16G";
            default:            return "";
        }
    }

    private View findViewByClass(ViewGroup root, String className) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child.getClass().getName().contains(className)) return child;
            if (child instanceof ViewGroup) {
                View found = findViewByClass((ViewGroup) child, className);
                if (found != null) return found;
            }
        }
        return null;
    }

    private int dp(View v, int dp) {
        return (int) (dp * v.getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
