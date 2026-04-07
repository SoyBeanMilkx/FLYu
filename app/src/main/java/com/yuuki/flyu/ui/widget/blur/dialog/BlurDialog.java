package com.yuuki.flyu.ui.widget.blur.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.utils.Miscellaneous;

public class BlurDialog {

    private final Context context;
    private String title;
    private String message;
    private View customView;
    private String positiveText;
    private String negativeText;
    private Integer negativeTextColor;
    private OnClickListener positiveListener;
    private OnClickListener negativeListener;
    private boolean cancelable = true;
    private float blurRadius = 50f;
    private float downscaleFactor = 2f;
    private int overlayColor = Color.parseColor("#94FFFFFF");
    private float cornerRadius = 40f;
    private float dimAmount = 0.2f;

    public interface OnClickListener {
        void onClick(AlertDialog dialog);
    }

    private BlurDialog(Context context) {
        this.context = context;
    }

    public static BlurDialog with(Context context) {
        return new BlurDialog(context);
    }

    public BlurDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public BlurDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public BlurDialog setCustomView(View view) {
        this.customView = view;
        return this;
    }

    public BlurDialog setPositiveButton(String text, OnClickListener listener) {
        this.positiveText = text;
        this.positiveListener = listener;
        return this;
    }

    public BlurDialog setNegativeButton(String text, OnClickListener listener) {
        this.negativeText = text;
        this.negativeListener = listener;
        return this;
    }

    public BlurDialog setNegativeButtonColor(int color) {
        this.negativeTextColor = color;
        return this;
    }

    public BlurDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public BlurDialog setBlurRadius(float radius) {
        this.blurRadius = radius;
        return this;
    }

    public BlurDialog setDownscaleFactor(float factor) {
        this.downscaleFactor = factor;
        return this;
    }

    public BlurDialog setOverlayColor(int color) {
        this.overlayColor = color;
        return this;
    }

    public BlurDialog setCornerRadius(float radius) {
        this.cornerRadius = radius;
        return this;
    }

    public BlurDialog setDimAmount(float amount) {
        this.dimAmount = amount;
        return this;
    }

    public AlertDialog show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (customView != null) {
            builder.setView(customView);
        } else if (title != null || message != null) {
            builder.setView(buildContentView());
        }

        if (positiveText != null) {
            builder.setPositiveButton(positiveText, null);
        }

        if (negativeText != null) {
            builder.setNegativeButton(negativeText, null);
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.setCancelable(cancelable);

        DialogUtil.setGravity(dialog, Gravity.CENTER);

        int width = context.getResources().getDisplayMetrics().widthPixels - Miscellaneous.dp2px(context, 70);
        DialogUtil.setWith(dialog, width);

        BlurView blurView = DialogUtil.setBlur(dialog);
        blurView.setBlurRadius(blurRadius);
        blurView.setDownscaleFactor(downscaleFactor);
        blurView.setOverlayColor(overlayColor);
        blurView.setOutLineRound(cornerRadius);

        DialogUtil.setBackgroundOverlay(dialog, dimAmount);

        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(Colors.DIALOG_BTN_CONFIRM);
            positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
            if (positiveListener != null) {
                positiveButton.setOnClickListener(v -> positiveListener.onClick(dialog));
            }
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(negativeTextColor != null ? negativeTextColor : Colors.DIALOG_BTN_CANCEL);
            if (negativeListener != null) {
                negativeButton.setOnClickListener(v -> negativeListener.onClick(dialog));
            }
        }

        return dialog;
    }

    private View buildContentView() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int hPad = Miscellaneous.dp2px(context, 24);
        int tPad = Miscellaneous.dp2px(context, 22);
        int bPad = Miscellaneous.dp2px(context, 8);
        layout.setPadding(hPad, tPad, hPad, bPad);

        if (title != null) {
            TextView titleView = new TextView(context);
            titleView.setText(title);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            titleView.setTextColor(Colors.DIALOG_TITLE);
            layout.addView(titleView);
        }

        if (message != null) {
            TextView msgView = new TextView(context);
            msgView.setText(message);
            msgView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            msgView.setTextColor(Colors.DIALOG_MESSAGE);
            msgView.setLineSpacing(Miscellaneous.dp2px(context, 3), 1f);
            if (title != null) {
                LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                msgParams.topMargin = Miscellaneous.dp2px(context, 10);
                msgView.setLayoutParams(msgParams);
            }
            layout.addView(msgView);
        }

        return layout;
    }
}
