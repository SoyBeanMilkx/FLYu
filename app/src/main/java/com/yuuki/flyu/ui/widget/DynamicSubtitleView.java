package com.yuuki.flyu.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.yuuki.flyu.ui.Strings;

import java.util.Calendar;
import java.util.List;

public class DynamicSubtitleView extends android.widget.TextView {

    public enum Mode { GREETING, CAROUSEL }

    private Mode mode = Mode.GREETING;
    private List<String> carouselTexts;
    private int carouselIndex = 0;
    private long carouselIntervalMs = 6000;
    private long fadeDurationMs = 320;
    private long revealDurationMs = 500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;

    private final Paint revealPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float revealProgress = 0f;
    private boolean revealCovering = true;
    private static final int REVEAL_COLOR = 0xFF8090EE;
    private float revealCornerRadius;
    private float revealWidth = 0f;
    private boolean revealActive = false;
    private int textAlpha = 255;

    private String greetingMorning   = Strings.GREETING_MORNING;
    private String greetingNoon      = Strings.GREETING_NOON;
    private String greetingAfternoon = Strings.GREETING_AFTERNOON;
    private String greetingEvening   = Strings.GREETING_EVENING;
    private String greetingNight     = Strings.GREETING_NIGHT;
    private String greetingLateNight = Strings.GREETING_LATE_NIGHT;

    public DynamicSubtitleView(Context context) {
        super(context);
        init();
    }

    public DynamicSubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicSubtitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        revealPaint.setColor(REVEAL_COLOR);
        revealCornerRadius = dp2px(4);
    }

    public void setGreetingMode() {
        this.mode = Mode.GREETING;
        applyGreeting();
        startLoop();
    }

    public void setGreetings(String morning, String noon, String afternoon,
                             String evening, String night, String lateNight) {
        this.greetingMorning = morning;
        this.greetingNoon = noon;
        this.greetingAfternoon = afternoon;
        this.greetingEvening = evening;
        this.greetingNight = night;
        this.greetingLateNight = lateNight;
    }

    public void setCarouselMode(List<String> texts) {
        setCarouselMode(texts, 6000);
    }

    public void setCarouselMode(List<String> texts, long intervalMs) {
        this.mode = Mode.CAROUSEL;
        this.carouselTexts = texts;
        this.carouselIntervalMs = intervalMs;
        this.carouselIndex = 0;
        if (texts != null && !texts.isEmpty()) {
            setText(texts.get(0));
        }
        startLoop();
    }

    public void setRevealColor(int color) {
        revealPaint.setColor(color);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoop();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopLoop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startLoop();
        } else {
            stopLoop();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (textAlpha < 255) {
            int savedAlpha = getPaint().getAlpha();
            getPaint().setAlpha(textAlpha);
            super.onDraw(canvas);
            getPaint().setAlpha(savedAlpha);
        } else {
            super.onDraw(canvas);
        }
        if (revealActive && revealWidth > 0f) {
            int h = getHeight();
            float pad = dp2px(2);
            if (revealCovering) {
                float right = revealWidth * revealProgress;
                canvas.drawRoundRect(-pad, -pad, right + pad, h + pad,
                        revealCornerRadius, revealCornerRadius, revealPaint);
            } else {
                float left = revealWidth * revealProgress;
                canvas.drawRoundRect(left - pad, -pad, revealWidth + pad, h + pad,
                        revealCornerRadius, revealCornerRadius, revealPaint);
            }
        }
    }

    private void startLoop() {
        if (running) return;
        running = true;
        scheduleNext();
    }

    private void stopLoop() {
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void scheduleNext() {
        if (!running) return;
        long delay = (mode == Mode.GREETING) ? 60_000 : carouselIntervalMs;
        handler.postDelayed(this::tick, delay);
    }

    private void tick() {
        if (!running) return;
        if (mode == Mode.GREETING) {
            String newGreeting = resolveGreeting();
            if (!newGreeting.equals(getText().toString())) {
                crossfadeTo(newGreeting);
            }
            scheduleNext();
        } else {
            if (carouselTexts == null || carouselTexts.size() <= 1) {
                scheduleNext();
                return;
            }
            carouselIndex = (carouselIndex + 1) % carouselTexts.size();
            sweepRevealTo(carouselTexts.get(carouselIndex));
        }
    }

    private void crossfadeTo(String newText) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        fadeOut.setDuration(fadeDurationMs);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setText(newText);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                        DynamicSubtitleView.this, "alpha", 0f, 1f);
                fadeIn.setDuration(fadeDurationMs);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.start();
            }
        });
        fadeOut.start();
    }

    private void sweepRevealTo(String newText) {
        float oldWidth = getWidth();
        float newWidth = getPaint().measureText(newText) + getPaddingLeft() + getPaddingRight();
        revealWidth = Math.max(oldWidth, newWidth);

        revealActive = true;
        revealCovering = true;
        ValueAnimator coverAnim = ValueAnimator.ofFloat(0f, 1f);
        coverAnim.setDuration(revealDurationMs);
        coverAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        coverAnim.addUpdateListener(a -> {
            revealProgress = (float) a.getAnimatedValue();
            invalidate();
        });
        coverAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                revealProgress = 0f;
                revealCovering = false;
                textAlpha = 0;
                setText(newText);

                ValueAnimator revealAnim = ValueAnimator.ofFloat(0f, 1f);
                revealAnim.setDuration(revealDurationMs);
                revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                revealAnim.addUpdateListener(a -> {
                    float val = (float) a.getAnimatedValue();
                    revealProgress = val;
                    textAlpha = (int) (255 * val);
                    invalidate();
                });
                revealAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        revealProgress = 0f;
                        revealActive = false;
                        textAlpha = 255;
                        invalidate();
                        scheduleNext();
                    }
                });
                revealAnim.start();
            }
        });
        coverAnim.start();
    }

    private void applyGreeting() {
        setText(resolveGreeting());
        setAlpha(1f);
    }

    private String resolveGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 11) return greetingMorning;
        if (hour >= 11 && hour < 13) return greetingNoon;
        if (hour >= 13 && hour < 17) return greetingAfternoon;
        if (hour >= 17 && hour < 19) return greetingEvening;
        if (hour >= 19 && hour < 23) return greetingNight;
        return greetingLateNight;  // 23:00 ~ 05:59
    }

    private float dp2px(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
