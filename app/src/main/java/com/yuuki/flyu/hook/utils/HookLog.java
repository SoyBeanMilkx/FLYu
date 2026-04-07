package com.yuuki.flyu.hook.utils;

import android.util.Log;

import com.yuuki.flyu.ui.Strings;

public final class HookLog {

    private static final String TAG = Strings.MODULE_TAG;

    private HookLog() {}

    public static void i(String msg) { Log.i(TAG, msg); }

    public static void d(String msg) { Log.d(TAG, msg); }

    public static void w(String msg) { Log.w(TAG, msg); }

    public static void e(String msg) { Log.e(TAG, msg); }

    public static void e(String msg, Throwable t) { Log.e(TAG, msg, t); }
}
