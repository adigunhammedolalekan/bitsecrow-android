package com.bitsescrow.app.bitsescrow.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class Wallet {

    public int user = 0;
    public String address = "";
    public double balance = 0;
    public double finalBalance = 0;

    public static final double S = 100000000.0;

    public Wallet() {}

    public Wallet(JSONObject jsonObject) throws JSONException {

        address = jsonObject.getString("address");
        balance = jsonObject.getDouble("balance");
        finalBalance = jsonObject.getDouble("final_balance");
    }

    public String balance() {
        return String.valueOf(finalBalance / S + "BTC");
    }
}
