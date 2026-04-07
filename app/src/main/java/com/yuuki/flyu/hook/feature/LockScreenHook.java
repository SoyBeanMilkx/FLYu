package com.yuuki.flyu.hook.feature;

import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.hook.BaseHook;
import com.yuuki.flyu.hook.HookConst;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;

public class LockScreenHook extends BaseHook {

    public LockScreenHook(XposedModule module) {
        super(module);
    }

    @Override
    public String name() {
        return "LockScreen";
    }

    @Override
    public void hook(XposedModule.PackageReadyParam param) throws Throwable {
        SharedPreferences prefs = module.getRemotePreferences(PrefConst.PREF_NAME);
        ClassLoader cl = param.getClassLoader();

        if (prefs.getBoolean(PrefConst.KEY_HIDE_UDFPS, false))                hookHideUdfps(cl);
        if (prefs.getBoolean(PrefConst.KEY_HIDE_LOCKSCREEN_CARRIER, false))    hookHideCarrier(cl);
        if (prefs.getBoolean(PrefConst.KEY_HIDE_LOCKSCREEN_FLASHLIGHT, false)) hookHideFlashlight(cl);
        if (prefs.getBoolean(PrefConst.KEY_HIDE_LOCKSCREEN_CAMERA, false))     hookHideCamera(cl);
    }

    private void hookHideUdfps(ClassLoader cl) throws Throwable {
        Class<?> udfpsClass = Class.forName(HookConst.UDFPS_VIEW_CLASS, true, cl);
        Method onAttach = udfpsClass.getMethod("onAttachedToWindow");

        module.hook(onAttach).intercept(chain -> {
            Object result = chain.proceed();
            ViewGroup viewGroup = (ViewGroup) chain.getThisObject();
            viewGroup.removeAllViews();
            return result;
        });
    }

    private void hookHideCarrier(ClassLoader cl) throws Throwable {
        Class<?> ctrlClass = Class.forName(HookConst.CARRIER_TEXT_CONTROLLER_CLASS, true, cl);
        Method onViewAttached = ctrlClass.getMethod("onViewAttached");

        module.hook(onViewAttached).intercept(chain -> {
            // 不调用 chain.proceed()，阻止注册回调，运营商文字不会被设置
            return null;
        });
    }

    private void hookHideFlashlight(ClassLoader cl) throws Throwable {
        Class<?> bottomAreaClass = Class.forName(HookConst.MZ_BOTTOM_AREA_VIEW_CLASS, true, cl);
        Method updateVisibility = bottomAreaClass.getDeclaredMethod("updateLeftRightClickVisibility");
        Field flashField = bottomAreaClass.getDeclaredField("mLeftClickAffordanceView");
        flashField.setAccessible(true);

        module.hook(updateVisibility).intercept(chain -> {
            Object result = chain.proceed();
            View flashView = (View) flashField.get(chain.getThisObject());
            if (flashView != null) flashView.setVisibility(View.GONE);
            return result;
        });
    }

    private void hookHideCamera(ClassLoader cl) throws Throwable {
        Class<?> bottomAreaClass = Class.forName(HookConst.MZ_BOTTOM_AREA_VIEW_CLASS, true, cl);
        Method updateVisibility = bottomAreaClass.getDeclaredMethod("updateLeftRightClickVisibility");
        Field cameraField = bottomAreaClass.getDeclaredField("mRightClickAffordanceView");
        cameraField.setAccessible(true);

        module.hook(updateVisibility).intercept(chain -> {
            Object result = chain.proceed();
            View cameraView = (View) cameraField.get(chain.getThisObject());
            if (cameraView != null) cameraView.setVisibility(View.GONE);
            return result;
        });
    }
}
