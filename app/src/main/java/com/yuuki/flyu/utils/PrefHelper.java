package com.yuuki.flyu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yuuki.flyu.App;
import com.yuuki.flyu.PrefConst;

import java.util.Map;
import java.util.Set;

import io.github.libxposed.service.XposedService;

public final class PrefHelper {

    private static final String TAG = "FLYu_Pref";

    private PrefHelper() {}

    // ── 读取（从本地） ──

    public static boolean readBoolean(Context context, String key, boolean defValue) {
        return getLocal(context).getBoolean(key, defValue);
    }

    public static Set<String> readStringSet(Context context, String key, Set<String> defValues) {
        return getLocal(context).getStringSet(key, defValues);
    }

    // ── 写入（双写本地 + 远程） ──

    public static void writeBoolean(Context context, String key, boolean value) {
        getLocal(context).edit().putBoolean(key, value).apply();
        SharedPreferences remote = getRemote();
        if (remote != null) {
            remote.edit().putBoolean(key, value).apply();
        }
    }

    public static void writeStringSet(Context context, String key, Set<String> value) {
        getLocal(context).edit().putStringSet(key, value).apply();
        SharedPreferences remote = getRemote();
        if (remote != null) {
            remote.edit().putStringSet(key, value).apply();
        }
    }

    // ── 同步（remote → local，service 连接时调用） ──

    @SuppressWarnings("unchecked")
    public static void syncFromRemote(Context context, XposedService service) {
        try {
            SharedPreferences remote = service.getRemotePreferences(PrefConst.PREF_NAME);
            Map<String, ?> all = remote.getAll();
            if (all == null || all.isEmpty()) return;

            SharedPreferences.Editor editor = getLocal(context).edit();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                Object v = entry.getValue();
                if (v instanceof Boolean)       editor.putBoolean(entry.getKey(), (Boolean) v);
                else if (v instanceof String)   editor.putString(entry.getKey(), (String) v);
                else if (v instanceof Integer)  editor.putInt(entry.getKey(), (Integer) v);
                else if (v instanceof Long)     editor.putLong(entry.getKey(), (Long) v);
                else if (v instanceof Float)    editor.putFloat(entry.getKey(), (Float) v);
                else if (v instanceof Set)      editor.putStringSet(entry.getKey(), (Set<String>) v);
            }
            editor.apply();
        } catch (Throwable t) {
            Log.e(TAG, "syncFromRemote failed", t);
        }
    }

    // ── 内部方法 ──

    private static SharedPreferences getLocal(Context context) {
        return context.getSharedPreferences(PrefConst.PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getRemote() {
        XposedService service = App.getService();
        return service != null ? service.getRemotePreferences(PrefConst.PREF_NAME) : null;
    }
}
