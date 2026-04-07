package com.yuuki.flyu.ui.fragment.children;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.LinearLayoutPro;
import com.yuuki.flyu.utils.Miscellaneous;

import io.github.libxposed.service.XposedService;

public class StatusCardBuilder {

    private static final int ANIM_DURATION = 600;

    private ImageView statusIcon;
    private TextView statusTitle;
    private TextView statusDesc;
    private LinearLayout contentRow;
    private LinearLayoutPro card;
    private Boolean lastActive = null;

    public void build(LinearLayoutPro card) {
        this.card = card;

        contentRow = new LinearLayout(card.getContext());
        contentRow.setOrientation(LinearLayout.HORIZONTAL);
        contentRow.setGravity(Gravity.CENTER_VERTICAL);
        int hPad = Miscellaneous.dp2px(card.getContext(), 20);
        int vPad = Miscellaneous.dp2px(card.getContext(), 20);
        contentRow.setPadding(hPad, vPad, hPad, vPad);

        statusIcon = new ImageView(card.getContext());
        int iconSize = Miscellaneous.dp2px(card.getContext(), 40);
        statusIcon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));

        LinearLayout textColumn = new LinearLayout(card.getContext());
        textColumn.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.leftMargin = Miscellaneous.dp2px(card.getContext(), 14);
        textColumn.setLayoutParams(textParams);

        statusTitle = new TextView(card.getContext());
        statusTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        statusTitle.setTypeface(Typeface.DEFAULT_BOLD);
        statusTitle.setTextColor(Colors.CARD_TEXT_TITLE);

        statusDesc = new TextView(card.getContext());
        statusDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusDesc.setTextColor(Colors.CARD_TEXT_SUBTITLE);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = Miscellaneous.dp2px(card.getContext(), 2);
        statusDesc.setLayoutParams(descParams);

        textColumn.addView(statusTitle);
        textColumn.addView(statusDesc);
        contentRow.addView(statusIcon);
        contentRow.addView(textColumn);
        card.addView(contentRow);
    }

    public void updateStatus(XposedService service) {
        if (statusIcon == null) return;
        boolean isActive = service != null;
        if (lastActive != null && lastActive == isActive) return;

        boolean animate = lastActive != null;
        lastActive = isActive;

        if (animate) {
            animateTransition(isActive, service);
        } else {
            applyState(isActive, service);
        }
    }

    private void applyState(boolean isActive, XposedService service) {
        if (isActive) {
            statusIcon.setImageResource(R.drawable.success);
            statusIcon.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
            statusTitle.setText(Strings.STATUS_ACTIVATED);
            statusTitle.setTextColor(Color.WHITE);
            String desc = String.format(Strings.STATUS_DESC_FORMAT,
                    service.getFrameworkName(), service.getFrameworkVersion(), service.getApiVersion());
            statusDesc.setText(desc);
            statusDesc.setTextColor(Colors.STATUS_ACTIVE_DESC);
            if (card != null) {
                card.setCardBackgroundColor(Colors.STATUS_ACTIVE_BG);
            }
        } else {
            statusIcon.setImageResource(R.drawable.error);
            statusIcon.setColorFilter(Colors.STATUS_INACTIVE_ICON, android.graphics.PorterDuff.Mode.SRC_IN);
            statusTitle.setText(Strings.STATUS_INACTIVE);
            statusTitle.setTextColor(Colors.STATUS_INACTIVE_TITLE);
            statusDesc.setText(Strings.STATUS_INACTIVE_DESC);
            statusDesc.setTextColor(Colors.CARD_TEXT_SUBTITLE);
            if (card != null) {
                card.setCardBackgroundColor(Color.WHITE);
            }
        }
    }

    private void animateTransition(boolean isActive, XposedService service) {
        // 淡出内容
        contentRow.animate()
                .alpha(0f)
                .setDuration(ANIM_DURATION / 2)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    applyState(isActive, service);
                    // 淡入内容
                    contentRow.animate()
                            .alpha(1f)
                            .setDuration(ANIM_DURATION / 2)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                })
                .start();

        // 背景色平滑过渡
        if (card != null) {
            int fromColor = isActive ? Color.WHITE : Colors.STATUS_ACTIVE_BG;
            int toColor = isActive ? Colors.STATUS_ACTIVE_BG : Color.WHITE;
            ValueAnimator bgAnim = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
            bgAnim.setDuration(ANIM_DURATION);
            bgAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            bgAnim.addUpdateListener(a -> card.setCardBackgroundColor((int) a.getAnimatedValue()));
            bgAnim.start();
        }
    }
}
