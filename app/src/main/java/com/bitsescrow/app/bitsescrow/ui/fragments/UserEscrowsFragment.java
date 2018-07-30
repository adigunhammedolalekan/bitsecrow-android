package com.bitsescrow.app.bitsescrow.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.Escrow;
import com.bitsescrow.app.bitsescrow.ui.activities.NewEscrowActivity;
import com.bitsescrow.app.bitsescrow.ui.adapters.EscrowsListAdapter;
import com.bitsescrow.app.bitsescrow.ui.base.BaseFragment;
import com.bitsescrow.app.bitsescrow.utils.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class UserEscrowsFragment extends BaseFragment {

    @BindView(R.id.toolbar_user_escrows)
    Toolbar mToolbar;
    @BindView(R.id.rv_user_escrows)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_layout_users_escrow)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_escrow_funded_main)
    LinearLayout emptyEscrowLayout;

    private List<Escrow> mEscrows = new ArrayList<>();
    private EscrowsListAdapter escrowsListAdapter;
    private int retryCount = 0;
    public static UserEscrowsFragment newInstance() {

        Bundle args = new Bundle();

        UserEscrowsFragment fragment = new UserEscrowsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_user_escrows, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDownload();
            }
        });
        mToolbar.setTitle(getString(R.string.app_name));

        setupAdapter();
        initDownload();
    }

    private void initDownload() {

        swipeRefreshLayout.setRefreshing(true);
        Requests.go("/me/escrows", new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                swipeRefreshLayout.setRefreshing(false);
                try {

                    retryCount = 0;
                    boolean success = jsonObject.has("status")
                            && jsonObject.getBoolean("status");
                    if (success) {
                        mEscrows.clear();
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            Escrow escrow = new Escrow(data.getJSONObject(i));
                            mEscrows.add(escrow);
                        }

                        if (mEscrows.size() <= 0)
                            show(emptyEscrowLayout);
                        else
                            hide(emptyEscrowLayout);

                        if (escrowsListAdapter != null)
                            escrowsListAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e) {
                    L.wtf(e);
                }

            }

            @Override
            public void error(Throwable throwable) {

                swipeRefreshLayout.setRefreshing(false);
                retryCount++;
                if (retryCount >= 5) {
                    initDownload();
                    return;
                }

                snack("Failed to fetch data. Are you sure you are connected to the internet?");
            }
        });
    }

    private void setupAdapter() {

        escrowsListAdapter = new EscrowsListAdapter(getActivity(), mEscrows);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(escrowsListAdapter);

    }

    @OnClick(R.id.btn_new_escrow) public void onNewEscrowClick() {
        startActivity(new Intent(getActivity(), NewEscrowActivity.class));
    }
}
