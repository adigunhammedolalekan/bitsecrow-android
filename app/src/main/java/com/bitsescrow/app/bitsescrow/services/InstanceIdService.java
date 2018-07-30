package com.bitsescrow.app.bitsescrow.services;

import com.bitsescrow.app.bitsescrow.core.RepositoryManager;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.utils.L;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

/**
 * Created by Lekan Adigun on 5/20/2018.
 */

public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        L.fine("Token Refreshed ==> " + token);
        pushToken(token);
    }

    private void pushToken(String token) {

        RepositoryManager.manager().preferences().edit().putString("fcm_token",
                token).putBoolean("token_synced", false).apply();
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
}
