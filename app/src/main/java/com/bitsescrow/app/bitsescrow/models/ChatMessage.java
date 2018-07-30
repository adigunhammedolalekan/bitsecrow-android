package com.bitsescrow.app.bitsescrow.models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lekan Adigun on 5/23/2018.
 */

public class ChatMessage {

    public String fromUsername = "";
    public String toUsername = "";
    public int fromId = 0;
    public int toId = 0;
    public String text = "";
    public String messageType = "";
    public String time = "";
    public boolean isAdmin= false;
    public String photoUri = "";
    public String action = "";
    public String channelName = "";

    public ChatMessage() {}

    public ChatMessage(JSONObject jsonObject) throws JSONException {
        fromUsername = jsonObject.getString("from_id");
        toUsername = jsonObject.getString("to_id");
        text = jsonObject.getString("text");
        messageType = jsonObject.getString("message_type");
        action = jsonObject.getString("action");
        channelName = jsonObject.getString("channel_name");
    }

    public boolean isImage() {
        return messageType.trim().equalsIgnoreCase("image");
    }

    public String json() {
        return new Gson().toJson(this);
    }

    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", action);
            jsonObject.put("channel_name", channelName);
            jsonObject.put("photo_uri", photoUri);
            jsonObject.put("text", text);
            jsonObject.put("message_type", messageType);
            jsonObject.put("is_admin", isAdmin);
            jsonObject.put("time", time);
            jsonObject.put("from_id", fromId);
            jsonObject.put("to_id", toId);
            jsonObject.put("from_username", fromUsername);
            jsonObject.put("to_username", toUsername);
        }catch (Exception e) {}

        return jsonObject.toString();
    }
}
