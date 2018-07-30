package com.bitsescrow.app.bitsescrow.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.Escrow;
import com.bitsescrow.app.bitsescrow.ui.adapters.EscrowsListAdapter;
import com.bitsescrow.app.bitsescrow.ui.base.BaseFragment;
import com.bitsescrow.app.bitsescrow.utils.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class EscrowFragment extends BaseFragment {

    @BindView(R.id.rv_escrows)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_layout_escrows)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_escrows)
    LinearLayout emptyEscrowLayout;

    private List<Escrow> mEscrows = new ArrayList<>();
    private EscrowsListAdapter escrowsListAdapter;
    private int retryCount = 0;

    public static EscrowFragment newInstance() {

        Bundle args = new Bundle();

        EscrowFragment fragment = new EscrowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_escrows, container, false);
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

        setupAdapter();
        initDownload();
    }

    private void initDownload() {

        swipeRefreshLayout.setRefreshing(true);
        Requests.go("/me/escrows/linked", new Requests.ResponseListener() {
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

    @Override
    public void onResume() {
        super.onResume();
        L.fine("Resumed");
    }
}
