package com.yuuki.flyu.ui;

import android.graphics.Color;

public final class Colors {

    private Colors() {}

    // 主色调 - Periwinkle Lavender
    public static final int PRIMARY = 0x998090EE;                            // Soft Periwinkle, 60% opacity
    public static final int PRIMARY_DARK = Color.parseColor("#6070CC");      // Deep Periwinkle
    public static final int PRIMARY_LIGHT = Color.parseColor("#E0DCFA");     // Light Lavender

    // 强调色
    public static final int ACCENT = Color.parseColor("#7B8BE8");            // Periwinkle Accent

    // 文字颜色
    public static final int TEXT_PRIMARY = Color.parseColor("#212121");      // 87% black
    public static final int TEXT_SECONDARY = Color.parseColor("#757575");    // 54% black
    public static final int TEXT_DISABLED = Color.parseColor("#BDBDBD");     // 38% black
    public static final int TEXT_ON_PRIMARY = Color.WHITE;

    // 背景色
    public static final int BACKGROUND = Color.WHITE;
    public static final int SURFACE = Color.parseColor("#FAFAFA");
    public static final int INDICATOR = Color.parseColor("#E8EAFB");         // Light Lavender

    // 边框和分割线
    public static final int DIVIDER = Color.parseColor("#E0E0E0");
    public static final int BORDER = Color.parseColor("#D0CBF0");

    // 状态色
    public static final int SUCCESS = Color.parseColor("#4CAF50");
    public static final int SUCCESS_LIGHT = Color.parseColor("#E8F5E9");   // Green 50
    public static final int ERROR = Color.parseColor("#F44336");
    public static final int ERROR_LIGHT = Color.parseColor("#FFEBEE");     // Red 50
    public static final int WARNING = Color.parseColor("#FF9800");
    public static final int DANGER = Color.parseColor("#C62828");           // Dark Red 800

    // BottomNavigation - Glass Effect
    public static final int NAV_SELECTED = Color.parseColor("#6070CC");
    public static final int NAV_UNSELECTED = Color.parseColor("#8A8FA6");
    public static final int NAV_INDICATOR = Color.parseColor("#266070CC");
    public static final int NAV_SURFACE = Color.parseColor("#C2F4F2FF");
    public static final int NAV_GLASS_OVERLAY = Color.parseColor("#59FFFFFF");

    // BlurDialog EditText - iOS Frosted Glass
    public static final int BLUR_EDIT_BG = Color.parseColor("#1A787880");              // iOS tertiaryFill
    public static final int BLUR_EDIT_BG_FOCUSED = Color.parseColor("#29787880");      // 聚焦时稍深
    public static final int BLUR_EDIT_BORDER = Color.parseColor("#1F787880");          // iOS separator
    public static final int BLUR_EDIT_BORDER_FOCUSED = Color.parseColor("#7B8BE8");    // Periwinkle Accent
    public static final int BLUR_EDIT_TEXT = Color.parseColor("#F5F5F7");              // Apple 浅色文字
    public static final int BLUR_EDIT_HINT = Color.parseColor("#98989D");              // iOS secondaryLabel

    // ChipView - Tag Style
    public static final int CHIP_BG = Color.parseColor("#EDE8FB");                     // Light Lavender
    public static final int CHIP_BG_PRESSED = Color.parseColor("#DDD6FA");             // Medium Lavender

    // ButtonPro
    public static final int BUTTON_BG = Color.parseColor("#7B8BE8");                   // Periwinkle
    public static final int BUTTON_BG_PRESSED = Color.parseColor("#6070CC");           // Deep Periwinkle
    public static final int BUTTON_BG_DISABLED = Color.parseColor("#CCCCCC");
    public static final int BUTTON_TEXT_DISABLED = Color.parseColor("#999999");

    // 卡片内容文字
    public static final int CARD_TEXT_TITLE = Color.parseColor("#333333");             // 卡片标题
    public static final int CARD_TEXT_SUBTITLE = Color.parseColor("#8A8FA6");          // 卡片副标题/描述
    public static final int CARD_TEXT_BODY = Color.parseColor("#555566");              // 卡片正文/标签
    public static final int CARD_TEXT_LINK = Color.parseColor("#8090EE");              // 可点击文字/链接
    public static final int CARD_ICON_TINT = Color.parseColor("#555566");             // 卡片图标着色

    // 状态卡片
    public static final int STATUS_ACTIVE_BG = Color.parseColor("#8090EE");           // 激活背景
    public static final int STATUS_ACTIVE_DESC = Color.parseColor("#D0D4F0");         // 激活描述文字
    public static final int STATUS_INACTIVE_TITLE = Color.parseColor("#555555");      // 未激活标题
    public static final int STATUS_INACTIVE_ICON = Color.parseColor("#A0A8C0");       // 未激活图标

    // 社交/捐赠按钮
    public static final int SOCIAL_ICON_TINT = Color.parseColor("#555566");
    public static final int DONATE_ICON_TINT = Color.parseColor("#8090EE");

    // FAB
    public static final int FAB_ICON = Color.parseColor("#888888");

    // BlurDialog
    public static final int DIALOG_TITLE = Color.parseColor("#1A1A2E");           // 深色标题，不纯黑
    public static final int DIALOG_MESSAGE = Color.parseColor("#5A5A6E");         // 柔和正文
    public static final int DIALOG_BTN_CONFIRM = Color.parseColor("#6070CC");     // 确认按钮，清晰可见
    public static final int DIALOG_BTN_CANCEL = Color.parseColor("#8A8FA6");      // 取消按钮，低调

}
