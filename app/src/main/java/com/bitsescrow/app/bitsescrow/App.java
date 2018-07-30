package com.bitsescrow.app.bitsescrow;

import android.app.Application;

import com.bitsescrow.app.bitsescrow.core.RepositoryManager;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.cloudinary.android.MediaManager;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Lekan Adigun on 5/16/2018.
 */

public class App extends Application {

    private static App app;
    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        MediaManager.init(this);
        setupFonts();
        initFcmToken();
    }

    private void initFcmToken() {

        boolean synced = RepositoryManager.manager().preferences().getBoolean("token_synced", false);
        if (synced) return;

        String token = RepositoryManager.manager().preferences().getString("fcm_token", "");
        if (token.isEmpty()) return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fcm_token", token);
        }catch (Exception e) {}

        Requests.post("/me/update", jsonObject, new Requests.ResponseListener() {
            @Override
            public void success(JSONObject jsonObject) {
                RepositoryManager.manager().preferences().edit().putBoolean("token_synced", true).apply();
            }

            @Override
            public void error(Throwable throwable) {

            }
        });
    }

    private void setupFonts() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                .build());
    }

    public static App getApp() {
        return app;
    }
}
