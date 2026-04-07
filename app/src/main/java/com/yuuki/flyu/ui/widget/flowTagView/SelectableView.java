package com.yuuki.flyu.ui.widget.flowTagView;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class SelectableView extends TextView {

    private static final int DEFAULT_ANIMATION_DURATION = 200;
    private static final int DEFAULT_COLOR_SELECTED = Color.parseColor("#FF6200EE");
    private static final int DEFAULT_COLOR_UNSELECTED = Color.parseColor("#FFF5F5F5");
    private static final int DEFAULT_TEXT_COLOR_SELECTED = Color.BLACK;
    private static final int DEFAULT_TEXT_COLOR_UNSELECTED = Color.WHITE;
    private static final float DEFAULT_CORNER_RADIUS = 30f;
    private static final float DEFAULT_TEXT_SIZE = 16f;

    private boolean isSelected = false;
    private GradientDrawable selectedDrawable;
    private GradientDrawable unselectedDrawable;
    private int selectedColor;
    private int unselectedColor;
    private int selectedTextColor;
    private int unselectedTextColor;
    private float cornerRadius;
    private int animationDuration;

    public SelectableView(Context context) {
        super(context);
        init();
    }

    public SelectableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        selectedColor = DEFAULT_COLOR_SELECTED;
        unselectedColor = DEFAULT_COLOR_UNSELECTED;
        selectedTextColor = DEFAULT_TEXT_COLOR_SELECTED;
        unselectedTextColor = DEFAULT_TEXT_COLOR_UNSELECTED;
        cornerRadius = DEFAULT_CORNER_RADIUS;
        animationDuration = DEFAULT_ANIMATION_DURATION;

        unselectedDrawable = new GradientDrawable();
        unselectedDrawable.setColor(unselectedColor);
        unselectedDrawable.setCornerRadius(cornerRadius);

        selectedDrawable = new GradientDrawable();
        selectedDrawable.setColor(selectedColor);
        selectedDrawable.setCornerRadius(cornerRadius);

        setBackground(unselectedDrawable);

        setTextColor(unselectedTextColor);

        setGravity(Gravity.CENTER);

        setTextSize(DEFAULT_TEXT_SIZE);

        setSingleLine(true);
        setMaxLines(Integer.MAX_VALUE);

        setEllipsize(null);

        setPadding(22, 10, 22, 10);

        setClickable(true);
    }

    @Override
    public void setSelected(boolean selected) {
        if (isSelected != selected) {
            isSelected = selected;

            setBackground(selected ? selectedDrawable : unselectedDrawable);

            int startColor = selected ? unselectedTextColor : selectedTextColor;
            int endColor = selected ? selectedTextColor : unselectedTextColor;
            ObjectAnimator colorAnimator = ObjectAnimator.ofObject(
                    this,
                    "textColor",
                    new ArgbEvaluator(),
                    startColor,
                    endColor
            );
            colorAnimator.setDuration(animationDuration);
            colorAnimator.start();

            super.setSelected(selected);
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        unselectedDrawable.setCornerRadius(radius);
        selectedDrawable.setCornerRadius(radius);
        setBackground(isSelected ? selectedDrawable : unselectedDrawable);
    }

    public void setColors(int selectedColor, int unselectedColor, int selectedTextColor, int unselectedTextColor) {
        this.selectedColor = selectedColor;
        this.unselectedColor = unselectedColor;
        this.selectedTextColor = selectedTextColor;
        this.unselectedTextColor = unselectedTextColor;

        unselectedDrawable.setColor(unselectedColor);
        selectedDrawable.setColor(selectedColor);
        setBackground(isSelected ? selectedDrawable : unselectedDrawable);
        setTextColor(isSelected ? selectedTextColor : unselectedTextColor);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);

        requestLayout();
    }

    public void setAnimationDuration(int duration) {
        this.animationDuration = duration;
    }
}