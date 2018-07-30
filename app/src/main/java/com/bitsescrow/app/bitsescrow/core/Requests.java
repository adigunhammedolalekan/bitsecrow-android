package com.bitsescrow.app.bitsescrow.core;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bitsescrow.app.bitsescrow.utils.L;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Lekan Adigun on 3/31/2018.
 */

public final class Requests {

    public static final String BASE_URI = "https://bitsescrowdev.herokuapp.com/api";
    public static final String WS_URI = "ws://bitsescrowdev.herokuapp.com/api/ws/connect";

    public static final String HEADER = "token_key";


    public interface ResponseListener {
        void success(JSONObject jsonObject);
        void error(Throwable throwable);
    }

    public static void go(final String path, final ResponseListener listener) {

        String uri = BASE_URI + path;

        String header = RepositoryManager.manager().preferences().getString(HEADER, "");

        String hd = "Token " + header;
        AndroidNetworking
                .get(uri)
                .addHeaders("Authorization", hd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (listener == null)
                            return;

                        L.fine("Response for path ==> "+ path + " ===> " + response.toString());
                        listener.success(response);
                    }

                    @Override
                    public void onError(ANError anError) {

                        if (listener == null)
                            return;
                        L.fine("Body => " + anError.getErrorBody() + "; Code => " + anError.getErrorCode());
                        L.wtf(anError);
                        listener.error(anError);
                    }
                });
    }

    private static void logParams(Map<String, String> map) {

        StringBuilder builder = new StringBuilder();
        for (String s : map.keySet()) {
            builder.append(s).append("=").append(map.get(s)).append("\n");
        }

        L.fine("Params => " + builder.toString());
    }



    public static void post(final String path, Map<String, String> params, final ResponseListener listener) {

        String uri = BASE_URI + path;

        String header = RepositoryManager.manager().preferences().getString(HEADER, "");

        String hd = "Token " + header;

        logParams(params);
        AndroidNetworking
                .post(uri)
                .addJSONObjectBody(new JSONObject(params))
                .addHeaders("Authorization", hd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (listener == null) return;

                        L.fine("Response for path ==> "+ path + " ===> " + response.toString());
                        listener.success(response);
                    }

                    @Override
                    public void onError(ANError anError) {

                        if (listener == null)
                             return;
                        L.fine("Code ==> " + anError.getErrorCode() + ";  Body ==> " + anError.getErrorBody());
                        L.wtf(anError);
                        listener.error(anError);
                    }
                });
    }

    public static void post(final String path, JSONObject params, final ResponseListener listener) {

        String uri = BASE_URI + path;

        String header = RepositoryManager.manager().preferences().getString(HEADER, "");

        String hd = "Bearer " + header;

        L.fine("Category => " + params.toString());
        AndroidNetworking
                .post(uri)
                .addJSONObjectBody(params)
                .addHeaders("Authorization", hd)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (listener == null) return;

                        L.fine("Response for path ==> "+ path + " ===> " + response.toString());
                        listener.success(response);
                    }

                    @Override
                    public void onError(ANError anError) {

                        if (listener == null)
                            return;
                        L.fine("Code ==> " + anError.getErrorCode() + ";  Body ==> " + anError.getErrorBody());
                        L.wtf(anError);
                        listener.error(anError);
                    }
                });
    }

    public static void put(final String path, Map<String, String> data, final ResponseListener listener) {

        String uri = BASE_URI + path;

        String header = RepositoryManager.manager().preferences().getString(HEADER, "");

        String hd = "Bearer " + header;

        ANRequest.PostRequestBuilder builder = AndroidNetworking.put(uri).addHeaders("Authorization", hd);
        if (data != null)
            builder.addJSONObjectBody(new JSONObject(data));

        ANRequest request = builder.build();
        request.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (listener == null) return;

                L.fine("Response for path ==> "+ path + " ===> " + response.toString());
                listener.success(response);
            }

            @Override
            public void onError(ANError anError) {

                if (listener == null)
                    return;
                L.fine("Code ==> " + anError.getErrorCode() + ";  Body ==> " + anError.getErrorBody());
                L.wtf(anError);
                listener.error(anError);
            }
        });
    }
}
