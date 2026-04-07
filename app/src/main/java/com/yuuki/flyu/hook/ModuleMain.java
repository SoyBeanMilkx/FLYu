package com.yuuki.flyu.hook;

import com.yuuki.flyu.hook.feature.ClockHook;
import com.yuuki.flyu.hook.feature.HwMonitorHook;
import com.yuuki.flyu.hook.feature.LockScreenHook;
import com.yuuki.flyu.hook.feature.MiscHook;
import com.yuuki.flyu.hook.feature.StatusBarIconHook;
import com.yuuki.flyu.hook.utils.HookLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.libxposed.api.XposedModule;

public class ModuleMain extends XposedModule {

    private final List<BaseHook> hooks = new ArrayList<>();

    @Override
    public void onModuleLoaded(ModuleLoadedParam param) {
        HookLog.i("onModuleLoaded: " + param.getProcessName());
        HookLog.i(String.format(Locale.getDefault(),
                "framework: %s (%s) API %d",
                getFrameworkName(), getFrameworkVersionCode(), getApiVersion()));

        hooks.add(new StatusBarIconHook(this));
        hooks.add(new ClockHook(this));
        hooks.add(new MiscHook(this));
        hooks.add(new LockScreenHook(this));
        hooks.add(new HwMonitorHook(this));
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        HookLog.i("onPackageLoaded: " + param.getPackageName());
    }

    @Override
    public void onPackageReady(PackageReadyParam param) {
        HookLog.i("onPackageReady: " + param.getPackageName());
        if (!param.isFirstPackage()) return;

        for (BaseHook hook : hooks) {
            try {
                hook.hook(param);
                HookLog.i("[" + hook.name() + "] hooked");
            } catch (Throwable t) {
                HookLog.e("[" + hook.name() + "] hook failed", t);
            }
        }
    }
}
