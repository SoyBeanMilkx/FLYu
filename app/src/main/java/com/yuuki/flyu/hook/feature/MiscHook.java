package com.yuuki.flyu.hook.feature;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.hook.BaseHook;
import com.yuuki.flyu.hook.HookConst;
import com.yuuki.flyu.hook.utils.HookLog;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;

public class MiscHook extends BaseHook {

    private static final String P = "[Misc] ";

    public MiscHook(XposedModule module) {
        super(module);
    }

    @Override
    public String name() {
        return "Misc";
    }

    @Override
    public void hook(XposedModule.PackageReadyParam param) throws Throwable {
        SharedPreferences prefs = module.getRemotePreferences(PrefConst.PREF_NAME);
        ClassLoader cl = param.getClassLoader();

        if (prefs.getBoolean(PrefConst.KEY_HIDE_LOW_SPEED, false))       hookHideLowSpeed(cl);
        if (prefs.getBoolean(PrefConst.KEY_STATUSBAR_BRIGHTNESS, false)) hookStatusBarBrightness(cl);
        if (prefs.getBoolean(PrefConst.KEY_DOUBLE_TAP_LOCK, false))      hookDoubleTapLock(cl);
    }

    private void hookHideLowSpeed(ClassLoader cl) throws Throwable {
        Class<?> rateViewClass = Class.forName(HookConst.CONNECTION_RATE_VIEW_CLASS, true, cl);
        Method onRateChange = rateViewClass.getMethod("onConnectionRateChange", boolean.class, double.class);

        module.hook(onRateChange).intercept(chain -> {
            double rate = (double) chain.getArgs().get(1);
            if (rate < 200.0d) {
                View view = (View) chain.getThisObject();
                view.setVisibility(View.GONE);
                return null;
            }
            return chain.proceed();
        });
    }

    private void hookStatusBarBrightness(ClassLoader cl) throws Throwable {
        Class<?> barViewClass = Class.forName(HookConst.PHONE_STATUS_BAR_VIEW_CLASS, true, cl);
        Method onTouchEvent = barViewClass.getMethod("onTouchEvent", MotionEvent.class);

        final float[] downX = new float[1];
        final float[] downY = new float[1];
        final boolean[] isHorizontal = new boolean[1];
        final boolean[] directionDecided = new boolean[1];
        final int DIRECTION_SLOP = 15;

        module.hook(onTouchEvent).intercept(chain -> {
            MotionEvent event = (MotionEvent) chain.getArgs().get(0);
            View view = (View) chain.getThisObject();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX[0] = event.getX();
                downY[0] = event.getY();
                isHorizontal[0] = false;
                directionDecided[0] = false;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!directionDecided[0]) {
                    float absDx = Math.abs(event.getX() - downX[0]);
                    float absDy = Math.abs(event.getY() - downY[0]);
                    if (absDx > DIRECTION_SLOP || absDy > DIRECTION_SLOP) {
                        isHorizontal[0] = absDx > 3 * absDy;
                        directionDecided[0] = true;
                    }
                }
                if (directionDecided[0] && isHorizontal[0]) {
                    float deltaX = event.getX() - downX[0];
                    if (Math.abs(deltaX) > 5) {
                        ContentResolver resolver = view.getContext().getContentResolver();
                        try {
                            int brightness = Settings.System.getInt(resolver,
                                    Settings.System.SCREEN_BRIGHTNESS);
                            brightness += (deltaX > 0) ? 30 : -30;
                            Settings.System.putInt(resolver,
                                    Settings.System.SCREEN_BRIGHTNESS, brightness);
                        } catch (Exception e) {
                            HookLog.e(P + "brightness adjust failed", e);
                        }
                        downX[0] = event.getX();
                    }
                }
            }

            return chain.proceed();
        });
    }

    private void hookDoubleTapLock(ClassLoader cl) throws Throwable {
        Class<?> barViewClass = Class.forName(HookConst.PHONE_STATUS_BAR_VIEW_CLASS, true, cl);
        Method onTouchEvent = barViewClass.getMethod("onTouchEvent", MotionEvent.class);

        final long[] lastTapTime = new long[1];

        module.hook(onTouchEvent).intercept(chain -> {
            MotionEvent event = (MotionEvent) chain.getArgs().get(0);
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                long now = System.currentTimeMillis();
                if (now - lastTapTime[0] <= 200) {
                    try {
                        View view = (View) chain.getThisObject();
                        Object pm = view.getContext().getSystemService(Context.POWER_SERVICE);
                        Method goToSleep = pm.getClass().getMethod("goToSleep", long.class);
                        goToSleep.invoke(pm, SystemClock.uptimeMillis());
                    } catch (Throwable t) {
                        HookLog.e(P + "doubleTapLock failed", t);
                    }
                }
                lastTapTime[0] = now;
            }
            return chain.proceed();
        });
    }

}
