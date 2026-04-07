package com.yuuki.flyu.hook.feature;

import android.content.SharedPreferences;
import android.telephony.SubscriptionManager;
import android.view.View;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.hook.BaseHook;
import com.yuuki.flyu.hook.HookConst;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.libxposed.api.XposedModule;

public class StatusBarIconHook extends BaseHook {

    public StatusBarIconHook(XposedModule module) {
        super(module);
    }

    @Override
    public String name() {
        return "StatusBarIcon";
    }

    @Override
    public void hook(XposedModule.PackageReadyParam param) throws Throwable {
        SharedPreferences prefs = module.getRemotePreferences(PrefConst.PREF_NAME);
        ClassLoader cl = param.getClassLoader();

        if (prefs.getBoolean(PrefConst.KEY_HIDE_RIGHT_ICONS, false)) hookHideSlotIcons(prefs, cl);
        if (prefs.getBoolean(PrefConst.KEY_HIDE_WIFI, false))        hookHideWifi(cl);
        if (prefs.getBoolean(PrefConst.KEY_HIDE_SIM1, false)
                || prefs.getBoolean(PrefConst.KEY_HIDE_SIM2, false)) hookHideSim(prefs, cl);
        if (prefs.getBoolean(PrefConst.KEY_HIDE_CHARGE_INDICATOR, false)) hookHideChargeIndicator(cl);
    }

    private void hookHideSlotIcons(SharedPreferences prefs, ClassLoader cl) throws Throwable {
        Set<String> slots = prefs.getStringSet(PrefConst.KEY_HIDDEN_RIGHT_SLOTS, Collections.emptySet());
        if (slots.isEmpty()) return;

        Set<String> hiddenSlots = new HashSet<>(slots);

        Class<?> iconClass = Class.forName(HookConst.STATUS_BAR_ICON_CLASS, true, cl);
        Class<?> iconViewClass = Class.forName(HookConst.STATUS_BAR_ICON_VIEW_CLASS, true, cl);
        Method setMethod = iconViewClass.getMethod("set", iconClass);
        Method getSlot = iconViewClass.getMethod("getSlot");
        Field visibleField = iconClass.getField("visible");

        module.hook(setMethod).intercept(chain -> {
            String slot = (String) getSlot.invoke(chain.getThisObject());
            if (slot != null) {
                boolean hide = hiddenSlots.contains(slot);
                if (!hide) {
                    for (String p : hiddenSlots) {
                        if ((p.endsWith("/") || p.endsWith(".")) && slot.startsWith(p)) {
                            hide = true;
                            break;
                        }
                    }
                }
                if (hide) visibleField.setBoolean(chain.getArgs().get(0), false);
            }
            return chain.proceed();
        });
    }

    private void hookHideWifi(ClassLoader cl) throws Throwable {
        Class<?> stateClass = Class.forName(HookConst.FLYME_WIFI_ICON_STATE_CLASS, true, cl);
        Class<?> wifiViewClass = Class.forName(HookConst.FLYME_WIFI_VIEW_CLASS, true, cl);
        Method applyWifiState = wifiViewClass.getMethod("applyWifiState", stateClass);
        Field visibleField = stateClass.getField("visible");

        module.hook(applyWifiState).intercept(chain -> {
            Object state = chain.getArgs().get(0);
            if (state != null) visibleField.setBoolean(state, false);
            return chain.proceed();
        });
    }

    private void hookHideSim(SharedPreferences prefs, ClassLoader cl) throws Throwable {
        boolean hideSim1 = prefs.getBoolean(PrefConst.KEY_HIDE_SIM1, false);
        boolean hideSim2 = prefs.getBoolean(PrefConst.KEY_HIDE_SIM2, false);

        Class<?> vmClass = Class.forName(HookConst.MOBILE_VIEWMODEL_CLASS, true, cl);
        Method isVisible = vmClass.getMethod("isVisible");
        Method getSubId = vmClass.getMethod("getSubscriptionId");

        Method newStateFlow = Class.forName("kotlinx.coroutines.flow.StateFlowKt", true, cl)
                .getMethod("MutableStateFlow", Object.class);
        Object falseFlow = newStateFlow.invoke(null, Boolean.FALSE);

        module.hook(isVisible).intercept(chain -> {
            int subId = (int) getSubId.invoke(chain.getThisObject());
            int slotIndex = SubscriptionManager.getSlotIndex(subId);
            if ((slotIndex == 0 && hideSim1) || (slotIndex == 1 && hideSim2)) {
                return falseFlow;
            }
            return chain.proceed();
        });
    }

    private void hookHideChargeIndicator(ClassLoader cl) throws Throwable {
        Class<?> batteryClass = Class.forName(HookConst.FLYME_BATTERY_VIEW_CLASS, true, cl);
        Method applyMethod = batteryClass.getMethod("apply", boolean.class);
        Field chargingField = batteryClass.getField("mCharging");
        Field lastPluggedField = batteryClass.getField("mLastPlugged");

        module.hook(applyMethod).intercept(chain -> {
            Object view = chain.getThisObject();
            chargingField.setBoolean(view, false);
            lastPluggedField.setBoolean(view, false);
            return chain.proceed();
        });
    }
}
