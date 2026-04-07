package com.yuuki.flyu.ui.widget.blur.liquidglass;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class LiquidTracker {
    private VelocityTracker velocityTracker;
    private final SpringAnimator springAnimX, springAnimY;
    private final SpringAnimator springAnimRotX, springAnimRotY;
    private final Handler liquidHandler;

    private static final float MAX_VELOCITY = 2000f;
    private static final float VELOCITY_DEAD_ZONE = 60f;
    private static final float MAX_STRETCH = 0.4f;

    public LiquidTracker(View view) {
        // Scale: resting at 1.0 — low stiffness + low damping = bouncy jelly
        springAnimX = new SpringAnimator(view, (v, val) -> v.setScaleX(val), 1f);
        springAnimX.setStiffness(60f);
        springAnimX.setDampingRatio(0.2f);

        springAnimY = new SpringAnimator(view, (v, val) -> v.setScaleY(val), 1f);
        springAnimY.setStiffness(60f);
        springAnimY.setDampingRatio(0.2f);

        // Rotation: resting at 0.0
        springAnimRotX = new SpringAnimator(view, (v, val) -> v.setRotationX(val), 0f);
        springAnimRotX.setStiffness(60f);
        springAnimRotX.setDampingRatio(0.3f);

        springAnimRotY = new SpringAnimator(view, (v, val) -> v.setRotationY(val), 0f);
        springAnimRotY.setStiffness(60f);
        springAnimRotY.setDampingRatio(0.3f);

        liquidHandler = new Handler(Looper.getMainLooper());
    }

    public void ensureTracking(MotionEvent e) {
        ensureAddMovement(e);
    }

    public void applyMovement(MotionEvent e, float baseScale) {
        ensureAddMovement(e);

        float[] scaleXY = getLiquidScale(baseScale);

        // Dead zone: preserve current press scale, don't animate
        if (scaleXY[0] == baseScale && scaleXY[1] == baseScale) return;

        animateToFinalPosition(scaleXY[0], scaleXY[1]);

        // After drag stops, spring back to base (press scale while touching, 1.0 otherwise)
        liquidHandler.removeCallbacksAndMessages(null);
        liquidHandler.postDelayed(() -> animateToFinalPosition(baseScale, baseScale), 200);
    }

    public void recycle() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private float[] getLiquidScale(float base) {
        if (velocityTracker == null)
            return new float[]{base, base};

        velocityTracker.computeCurrentVelocity(1000);
        float absVx = Math.abs(velocityTracker.getXVelocity());
        float absVy = Math.abs(velocityTracker.getYVelocity());

        if (absVx < VELOCITY_DEAD_ZONE && absVy < VELOCITY_DEAD_ZONE) {
            return new float[]{base, base};
        }

        float normVx = Math.min(absVx / MAX_VELOCITY, 1f);
        float normVy = Math.min(absVy / MAX_VELOCITY, 1f);

        float scaleX, scaleY;
        if (normVx > normVy) {
            scaleX = base + normVx * MAX_STRETCH;
            scaleY = base - normVx * MAX_STRETCH * 0.5f;
        } else {
            scaleX = base - normVy * MAX_STRETCH * 0.5f;
            scaleY = base + normVy * MAX_STRETCH;
        }

        return new float[]{scaleX, scaleY};
    }

    private void ensureAddMovement(MotionEvent e) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(e);
    }

    public void animateScale(float scale) {
        animateToFinalPosition(scale, scale);
    }

    public void animateTilt(float rotX, float rotY) {
        springAnimRotX.animateToFinalPosition(rotX);
        springAnimRotY.animateToFinalPosition(rotY);
    }

    private void animateToFinalPosition(float x, float y) {
        springAnimX.animateToFinalPosition(x);
        springAnimY.animateToFinalPosition(y);
    }
}
