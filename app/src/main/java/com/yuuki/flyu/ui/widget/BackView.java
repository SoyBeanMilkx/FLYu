package com.yuuki.flyu.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.yuuki.flyu.ui.widget.blur.dialog.BlurView;

public class BackView extends FrameLayout {

    private static final int SHAPE_COUNT = 5;
    private static final float[] DIAMETER_FACTORS = new float[]{
            0.74f, 0.68f, 0.64f, 0.51f, 0.47f
    };

    private static final float[] START_X_FACTORS = new float[]{
            0.15f, 0.85f, 0.50f, 0.20f, 0.80f
    };
    private static final float[] START_Y_FACTORS = new float[]{
            0.15f, 0.15f, 0.50f, 0.75f, 0.25f
    };

    private static final float[][] VELOCITY_DP = new float[][]{
            {16.0f, 4.5f},   // 大圆1：右下（左上角出发）
            {-15.5f, 4.0f},  // 大圆2：左下（右上角出发）
            {14.0f, -3.5f},  // 大圆3：右上（中心出发）
            {15.0f, -4.0f},  // 小圆1：右上（左下角出发）
            {-15.0f, 4.0f}   // 小圆2：左上（右下角出发，与小圆1相反）
    };

    private static final float[] BASE_SCALES = new float[]{
            0.98f, 1.01f, 0.96f, 1.03f, 0.97f
    };
    private static final float[] SCALE_AMPLITUDES = new float[]{
            0.045f, 0.040f, 0.048f, 0.055f, 0.050f
    };
    private static final float[] SCALE_PHASES = new float[]{
            0.0f, 1.2f, 2.5f, 0.8f, 1.9f
    };
    private static final float[] SCALE_SPEEDS = new float[]{
            0.25f, 0.22f, 0.24f, 0.28f, 0.26f
    };

    private static final int[] FILL_COLORS = new int[]{
            Color.parseColor("#E3E8FF"),
            Color.parseColor("#DBE2FF"),
            Color.parseColor("#D2DAFF"),
            Color.parseColor("#D8DEFF"),
            Color.parseColor("#DFE4FF")
    };
    private static final int[] EDGE_COLORS = new int[]{
            Color.parseColor("#D0D6FF"),
            Color.parseColor("#C6CEFF"),
            Color.parseColor("#BCC6FF"),
            Color.parseColor("#C0CAFF"),
            Color.parseColor("#CDD4FF")
    };

    private static final int[] ALPHAS = new int[]{
            84, 88, 92, 110, 116
    };

    private static final float BOTTOM_CIRCLE_MIN_X_FACTOR = 0.12f;
    private static final float BOTTOM_CIRCLE_MAX_X_FACTOR = 0.36f;
    private static final float BOTTOM_CIRCLE_MIN_TOP_FACTOR = 0.75f;
    private static final float BOTTOM_CIRCLE_MAX_TOP_FACTOR = 1.00f;
    private static final float BOTTOM_STATIC_CIRCLE_RADIUS_FACTOR = 0.46f;
    private static final float BOTTOM_STATIC_CIRCLE_SEGMENT_SECONDS = 6.5f;
    private static final float BOTTOM_NAV_HEIGHT_DP = 64f;
    private static final float BOTTOM_NAV_MARGIN_BOTTOM_DP = 16f;
    private static final int BOTTOM_STATIC_CIRCLE_COLOR = Color.parseColor("#9098FF");
    private static final int BOTTOM_STATIC_CIRCLE_ALPHA = 164;

    private final AnimatedGeometryView geometryView;
    private final BlurView blurView;

    public BackView(Context context) {
        this(context, null);
    }

    public BackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipToPadding(false);
        setClipChildren(false);

        geometryView = new AnimatedGeometryView(context);
        LayoutParams geometryParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(geometryView, geometryParams);

        blurView = new BlurView(context);
        blurView.setBlurRadius(0f);
        blurView.setDownscaleFactor(10f);
        blurView.setOverlayColor(Color.parseColor("#10F0EEFF"));
        blurView.setOutLineRound(0f);
        LayoutParams blurParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(blurView, blurParams);
        geometryView.setFrameUpdateListener(blurView::postInvalidateOnAnimation);
    }

    public BlurView getBlurView() {
        return blurView;
    }

    public void setBlurRadius(float radius) {
        blurView.setBlurRadius(radius);
    }

    public void setDownscaleFactor(float factor) {
        blurView.setDownscaleFactor(factor);
    }

    public void setOverlayColor(int color) {
        blurView.setOverlayColor(color);
    }

    private static final class AnimatedGeometryView extends View {

        private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Matrix gradientMatrix = new Matrix();
        private final Shape[] shapes = new Shape[SHAPE_COUNT];

        private LinearGradient backgroundGradient;
        private long startTimeMillis;
        private ValueAnimator animator;
        private Runnable frameUpdateListener;

        public AnimatedGeometryView(Context context) {
            super(context);
            init();
        }

        public AnimatedGeometryView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public void setFrameUpdateListener(Runnable listener) {
            frameUpdateListener = listener;
        }

        private void init() {
            setWillNotDraw(false);
            backgroundPaint.setStyle(Paint.Style.FILL);
            shapePaint.setStyle(Paint.Style.FILL);
            glowPaint.setStyle(Paint.Style.FILL);
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(16000L);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                if (frameUpdateListener != null) {
                    frameUpdateListener.run();
                }
                postInvalidateOnAnimation();
            });
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            startTimeMillis = SystemClock.uptimeMillis();
            animator.start();
        }

        @Override
        protected void onDetachedFromWindow() {
            animator.cancel();
            super.onDetachedFromWindow();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w <= 0 || h <= 0) {
                return;
            }
            backgroundGradient = new LinearGradient(
                    0f,
                    0f,
                    w,
                    h,
                    new int[]{
                            Color.parseColor("#FDFEFF"),
                            Color.parseColor("#F5F3FF"),
                            Color.parseColor("#EDEBFF")
                    },
                    new float[]{0f, 0.48f, 1f},
                    Shader.TileMode.CLAMP
            );
            buildShapes(w, h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width <= 0 || height <= 0) {
                return;
            }
            float elapsedSeconds = (SystemClock.uptimeMillis() - startTimeMillis) / 1000f;
            drawBackground(canvas, width, height, elapsedSeconds);
            drawGlows(canvas, width, height, elapsedSeconds);
            drawShapes(canvas, width, height, elapsedSeconds);
            drawBottomStaticCircle(canvas, width, height, elapsedSeconds);
        }

        private void drawBackground(Canvas canvas, int width, int height, float elapsedSeconds) {
            if (backgroundGradient != null) {
                float shiftX = (float) (Math.sin(elapsedSeconds * 0.12f) * width * 0.10f);
                float shiftY = (float) (Math.cos(elapsedSeconds * 0.10f) * height * 0.08f);
                gradientMatrix.reset();
                gradientMatrix.setTranslate(shiftX, shiftY);
                backgroundGradient.setLocalMatrix(gradientMatrix);
                backgroundPaint.setShader(backgroundGradient);
            } else {
                backgroundPaint.setShader(null);
                backgroundPaint.setColor(Color.parseColor("#F7FAFF"));
            }
            canvas.drawRect(0f, 0f, width, height, backgroundPaint);
        }

        private void drawGlows(Canvas canvas, int width, int height, float elapsedSeconds) {
            float baseRadius = Math.max(width, height) * 0.50f;
            drawGlow(
                    canvas,
                    width * 0.20f + (float) Math.sin(elapsedSeconds * 0.18f) * width * 0.05f,
                    height * 0.18f + (float) Math.cos(elapsedSeconds * 0.15f) * height * 0.05f,
                    baseRadius,
                    Color.argb(52, 173, 215, 255)
            );
            drawGlow(
                    canvas,
                    width * 0.82f + (float) Math.cos(elapsedSeconds * 0.16f) * width * 0.05f,
                    height * 0.74f + (float) Math.sin(elapsedSeconds * 0.14f) * height * 0.04f,
                    baseRadius * 0.95f,
                    Color.argb(40, 196, 182, 255)
            );
        }

        private void drawGlow(Canvas canvas, float cx, float cy, float radius, int color) {
            RadialGradient gradient = new RadialGradient(
                    cx,
                    cy,
                    radius,
                    new int[]{color, Color.TRANSPARENT},
                    new float[]{0f, 1f},
                    Shader.TileMode.CLAMP
            );
            glowPaint.setShader(gradient);
            canvas.drawCircle(cx, cy, radius, glowPaint);
            glowPaint.setShader(null);
        }

        private void drawShapes(Canvas canvas, int width, int height, float elapsedSeconds) {
            for (int i = 0; i < shapes.length; i++) {
                Shape shape = shapes[i];
                if (shape == null) {
                    continue;
                }
                float scale = shape.baseScale
                        + shape.scaleAmplitude * (float) Math.sin(elapsedSeconds * shape.scaleSpeed + shape.scalePhase);
                float radius = shape.baseRadius * scale;
                float edgeInset = shape.baseRadius * 0.30f;
                
                float baseX = shape.startX + shape.velocityX * elapsedSeconds;
                float baseY = shape.startY + shape.velocityY * elapsedSeconds;
                
                float minY, maxY;
                if (i == 3) {
                    minY = height * 0.5f;
                    maxY = height;
                } else if (i == 4) {
                    minY = 0f;
                    maxY = height * 0.5f;
                } else {
                    minY = -edgeInset;
                    maxY = height + edgeInset;
                }
                
                float x = reflectPosition(baseX, -edgeInset, width + edgeInset);
                float y = reflectPosition(baseY, minY, maxY);

                shapePaint.setShader(null);
                shapePaint.setColor(applyAlpha(shape.fillColor, shape.alpha));

                canvas.save();
                canvas.translate(x, y);
                canvas.drawCircle(0f, 0f, radius, shapePaint);
                canvas.restore();
            }
        }

        private void drawBottomStaticCircle(Canvas canvas, int width, int height, float elapsedSeconds) {
            float radius = Math.min(width, height) * BOTTOM_STATIC_CIRCLE_RADIUS_FACTOR;
            float density = getResources().getDisplayMetrics().density;
            float navHeight = BOTTOM_NAV_HEIGHT_DP * density;
            float navBottomMargin = BOTTOM_NAV_MARGIN_BOTTOM_DP * density;
            float navTop = height - navBottomMargin - navHeight;
            float navInteriorBottom = navTop + navHeight * 0.70f;
            float topEdgeMin = height * BOTTOM_CIRCLE_MIN_TOP_FACTOR;
            float topEdgeMax = Math.min(height * BOTTOM_CIRCLE_MAX_TOP_FACTOR, navInteriorBottom);
            float fallbackTopEdge = navTop + navHeight * 0.42f;
            if (topEdgeMin > topEdgeMax) {
                topEdgeMin = fallbackTopEdge;
                topEdgeMax = fallbackTopEdge;
            }
            float xMin = width * BOTTOM_CIRCLE_MIN_X_FACTOR;
            float xMax = width * BOTTOM_CIRCLE_MAX_X_FACTOR;
            int segmentIndex = (int) Math.floor(elapsedSeconds / BOTTOM_STATIC_CIRCLE_SEGMENT_SECONDS);
            float segmentProgress = elapsedSeconds / BOTTOM_STATIC_CIRCLE_SEGMENT_SECONDS - segmentIndex;
            float easedProgress = smoothStep(segmentProgress);
            float startX = lerp(xMin, xMax, pseudoRandom01(segmentIndex, 11));
            float endX = lerp(xMin, xMax, pseudoRandom01(segmentIndex + 1, 11));
            float startTopEdge = lerp(topEdgeMin, topEdgeMax, pseudoRandom01(segmentIndex, 29));
            float endTopEdge = lerp(topEdgeMin, topEdgeMax, pseudoRandom01(segmentIndex + 1, 29));
            float cx = lerp(startX, endX, easedProgress);
            float topEdgeY = lerp(startTopEdge, endTopEdge, easedProgress);
            float cy = topEdgeY + radius;
            shapePaint.setShader(null);
            shapePaint.setColor(applyAlpha(BOTTOM_STATIC_CIRCLE_COLOR, BOTTOM_STATIC_CIRCLE_ALPHA));
            canvas.drawCircle(cx, cy, radius, shapePaint);
        }

        private float pseudoRandom01(int index, int salt) {
            double value = Math.sin(index * 12.9898d + salt * 78.233d) * 43758.5453d;
            return (float) (value - Math.floor(value));
        }

        private float smoothStep(float value) {
            float clampedValue = Math.max(0f, Math.min(1f, value));
            return clampedValue * clampedValue * (3f - 2f * clampedValue);
        }

        private float lerp(float start, float end, float progress) {
            return start + (end - start) * progress;
        }

        private Shader createCircleShader(Shape shape, float radius) {
            // 改为颜色均匀，去掉径向渐变
            shapePaint.setShader(null);
            return null;
        }

        private void buildShapes(int width, int height) {
            float minEdge = Math.min(width, height);
            float density = getResources().getDisplayMetrics().density;
            for (int i = 0; i < shapes.length; i++) {
                Shape shape = new Shape();
                shape.startX = width * START_X_FACTORS[i];
                shape.startY = height * START_Y_FACTORS[i];
                shape.baseRadius = minEdge * DIAMETER_FACTORS[i] * 0.5f;
                shape.velocityX = VELOCITY_DP[i][0] * density;
                shape.velocityY = VELOCITY_DP[i][1] * density;
                shape.baseScale = BASE_SCALES[i];
                shape.scaleAmplitude = SCALE_AMPLITUDES[i];
                shape.scalePhase = SCALE_PHASES[i];
                shape.scaleSpeed = SCALE_SPEEDS[i];
                shape.fillColor = FILL_COLORS[i];
                shape.edgeColor = EDGE_COLORS[i];
                shape.alpha = ALPHAS[i];
                shapes[i] = shape;
            }
        }

        private float reflectPosition(float value, float min, float max) {
            float range = max - min;
            if (range <= 0f) {
                return min;
            }
            float cycle = range * 2f;
            float offset = (value - min) % cycle;
            if (offset < 0f) {
                offset += cycle;
            }
            if (offset > range) {
                offset = cycle - offset;
            }
            return min + offset;
        }

        private static int blendColors(int startColor, int endColor, float ratio) {
            float clampedRatio = Math.max(0f, Math.min(1f, ratio));
            int red = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * clampedRatio);
            int green = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * clampedRatio);
            int blue = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * clampedRatio);
            return Color.rgb(red, green, blue);
        }

        private static int applyAlpha(int color, int alpha) {
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        }

        private static final class Shape {
            int fillColor;
            int edgeColor;
            int alpha;
            float baseRadius;
            float startX;
            float startY;
            float velocityX;
            float velocityY;
            float baseScale;
            float scaleAmplitude;
            float scalePhase;
            float scaleSpeed;
        }
    }
}
