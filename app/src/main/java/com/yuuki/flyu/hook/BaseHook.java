package com.yuuki.flyu.hook;

import io.github.libxposed.api.XposedModule;

public abstract class BaseHook {

    protected final XposedModule module;

    public BaseHook(XposedModule module) {
        this.module = module;
    }

    public abstract String name();

    public abstract void hook(XposedModule.PackageReadyParam param) throws Throwable;
}
