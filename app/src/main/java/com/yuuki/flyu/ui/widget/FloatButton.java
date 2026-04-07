package com.yuuki.flyu.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yuuki.flyu.ui.widget.blur.liquidglass.LiquidGlassView;
import com.yuuki.flyu.ui.widget.blur.liquidglass.SpringAnimator;

public class FloatButton extends FrameLayout {

    private LiquidGlassView glassView;
    private ImageView iconView;
    private final float cornerRadius;
    private SpringAnimator springScale;
    private final Handler longPressHandler = new Handler(Looper.getMainLooper());
    private boolean longPressTriggered;

    public FloatButton(Context context) {
        this(context, null);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cornerRadius = dp(16);
        init();
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);

        setElevation(dp(10));
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(android.view.View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
        setClipToOutline(true);

        glassView = new LiquidGlassView(getContext());
        glassView.setCornerRadius(cornerRadius);
        glassView.setBlurRadius(50f);
        glassView.setDispersion(1.0f);
        glassView.setRefractionHeight(50f);
        glassView.setRefractionOffset(120f);

        glassView.setElasticEnabled(true);
        glassView.setTouchEffectEnabled(true);
        addView(glassView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        iconView = new ImageView(getContext());
        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int iconPad = (int) dp(14);
        iconView.setPadding(iconPad, iconPad, iconPad, iconPad);
        LayoutParams iconLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        iconLp.gravity = Gravity.CENTER;
        addView(iconView, iconLp);

        springScale = new SpringAnimator(this, (v, val) -> {
            v.setScaleX(val);
            v.setScaleY(val);
        }, 1f);
        springScale.setStiffness(150f);
        springScale.setDampingRatio(0.4f);
    }

    public void setIcon(int resId) {
        iconView.setImageResource(resId);
    }

    public void setIconColor(int color) {
        iconView.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void bindGlassSource(ViewGroup source) {
        glassView.bind(source);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 让 LiquidGlassView 处理触摸光效
        glassView.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                springScale.animateToFinalPosition(0.86f);
                longPressTriggered = false;
                longPressHandler.postDelayed(() -> {
                    longPressTriggered = true;
                    performLongClick();
                }, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_UP:
                springScale.animateToFinalPosition(1f);
                longPressHandler.removeCallbacksAndMessages(null);
                if (!longPressTriggered && isClickable()) performClick();
                break;
            case MotionEvent.ACTION_CANCEL:
                springScale.animateToFinalPosition(1f);
                longPressHandler.removeCallbacksAndMessages(null);
                break;
        }
        return true;
    }

    private float dp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
