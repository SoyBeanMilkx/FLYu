package com.yuuki.flyu.ui.fragment.children;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.LinearLayoutPro;
import com.yuuki.flyu.utils.Miscellaneous;

public class ThanksCardBuilder {

    public void build(LinearLayoutPro card) {
        int hPad = Miscellaneous.dp2px(card.getContext(), 20);
        int vPad = Miscellaneous.dp2px(card.getContext(), 16);
        card.setPadding(hPad, vPad, hPad, vPad);

        // 标题
        TextView title = new TextView(card.getContext());
        title.setText(Strings.THANKS_TITLE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Colors.CARD_TEXT_TITLE);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = Miscellaneous.dp2px(card.getContext(), 8);
        title.setLayoutParams(titleParams);
        card.addView(title);

        addThanksItem(card, true, Strings.THANKS_REPO_1, Strings.THANKS_URL_1);
        addThanksItem(card, true, Strings.THANKS_REPO_2, Strings.THANKS_URL_2);
        addThanksItem(card, true, Strings.THANKS_REPO_3, Strings.THANKS_URL_3);
        addThanksItem(card, true, Strings.THANKS_REPO_4, Strings.THANKS_URL_4);
        addThanksItem(card, false, Strings.THANKS_ARTICLE_1, Strings.THANKS_ARTICLE_URL_1);
    }

    private void addThanksItem(LinearLayoutPro card, boolean isRepo, String name, String url) {
        LinearLayout row = new LinearLayout(card.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int rowVPad = Miscellaneous.dp2px(card.getContext(), 8);
        row.setPadding(0, rowVPad, 0, rowVPad);

        // 图标
        ImageView icon = new ImageView(card.getContext());
        int iconSize = Miscellaneous.dp2px(card.getContext(), 28);
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        int iconPad = Miscellaneous.dp2px(card.getContext(), 3);
        icon.setPadding(iconPad, iconPad, iconPad, iconPad);
        icon.setImageResource(isRepo ? R.drawable.icon_github : R.drawable.icon_blog);
        icon.setColorFilter(Colors.CARD_ICON_TINT, android.graphics.PorterDuff.Mode.SRC_IN);

        // 文字区域
        LinearLayout textCol = new LinearLayout(card.getContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.leftMargin = Miscellaneous.dp2px(card.getContext(), 14);
        textCol.setLayoutParams(textParams);

        TextView nameView = new TextView(card.getContext());
        nameView.setText(name);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        nameView.setTextColor(Colors.CARD_TEXT_TITLE);
        nameView.setTypeface(Typeface.DEFAULT_BOLD);

        TextView urlView = new TextView(card.getContext());
        urlView.setText(url);
        urlView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        urlView.setTextColor(Colors.CARD_TEXT_SUBTITLE);
        urlView.setSingleLine(true);
        urlView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        urlParams.topMargin = Miscellaneous.dp2px(card.getContext(), 1);
        urlView.setLayoutParams(urlParams);

        textCol.addView(nameView);
        textCol.addView(urlView);

        row.addView(icon);
        row.addView(textCol);
        card.addView(row);

        // 点击跳转
        row.setOnClickListener(v -> {
            try {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception ignored) {}
        });
    }
}
