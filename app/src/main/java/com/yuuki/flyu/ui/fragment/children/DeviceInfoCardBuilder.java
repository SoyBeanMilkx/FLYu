package com.yuuki.flyu.ui.fragment.children;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuuki.flyu.ui.Colors;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.widget.LinearLayoutPro;
import com.yuuki.flyu.utils.DeviceInfo;
import com.yuuki.flyu.utils.Miscellaneous;

public class DeviceInfoCardBuilder {

    public void build(LinearLayoutPro card) {
        int hPad = Miscellaneous.dp2px(card.getContext(), 20);
        int vPad = Miscellaneous.dp2px(card.getContext(), 16);
        card.setPadding(hPad, vPad, hPad, vPad);

        addInfoRow(card, Strings.LABEL_DEVICE_NAME, DeviceInfo.getDeviceName());
        addInfoRow(card, Strings.LABEL_SYSTEM_VERSION, DeviceInfo.getSystemVersion());
        addInfoRow(card, Strings.LABEL_DEVICE_ARCH, DeviceInfo.getDeviceArch());
        addInfoRow(card, Strings.LABEL_SYSTEMUI_VERSION, DeviceInfo.getSystemUiVersion(card.getContext()));

        TextView copyBtn = new TextView(card.getContext());
        copyBtn.setText(Strings.COPY);
        copyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        copyBtn.setTextColor(Colors.CARD_TEXT_LINK);
        copyBtn.setTypeface(Typeface.DEFAULT_BOLD);
        int btnPad = Miscellaneous.dp2px(card.getContext(), 6);
        copyBtn.setPadding(btnPad, btnPad, btnPad, btnPad);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = Gravity.END;
        copyBtn.setLayoutParams(btnParams);
        copyBtn.setOnClickListener(v -> {
            String text = String.format(Strings.DEVICE_INFO_COPY_FORMAT,
                    DeviceInfo.getDeviceName(), DeviceInfo.getSystemVersion(),
                    DeviceInfo.getDeviceArch(), DeviceInfo.getSystemUiVersion(v.getContext()));
            ClipboardManager cm = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("DeviceInfo", text));
            Toast.makeText(v.getContext(), Strings.COPIED_TOAST, Toast.LENGTH_SHORT).show();
        });
        card.addView(copyBtn);
    }

    private void addInfoRow(LinearLayoutPro card, String label, String value) {
        LinearLayout row = new LinearLayout(card.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int rowVPad = Miscellaneous.dp2px(card.getContext(), 6);
        row.setPadding(0, rowVPad, 0, rowVPad);

        TextView labelView = new TextView(card.getContext());
        labelView.setText(label);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        labelView.setTextColor(Colors.CARD_TEXT_BODY);
        labelView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView valueView = new TextView(card.getContext());
        valueView.setText(value);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        valueView.setTextColor(Colors.CARD_TEXT_BODY);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        valueParams.leftMargin = Miscellaneous.dp2px(card.getContext(), 12);
        valueView.setLayoutParams(valueParams);

        row.addView(labelView);
        row.addView(valueView);
        card.addView(row);
    }
}
