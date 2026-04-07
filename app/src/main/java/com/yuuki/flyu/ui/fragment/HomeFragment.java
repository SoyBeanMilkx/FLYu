package com.yuuki.flyu.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuuki.flyu.App;
import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.fragment.children.AuthorCardBuilder;
import com.yuuki.flyu.ui.fragment.children.DeviceInfoCardBuilder;
import com.yuuki.flyu.ui.fragment.children.StatusCardBuilder;
import com.yuuki.flyu.ui.fragment.children.ThanksCardBuilder;
import com.yuuki.flyu.ui.widget.DynamicSubtitleView;
import com.yuuki.flyu.ui.widget.LinearLayoutPro;

import io.github.libxposed.service.XposedService;

public class HomeFragment extends Fragment implements App.ServiceStateListener {

    private LinearLayoutPro cardStatus;
    private final StatusCardBuilder statusBuilder = new StatusCardBuilder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        DynamicSubtitleView subtitle = root.findViewById(R.id.home_subtitle);
        subtitle.setGreetingMode();

        cardStatus = root.findViewById(R.id.card_status);
        setupCard(cardStatus);
        statusBuilder.build(cardStatus);
        statusBuilder.updateStatus(App.getService());

        LinearLayoutPro cardDeviceInfo = root.findViewById(R.id.card_device_info);
        setupCard(cardDeviceInfo);
        new DeviceInfoCardBuilder().build(cardDeviceInfo);

        LinearLayoutPro cardAuthor = root.findViewById(R.id.card_author);
        setupCard(cardAuthor);
        new AuthorCardBuilder().build(cardAuthor);

        LinearLayoutPro cardThanks = root.findViewById(R.id.card_thanks);
        setupCard(cardThanks);
        new ThanksCardBuilder().build(cardThanks);

        // 液体玻璃绑定（需要 layout 完成后，状态卡片不使用液体玻璃）
        root.post(() -> {
            ViewGroup backView = getActivity().findViewById(R.id.back_view);
            cardDeviceInfo.enableLiquidGlass(backView);
            cardAuthor.enableLiquidGlass(backView);
            cardThanks.enableLiquidGlass(backView);
        });

        return root;
    }

    private void setupCard(LinearLayoutPro card) {
        card.setCornerRadius(28);
        card.setBorderWidth(0);
        card.setContentPadding(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        App.addServiceStateListener(this, true);
    }

    @Override
    public void onStop() {
        App.removeServiceStateListener(this);
        super.onStop();
    }

    @Override
    public void onServiceStateChanged(XposedService service) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> statusBuilder.updateStatus(service));
    }
}
