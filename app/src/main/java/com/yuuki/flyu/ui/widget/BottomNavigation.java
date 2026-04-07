package com.yuuki.flyu.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.yuuki.flyu.ui.widget.blur.liquidglass.LiquidGlassView;
import com.yuuki.flyu.ui.widget.blur.liquidglass.SpringAnimator;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigation extends ViewGroup {

    private List<NavigationItem> items = new ArrayList<>();
    private int selectedIndex = -1;
    private int previousSelectedIndex = -1;
    private OnItemSelectedListener listener;

    private Paint textPaint;
    private Paint dividerPaint;
    private Paint indicatorPaint;
    private Paint ripplePaint;

    private float textSize;
    private int selectedColor;
    private int unselectedColor;
    private int indicatorColor;
    private boolean showDivider;
    private boolean showLabels = true;
    private int iconTextGap;

    private RectF indicatorRect = new RectF();
    private float indicatorCornerRadius;
    private float indicatorPaddingHorizontal;
    private float indicatorPaddingVertical;
    private float indicatorProgress = 0f;
    private float indicatorX = 0f;
    private float indicatorY = 0f;
    private boolean indicatorWrapText = true;

    private List<Rect> itemRects = new ArrayList<>();
    private List<ItemAnimationState> animationStates = new ArrayList<>();
    private ValueAnimator selectionAnimator;
    private ValueAnimator indicatorAnimator;
    private long animationDuration = 300;
    private boolean enableAnimation = true;

    private float rippleRadius = 0f;
    private float rippleX = 0f;
    private float rippleY = 0f;
    private ValueAnimator rippleAnimator;

    // liquid glass
    private static final float GLASS_BLUR_RADIUS = 10f;
    private static final float GLASS_DISPERSION = 1.2f;
    private static final float GLASS_REFRACTION_HEIGHT = 46f;
    private static final float GLASS_REFRACTION_OFFSET = 96f;
    private static final int GLASS_OVERLAY_COLOR = 0x60FFFFFF;
    private static final float SHADOW_ELEVATION_DP = 16f;
    private static final float NAV_HEIGHT_DP = 64f;
    private static final float SPRING_PRESS_SCALE = 0.88f;
    private static final float SPRING_STIFFNESS = 150f;
    private static final float SPRING_DAMPING = 0.4f;
    private LiquidGlassView liquidGlassBackground;
    private View glassOverlay;
    private GradientDrawable glassOverlayDrawable;
    private GradientDrawable shadowBackgroundDrawable;
    private SpringAnimator springScale;

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(2f);
    private AccelerateDecelerateInterpolator smoothInterpolator = new AccelerateDecelerateInterpolator();

    public BottomNavigation(Context context) {
        this(context, null);
    }

    public BottomNavigation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void bindContentSource(ViewGroup source) {
        if (liquidGlassBackground != null) {
            liquidGlassBackground.bind(source);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) getParent();
            parent.setClipChildren(false);
            parent.setClipToPadding(false);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

        int navHeight = dpToPx(NAV_HEIGHT_DP);
        float cornerRadius = navHeight / 2f;

        liquidGlassBackground = new LiquidGlassView(context);
        liquidGlassBackground.setCornerRadius(cornerRadius);
        liquidGlassBackground.setBlurRadius(GLASS_BLUR_RADIUS);
        liquidGlassBackground.setDispersion(GLASS_DISPERSION);
        liquidGlassBackground.setRefractionHeight(GLASS_REFRACTION_HEIGHT);
        liquidGlassBackground.setRefractionOffset(GLASS_REFRACTION_OFFSET);
        addView(liquidGlassBackground);

        glassOverlay = new View(context);
        glassOverlayDrawable = new GradientDrawable();
        glassOverlayDrawable.setColor(GLASS_OVERLAY_COLOR);
        glassOverlayDrawable.setCornerRadius(cornerRadius);
        glassOverlay.setBackground(glassOverlayDrawable);
        addView(glassOverlay);

        shadowBackgroundDrawable = new GradientDrawable();
        shadowBackgroundDrawable.setColor(0xFFFFFFFF);
        shadowBackgroundDrawable.setCornerRadius(cornerRadius);
        setBackground(shadowBackgroundDrawable);
        setClipToOutline(true);
        setElevation(dpToPx(SHADOW_ELEVATION_DP));

        springScale = new SpringAnimator(this, (v, val) -> {
            v.setScaleX(val);
            v.setScaleY(val);
        }, 1f);
        springScale.setStiffness(SPRING_STIFFNESS);
        springScale.setDampingRatio(SPRING_DAMPING);

        // 默认值 - Material 淡蓝色主题
        textSize = dpToPx(12);
        selectedColor = Color.parseColor("#6070CC");        // Deep Periwinkle
        unselectedColor = Color.parseColor("#8A8FA6");      // 柔和灰紫
        indicatorColor = Color.parseColor("#E8EAFB");       // Light Lavender
        showDivider = false; // MD3 默认不显示分割线
        iconTextGap = dpToPx(4);
        indicatorCornerRadius = dpToPx(16);
        indicatorPaddingHorizontal = dpToPx(12);
        indicatorPaddingVertical = dpToPx(4);
        indicatorWrapText = true;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        dividerPaint = new Paint();
        dividerPaint.setColor(Color.parseColor("#E0E0E0"));
        dividerPaint.setStrokeWidth(dpToPx(0.5f));

        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStyle(Paint.Style.FILL);

        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setColor(selectedColor);
        ripplePaint.setAlpha(30);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int defaultHeight = dpToPx(NAV_HEIGHT_DP);
        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = defaultHeight;
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);

        int childW = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int childH = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        if (liquidGlassBackground != null) liquidGlassBackground.measure(childW, childH);
        if (glassOverlay != null) glassOverlay.measure(childW, childH);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l, h = b - t;
        if (liquidGlassBackground != null) liquidGlassBackground.layout(0, 0, w, h);
        if (glassOverlay != null) glassOverlay.layout(0, 0, w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateItemRects();
    }

    private void updateItemRects() {
        itemRects.clear();
        animationStates.clear();

        if (items.isEmpty()) return;

        int itemWidth = getWidth() / items.size();
        int height = getHeight();

        for (int i = 0; i < items.size(); i++) {
            Rect rect = new Rect(
                    i * itemWidth, 0,
                    (i + 1) * itemWidth, height
            );
            itemRects.add(rect);

            ItemAnimationState state = new ItemAnimationState();
            if (i == selectedIndex) {
                state.scale = 1.0f;
                state.iconScale = 1.0f;
                state.labelAlpha = 1.0f;
                state.labelScale = 1.0f;
                state.iconPositionProgress = 1.0f;
            } else {
                state.labelAlpha = 0f;
                state.labelScale = 0.8f;
                state.iconPositionProgress = 0f;
            }
            animationStates.add(state);
        }

        if (selectedIndex >= 0 && selectedIndex < itemRects.size()) {
            updateIndicatorPosition(selectedIndex, false);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (showDivider) {
            canvas.drawLine(0, 0, getWidth(), 0, dividerPaint);
        }

        if (rippleRadius > 0) {
            canvas.drawCircle(rippleX, rippleY, rippleRadius, ripplePaint);
        }

        if (indicatorProgress > 0 && selectedIndex >= 0) {
            indicatorPaint.setColor(indicatorColor);
            indicatorPaint.setAlpha((int) (Color.alpha(indicatorColor) * indicatorProgress));
            canvas.drawRoundRect(indicatorRect, indicatorCornerRadius, indicatorCornerRadius, indicatorPaint);
        }

        for (int i = 0; i < items.size(); i++) {
            drawItem(canvas, i);
        }
    }

    private void drawItem(Canvas canvas, int index) {
        if (index >= itemRects.size() || index >= animationStates.size()) return;

        NavigationItem item = items.get(index);
        Rect rect = itemRects.get(index);
        ItemAnimationState animState = animationStates.get(index);
        boolean isSelected = index == selectedIndex;

        canvas.save();

        float baseIconSize = dpToPx(24);
        float iconSize = baseIconSize * animState.iconScale;

        float iconY;

        float iconYWithoutText = (rect.height() - iconSize) / 2;  
        float iconYWithText;  

        if (showLabels && item.title != null) {
            float totalHeight = iconSize + iconTextGap + textSize;
            float startY = (rect.height() - totalHeight) / 2;
            iconYWithText = startY;
        } else {
            iconYWithText = iconYWithoutText;  
        }

        iconY = iconYWithoutText + (iconYWithText - iconYWithoutText) * animState.iconPositionProgress;
        iconY += animState.translateY * dpToPx(1);

        boolean shouldShowLabel = showLabels && item.title != null && animState.labelAlpha > 0.01f;

        if (item.icon != null) {
            int iconColor = isSelected ? selectedColor : unselectedColor;

            int alpha = (int)(255 * (0.6f + 0.4f * animState.alpha));

            Drawable icon = item.icon.mutate();
            icon.setTint(iconColor);
            icon.setAlpha(alpha);

            int iconLeft = rect.centerX() - (int)(iconSize / 2);
            int iconTop = (int)iconY;

            icon.setBounds(
                    iconLeft,
                    iconTop,
                    iconLeft + (int)iconSize,
                    iconTop + (int)iconSize
            );
            icon.draw(canvas);
        }

        if (shouldShowLabel) {
            int textColor = selectedColor;
            textPaint.setColor(textColor);

            int textAlpha = (int)(255 * animState.labelAlpha);
            textPaint.setAlpha(textAlpha);

            textPaint.setTextSize(textSize);
            Paint.FontMetrics fm = textPaint.getFontMetrics();

            float textX = rect.centerX();
            float textY = iconY + iconSize + iconTextGap - fm.top + animState.translateY * dpToPx(0.5f);

            canvas.save();
            canvas.scale(animState.labelScale, animState.labelScale, textX, textY);
            canvas.drawText(item.title, textX, textY, textPaint);
            canvas.restore();

            textPaint.setAlpha(255);
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();

                for (int i = 0; i < itemRects.size(); i++) {
                    if (itemRects.get(i).contains((int) downX, (int) downY)) {
                        startRippleAnimation(downX, downY);
                        break;
                    }
                }
                springScale.animateToFinalPosition(SPRING_PRESS_SCALE);
                return true;

            case MotionEvent.ACTION_UP:
                springScale.animateToFinalPosition(1f);

                float x = event.getX();
                float y = event.getY();
                for (int i = 0; i < itemRects.size(); i++) {
                    if (itemRects.get(i).contains((int) x, (int) y)) {
                        setSelectedIndex(i);
                        performClick();
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                springScale.animateToFinalPosition(1f);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    /**
     * 开始波纹动画
     */
    private void startRippleAnimation(float x, float y) {
        if (rippleAnimator != null && rippleAnimator.isRunning()) {
            rippleAnimator.cancel();
        }

        rippleX = x;
        rippleY = y;

        rippleAnimator = ValueAnimator.ofFloat(0, dpToPx(48));
        rippleAnimator.setDuration(400);
        rippleAnimator.setInterpolator(decelerateInterpolator);

        rippleAnimator.addUpdateListener(animation -> {
            rippleRadius = (float) animation.getAnimatedValue();
            ripplePaint.setAlpha((int)(30 * (1f - animation.getAnimatedFraction())));
            invalidate();
        });

        rippleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rippleRadius = 0f;
                invalidate();
            }
        });

        rippleAnimator.start();
    }

    /**
     * 更新指示器位置
     */
    private void updateIndicatorPosition(int index, boolean animate) {
        if (index < 0 || index >= itemRects.size()) return;

        Rect itemRect = itemRects.get(index);
        NavigationItem item = items.get(index);

        float iconSize = dpToPx(24);
        float contentHeight = iconSize;
        float contentWidth = dpToPx(48);

        if (indicatorWrapText && showLabels && item.title != null) {
            contentHeight += iconTextGap + textSize;
            textPaint.setTextSize(textSize);
            float textWidth = textPaint.measureText(item.title);
            contentWidth = Math.max(contentWidth, textWidth + dpToPx(8));
        }

        float indicatorWidth = contentWidth + indicatorPaddingHorizontal * 2;
        float indicatorHeight = contentHeight + indicatorPaddingVertical * 2;

        float targetX = itemRect.centerX();
        float targetY;

        if (indicatorWrapText && showLabels && item.title != null) {
            targetY = itemRect.centerY();
        } else {
            boolean shouldShowLabel = showLabels && item.title != null;
            if (shouldShowLabel) {
                float totalHeight = iconSize + iconTextGap + textSize;
                float iconCenterY = itemRect.centerY() - totalHeight / 2 + iconSize / 2;
                targetY = iconCenterY;
            } else {
                targetY = itemRect.centerY();
            }
        }

        if (animate && enableAnimation) {
            ValueAnimator indicatorAnimator = ValueAnimator.ofFloat(0f, 1f);
            indicatorAnimator.setDuration(animationDuration);
            indicatorAnimator.setInterpolator(smoothInterpolator);

            final float startX = indicatorX;
            final float startY = indicatorY;
            final float startWidth = indicatorRect.width();
            final float startHeight = indicatorRect.height();

            indicatorAnimator.addUpdateListener(animation -> {
                float progress = (float) animation.getAnimatedValue();

                indicatorX = startX + (targetX - startX) * progress;
                indicatorY = startY + (targetY - startY) * progress;

                float currentWidth = startWidth + (indicatorWidth - startWidth) * progress;
                float currentHeight = startHeight + (indicatorHeight - startHeight) * progress;

                updateIndicatorRect(indicatorX, indicatorY, currentWidth, currentHeight);
                invalidate();
            });

            indicatorAnimator.start();
        } else {
            indicatorX = targetX;
            indicatorY = targetY;
            updateIndicatorRect(indicatorX, indicatorY, indicatorWidth, indicatorHeight);
        }
    }

    /**
     * 更新指示器矩形
     */
    private void updateIndicatorRect(float centerX, float centerY, float width, float height) {
        indicatorRect.set(
                centerX - width / 2,
                centerY - height / 2,
                centerX + width / 2,
                centerY + height / 2
        );
    }

    /**
     * 添加导航项
     */
    public void addItem(NavigationItem item) {
        items.add(item);
        ItemAnimationState state = new ItemAnimationState();
        animationStates.add(state);

        if (items.size() == 1 && selectedIndex < 0) {
            setSelectedIndex(0);
        }

        updateItemRects();
        invalidate();
    }

    /**
     * 设置选中的索引
     */
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size()) {
            if (index != selectedIndex) {
                previousSelectedIndex = selectedIndex;
                selectedIndex = index;

                if (enableAnimation) {
                    animateSelection();
                    updateIndicatorPosition(index, true);
                } else {
                    updateSelectionStates();
                    updateIndicatorPosition(index, false);
                    invalidate();
                }

                if (listener != null) {
                    listener.onItemSelected(index, items.get(index));
                }
            }
        }
    }

    /**
     * 动画选中效果
     */
    private void animateSelection() {
        if (selectionAnimator != null && selectionAnimator.isRunning()) {
            selectionAnimator.cancel();
        }
        if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
            indicatorAnimator.cancel();
        }

        selectionAnimator = ValueAnimator.ofFloat(0f, 1f);
        selectionAnimator.setDuration(animationDuration + 100);
        selectionAnimator.setInterpolator(smoothInterpolator);

        selectionAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();

            for (int i = 0; i < animationStates.size(); i++) {
                ItemAnimationState state = animationStates.get(i);

                if (i == selectedIndex) {
                    if (progress <= 0.6f) {
                        float scaleProgress = progress / 0.6f;
                        state.iconScale = 0.9f + 0.15f * scaleProgress;
                        state.scale = state.iconScale;
                    } else {
                        float bounceProgress = (progress - 0.6f) / 0.4f;
                        state.iconScale = 1.05f - 0.05f * bounceProgress;
                        state.scale = state.iconScale;
                    }

                    state.alpha = progress;
                    state.translateY = -0.5f * progress;

                    if (progress <= 0.2f) {
                        state.labelAlpha = 0f;
                        state.labelScale = 0.7f;
                    } else if (progress >= 0.95f) {
                        state.labelAlpha = 1.0f;
                        state.labelScale = 1.0f;
                    } else {
                        float labelProgress = (progress - 0.2f) / 0.75f;
                        float easedProgress = 1f - (float)Math.pow(1f - labelProgress, 3);
                        state.labelAlpha = easedProgress;
                        state.labelScale = 0.7f + 0.3f * easedProgress;
                    }

                    if (progress <= 0.15f) {
                        state.iconPositionProgress = 0f;
                    } else {
                        float positionProgress = (progress - 0.15f) / 0.85f;
                        state.iconPositionProgress = 1f - (float)Math.pow(1f - positionProgress, 2);
                    }

                } else if (i == previousSelectedIndex) {
                    state.scale = 1f - 0.1f * progress;
                    state.iconScale = 1f - 0.1f * progress;
                    state.alpha = 1f - progress;
                    state.translateY = -0.5f * (1f - progress);

                    if (progress <= 0.4f) {
                        float fadeProgress = progress / 0.4f;
                        state.labelAlpha = 1f - fadeProgress;
                        state.labelScale = 1f - 0.3f * fadeProgress;
                    } else {
                        state.labelAlpha = 0f;
                        state.labelScale = 0.7f;
                    }

                    if (progress <= 0.1f) {
                        state.iconPositionProgress = 1f;
                    } else if (progress >= 0.7f) {
                        state.iconPositionProgress = 0f;
                    } else {
                        float positionProgress = (progress - 0.1f) / 0.6f;
                        state.iconPositionProgress = 1f - (positionProgress * positionProgress);
                    }

                } else {
                    state.scale = 0.9f;
                    state.iconScale = 0.9f;
                    state.alpha = 0f;
                    state.translateY = 0f;
                    state.labelAlpha = 0f;
                    state.labelScale = 0.7f;
                    state.iconPositionProgress = 0f;
                }
            }

            invalidate();
        });

        selectionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < animationStates.size(); i++) {
                    ItemAnimationState state = animationStates.get(i);
                    if (i == selectedIndex) {
                        state.scale = 1.0f;
                        state.iconScale = 1.0f;
                        state.alpha = 1.0f;
                        state.translateY = -0.5f;
                        state.labelAlpha = 1.0f;
                        state.labelScale = 1.0f;
                        state.iconPositionProgress = 1.0f;
                    } else {
                        state.scale = 0.9f;
                        state.iconScale = 0.9f;
                        state.alpha = 0f;
                        state.translateY = 0f;
                        state.labelAlpha = 0f;
                        state.labelScale = 0.7f;
                        state.iconPositionProgress = 0f;
                    }
                }
                invalidate();
            }
        });

        indicatorAnimator = ValueAnimator.ofFloat(indicatorProgress, 1f);
        indicatorAnimator.setDuration(animationDuration);
        indicatorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        indicatorAnimator.addUpdateListener(animation -> {
            indicatorProgress = (float) animation.getAnimatedValue();
            invalidate();
        });

        selectionAnimator.start();
        indicatorAnimator.start();
    }

    private void updateSelectionStates() {
        for (int i = 0; i < animationStates.size(); i++) {
            ItemAnimationState state = animationStates.get(i);
            if (i == selectedIndex) {
                state.scale = 1.0f;
                state.iconScale = 1.0f;
                state.alpha = 1.0f;
                state.translateY = -0.5f;
                state.labelAlpha = 1.0f;
                state.labelScale = 1.0f;
                state.iconPositionProgress = 1.0f;
            } else {
                state.scale = 0.9f;
                state.iconScale = 0.9f;
                state.alpha = 0f;
                state.translateY = 0f;
                state.labelAlpha = 0f;
                state.labelScale = 0.7f;
                state.iconPositionProgress = 0f;
            }
        }
        indicatorProgress = 1f;
        if (selectedIndex >= 0) {
            updateIndicatorPosition(selectedIndex, false);
        }
    }

    public void setItems(List<NavigationItem> items) {
        this.items.clear();
        this.items.addAll(items);
        if (!this.items.isEmpty() && selectedIndex < 0) {
            selectedIndex = 0;
        }
        updateItemRects();
        invalidate();
    }

    public void setSelectedColor(int color) {
        this.selectedColor = color;
        invalidate();
    }

    public void setUnselectedColor(int color) {
        this.unselectedColor = color;
        invalidate();
    }

    public void setIndicatorColor(int color) {
        this.indicatorColor = color;
        indicatorPaint.setColor(color);
        invalidate();
    }

    public void setShowLabels(boolean show) {
        this.showLabels = show;
        invalidate();
    }

    public void setShadowSize(float elevationDp) {
        setElevation(Math.max(0, dpToPx(elevationDp)));
    }

    public void setSurfaceColor(int color) {
        if (shadowBackgroundDrawable != null) {
            shadowBackgroundDrawable.setColor(color);
            invalidate();
        }
    }

    public void setGlassOverlayColor(int color) {
        if (glassOverlayDrawable != null) {
            glassOverlayDrawable.setColor(color);
            if (glassOverlay != null) {
                glassOverlay.invalidate();
            }
            invalidate();
        }
    }

    public void setAnimationDuration(long duration) {
        this.animationDuration = duration;
    }

    public void setEnableAnimation(boolean enable) {
        this.enableAnimation = enable;
    }

    public void setIndicatorWrapText(boolean wrapText) {
        if (this.indicatorWrapText != wrapText) {
            this.indicatorWrapText = wrapText;
            if (selectedIndex >= 0) {
                updateIndicatorPosition(selectedIndex, enableAnimation);
            }
            invalidate();
        }
    }

    public void setIndicatorCornerRadius(float radiusDp) {
        this.indicatorCornerRadius = dpToPx(radiusDp);
        invalidate();
    }

    public void setIndicatorPadding(float horizontalDp, float verticalDp) {
        this.indicatorPaddingHorizontal = dpToPx(horizontalDp);
        this.indicatorPaddingVertical = dpToPx(verticalDp);
        if (selectedIndex >= 0) {
            updateIndicatorPosition(selectedIndex, false);
        }
        invalidate();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private int dpToPx(int dp) {
        return dpToPx((float) dp);
    }

    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics()
        );
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp,
                getContext().getResources().getDisplayMetrics()
        );
    }

    private static class ItemAnimationState {
        float scale = 0.9f;
        float iconScale = 0.9f;
        float alpha = 0f;
        float translateY = 0f;
        float labelAlpha = 0f;
        float labelScale = 0.7f;
        // icon position progress (0 = no-text layout, 1 = with-text layout)
        float iconPositionProgress = 0f;
    }

    public static class NavigationItem {
        public String title;
        public Drawable icon;
        public Object tag;

        public NavigationItem(String title, Drawable icon) {
            this.title = title;
            this.icon = icon;
        }

        public NavigationItem(String title, Drawable icon, Object tag) {
            this.title = title;
            this.icon = icon;
            this.tag = tag;
        }
    }

    /**
     * 项目选中监听器接口
     */
    public interface OnItemSelectedListener {
        void onItemSelected(int index, NavigationItem item);
    }
}