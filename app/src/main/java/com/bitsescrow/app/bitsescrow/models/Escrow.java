package com.bitsescrow.app.bitsescrow.models;

import com.bitsescrow.app.bitsescrow.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

@Parcel
public class Escrow {

    public enum Status {

        COMPLETED("completed"), UNKNOWN("unknown"), CANCELED("cancelled");

        String mStatus = "";
        Status(String statusString) {
            mStatus = statusString;
        }

        public String getStatus() {
            return mStatus;
        }

        public static Status from(String s) {

            switch (s.trim()) {
                case "completed":
                    return COMPLETED;
                case "cancelled":
                    return CANCELED;
                default:
                    return UNKNOWN;
            }
        }
    }

    public String amount() {
        return String.valueOf(amount + "BTC");
    }

    public int ID = 0;
    public double amount = 0.0;
    public User from;
    public User to;
    public Status mStatus = Status.UNKNOWN;
    public String time = "";
    public String narration = "";
    public static final String KEY = "EscrowKey";


    public Escrow() {}

    public Escrow(JSONObject jsonObject) throws JSONException {

        ID = jsonObject.getInt("ID");
        amount = jsonObject.getDouble("amount");
        from = new User(jsonObject.getJSONObject("From"));
        to = new User(jsonObject.getJSONObject("To"));
        mStatus = Status.from(jsonObject.getString("status"));
        narration = jsonObject.getString("narration");
        time = jsonObject.has("formatted_time") ?
                jsonObject.getString("formatted_time") : "";
    }

    public String narration() {

        if (narration.isEmpty())
            return "No narration";

        return narration;
    }

    public boolean hasStatus() {
        return !status().isEmpty();
    }

    public String status() {
        if (mStatus == Status.UNKNOWN)
            return "";
        if (mStatus == Status.COMPLETED)
            return "completed".toUpperCase();

        return "cancelled".toUpperCase();
    }

    public int color() {

        if (mStatus == Status.COMPLETED)
            return R.color.green;

        return R.color.red;
    }
}
