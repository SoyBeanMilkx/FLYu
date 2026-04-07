package com.yuuki.flyu.utils;

import android.graphics.Typeface;
import android.os.Build;

import com.yuuki.flyu.ui.Strings;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class FontHelper {

    private FontHelper() {}

    // 按优先级尝试加载的系统字体
    private static final String[] PREFERRED_FONTS = {
            Strings.FONT_ROBOTO_FLEX,
            Strings.FONT_ROBOTO,
    };

    /**
     * 替换全局默认字体为系统字体
     * 在 Activity.onCreate 的 setContentView 之前调用
     */
    public static void applySystemFont() {
        String fontPath = findPreferredFontPath();
        if (fontPath == null) return;

        Typeface regular = Typeface.createFromFile(fontPath);
        Typeface medium = buildWeight(fontPath, 500, regular);
        Typeface bold = buildWeight(fontPath, 700, regular);

        replaceDefaultTypeface("DEFAULT", regular);
        replaceDefaultTypeface("DEFAULT_BOLD", bold);
        replaceDefaultTypeface("SANS_SERIF", regular);
        replaceDefaultTypeface("SERIF", regular);
        replaceDefaultTypeface("MONOSPACE", regular);

        // 替换 sSystemFontMap（API 21+）
        try {
            Field mapField = Typeface.class.getDeclaredField("sSystemFontMap");
            mapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Typeface> map = (Map<String, Typeface>) mapField.get(null);
            if (map != null) {
                map = new HashMap<>(map);
                map.put("sans-serif", regular);
                map.put("sans-serif-light", buildWeight(fontPath, 300, regular));
                map.put("sans-serif-medium", medium);
                map.put("sans-serif-bold", bold);
                map.put("sans-serif-black", buildWeight(fontPath, 900, regular));
                map.put("sans-serif-display", buildWeight(fontPath, 900, regular));
                mapField.set(null, map);
            }
        } catch (Exception ignored) {}
    }

    /**
     * 用 Typeface.Builder 设置可变字体字重轴，API 26 以下回退伪加粗
     */
    private static Typeface buildWeight(String path, int weight, Typeface fallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return new Typeface.Builder(path)
                        .setFontVariationSettings("'wght' " + weight)
                        .build();
            } catch (Exception ignored) {}
        }
        // 回退：700 以上用伪加粗
        if (weight >= 700) {
            return Typeface.create(fallback, Typeface.BOLD);
        }
        return fallback;
    }

    private static String findPreferredFontPath() {
        for (String path : PREFERRED_FONTS) {
            if (new File(path).exists()) return path;
        }
        return null;
    }

    private static void replaceDefaultTypeface(String fieldName, Typeface typeface) {
        try {
            Field field = Typeface.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, typeface);
        } catch (Exception ignored) {}
    }
}
