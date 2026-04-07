package com.yuuki.flyu.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuuki.flyu.App;
import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.ui.fragment.children.ClockItems;
import com.yuuki.flyu.ui.fragment.children.HwMonitorItems;
import com.yuuki.flyu.ui.fragment.children.LockScreenItems;
import com.yuuki.flyu.ui.fragment.children.MiscItems;
import com.yuuki.flyu.ui.fragment.children.StatusBarItems;
import com.yuuki.flyu.ui.widget.DynamicSubtitleView;
import com.yuuki.flyu.ui.widget.LinearLayoutPro;
import com.yuuki.flyu.ui.widget.collapse.CollapsibleListAdapter;

import java.util.Arrays;

import io.github.libxposed.service.XposedService;

public class ConfigFragment extends Fragment implements App.ServiceStateListener {

    private LinearLayoutPro cardStatusBar;
    private LinearLayoutPro cardClock;
    private LinearLayoutPro cardMisc;
    private LinearLayoutPro cardLockScreen;
    private LinearLayoutPro cardHwMonitor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config, container, false);

        cardStatusBar = root.findViewById(R.id.card_statusbar);
        cardClock = root.findViewById(R.id.card_clock);
        cardMisc = root.findViewById(R.id.card_misc);
        cardLockScreen = root.findViewById(R.id.card_lockscreen);
        cardHwMonitor = root.findViewById(R.id.card_hw_monitor);

        setupCard(cardStatusBar);
        setupCard(cardClock);
        setupCard(cardMisc);
        setupCard(cardLockScreen);
        setupCard(cardHwMonitor);

        populateCards();

        // 轮播副标题
        DynamicSubtitleView subtitle = root.findViewById(R.id.config_subtitle);
        subtitle.setCarouselMode(Arrays.asList(
                Strings.CAROUSEL_1, Strings.CAROUSEL_2, Strings.CAROUSEL_3,
                Strings.CAROUSEL_4, Strings.CAROUSEL_5
        ));

        // 液体玻璃背景
        root.post(() -> {
            ViewGroup backView = getActivity().findViewById(R.id.back_view);
            cardStatusBar.enableLiquidGlass(backView);
            cardClock.enableLiquidGlass(backView);
            cardMisc.enableLiquidGlass(backView);
            cardLockScreen.enableLiquidGlass(backView);
            cardHwMonitor.enableLiquidGlass(backView);
        });

        return root;
    }

    private void populateCards() {
        if (cardStatusBar == null || cardClock == null || cardMisc == null || cardLockScreen == null || cardHwMonitor == null) return;
        CollapsibleListAdapter adapter = new CollapsibleListAdapter(cardStatusBar.getContext());
        adapter.populate(cardStatusBar, StatusBarItems.build(cardStatusBar.getContext()));
        adapter.populate(cardClock, ClockItems.build());
        adapter.populate(cardMisc, MiscItems.build());
        adapter.populate(cardLockScreen, LockScreenItems.build());
        adapter.populate(cardHwMonitor, HwMonitorItems.build(cardHwMonitor.getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        App.addServiceStateListener(this, false);
    }

    @Override
    public void onStop() {
        App.removeServiceStateListener(this);
        super.onStop();
    }

    @Override
    public void onServiceStateChanged(XposedService service) {
        if (service != null && getActivity() != null) {
            getActivity().runOnUiThread(this::populateCards);
        }
    }

    private void setupCard(LinearLayoutPro card) {
        card.setCornerRadius(28);
        card.setBorderWidth(0);
        card.setContentPadding(0);
    }
}
