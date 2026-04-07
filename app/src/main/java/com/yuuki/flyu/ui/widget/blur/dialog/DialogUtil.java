package com.yuuki.flyu.ui.widget.blur.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public final class DialogUtil {
    private static final String TAG = "DialogUtil";

    private DialogUtil() {
        throw new AssertionError();
    }

    public static void setGravity(Dialog dialog, int gravity) {

        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setGravity(gravity);
    }

    public static void setWith(Dialog dialog, int width) {


        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        View decorView = window.getDecorView();
        decorView.setPadding(0, decorView.getTop(), 0, decorView.getBottom());

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        window.setAttributes(lp);
    }

    public static void setHeight(Dialog dialog, int height) {

        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        View decorView = window.getDecorView();
        decorView.setPadding(decorView.getLeft(), 0, decorView.getRight(), 0);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = height;
        window.setAttributes(lp);
    }


    public static void setAnimations(Dialog dialog, int styleRes) {
        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setWindowAnimations(styleRes);
    }

    public static void setBackgroundDrawable(Dialog dialog, Drawable drawable) {

        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setBackgroundDrawable(drawable);
    }

    public static void setBackgroundDrawableResource(Dialog dialog, int drawableId) {
        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setBackgroundDrawableResource(drawableId);
    }

    public static BlurView setBlur(Dialog dialog) {
        final ViewGroup dialogRoot = (ViewGroup) dialog.getWindow().getDecorView();
        final BlurView blurView = new BlurView(dialog.getContext());
        dialogRoot.setBackgroundColor(Color.TRANSPARENT);
        final View v = dialogRoot.getChildAt(0);
        v.post(() -> {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
            dialogRoot.addView(blurView, 0, params);
            zoomInAnimation(v);
            alphaAnimation(blurView);
        });
        // Listen for layout changes to update BlurView size
        v.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int newWidth = right - left;
            int newHeight = bottom - top;
            if (blurView.getLayoutParams() != null) {
                blurView.getLayoutParams().width = newWidth;
                blurView.getLayoutParams().height = newHeight;
                blurView.requestLayout();
            }
        });
        return blurView;
    }

    public static void setBackgroundOverlay(Dialog dialog, float f) {
        dialog.getWindow().setDimAmount(f);
    }

    private static void zoomInAnimation(View v) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(v, "scaleX", 0.45f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(v, "scaleY", 0.45f, 1f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private static void alphaAnimation(View blurView) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(blurView, "alpha", 0f, 1f);
        alphaAnimator.setDuration(600);
        alphaAnimator.setInterpolator(new DecelerateInterpolator());
        alphaAnimator.start();
    }
}
