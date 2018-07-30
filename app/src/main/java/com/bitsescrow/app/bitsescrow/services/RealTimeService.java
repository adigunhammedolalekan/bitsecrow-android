package com.bitsescrow.app.bitsescrow.services;

import com.bitsescrow.app.bitsescrow.models.ChatMessage;
import com.bitsescrow.app.bitsescrow.utils.L;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Lekan Adigun on 5/20/2018.
 */

public class RealTimeService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage != null) {
            Map<String, String> data = remoteMessage.getData();
            JSONObject jsonObject = new JSONObject(data);
            L.fine(jsonObject.toString());


            ChatMessage chatMessage = new ChatMessage();
            chatMessage.text = data.get("text");
            chatMessage.toUsername = data.get("to_user");
            chatMessage.fromUsername = data.get("from_user");
            chatMessage.messageType = data.get("message_type");
            chatMessage.time = data.get("time_when");

            L.fine("New Message =>  " + chatMessage.json());
        }else {
            L.fine("New Message => is null");
        }
    }
}
