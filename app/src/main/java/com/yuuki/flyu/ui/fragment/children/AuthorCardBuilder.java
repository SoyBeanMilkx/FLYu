package com.yuuki.flyu.ui.fragment.children;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.LinearLayoutPro;
import com.yuuki.flyu.utils.Miscellaneous;

public class AuthorCardBuilder {

    public void build(LinearLayoutPro card) {
        int hPad = Miscellaneous.dp2px(card.getContext(), 20);
        int vPad = Miscellaneous.dp2px(card.getContext(), 20);
        card.setPadding(hPad, vPad, hPad, vPad);
        card.setClipChildren(false);
        card.setClipToPadding(false);

        // 上半部分：头像 + 信息
        LinearLayout topRow = new LinearLayout(card.getContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        topRow.setClipChildren(false);
        topRow.setClipToPadding(false);

        // 头像（圆角）
        ImageView avatar = new ImageView(card.getContext());
        int avatarSize = Miscellaneous.dp2px(card.getContext(), 56);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        avatar.setImageResource(R.drawable.head);
        float avatarRadius = Miscellaneous.dp2px(card.getContext(), 15);
        avatar.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), avatarRadius);
            }
        });
        avatar.setClipToOutline(true);
        avatar.setElevation(Miscellaneous.dp2px(card.getContext(), 6));
        avatar.invalidateOutline();

        // 名字 + 签名
        LinearLayout textCol = new LinearLayout(card.getContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textColParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textColParams.leftMargin = Miscellaneous.dp2px(card.getContext(), 14);
        textCol.setLayoutParams(textColParams);

        TextView name = new TextView(card.getContext());
        name.setText(Strings.AUTHOR_NAME);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setTextColor(Colors.CARD_TEXT_TITLE);

        TextView signature = new TextView(card.getContext());
        signature.setText(Strings.AUTHOR_SIGNATURE);
        signature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        signature.setTextColor(Colors.CARD_TEXT_SUBTITLE);
        LinearLayout.LayoutParams sigParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sigParams.topMargin = Miscellaneous.dp2px(card.getContext(), 2);
        signature.setLayoutParams(sigParams);

        textCol.addView(name);
        textCol.addView(signature);
        topRow.addView(avatar);
        topRow.addView(textCol);
        card.addView(topRow);

        // 下半部分：社交按钮靠右
        LinearLayout socialRow = new LinearLayout(card.getContext());
        socialRow.setOrientation(LinearLayout.HORIZONTAL);
        socialRow.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams socialParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        socialParams.topMargin = Miscellaneous.dp2px(card.getContext(), 10);
        socialRow.setLayoutParams(socialParams);

        addDonateButton(socialRow);
        addSocialButton(socialRow, R.drawable.icon_github, Strings.URL_GITHUB);
        addSocialButton(socialRow, R.drawable.icon_blog, Strings.URL_BLOG);
        addSocialButton(socialRow, R.drawable.icon_qq, Strings.URL_QQ);

        card.addView(socialRow);
    }

    private void addDonateButton(LinearLayout container) {
        ImageView btn = new ImageView(container.getContext());
        btn.setImageResource(R.drawable.donate);
        btn.setColorFilter(Colors.DONATE_ICON_TINT, android.graphics.PorterDuff.Mode.SRC_IN);
        int size = Miscellaneous.dp2px(container.getContext(), 32);
        int pad = Miscellaneous.dp2px(container.getContext(), 4);
        btn.setPadding(pad, pad, pad, pad);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(Miscellaneous.dp2px(container.getContext(), 12), 0, 0, 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("wechat", Strings.WE_CHAT));
            Toast.makeText(v.getContext(), Strings.DONATE_TOAST, Toast.LENGTH_LONG).show();
        });
        container.addView(btn);
    }

    private void addSocialButton(LinearLayout container, int iconRes, String url) {
        ImageView btn = new ImageView(container.getContext());
        btn.setImageResource(iconRes);
        btn.setColorFilter(Colors.SOCIAL_ICON_TINT, android.graphics.PorterDuff.Mode.SRC_IN);
        int size = Miscellaneous.dp2px(container.getContext(), 32);
        int pad = Miscellaneous.dp2px(container.getContext(), 4);
        btn.setPadding(pad, pad, pad, pad);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(Miscellaneous.dp2px(container.getContext(), 12), 0, 0, 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> {
            try {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception ignored) {}
        });
        container.addView(btn);
    }
}
