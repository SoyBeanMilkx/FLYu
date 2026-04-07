package com.yuuki.flyu.hook.utils;

import java.util.HashMap;
import java.util.Map;

public class TimeConvert {

    private static final Map<Integer, String> SHICHEN_MAP = new HashMap<>();

    static {
        SHICHEN_MAP.put(23, "子"); SHICHEN_MAP.put(0, "子");
        SHICHEN_MAP.put(1, "丑");  SHICHEN_MAP.put(2, "丑");
        SHICHEN_MAP.put(3, "寅");  SHICHEN_MAP.put(4, "寅");
        SHICHEN_MAP.put(5, "卯");  SHICHEN_MAP.put(6, "卯");
        SHICHEN_MAP.put(7, "辰");  SHICHEN_MAP.put(8, "辰");
        SHICHEN_MAP.put(9, "巳");  SHICHEN_MAP.put(10, "巳");
        SHICHEN_MAP.put(11, "午"); SHICHEN_MAP.put(12, "午");
        SHICHEN_MAP.put(13, "未"); SHICHEN_MAP.put(14, "未");
        SHICHEN_MAP.put(15, "申"); SHICHEN_MAP.put(16, "申");
        SHICHEN_MAP.put(17, "酉"); SHICHEN_MAP.put(18, "酉");
        SHICHEN_MAP.put(19, "戌"); SHICHEN_MAP.put(20, "戌");
        SHICHEN_MAP.put(21, "亥"); SHICHEN_MAP.put(22, "亥");
    }

    public static String timeToShiChen(int hour, int minute) {
        String shichen = SHICHEN_MAP.getOrDefault(hour, "未知");

        String chuZheng = (hour % 2 == 0) ? "正" : "初";

        String ke;
        if (minute < 15) {
            ke = "一";
        } else if (minute < 30) {
            ke = "二";
        } else if (minute < 45) {
            ke = "三";
        } else {
            ke = "四";
        }

        return shichen + chuZheng + ":" + ke + "刻";
    }
}
