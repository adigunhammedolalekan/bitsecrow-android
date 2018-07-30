package com.bitsescrow.app.bitsescrow.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.bitsescrow.app.bitsescrow.models.ChatMessage;
import com.bitsescrow.app.bitsescrow.models.Escrow;
import com.bitsescrow.app.bitsescrow.models.User;
import com.bitsescrow.app.bitsescrow.ui.adapters.ReportProblemAdapter;
import com.bitsescrow.app.bitsescrow.ui.base.BaseActivity;
import com.bitsescrow.app.bitsescrow.utils.L;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lekan Adigun on 5/20/2018.
 */

public class ReportProblemActivity extends BaseActivity {

    @BindView(R.id.rv_chats_report_problems)
    RecyclerView mRecyclerView;
    @BindView(R.id.edt_text_report_a_problem)
    EditText textEditText;

    private ReportProblemAdapter mReportProblemAdapter;
    private List<ChatMessage> mChatMessages = new ArrayList<>();
    private User mUser;
    private Escrow mEscrow;

    private static WebSocketFactory webSocketFactory = new WebSocketFactory();
    private WebSocket mWebSocket;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int retryConnectCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_report_a_problem);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Report a Problem");
        }

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        createAdapter();
        mUser = User.user();
        mEscrow = Parcels.unwrap(intent.getParcelableExtra(Escrow.KEY));
    }

    private void createAdapter() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReportProblemAdapter = new ReportProblemAdapter(this, mChatMessages);
        mRecyclerView.setAdapter(mReportProblemAdapter);

    }

    private void connect() {

        try {
            mWebSocket = webSocketFactory.createSocket(Requests.WS_URI, 10000);
            mWebSocket.connectAsynchronously();

            mWebSocket.addListener(new WebSocketAdapter() {

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);

                    L.fine("New Message => " + text);
                    try {
                        final ChatMessage chatMessage = new ChatMessage(new JSONObject(text));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mChatMessages.add(chatMessage);

                                if (mReportProblemAdapter != null)
                                    mReportProblemAdapter.notifyDataSetChanged();

                                mRecyclerView.smoothScrollToPosition((mChatMessages.size() - 1));
                            }
                        });
                    }catch (Exception e) {
                        L.wtf(e);
                    }
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);

                    L.fine("Connected");
                    subscribeToChannel();
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onError(websocket, cause);

                    L.wtf(cause);
                    retryConnectCount++;
                    if (retryConnectCount < 11) {

                        L.fine("Retrying => " + retryConnectCount);
                        try {
                            if (mWebSocket != null)
                                mWebSocket.recreate(10000);
                        }catch (Exception e) {}

                    }
                }
            });
        }catch (Exception e) {
            L.wtf(e);
        }
    }

    private void subscribeToChannel() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("action", "sub");
            jsonObject.put("channel_name", String.format(Locale.getDefault(), "%s%d", "escrow", mEscrow.ID));

            mWebSocket.sendText(jsonObject.toString());
        }catch (Exception e) {}

    }

    private void unSubscribeFromChannel() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("action", "unsub");
            jsonObject.put("channel_name", String.format(Locale.getDefault(), "%s%d", "escrow", mEscrow.ID));

            mWebSocket.sendText(jsonObject.toString());
        }catch (Exception e) {}

    }

    @OnClick(R.id.btn_send_message_report_problem) public void onSendClick() {

        String text = Util.textOf(textEditText);
        if (text.length() <= 0) return;

        ChatMessage message = new ChatMessage();
        message.action = "message";
        message.channelName = String.format(Locale.getDefault(), "%s%d", "escrow", mEscrow.ID);
        message.text = text;
        message.fromUsername = mUser.username;
        message.toUsername = mEscrow.to.username;
        message.fromId = mUser.ID;
        message.toId = mEscrow.to.ID;

        try {
            L.fine("Sending => " + message.toJSON());
            mWebSocket.sendText(message.toJSON());
        }catch (Exception e) {
            L.wtf("Failed to send => " + e);
        }

        mChatMessages.add(message);
        if (mReportProblemAdapter != null)
            mReportProblemAdapter.notifyDataSetChanged();

        mRecyclerView.smoothScrollToPosition((mChatMessages.size() - 1));
        textEditText.setText("");
    }

    @OnClick(R.id.btn_attach_file_report_a_problem) public void onAttachFileClick() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        connect();

        L.fine(":onResume");
    }

    @Override
    protected void onPause() {
        unSubscribeFromChannel();
        disConnect();

        L.fine(":onPause");
        super.onPause();
    }

    private void disConnect() {
        try {
            mWebSocket.disconnect();
        }catch (Exception e)  {
            L.wtf(e);
        }
    }
}
