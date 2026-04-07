package com.yuuki.flyu.ui.widget.collapse;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

final class ExpandCollapseAnimator {

    private static final int DURATION = 300;
    private static final Interpolator INTERPOLATOR = new PathInterpolator(0.4f, 0f, 0.2f, 1f);

    static void expand(View target) {
        target.setVisibility(View.VISIBLE);
        View parent = (View) target.getParent();
        int availableWidth = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
        target.measure(
                View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int targetHeight = target.getMeasuredHeight();

        int[] saved = freezeChildrenHeight(target);

        target.getLayoutParams().height = 0;
        target.requestLayout();

        ValueAnimator anim = ValueAnimator.ofInt(0, targetHeight);
        anim.setDuration(DURATION);
        anim.setInterpolator(INTERPOLATOR);
        anim.addUpdateListener(a -> {
            target.getLayoutParams().height = (int) a.getAnimatedValue();
            target.requestLayout();
        });
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                unfreezeChildrenHeight(target, saved);
                target.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                target.requestLayout();
            }
        });
        anim.start();
    }

    static void collapse(View target) {
        int startHeight = target.getHeight();

        int[] saved = freezeChildrenHeight(target);

        ValueAnimator anim = ValueAnimator.ofInt(startHeight, 0);
        anim.setDuration(DURATION);
        anim.setInterpolator(INTERPOLATOR);
        anim.addUpdateListener(a -> {
            target.getLayoutParams().height = (int) a.getAnimatedValue();
            target.requestLayout();
        });
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                target.setVisibility(View.GONE);
                unfreezeChildrenHeight(target, saved);
                target.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                target.requestLayout();
            }
        });
        anim.start();
    }

    private static int[] freezeChildrenHeight(View target) {
        if (target instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) target;
            int[] originals = new int[vg.getChildCount()];
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                originals[i] = child.getLayoutParams().height;
                child.getLayoutParams().height = child.getMeasuredHeight();
            }
            return originals;
        }
        return null;
    }

    private static void unfreezeChildrenHeight(View target, int[] originals) {
        if (target instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) target;
            for (int i = 0; i < vg.getChildCount(); i++) {
                vg.getChildAt(i).getLayoutParams().height =
                        (originals != null && i < originals.length)
                                ? originals[i]
                                : ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        }
    }

    private ExpandCollapseAnimator() {}
}
