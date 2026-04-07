package com.yuuki.flyu.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

public class Miscellaneous {

    public static void setFullImmersive(Activity activity) {
        Window window = activity.getWindow();

        // 设置状态栏和导航栏透明
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        // 组合所有需要的标志
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        // Android 8.0+ 添加导航栏图标颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        window.getDecorView().setSystemUiVisibility(flags);
    }

    public static void setViewTopPadding(View rootView, Context context){
        rootView.setPadding(
                rootView.getPaddingLeft(),
                rootView.getPaddingTop() + getStatusBarHeight(context),
                rootView.getPaddingRight(),
                rootView.getPaddingBottom()
        );

    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void initWindow(View rootView, Activity activity) {
        setFullImmersive(activity);
        setViewTopPadding(rootView, activity);
    }

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}

