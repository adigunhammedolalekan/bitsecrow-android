package com.bitsescrow.app.bitsescrow.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.models.ChatMessage;
import com.bitsescrow.app.bitsescrow.models.User;
import com.bitsescrow.app.bitsescrow.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lekan Adigun on 5/23/2018.
 */

public class ReportProblemAdapter extends RecyclerView.Adapter<ReportProblemAdapter.ReportProblemViewHolder> {

    private List<ChatMessage> mChatMessages = new ArrayList<>();
    public static final int IN = 1, OUT = 2, ADMIN = 3, IMAGE = 4;
    private User currentUser;
    private Context mContext;


    public ReportProblemAdapter(Context context, List<ChatMessage> chatMessages) {
        mContext = context;
        currentUser = User.user();
        mChatMessages = chatMessages;
    }

    @Override
    public ReportProblemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case IN:
                return new InViewHolder(layoutInflater.inflate(R.layout.layout_report_message_in,
                        parent, false));
            case OUT:
                return new OutViewHolder(layoutInflater.inflate(R.layout.layout_report_message_out,
                        parent, false));
            case ADMIN:
                return new AdminViewHolder(layoutInflater.inflate(R.layout.layout_mod_message_report_a_problem,
                        parent, false));
            case IMAGE:
                return new ImageViewHolder(layoutInflater.inflate(R.layout.layout_photo_message,
                        parent, false));
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {

        ChatMessage message = mChatMessages.get(position);

        if (message.isImage())
            return IMAGE;

        if (message.isAdmin)
            return ADMIN;

        if (message.fromUsername.trim().equalsIgnoreCase(currentUser.username))
            return OUT;



        return IN;
    }

    @Override
    public void onBindViewHolder(ReportProblemViewHolder holder, int position) {

        ChatMessage chatMessage = mChatMessages.get(position);
        if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            String text = chatMessage.fromUsername + " attached a photo";
            imageViewHolder.text.setText(text);

        } else if (holder instanceof InViewHolder) {
            InViewHolder inViewHolder = (InViewHolder) holder;
            inViewHolder.nameTextView.setText(chatMessage.fromUsername);
            inViewHolder.text.setText(chatMessage.text);
            inViewHolder.time.setText(chatMessage.time);
        } else if (holder instanceof OutViewHolder) {
            OutViewHolder outViewHolder = (OutViewHolder) holder;
            outViewHolder.nameTextView.setText(chatMessage.fromUsername);
            outViewHolder.text.setText(chatMessage.text);
            outViewHolder.time.setText(chatMessage.time);
        }else {
            AdminViewHolder adminViewHolder = (AdminViewHolder) holder;
            adminViewHolder.text.setText(chatMessage.text);
            adminViewHolder.time.setText(chatMessage.time);
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    class ReportProblemViewHolder extends BaseViewHolder {

        public ReportProblemViewHolder(View itemView) {
            super(itemView);
        }
    }

    class InViewHolder extends ReportProblemViewHolder {

        @BindView(R.id.tv_username_message_in)
        TextView nameTextView;
        @BindView(R.id.tv_text_message_in)
        TextView text;
        @BindView(R.id.tv_date_time_report_message_in)
        TextView time;

        public InViewHolder(View itemView) {
            super(itemView);
        }
    }

    class OutViewHolder extends ReportProblemViewHolder {

        @BindView(R.id.tv_username_message_out)
        TextView nameTextView;
        @BindView(R.id.tv_text_message_out)
        TextView text;
        @BindView(R.id.tv_date_time_report_message_out)
        TextView time;

        public OutViewHolder(View itemView) {
            super(itemView);
        }
    }

    class AdminViewHolder extends ReportProblemViewHolder {

        @BindView(R.id.tv_text_message_mod)
        TextView text;
        @BindView(R.id.tv_time_message_mod)
        TextView time;
        public AdminViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ImageViewHolder extends ReportProblemViewHolder {

        @BindView(R.id.iv_photo_report_problem_message)
        ImageView imageView;
        @BindView(R.id.tv_photo_message_report_a_problem)
        TextView text;

        public ImageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
