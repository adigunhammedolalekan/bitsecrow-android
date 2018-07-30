package com.bitsescrow.app.bitsescrow.ui.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bitsescrow.app.bitsescrow.ui.fragments.EscrowFragment;
import com.bitsescrow.app.bitsescrow.ui.fragments.UserEscrowsFragment;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class MainTabAdapter extends FragmentPagerAdapter {

    public MainTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return EscrowFragment.newInstance();
            case 1:
                return UserEscrowsFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Escrows";
            case 1:
                return "My Escrows";
        }

        return "";
    }
}
