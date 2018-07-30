package com.bitsescrow.app.bitsescrow.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.utils.L;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.policy.TimeWindow;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/23/2018.
 */

public class SendFeedbackActivity extends BaseActivity {


    @BindView(R.id.edt_title_send_feedback)
    MaterialEditText titleEditText;
    @BindView(R.id.edt_body_send_feedback)
    MaterialEditText bodyEditText;
    @BindView(R.id.tv_label_add_attachment_send_feedback)
    TextView addAttachmentLabelTextView;

    private String attachmentURI = "", localURI = "";
    public static final int RC_ADD_ATTACHMENT = 12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_send_feedback);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Send Feedback");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.btn_send_feedback_) public void onSendFeedbackClick() {
        sendFeedback();
    }

    private void sendFeedback() {

        String title = Util.textOf(titleEditText);
        String body = Util.textOf(bodyEditText);

        if (title.isEmpty()) {
            titleEditText.setError("Add title");
            return;
        }

        if (body.isEmpty()) {
            bodyEditText.setError("Add description/body");
            return;
        }

        if (localURI != null
                && !localURI.isEmpty()) {
            uploadAttachment();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("body", body);
            if (attachmentURI != null
                    && !attachmentURI.isEmpty()) {
                jsonObject.put("attachment", attachmentURI);
            }
        }catch (Exception e) {}

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending...");
        progressDialog.show();

        Requests.post("/feedback/new", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {
                progressDialog.cancel();
                try {
                    if (jsonObject.getBoolean("status")) {
                        snack("Feedback Posted. Thanks!");
                    }
                }catch (Exception e) {}
            }

            @Override
            public void error(Throwable throwable) {

                progressDialog.cancel();
                snack("Failed to post feedback due to error. Please, retry.");
            }
        });
    }

    private void uploadAttachment() {

        String pubId = Util.randString() + ".jpg";
        MediaManager.get()
                .upload(Uri.fromFile(new File(localURI)))
                .option("public_id", pubId)
                .unsigned(Util.CLOUDPRESET)
                .constrain(TimeWindow.immediate())
                .dispatch();

        attachmentURI = MediaManager.get().url().generate(pubId);
    }

    private void openImagePicker() {

        ImagePicker.create(this)
                .single()
                .start(RC_ADD_ATTACHMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null
                && requestCode == RC_ADD_ATTACHMENT) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            addAttachmentLabelTextView.setText(String.valueOf("Attachment Added : " + image.getName()));

            resize(image);
        }
    }

    private void resize(Image image) {

        if (image == null) return;

        Tiny.getInstance().source(new File(image.getPath()))
                .asFile().compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {

                if (!isSuccess || t != null) {
                    L.wtf(t);
                    return;
                }

                localURI = outfile;
            }
        });
    }

    @OnClick(R.id.btn_add_attachment_send_feedback) public void onAddAttachmentClick() {
        openImagePicker();
    }
}
