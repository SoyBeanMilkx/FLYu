package com.yuuki.flyu.ui;

public final class Strings {
    private Strings() {}

    // ==================== Navigation ====================
    public static final String NAV_HOME = "Home";
    public static final String NAV_CONFIG = "Config";

    // ==================== Greetings ====================
    public static final String GREETING_MORNING   = "新的一天，从此刻开始 \uD83D\uDE0A";
    public static final String GREETING_NOON      = "午后时光，慢下来也不错 \uD83D\uDE0C";
    public static final String GREETING_AFTERNOON = "保持专注，将做得很好 \uD83D\uDCAA";
    public static final String GREETING_EVENING   = "日落时分，记得放松 \uD83E\uDD71";
    public static final String GREETING_NIGHT     = "忙碌了一天，辛苦啦 \uD83E\uDD17";
    public static final String GREETING_LATE_NIGHT = "夜深了，早点休息吧 \uD83D\uDE34";

    // ==================== Config carousel ====================
    public static final String CAROUSEL_1 = "把喜欢的样子，一点点拼凑出来";
    public static final String CAROUSEL_2 = "你看到的，刚好是你想看到的 \uD83D\uDE0A";
    public static final String CAROUSEL_3 = "好的设计，藏在细枝末节里";
    public static final String CAROUSEL_4 = "美，是秩序与混沌之间的一线平衡";
    public static final String CAROUSEL_5 = "广告位出租......";

    // ==================== Status card ====================
    public static final String STATUS_ACTIVATED = "模块已激活";
    public static final String STATUS_INACTIVE = "模块未激活";
    public static final String STATUS_INACTIVE_DESC = "请先授予root，并激活此模块～";
    public static final String STATUS_DESC_FORMAT = "%s %s (API %d)";

    // ==================== Device info card ====================
    public static final String LABEL_DEVICE_NAME = "设备名称";
    public static final String LABEL_SYSTEM_VERSION = "系统版本";
    public static final String LABEL_DEVICE_ARCH = "设备架构";
    public static final String LABEL_SYSTEMUI_VERSION = "SystemUI 版本";
    public static final String COPY = "复制";
    public static final String COPIED_TOAST = "已复制到剪贴板";
    public static final String DEVICE_INFO_COPY_FORMAT = "设备名称: %s\n系统版本: %s\n设备架构: %s\nSystemUI 版本: %s";
    public static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    public static final String UNKNOWN = "unknown";
    public static final String ANDROID_VERSION_FORMAT = "Android %s (API %d)";

    // ==================== Author card ====================
    public static final String AUTHOR_NAME = "Yuuki";
    public static final String AUTHOR_SIGNATURE = "what you seek is seeking you.";
    public static final String URL_GITHUB = "https://github.com/SoyBeanMilkx";
    public static final String URL_BLOG = "https://yuuki.cool/";
    public static final String URL_QQ = "mqqwpa://im/chat?chat_type=wpa&uin=2661293913";
    public static final String WE_CHAT = "lIIIIlIllIlIllIIIllI";
    public static final String DONATE_TOAST = "微信号复制到剪贴板啦，快添加好友给我转账吧~";

    // ==================== Thanks card ====================
    public static final String THANKS_TITLE = "特别鸣谢";
    public static final String THANKS_REPO_1 = "Flassers/Layout Inspect";
    public static final String THANKS_URL_1 = "https://github.com/Xposed-Modules-Repo/com.flass.layoutinspect";
    public static final String THANKS_REPO_2 = "libxposed/api";
    public static final String THANKS_URL_2 = "https://github.com/libxposed/api";
    public static final String THANKS_REPO_3 = "SoyBeanMilkx/BetterBar";
    public static final String THANKS_URL_3 = "https://github.com/SoyBeanMilkx/BetterBar";
    public static final String THANKS_REPO_4 = "QmDeve/AndroidLiquidGlassView";
    public static final String THANKS_URL_4 = "https://github.com/QmDeve/AndroidLiquidGlassView";
    public static final String THANKS_ARTICLE_1 = "Some beautiful icons";
    public static final String THANKS_ARTICLE_URL_1 = "https://www.iconfont.cn/";


    // ==================== Restart SystemUI ====================
    public static final String RESTART_SYSTEMUI_TITLE = "重启 SystemUI";
    public static final String RESTART_SYSTEMUI_MSG = "重启 SystemUI 可使 Hook 配置立即生效，设备会短暂黑屏后恢复。确定要重启吗？";
    public static final String RESTART_CONFIRM = "重启";
    public static final String RESTART_CANCEL = "取消";
    public static final String RESTART_FAIL_TOAST = "重启 SystemUI 失败，请检查 Root 权限";

    // ==================== Hook / Module ====================
    public static final String MODULE_TAG = "FLYu";
    public static final String SCOPE_REQUEST_FAILED_TOAST = "授权失败，请在框架中手动勾选 SystemUI";

    // ==================== StatusBar Items ====================
    public static final String SB_HIDE_WIFI_TITLE = "隐藏WiFi图标";
    public static final String SB_HIDE_WIFI_SUBTITLE = "隐藏状态栏中的WiFi信号图标";
    public static final String SB_HIDE_SIM_TITLE = "隐藏SIM卡图标";
    public static final String SB_HIDE_SIM_SUBTITLE = "隐藏状态栏中的移动信号图标";
    public static final String SB_HIDE_SIM1_TITLE = "隐藏SIM卡1";
    public static final String SB_HIDE_SIM1_SUBTITLE = "隐藏第一张SIM卡信号图标";
    public static final String SB_HIDE_SIM2_TITLE = "隐藏SIM卡2";
    public static final String SB_HIDE_SIM2_SUBTITLE = "隐藏第二张SIM卡信号图标";
    public static final String SB_HIDE_CHARGE_TITLE = "隐藏充电指示器";
    public static final String SB_HIDE_CHARGE_SUBTITLE = "隐藏状态栏中的充电闪电图标";
    public static final String SB_HIDE_ICONS_TITLE = "隐藏状态栏图标";
    public static final String SB_HIDE_ICONS_SUBTITLE = "选择要隐藏的状态栏图标";

    // ==================== Clock Items ====================
    public static final String CLK_BOLD_TITLE = "加粗时钟";
    public static final String CLK_BOLD_SUBTITLE = "使状态栏时钟文本加粗显示";
    public static final String CLK_SECONDS_TITLE = "显示秒数";
    public static final String CLK_SECONDS_SUBTITLE = "在时钟中显示精确到秒";
    public static final String CLK_12HOUR_TITLE = "12小时制";
    public static final String CLK_12HOUR_SUBTITLE = "使用 12 小时制并显示 AM/PM";
    public static final String CLK_12HOUR_CN_TITLE = "使用中文";
    public static final String CLK_12HOUR_CN_SUBTITLE = "上午/下午 替代 AM/PM";
    public static final String CLK_DAY_OF_WEEK_TITLE = "显示星期";
    public static final String CLK_DAY_OF_WEEK_SUBTITLE = "在时钟旁显示星期缩写";
    public static final String CLK_DAY_OF_WEEK_CN_TITLE = "使用中文";
    public static final String CLK_DAY_OF_WEEK_CN_SUBTITLE = "星期一 替代 MON";
    public static final String CLK_SHICHEN_TITLE = "时辰模式";
    public static final String CLK_SHICHEN_SUBTITLE = "显示中国传统时辰（覆盖其他格式设置）";
    public static final String CLK_QS_SECONDS_TITLE = "控制中心时钟秒数";
    public static final String CLK_QS_SECONDS_SUBTITLE = "下拉控制中心的时钟显示精确到秒";

    // ==================== Misc Items ====================
    public static final String MISC_HIDE_LOW_SPEED_TITLE = "网速低时隐藏指示器";
    public static final String MISC_HIDE_LOW_SPEED_SUBTITLE = "当网速小于 200KB/s 时隐藏指示器";
    public static final String MISC_BRIGHTNESS_TITLE = "滑动状态栏调节亮度";
    public static final String MISC_BRIGHTNESS_SUBTITLE = "在状态栏上左右滑动即可快速调节屏幕亮度";
    public static final String MISC_DOUBLE_TAP_TITLE = "双击状态栏锁屏";
    public static final String MISC_DOUBLE_TAP_SUBTITLE = "双击状态栏快速锁定屏幕";

    // ==================== LockScreen Items ====================
    public static final String LS_HIDE_UDFPS_TITLE = "隐藏锁屏指纹图标";
    public static final String LS_HIDE_UDFPS_SUBTITLE = "隐藏锁屏界面的屏下指纹图标";
    public static final String LS_HIDE_CARRIER_TITLE = "隐藏锁屏顶部运营商";
    public static final String LS_HIDE_CARRIER_SUBTITLE = "隐藏锁屏顶部的运营商名称";
    public static final String LS_HIDE_FLASHLIGHT_TITLE = "隐藏锁屏底部手电筒";
    public static final String LS_HIDE_FLASHLIGHT_SUBTITLE = "隐藏锁屏底部的手电筒按钮";
    public static final String LS_HIDE_CAMERA_TITLE = "隐藏锁屏底部相机";
    public static final String LS_HIDE_CAMERA_SUBTITLE = "隐藏锁屏底部的相机按钮";

    // ==================== HwMonitor Items ====================
    public static final String HW_MONITOR_TITLE = "硬件监控";
    public static final String HW_MONITOR_SUBTITLE = "在状态栏显示硬件实时数据（需要 Root）";
    public static final String HW_CPU_TEMP_TITLE = "CPU 温度";
    public static final String HW_CPU_TEMP_SUBTITLE = "显示 CPU 核心温度";
    public static final String HW_BAT_TEMP_TITLE = "电池温度";
    public static final String HW_BAT_TEMP_SUBTITLE = "显示电池温度";
    public static final String HW_BAT_POWER_TITLE = "充放电功率";
    public static final String HW_BAT_POWER_SUBTITLE = "显示电池充放电功率 (W)";
    public static final String HW_MEM_USAGE_TITLE = "运存占用";
    public static final String HW_MEM_USAGE_SUBTITLE = "显示已用/总运存 (GB)";
    public static final String HW_SHOW_LEFT_TITLE = "显示在左侧";
    public static final String HW_SHOW_LEFT_SUBTITLE = "显示在状态栏左半边 (时钟后)";

    // ==================== Font ====================
    public static final String FONT_ROBOTO_FLEX = "/system/fonts/RobotoFlex-Regular.ttf";
    public static final String FONT_ROBOTO = "/system/fonts/Roboto-Regular.ttf";
}
