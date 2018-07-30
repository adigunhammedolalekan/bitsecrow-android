package com.bitsescrow.app.bitsescrow.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.RepositoryManager;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.User;
import com.bitsescrow.app.bitsescrow.ui.activities.SendFeedbackActivity;
import com.bitsescrow.app.bitsescrow.ui.activities.SplashActivity;
import com.bitsescrow.app.bitsescrow.ui.base.BaseFragment;
import com.bitsescrow.app.bitsescrow.utils.L;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.policy.TimeWindow;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Lekan Adigun on 5/19/2018.
 */

public class  AccountFragment extends BaseFragment {

    @BindView(R.id.toolbar_account)
    Toolbar mToolbar;
    @BindView(R.id.switch_notification_account_fragment)
    SwitchCompat switchCompat;
    @BindView(R.id.tv_username_account_fragment)
    TextView usernameTextView;
    @BindView(R.id.tv_email_account_fragment)
    TextView emailTextView;
    @BindView(R.id.iv_user_account_fragment)
    CircleImageView circleImageView;

    private User mUser;

    public static final String TAG = "AccountFragmentTag";
    public static final int RC_SELECT_PHOTO = 12;

    public static AccountFragment newInstance() {

        Bundle args = new Bundle();

        AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_account_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar.setTitle("Account");

        mUser = User.user();

        render();
    }

    private void render() {
        if (mUser != null) {
            usernameTextView.setText(mUser.name());
            emailTextView.setText(mUser.email);

            if (mUser.hasPhoto()) {
                Glide.with(this)
                        .load(mUser.picture)
                        .apply(Util.requestOptions())
                        .into(circleImageView);
            }
        }

        switchCompat.setChecked(RepositoryManager.manager().preferences().getBoolean("enable_notification", false));
    }

    private void startImagePicker() {

        ImagePicker.create(this)
                .single()
                .toolbarImageTitle("Select Photo")
                .showCamera(true)
                .folderMode(false)
                .start(RC_SELECT_PHOTO);
    }

    @OnClick(R.id.btn_change_photo_account_fragment) public void onChangePhotoClick() {
        startImagePicker();
    }

    @OnClick({R.id.btn_enable_notification_account_fragment, R.id.btn_edit_profile_account_fragment,
            R.id.btn_send_feedback_account_fragment, R.id.btn_invite_friends_account_fragment, R.id.btn_logout_account_fragment})
    public void onOperate(View view) {

        switch (view.getId()) {
            case R.id.btn_enable_notification_account_fragment:
                switchCompat.toggle();
                RepositoryManager.manager().preferences()
                        .edit().putBoolean("enable_notification", switchCompat.isChecked()).apply();
                break;
            case R.id.btn_logout_account_fragment:
                performLogout();
                break;
            case R.id.btn_send_feedback_account_fragment:
                startActivity(new Intent(getActivity(), SendFeedbackActivity.class));
                break;
        }
    }

    private void performLogout() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Sign out?")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RepositoryManager
                                .manager()
                                .preferences()
                                .edit().clear().apply();

                        Intent intent = new Intent(getActivity(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("NO", null)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null
                && requestCode == RC_SELECT_PHOTO) {

            Image image = ImagePicker.getFirstImageOrNull(data);
            resizeAndUpload(image);
        }
    }

    private void resizeAndUpload(Image image) {

        if (image == null) return;

        Tiny.getInstance().source(image.getPath())
                .asFile().compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {

                if (!isSuccess || t != null) {
                    L.wtf(t);
                    return;
                }

                uploadPhoto(outfile);

            }
        });
    }

    private void uploadPhoto(String outfile) {

        File photo = new File(outfile);
        Glide.with(this).load(photo).apply(Util.requestOptions()).into(circleImageView);
        String publicId = mUser.name() + mUser.ID;
        MediaManager.get().upload(Uri.fromFile(photo)).unsigned(Util.CLOUDPRESET)
                .option("public_id", publicId)
                .constrain(TimeWindow.immediate())
                .dispatch();

        String picture = MediaManager.get().url().generate(publicId + ".jpg");
        L.fine(picture);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("picture", picture);
        }catch (Exception e) {}

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating photo...");
        progressDialog.show();

        Requests.post("/me/update", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                try {
                    progressDialog.cancel();
                    if (jsonObject.has("status")
                            && jsonObject.getBoolean("status")) {
                        snack("Photo updated!");

                        User user = new User(jsonObject.getJSONObject("user"));
                        user.persist();
                    }
                }catch (Exception e) {}
            }

            @Override
            public void error(Throwable throwable) {
                progressDialog.cancel();

                toast("Failed to update photo. Please, retry");
                invalidatePhoto();
            }
        });
    }

    private void invalidatePhoto() {

        if (mUser != null && mUser.hasPhoto()) {
            Glide.with(this)
                    .load(mUser.picture)
                    .apply(Util.requestOptions())
                    .into(circleImageView);
        }else {
            Glide.with(this)
                    .load(R.color.divider)
                    .apply(Util.requestOptions())
                    .into(circleImageView);
        }
    }
}
