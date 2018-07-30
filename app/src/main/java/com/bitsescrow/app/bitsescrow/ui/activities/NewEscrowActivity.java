package com.bitsescrow.app.bitsescrow.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class NewEscrowActivity extends BaseActivity {

    @BindView(R.id.layout_amount)
    LinearLayout amountLayout;
    @BindView(R.id.tv_btc_fund_escrow)
    TextView btcTextView;
    @BindView(R.id.tv_usd_fund_escrow)
    TextView usdTextView;
    @BindView(R.id.edt_beneficiary_email_fund_escrow)
    MaterialEditText emailEditText;
    @BindView(R.id.edt_amount_fund_escrow)
    MaterialEditText amountEditText;
    @BindView(R.id.edt_narration_fund_escrow)
    MaterialEditText narrationEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fund_escrow);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Fund Escrow");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        amountEditText.addTextChangedListener(textWatcher);
    }

    @OnClick(R.id.btn_fund_escrow) public void onFundClick() {
        fund();
    }

    public void fund() {

        Util.hideKeyboard(this);
        String email = Util.textOf(emailEditText);
        String amountSt = Util.textOf(amountEditText);
        String narration = Util.textOf(narrationEditText);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email address.");
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountSt);
        }catch (Exception e) {
            amount = 0;
        }

        if (amount <= 0) {
            amountEditText.setError("Enter a valid BTC amount");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to_email", email);
            jsonObject.put("amount", amount);
            jsonObject.put("narration", narration);
        }catch (Exception e) {}

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Progress status");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Requests.post("/escrow/new", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                progressDialog.cancel();
                try {
                    if (jsonObject.has("status")
                            && jsonObject.getBoolean("status")) {
                        toast("Escrow has been funded successfully.");
                        Intent intent = new Intent(NewEscrowActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }else {
                        snack(jsonObject.getString("message"));
                    }
                }catch (Exception e) {}
            }

            @Override
            public void error(Throwable throwable) {

                progressDialog.cancel();
                snack("Error occurred while connecting to server. Make sure you have a valid internet connection");
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            int amountBtc = 0;
            try {
                amountBtc = Integer.parseInt(s.toString().trim());
            }catch (Exception e) {}


            if (amountBtc <= 0) {
                hide(amountLayout);
            }else {
                show(amountLayout);
            }

            btcTextView.setText(s.toString());
            usdTextView.setText(String.valueOf(amountBtc * 8001 + "USD"));
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
