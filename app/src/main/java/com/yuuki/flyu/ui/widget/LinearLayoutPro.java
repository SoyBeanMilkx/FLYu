package com.yuuki.flyu.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yuuki.flyu.ui.widget.blur.liquidglass.LiquidGlassView;

public class LinearLayoutPro extends LinearLayout {

    private int backgroundColor = Color.WHITE;
    private int pressedBackgroundColor = 0xFFF5F5F5;
    private int borderColor = 0xFFE0E0E0;
    private int borderWidth;
    private float cornerRadius;
    private boolean enableRipple = true;
    private int rippleColor = 0x1A000000;

    private int contentPadding;
    private int contentPaddingLeft = -1;
    private int contentPaddingTop = -1;
    private int contentPaddingRight = -1;
    private int contentPaddingBottom = -1;

    public LinearLayoutPro(Context context) {
        super(context);
        init(context, null);
    }

    public LinearLayoutPro(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LinearLayoutPro(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        borderWidth = dp2px(context, 1);
        cornerRadius = dp2px(context, 8);
        contentPadding = dp2px(context, 16);

        setOrientation(VERTICAL);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, new int[]{
                    android.R.attr.orientation,
                    android.R.attr.background,
                    android.R.attr.padding,
                    android.R.attr.paddingLeft,
                    android.R.attr.paddingTop,
                    android.R.attr.paddingRight,
                    android.R.attr.paddingBottom
            });

            int orientation = a.getInt(0, VERTICAL);
            setOrientation(orientation);

            int padding = a.getDimensionPixelSize(2, -1);
            if (padding != -1) {
                contentPadding = padding;
            }

            contentPaddingLeft = a.getDimensionPixelSize(3, contentPaddingLeft);
            contentPaddingTop = a.getDimensionPixelSize(4, contentPaddingTop);
            contentPaddingRight = a.getDimensionPixelSize(5, contentPaddingRight);
            contentPaddingBottom = a.getDimensionPixelSize(6, contentPaddingBottom);

            a.recycle();
        }

        updateBackground();
        updatePadding();
    }

    private void updateBackground() {
        GradientDrawable backgroundDrawable = createBackgroundDrawable(backgroundColor);

        if (isClickable() && enableRipple) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable rippleDrawable = new RippleDrawable(
                        android.content.res.ColorStateList.valueOf(rippleColor),
                        backgroundDrawable,
                        createBackgroundDrawable(backgroundColor)
                );
                setBackground(rippleDrawable);
            } else {
                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                        createBackgroundDrawable(pressedBackgroundColor));
                stateListDrawable.addState(new int[]{}, backgroundDrawable);
                setBackground(stateListDrawable);
            }
        } else {
            setBackground(backgroundDrawable);
        }
    }

    private GradientDrawable createBackgroundDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(cornerRadius);

        if (borderWidth > 0) {
            drawable.setStroke(borderWidth, borderColor);
        }

        return drawable;
    }

    private void updatePadding() {
        int left = contentPaddingLeft != -1 ? contentPaddingLeft : contentPadding;
        int top = contentPaddingTop != -1 ? contentPaddingTop : contentPadding;
        int right = contentPaddingRight != -1 ? contentPaddingRight : contentPadding;
        int bottom = contentPaddingBottom != -1 ? contentPaddingBottom : contentPadding;

        super.setPadding(left, top, right, bottom);
    }

    public void setCardBackgroundColor(int color) {
        this.backgroundColor = color;
        updateBackground();
    }

    public void setPressedBackgroundColor(int color) {
        this.pressedBackgroundColor = color;
        updateBackground();
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
        updateBackground();
    }

    public void setBorderWidth(float widthDp) {
        this.borderWidth = dp2px(getContext(), widthDp);
        updateBackground();
    }

    public void setCornerRadius(float radiusDp) {
        this.cornerRadius = dp2px(getContext(), radiusDp);
        updateBackground();
    }

    public void setContentPadding(int paddingDp) {
        this.contentPadding = dp2px(getContext(), paddingDp);
        contentPaddingLeft = contentPaddingTop = contentPaddingRight = contentPaddingBottom = -1;
        updatePadding();
    }

    public void setContentPadding(int leftDp, int topDp, int rightDp, int bottomDp) {
        contentPaddingLeft = dp2px(getContext(), leftDp);
        contentPaddingTop = dp2px(getContext(), topDp);
        contentPaddingRight = dp2px(getContext(), rightDp);
        contentPaddingBottom = dp2px(getContext(), bottomDp);
        updatePadding();
    }

    public void setRippleEnabled(boolean enabled) {
        this.enableRipple = enabled;
        updateBackground();
    }

    public void setRippleColor(int color) {
        this.rippleColor = color;
        updateBackground();
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        updateBackground();
    }

    public void removeBorder() {
        this.borderWidth = 0;
        updateBackground();
    }

    private LiquidGlassView liquidGlass;
    private View glassOverlay;
    private static final int DEFAULT_GLASS_OVERLAY_COLOR = 0xB3FFFFFF;

    public void enableLiquidGlass(ViewGroup source) {
        enableLiquidGlass(source, DEFAULT_GLASS_OVERLAY_COLOR);
    }

    public void enableLiquidGlass(ViewGroup source, int overlayColor) {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) return;

        int index = parent.indexOfChild(this);
        ViewGroup.LayoutParams lp = getLayoutParams();
        parent.removeView(this);

        FrameLayout wrapper = new FrameLayout(getContext());
        wrapper.setOutlineProvider(new CardOutlineProvider(cornerRadius));
        wrapper.setClipToOutline(true);

        liquidGlass = new LiquidGlassView(getContext());
        liquidGlass.setCornerRadius(cornerRadius);
        liquidGlass.setBlurRadius(40f);
        liquidGlass.setDispersion(0.8f);
        liquidGlass.setRefractionHeight(48f);
        liquidGlass.setRefractionOffset(96f);
        wrapper.addView(liquidGlass, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        glassOverlay = new View(getContext());
        GradientDrawable overlayBg = new GradientDrawable();
        overlayBg.setColor(overlayColor);
        overlayBg.setCornerRadius(cornerRadius);
        glassOverlay.setBackground(overlayBg);
        wrapper.addView(glassOverlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        this.backgroundColor = Color.TRANSPARENT;
        updateBackground();
        setClipToOutline(true);
        wrapper.addView(this, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        parent.addView(wrapper, index, lp);

        liquidGlass.bind(source);
    }

    public void setGlassOverlayColor(int color) {
        if (glassOverlay != null) {
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(color);
            bg.setCornerRadius(cornerRadius);
            glassOverlay.setBackground(bg);
        }
    }

    public void setLiquidGlassVisible(boolean visible) {
        if (liquidGlass != null) liquidGlass.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (glassOverlay != null) glassOverlay.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible) {
            updateBackground();
        } else {
            this.backgroundColor = Color.TRANSPARENT;
            updateBackground();
        }
    }

    private int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    private static class CardOutlineProvider extends android.view.ViewOutlineProvider {
        private float radius;

        CardOutlineProvider(float radius) {
            this.radius = radius;
        }

        @Override
        public void getOutline(View view, android.graphics.Outline outline) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        }
    }
}