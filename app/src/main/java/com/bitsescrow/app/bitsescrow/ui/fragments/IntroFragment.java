package com.bitsescrow.app.bitsescrow.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.models.Slide;
import com.bitsescrow.app.bitsescrow.ui.activities.SignUpActivity;
import com.bitsescrow.app.bitsescrow.ui.adapters.SlideAdapter;
import com.bitsescrow.app.bitsescrow.ui.base.BaseFragment;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class IntroFragment extends BaseFragment {

    @BindView(R.id.view_pager_entry_)
    ViewPager mViewPager;
    @BindView(R.id.pager_indicator_entry_activity)
    ViewPagerIndicator mViewPagerIndicator;

    private List<Slide> slides = new ArrayList<>();

    public static IntroFragment newInstance() {

        Bundle args = new Bundle();

        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_intro_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addSlides();
    }

    private void addSlides() {

        slides.add(new Slide(R.drawable.ic_world, "Portable and Reliable",
                "The amazing power of Bitcoin brought to your mobile phone."));
        slides.add(new Slide(R.drawable.ic_fast, "Fast", "Browse through offers with the smoothest, seamless experience."));
        slides.add(new Slide(R.drawable.ic_security_black_24dp, "Secure", "Our system has been tested by high level security researchers to make sure you are save from attacks."));

        SlideAdapter slideAdapter = new SlideAdapter(getActivity(), slides);
        mViewPager.setAdapter(slideAdapter);
        mViewPagerIndicator.setupWithViewPager(mViewPager);
    }

    @OnClick(R.id.btn_get_started_entry_activity) public void onGetStartedClick() {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
