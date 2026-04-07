package com.yuuki.flyu.ui.widget.blur.liquidglass;

import android.view.Choreographer;
import android.view.View;

public class SpringAnimator {

    public interface PropertySetter {
        void setValue(View view, float value);
    }

    // Convergence thresholds (matches AndroidX defaults for px-scale properties)
    private static final float VALUE_THRESHOLD = 0.001f;
    private static final float VELOCITY_THRESHOLD_MULTIPLIER = 62.5f;

    private final View view;
    private final PropertySetter setter;
    private final float startValue;

    // Spring parameters
    private float naturalFreq;    // ω = sqrt(stiffness / mass), mass = 1
    private float dampingRatio = 0.5f;

    // Animation state
    private float displacement;   // distance from target (x - target)
    private float velocity = 0f;  // current velocity in units/s
    private float targetValue;
    private boolean running = false;
    private long lastFrameTimeNanos = 0;

    private final Choreographer choreographer;
    private final Choreographer.FrameCallback frameCallback = this::onFrame;

    public SpringAnimator(View view, PropertySetter setter, float startValue) {
        this.view = view;
        this.setter = setter;
        this.startValue = startValue;
        this.targetValue = startValue;
        this.displacement = 0f;
        this.choreographer = Choreographer.getInstance();
        setStiffness(200f);
    }

    public void setStiffness(float stiffness) {
        this.naturalFreq = (float) Math.sqrt(stiffness);
    }

    public void setDampingRatio(float ratio) {
        this.dampingRatio = ratio;
    }

    public void animateToFinalPosition(float target) {
        // Convert current absolute state to displacement relative to NEW target
        float currentValue = targetValue + displacement;
        this.targetValue = target;
        this.displacement = currentValue - target;

        if (!running && (Math.abs(displacement) > VALUE_THRESHOLD || Math.abs(velocity) > VALUE_THRESHOLD)) {
            running = true;
            lastFrameTimeNanos = 0;
            choreographer.postFrameCallback(frameCallback);
        }
    }

    private void onFrame(long frameTimeNanos) {
        if (!running) return;

        if (!view.isAttachedToWindow()) {
            cancel();
            return;
        }

        if (lastFrameTimeNanos == 0) {
            lastFrameTimeNanos = frameTimeNanos;
            choreographer.postFrameCallback(frameCallback);
            return;
        }

        float dt = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f;
        lastFrameTimeNanos = frameTimeNanos;
        if (dt <= 0f) dt = 0.016f;
        if (dt > 0.064f) dt = 0.064f;

        // Compute new displacement and velocity using exact analytical solution
        updateSpring(dt);

        float currentValue = clampValue(targetValue + displacement);
        setter.setValue(view, currentValue);

        // Check convergence (same logic as AndroidX)
        float velocityThreshold = VALUE_THRESHOLD * VELOCITY_THRESHOLD_MULTIPLIER;
        if (Math.abs(displacement) < VALUE_THRESHOLD && Math.abs(velocity) < velocityThreshold) {
            setter.setValue(view, targetValue);
            displacement = 0f;
            velocity = 0f;
            running = false;
            return;
        }

        choreographer.postFrameCallback(frameCallback);
    }

    private float clampValue(float value) {
        // Safety: prevent extreme values that could crash rendering
        if (Float.isNaN(value) || Float.isInfinite(value)) return targetValue;
        // Clamp to reasonable range around target (±5 units covers any scale/rotation)
        float maxDeviation = 5f;
        return Math.max(targetValue - maxDeviation, Math.min(value, targetValue + maxDeviation));
    }

    private void updateSpring(float dt) {
        float x0 = displacement;
        float v0 = velocity;

        if (dampingRatio < 1f) {
            // Under-damped: oscillatory decay
            float gamma = -dampingRatio * naturalFreq;
            float dampedFreq = naturalFreq * (float) Math.sqrt(1.0 - dampingRatio * dampingRatio);

            float coeffA = x0;
            float coeffB = (v0 - gamma * x0) / dampedFreq;

            float decay = (float) Math.exp(gamma * dt);
            float cosVal = (float) Math.cos(dampedFreq * dt);
            float sinVal = (float) Math.sin(dampedFreq * dt);

            displacement = decay * (coeffA * cosVal + coeffB * sinVal);

            // v(t) = γ·x(t) + e^(γt)·ωd·(-A·sin(ωd·t) + B·cos(ωd·t))
            velocity = displacement * gamma
                    + decay * dampedFreq * (-coeffA * sinVal + coeffB * cosVal);

        } else if (dampingRatio == 1f) {
            // Critically damped: fastest non-oscillatory return
            float coeffA = x0;
            float coeffB = v0 + naturalFreq * x0;

            float decay = (float) Math.exp(-naturalFreq * dt);

            displacement = (coeffA + coeffB * dt) * decay;
            velocity = (coeffB - naturalFreq * (coeffA + coeffB * dt)) * decay;

        } else {
            // Over-damped: slow non-oscillatory return
            float sqrtTerm = naturalFreq * (float) Math.sqrt(dampingRatio * dampingRatio - 1.0);
            float gammaPlus = -dampingRatio * naturalFreq + sqrtTerm;
            float gammaMinus = -dampingRatio * naturalFreq - sqrtTerm;

            float coeffB = (v0 - gammaMinus * x0) / (gammaPlus - gammaMinus);
            float coeffA = x0 - coeffB;

            float decayMinus = (float) Math.exp(gammaMinus * dt);
            float decayPlus = (float) Math.exp(gammaPlus * dt);

            displacement = coeffA * decayMinus + coeffB * decayPlus;
            velocity = coeffA * gammaMinus * decayMinus + coeffB * gammaPlus * decayPlus;
        }
    }

    public void cancel() {
        running = false;
        velocity = 0f;
        displacement = 0f;
        choreographer.removeFrameCallback(frameCallback);
    }
}
