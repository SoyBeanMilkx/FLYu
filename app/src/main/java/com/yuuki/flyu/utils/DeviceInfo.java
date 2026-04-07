package com.yuuki.flyu.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.yuuki.flyu.ui.Strings;

public class DeviceInfo {

    public static String getDeviceName() {
        return Build.BRAND + " " + Build.MODEL;
    }

    public static String getSystemVersion() {
        return String.format(Strings.ANDROID_VERSION_FORMAT, Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
    }

    public static String getDeviceArch() {
        return Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI;
    }

    public static String getSystemUiVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(Strings.SYSTEMUI_PACKAGE, 0);
            return info.versionName != null ? info.versionName : Strings.UNKNOWN;
        } catch (PackageManager.NameNotFoundException e) {
            return Strings.UNKNOWN;
        }
    }
}
