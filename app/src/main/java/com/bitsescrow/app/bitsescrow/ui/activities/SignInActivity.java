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
import com.bitsescrow.app.bitsescrow.utils.L;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class SignInActivity extends BaseActivity {

    @BindView(R.id.edt_email_login)
    MaterialEditText emailEditText;
    @BindView(R.id.edt_password_login)
    MaterialEditText passwordEditText;
    @BindView(R.id.loading_layout_sign_in)
    View loadingView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Sign In");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @OnClick(R.id.fab_sign_in_) public void onSignInClick() {
        signIn();
    }

    @OnClick(R.id.btn_create_account) public void onCreateAccountClick() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void signIn() {

        Util.hideKeyboard(this);
        String mail = Util.textOf(emailEditText);
        String password = Util.textOf(passwordEditText);

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            snack("Invalid email addres");
            return;
        }

        if (password.length() < 6) {
            snack("Invalid password. Too short");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", mail);
            jsonObject.put("password", password);
        }catch (Exception e) {}

        show(loadingView);
        Requests.post("/authenticate", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {

                hide(loadingView);
                try {
                    boolean success = jsonObject.has("status")
                            && jsonObject.getBoolean("status");

                    if (success) {

                        User user = new User(jsonObject.getJSONObject("user"));
                        user.persist();

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {
                        snack(jsonObject.getString("message"));
                    }
                }catch (Exception e) {
                    L.wtf(e);
                }
            }

            @Override
            public void error(Throwable throwable) {
                hide(loadingView);
                snack("Error response from network connection. Please, retry");
            }
        });
    }
}
