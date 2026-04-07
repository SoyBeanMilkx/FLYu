package com.yuuki.flyu.ui.fragment.manager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.yuuki.flyu.R;
import com.yuuki.flyu.ui.fragment.ConfigFragment;
import com.yuuki.flyu.ui.fragment.HomeFragment;

public class FragmentNavigator {

    public static final int INDEX_HOME = 0;
    public static final int INDEX_CONFIG = 1;

    private static final String TAG_HOME = "fragment_home";
    private static final String TAG_CONFIG = "fragment_config";

    private final FragmentManager fragmentManager;
    private final int containerId;
    private final Fragment[] fragments;
    private Fragment activeFragment;

    public FragmentNavigator(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        Fragment homeFragment = fragmentManager.findFragmentByTag(TAG_HOME);
        Fragment configFragment = fragmentManager.findFragmentByTag(TAG_CONFIG);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        if (configFragment == null) {
            configFragment = new ConfigFragment();
        }
        this.fragments = new Fragment[]{
                homeFragment,
                configFragment
        };
    }

    public void init() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerId, fragments[INDEX_HOME], TAG_HOME);
        transaction.add(containerId, fragments[INDEX_CONFIG], TAG_CONFIG);
        transaction.hide(fragments[INDEX_CONFIG]);
        transaction.commit();
        activeFragment = fragments[INDEX_HOME];
    }

    public void restore() {
        activeFragment = resolveActiveFragment();
    }

    public void switchTo(int index) {
        if (index < 0 || index >= fragments.length) {
            return;
        }
        if (activeFragment == null) {
            activeFragment = resolveActiveFragment();
        }

        Fragment target = fragments[index];
        if (target == activeFragment) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        transaction.hide(activeFragment);
        transaction.show(target);
        transaction.commit();
        activeFragment = target;
    }

    public int getCurrentIndex() {
        if (activeFragment == fragments[INDEX_CONFIG]) {
            return INDEX_CONFIG;
        }
        return INDEX_HOME;
    }

    public int handleBackPress() {
        if (activeFragment != fragments[INDEX_HOME]) {
            switchTo(INDEX_HOME);
            return INDEX_HOME;
        }
        return -1;
    }

    private Fragment resolveActiveFragment() {
        if (fragments[INDEX_CONFIG].isAdded() && !fragments[INDEX_CONFIG].isHidden()) {
            return fragments[INDEX_CONFIG];
        }
        return fragments[INDEX_HOME];
    }
}
