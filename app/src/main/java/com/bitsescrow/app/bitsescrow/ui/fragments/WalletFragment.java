package com.bitsescrow.app.bitsescrow.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.Wallet;
import com.bitsescrow.app.bitsescrow.ui.base.BaseFragment;
import com.bitsescrow.app.bitsescrow.utils.L;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONObject;

import butterknife.BindView;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class WalletFragment extends BaseFragment {

    @BindView(R.id.tv_wallet_address)
    TextView addressTextView;
    @BindView(R.id.tv_wallet_balance)
    TextView walletBalanceTextView;
    @BindView(R.id.iv_qr)
    ImageView qrImageView;
    @BindView(R.id.toolbar_wallet)
    Toolbar mToolbar;

    private Wallet mWallet;

    public static final String TAG = "WalletFragment";

    public static WalletFragment newInstance() {

        Bundle args = new Bundle();

        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wallet_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar.setTitle("Wallet");
        fetchWallet();
    }

    private void fetchWallet() {

        Requests.go("/me/wallet", new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                try {
                    if (jsonObject.getBoolean("status")) {
                        mWallet = new Wallet(jsonObject.getJSONObject("data"));
                    }

                    if (mWallet != null) {
                        walletBalanceTextView.setText(mWallet.balance());
                        addressTextView.setText(mWallet.address);

                        Bitmap bitmap = QRCode.from(mWallet.address).bitmap();
                        qrImageView.setImageBitmap(bitmap);
                    }
                }catch (Exception e) {
                    L.wtf(e);
                }
            }

            @Override
            public void error(Throwable throwable) {

            }
        });
    }
}
