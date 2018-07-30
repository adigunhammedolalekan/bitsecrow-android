package com.bitsescrow.app.bitsescrow.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by Lekan Adigun on 12/21/2017.
 */

public class BaseFragment extends Fragment {


    private volatile boolean isOn = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onPause() {
        super.onPause();
        isOn = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        isOn = true;
    }

    public void showDialog(String title, String message) {

        /*
        *
        * Do not attempt to show dialog if activity is not visible
        * */
        if(!isOn) return;

        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    public void toast(String message) {

        if(!isOn) return;

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

    public void snack(String message) {

        if(!isOn) return;

        View view = getView();
        if (view != null) {
            try {
                Snackbar.make(view.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                        .show();
            }catch (Exception e) {
                toast(message);
            }
        }else {
            toast(message);
        }
    }

    public void show(View... views) {

        if(views == null) return;
        for (View view : views)
            view.setVisibility(View.VISIBLE);
    }

    public void hide(View... views) {

        if(views == null) return;
        for (View view : views)
            view.setVisibility(View.GONE);

    }

}
