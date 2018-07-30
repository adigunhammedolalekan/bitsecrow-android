package com.bitsescrow.app.bitsescrow.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Patterns;
import android.view.View;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.User;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.loading_layout_sign_up)
    View loadingView;
    @BindView(R.id.edt_username_sign_up)
    MaterialEditText usernameEditText;
    @BindView(R.id.edt_email_sign_up)
    MaterialEditText emailEditText;
    @BindView(R.id.edt_password_sign_up)
    MaterialEditText passwordEditText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_account);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Create Account");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.fab_sign_up_) public void onSignUpClick() {
        signUp();
    }

    private void signUp() {

        Util.hideKeyboard(this);
        String name = Util.textOf(usernameEditText);
        String password = Util.textOf(passwordEditText);
        String email = Util.textOf(emailEditText);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            snack("Invalid email address");
            return;
        }
        if (password.length() < 6) {
            snack("Invalid password. Too short");
            return;
        }

        if (name.isEmpty() || name.split(" ").length > 1) {
            snack("Invalid username. Username should not contain any space");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", name);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        }catch (Exception e) {}

        show(loadingView);
        Requests.post("/user/new", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                hide(loadingView);
                try {
                    boolean success = jsonObject.has("status")
                            && jsonObject.getBoolean("status");

                    if (success) {

                        User user = new User(jsonObject.getJSONObject("user"));
                        user.persist();

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {
                        snack(jsonObject.getString("message"));
                    }
                }catch (Exception e) {}
            }

            @Override
            public void error(Throwable throwable) {
                hide(loadingView);
                snack("Error response from network connection. Please retry");
            }
        });
    }
    @OnClick(R.id.btn_login) public void onCreateAccountClick() {
        startActivity(new Intent(this, SignInActivity.class));
    }
}
