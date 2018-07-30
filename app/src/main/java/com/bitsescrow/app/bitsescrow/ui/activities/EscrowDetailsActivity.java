package com.bitsescrow.app.bitsescrow.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.Escrow;
import com.bitsescrow.app.bitsescrow.models.User;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Locale;

import javax.microedition.khronos.opengles.GL;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class EscrowDetailsActivity extends BaseActivity {

    @BindView(R.id.tv_amount_escrow_view_)
    TextView btcTextView;
    @BindView(R.id.tv_name_es_view)
    TextView nameTextView;
    @BindView(R.id.tv_email_es_view)
    TextView emailTextView;
    @BindView(R.id.iv_user_es_view)
    CircleImageView circleImageView;
    @BindView(R.id.tv_narration_es_view)
    TextView narrationTextView;
    @BindView(R.id.tv_release_btc_instruction)
    TextView releaseCoinTextView;
    @BindView(R.id.tv_report_problem_instruction)
    TextView reportProblemTextView;
    @BindView(R.id.tv_cancel_btc_instruction)
    TextView cancelTextView;
    @BindView(R.id.tv_es_status)
    TextView statusTextView;

    @BindView(R.id.btn_cancel_escrow)
    RelativeLayout cancelLayout;
    @BindView(R.id.btn_release_coin)
    RelativeLayout releaseLayout;
    @BindView(R.id.btn_report_problem)
    RelativeLayout reportProblemLayout;

    private Escrow mEscrow;
    private String operation = ""; //Could be cancel or release

    public static final String OP = "Operation", C = "cancel", RE = "release";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_escrow_details);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        mEscrow = Parcels.unwrap(intent.getParcelableExtra(Escrow.KEY));
        render();
        showAppropriateOperations();
        subscribeToTopic();
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(String.format(Locale.getDefault(), "%s%d", "/topics/escrow", mEscrow.ID));
    }

    private void render() {

        nameTextView.setText(mEscrow.from.name());
        btcTextView.setText(mEscrow.amount());
        emailTextView.setText(mEscrow.from.email);
        narrationTextView.setText(mEscrow.narration());
        statusTextView.setText(mEscrow.status());
        statusTextView.setTextColor(ContextCompat.getColor(this, mEscrow.color()));

        if (mEscrow.from.hasPhoto()) {
            Glide.with(this)
                    .load(mEscrow.from.picture)
                    .apply(Util.requestOptions())
                    .into(circleImageView);
        }

        String text = "Coin will be released to <b>" + mEscrow.to.name() + "</b> and escrow will be marked COMPLETED.";
        releaseCoinTextView.setText(text);

        text = "Report any issue here.";
        reportProblemTextView.setText(text);

        text = "Bitcoin will be sent back to " + mEscrow.from.name() + " if this transaction is cancelled";
        cancelTextView.setText(text);
    }

    @OnClick(R.id.btn_release_coin) public void onReleaseCoinClick() {

        if (mEscrow.mStatus == Escrow.Status.COMPLETED) {
            snack("Bitcoin has already been released");
            return;
        }

        displayWarning();
    }

    private void displayWarning() {

        new AlertDialog.Builder(this)
                .setTitle("Release Bitcoin?")
                .setMessage("Do you really want to release this coin?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        operation = RE;
                        generateCode();
                    }
                })
                .setNegativeButton("No,Thanks", null)
                .create()
                .show();
    }

    private void generateCode() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.show();
        Requests.post("/me/auth", new HashMap<String, String>(), new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {
                progressDialog.cancel();

                try {
                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        Intent intent = new Intent(EscrowDetailsActivity.this, VerificationActivity.class);
                        intent.putExtra(Escrow.KEY, Parcels.wrap(mEscrow));
                        intent.putExtra(OP, operation);
                        startActivity(intent);
                    }else {
                        snack(jsonObject.getString("message"));
                    }
                }catch (Exception e) {}
            }

            @Override
            public void error(Throwable throwable) {
                progressDialog.cancel();
                toast("Failed to generate code. Please, retry.");
            }
        });
    }

    @OnClick(R.id.btn_report_problem) public void onReportProblemClick() {
        Intent intent = new Intent(this, ReportProblemActivity.class);
        intent.putExtra(Escrow.KEY, Parcels.wrap(mEscrow));
        startActivity(intent);
    }

    @OnClick(R.id.btn_cancel_escrow) public void onCancelClick() {
        showWarningForCancelTransaction();
    }

    private void showWarningForCancelTransaction() {

        new AlertDialog.Builder(this)
                .setTitle("Cancel Escrow Transaction")
                .setMessage("Are you sure you want to cancel this transaction? Be aware that Bitcoin will be sent back to "
                + mEscrow.from.name() + "")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        operation = C;
                        generateCode();
                    }
                })
                .create()
                .show();
    }

    /*
    * Hide and show appropriate Buttons/Operations for users
    * */
    private void showAppropriateOperations() {

        User user = User.user();
        if (user == null) return;

        if (user.ID == mEscrow.from.ID) {
            hide(cancelLayout);
            show(releaseLayout);
        }else if (user.ID == mEscrow.to.ID) {
            hide(releaseLayout);
            show(cancelLayout);
        }
    }
}
