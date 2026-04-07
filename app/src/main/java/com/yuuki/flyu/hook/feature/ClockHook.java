package com.yuuki.flyu.hook.feature;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import com.yuuki.flyu.PrefConst;
import com.yuuki.flyu.hook.BaseHook;
import com.yuuki.flyu.hook.HookConst;
import com.yuuki.flyu.hook.utils.TimeConvert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.libxposed.api.XposedModule;

public class ClockHook extends BaseHook {

    public ClockHook(XposedModule module) {
        super(module);
    }

    @Override
    public String name() {
        return "Clock";
    }

    @Override
    public void hook(XposedModule.PackageReadyParam param) throws Throwable {
        ClassLoader cl = param.getClassLoader();
        Class<?> clockClass = Class.forName(HookConst.CLOCK_CLASS, true, cl);

        SharedPreferences prefs = module.getRemotePreferences(PrefConst.PREF_NAME);

        if (prefs.getBoolean(PrefConst.KEY_BOLD_CLOCK, false)) hookBoldClock(clockClass);
        if (prefs.getBoolean(PrefConst.KEY_SHOW_SECONDS, false)) hookShowSeconds(clockClass);
        if (prefs.getBoolean(PrefConst.KEY_12HOUR_FORMAT, false))
            hook12HourFormat(clockClass, prefs.getBoolean(PrefConst.KEY_12HOUR_CHINESE, false));
        if (prefs.getBoolean(PrefConst.KEY_SHOW_DAY_OF_WEEK, false))
            hookDayOfWeek(clockClass, prefs.getBoolean(PrefConst.KEY_DAY_OF_WEEK_CHINESE, false));
        if (prefs.getBoolean(PrefConst.KEY_SHICHEN_MODE, false)) hookShiChen(clockClass);
        if (prefs.getBoolean(PrefConst.KEY_QS_CLOCK_SECONDS, false)) hookQsClockSeconds(cl);
    }

    private void hookBoldClock(Class<?> clockClass) throws Throwable {
        Method onAttached = clockClass.getMethod("onAttachedToWindow");

        module.hook(onAttached).intercept(chain -> {
            chain.proceed();
            TextView clock = (TextView) chain.getThisObject();
            clock.getPaint().setFakeBoldText(true);
            clock.invalidate();
            return null;
        });
    }

    private void hook12HourFormat(Class<?> clockClass, boolean chinese) throws Throwable {
        Method getSmallTime = clockClass.getDeclaredMethod("getSmallTime");

        Field calendarField = clockClass.getDeclaredField("mCalendar");
        calendarField.setAccessible(true);
        Field showSecondsField = clockClass.getDeclaredField("mShowSeconds");
        showSecondsField.setAccessible(true);

        module.hook(getSmallTime).setPriority(HookConst.PRIORITY_LOW).intercept(chain -> {
            chain.proceed();

            Object clock = chain.getThisObject();
            Calendar calendar = (Calendar) calendarField.get(clock);
            boolean secs = showSecondsField.getBoolean(clock);

            if (chinese) {
                int hour = calendar.get(Calendar.HOUR);
                if (hour == 0) hour = 12;
                String ampm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "\u4e0a\u5348" : "\u4e0b\u5348";
                return secs
                        ? String.format("%s %d:%02d:%02d", ampm, hour, calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))
                        : String.format("%s %d:%02d", ampm, hour, calendar.get(Calendar.MINUTE));
            } else {
                String pattern = secs ? "h:mm:ss a" : "h:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
                return sdf.format(calendar.getTime());
            }
        });
    }

    private void hookDayOfWeek(Class<?> clockClass, boolean chinese) throws Throwable {
        Method getSmallTime = clockClass.getDeclaredMethod("getSmallTime");

        Field calendarField = clockClass.getDeclaredField("mCalendar");
        calendarField.setAccessible(true);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", chinese ? Locale.CHINESE : Locale.ENGLISH);

        module.hook(getSmallTime).setPriority(HookConst.PRIORITY_NORMAL).intercept(chain -> {
            CharSequence time = (CharSequence) chain.proceed();
            Calendar calendar = (Calendar) calendarField.get(chain.getThisObject());
            return time + " " + dayFormat.format(calendar.getTime());
        });
    }

    private void hookShowSeconds(Class<?> clockClass) throws Throwable {
        Field showSecondsField = clockClass.getDeclaredField("mShowSeconds");
        showSecondsField.setAccessible(true);
        Method updateShowSeconds = clockClass.getDeclaredMethod("updateShowSeconds");
        updateShowSeconds.setAccessible(true);
        Method onAttached = clockClass.getMethod("onAttachedToWindow");

        module.hook(onAttached).intercept(chain -> {
            chain.proceed();
            Object clock = chain.getThisObject();
            showSecondsField.setBoolean(clock, true);
            updateShowSeconds.invoke(clock);
            return null;
        });
    }

    private void hookShiChen(Class<?> clockClass) throws Throwable {
        Method getSmallTime = clockClass.getDeclaredMethod("getSmallTime");
        Method onAttached = clockClass.getMethod("onAttachedToWindow");

        Field calendarField = clockClass.getDeclaredField("mCalendar");
        calendarField.setAccessible(true);

        module.hook(getSmallTime).setPriority(HookConst.PRIORITY_OVERRIDE).intercept(chain -> {
            chain.proceed();
            Calendar calendar = (Calendar) calendarField.get(chain.getThisObject());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            return TimeConvert.timeToShiChen(hour, minute);
        });

        module.hook(onAttached).intercept(chain -> {
            chain.proceed();
            TextView clock = (TextView) chain.getThisObject();
            clock.getPaint().setFakeBoldText(true);
            clock.invalidate();
            return null;
        });
    }

    private void hookQsClockSeconds(ClassLoader cl) throws Throwable {
        Class<?> splitClockClass = Class.forName(HookConst.SPLIT_CLOCK_VIEW_CLASS, true, cl);
        Method updatePatterns = splitClockClass.getDeclaredMethod("updatePatterns");
        Field timeViewField = splitClockClass.getDeclaredField("mTimeView");
        timeViewField.setAccessible(true);

        module.hook(updatePatterns).intercept(chain -> {
            Object result = chain.proceed();
            TextClock timeView = (TextClock) timeViewField.get(chain.getThisObject());
            if (timeView != null) {
                CharSequence fmt12 = timeView.getFormat12Hour();
                CharSequence fmt24 = timeView.getFormat24Hour();
                if (fmt12 != null && !fmt12.toString().contains("ss")) {
                    timeView.setFormat12Hour(fmt12.toString().replace("mm", "mm:ss"));
                }
                if (fmt24 != null && !fmt24.toString().contains("ss")) {
                    timeView.setFormat24Hour(fmt24.toString().replace("mm", "mm:ss"));
                }
            }
            return result;
        });
    }
}
