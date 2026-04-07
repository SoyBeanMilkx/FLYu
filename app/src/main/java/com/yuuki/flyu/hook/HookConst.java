package com.yuuki.flyu.hook;

public final class HookConst {

    private HookConst() {}

    // ── Hook 优先级（数值越大越先执行） ──
    public static final int PRIORITY_LOW = 50;
    public static final int PRIORITY_NORMAL = 100;
    public static final int PRIORITY_HIGH = 150;
    public static final int PRIORITY_OVERRIDE = Integer.MAX_VALUE;

    // ── 目标包名 ──
    public static final String SYSTEMUI_PACKAGE = "com.android.systemui";

    // ── 目标类名 ──
    public static final String CLOCK_CLASS = "com.android.systemui.statusbar.policy.Clock";
    public static final String STATUS_BAR_ICON_VIEW_CLASS = "com.android.systemui.statusbar.StatusBarIconView";
    public static final String STATUS_BAR_ICON_CLASS = "com.android.internal.statusbar.StatusBarIcon";
    public static final String MOBILE_VIEWMODEL_CLASS = "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.LocationBasedMobileViewModel";
    public static final String FLYME_WIFI_VIEW_CLASS = "com.flyme.systemui.statusbar.net.wifi.FlymeStatusBarWifiView";
    public static final String FLYME_WIFI_ICON_STATE_CLASS = "com.flyme.systemui.statusbar.net.wifi.WifiIconState";
    public static final String FLYME_BATTERY_VIEW_CLASS = "com.flyme.statusbar.battery.FlymeBatteryMeterView";
    public static final String CONNECTION_RATE_VIEW_CLASS = "com.flyme.statusbar.connectionRateView.ConnectionRateView";
    public static final String PHONE_STATUS_BAR_VIEW_CLASS = "com.android.systemui.statusbar.phone.PhoneStatusBarView";
    public static final String UDFPS_VIEW_CLASS = "com.android.systemui.biometrics.UdfpsView";
    public static final String SPLIT_CLOCK_VIEW_CLASS = "com.flyme.systemui.statusbar.phone.SplitClockView";
    public static final String CARRIER_TEXT_CONTROLLER_CLASS = "com.android.keyguard.CarrierTextController";
    public static final String MZ_BOTTOM_AREA_VIEW_CLASS = "com.flyme.systemui.affordance.MZKeyguardBottomAreaView";
}
