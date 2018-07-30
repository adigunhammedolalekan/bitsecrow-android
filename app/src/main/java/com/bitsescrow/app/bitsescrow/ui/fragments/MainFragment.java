package com.bitsescrow.app.bitsescrow.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.ui.activities.MainActivity;
import com.bitsescrow.app.bitsescrow.ui.activities.NewEscrowActivity;
import com.bitsescrow.app.bitsescrow.ui.adapters.MainTabAdapter;
import com.bitsescrow.app.bitsescrow.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.view_pager_main)
    ViewPager mViewPager;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    public static final String TAG = "MainFragmentTAG";

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(mToolbar);
            ActionBar actionBar = mainActivity.getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(getString(R.string.app_name));
        }

        initTabs();
    }

    private void initTabs() {

        MainTabAdapter mainTabAdapter = new MainTabAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mainTabAdapter);

    }

    @OnClick(R.id.btn_new_escrow) public void onNewEscrowClick() {
        startActivity(new Intent(getActivity(), NewEscrowActivity.class));
    }
}
