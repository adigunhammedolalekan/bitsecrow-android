package com.bitsescrow.app.bitsescrow.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.Escrow;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.utils.L;
import com.bitsescrow.app.bitsescrow.utils.Util;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/20/2018.
 */

public class VerificationActivity extends BaseActivity {

    @BindView(R.id.edt_code_verification_activity)
    EditText codeEditText;
    @BindView(R.id.btn_verify_code_)
    Button verifyCodeButton;
    @BindView(R.id.btn_release__coin)
    Button releaseCoinButton;
    @BindView(R.id.tv_count_down_timer)
    TextView countDownTextView;

    private Escrow mEscrow;
    private String operation = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_otp_verification_activity);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Verification");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEscrow = Parcels.unwrap(intent.getParcelableExtra(Escrow.KEY));
        operation = intent.getStringExtra(EscrowDetailsActivity.OP);
        downTimer.start();
    }

    @OnClick(R.id.btn_verify_code_) public void onVerifyCodeClick() {
        verifyCode();
    }

    private void verifyCode() {

        String code = Util.textOf(codeEditText);
        if (code.length() <= 0) {
            codeEditText.setError("Enter authentication code");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
        }catch (Exception e) {}

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying code...");
        progressDialog.show();

        Requests.post("/me/code/verify", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                progressDialog.cancel();
                try {
                    boolean success = jsonObject.has("status")
                            && jsonObject.getBoolean("status");
                    if (success) {
                        hide(verifyCodeButton);
                        show(releaseCoinButton);

                        if (operation.equalsIgnoreCase(EscrowDetailsActivity.C)) {
                            releaseCoinButton.setText("CANCEL TRANSACTION");
                            cancelTransaction();
                        }else {
                            releaseCoinButton.setText("RELEASE BITCOIN");
                            releaseCoin();
                        }
                    }else {
                        snack(jsonObject.getString("message"));
                    }
                }catch (Exception e) {}
            }

            @Override
            public void error(Throwable throwable) {
                progressDialog.cancel();
                toast("Failed to complete network request. Please, retry.");
            }
        });
    }

    private void cancelTransaction() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cancelling transaction...");
        progressDialog.show();

        Requests.post("/escrow/cancel/" + mEscrow.ID, new HashMap<String, String>(), new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                progressDialog.cancel();
                try {
                    boolean success = jsonObject.has("status")
                            && jsonObject.getBoolean("status");
                    if (success) {
                        toast("Transaction has been cancelled");
                        Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }catch (Exception e) {}

            }

            @Override
            public void error(Throwable throwable) {

                progressDialog.cancel();
                toast("Failed to complete request. Please, retry");
            }
        });
    }

    @OnClick(R.id.btn_resend_code) public void onResendCodeClick() {
        reGenerateCode();
    }

    private void reGenerateCode() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resending authentication code...");
        progressDialog.show();


        Requests.post("/me/auth", new HashMap<String, String>(), new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                progressDialog.cancel();
                toast("Code sent!");
            }

            @Override
            public void error(Throwable throwable) {
                progressDialog.cancel();
                toast("Failed to send code. Please, retry");
            }
        });
    }

    private void releaseCoin() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Releasing coin...");
        progressDialog.show();

        Requests.post("/escrow/release/" + mEscrow.ID, new HashMap<String, String>(), new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                progressDialog.cancel();
                try {
                    boolean success = jsonObject.has("status")
                            && jsonObject.getBoolean("status");
                    if (success) {
                        toast("Coin has been released");
                        Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }catch (Exception e) {}

            }

            @Override
            public void error(Throwable throwable) {

                progressDialog.cancel();
                toast("Failed to complete request. Please, retry");
            }
        });
    }

    private CountDownTimer downTimer = new CountDownTimer(2 * 60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            long secsRem = millisUntilFinished / 1000;
            L.fine("Time Remained => " + secsRem);

            String time = timeLeft(secsRem);
            countDownTextView.setText(String.valueOf("Time Remained : " + time));
        }

        @Override
        public void onFinish() {

        }
    };

    @OnClick(R.id.btn_release__coin) public void onReleaseCoinClick() {
        releaseCoin();
    }

    String timeLeft(long seconds) {

        long left = seconds - 59;
        if (left > 59)
            return "01 : " + (59 - left);

        return "00 : " + (59 - left);
    }
}
