package com.bitsescrow.app.bitsescrow.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.models.Escrow;
import com.bitsescrow.app.bitsescrow.ui.activities.EscrowDetailsActivity;
import com.bitsescrow.app.bitsescrow.ui.base.BaseViewHolder;
import com.bitsescrow.app.bitsescrow.utils.Util;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

public class EscrowsListAdapter extends RecyclerView.Adapter<EscrowsListAdapter.EscrowListViewHolder> {

    private List<Escrow> mEscrows = new ArrayList<>();
    private Context mContext;

    public EscrowsListAdapter(Context context, List<Escrow> escrows) {
        mContext = context;
        mEscrows = escrows;
    }
    @Override
    public EscrowListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EscrowListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_escrow_view_,
                parent, false));
    }

    @Override
    public void onBindViewHolder(EscrowListViewHolder holder, int position) {

        final Escrow escrow = mEscrows.get(position);
        holder.btcTextView.setText(String.valueOf(escrow.amount()));
        holder.nameTextView.setText(escrow.from.name());
        holder.emailTextView.setText(escrow.from.email);
        holder.narrationTextView.setText(escrow.narration);
        holder.statusTextView.setText(escrow.status());

        if (escrow.hasStatus()) {
            holder.statusTextView.setVisibility(View.VISIBLE);
        }else {
            holder.statusTextView.setVisibility(View.GONE);
        }

        if (escrow.status().equalsIgnoreCase("cancelled")) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }else {
            holder.statusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }

        if (escrow.from.hasPhoto()) {
            Glide.with(mContext)
                    .load(escrow.from.picture)
                    .apply(Util.requestOptions())
                    .into(holder.circleImageView);
        }else {
            Glide.with(mContext)
                    .load(R.color.divider)
                    .apply(Util.requestOptions())
                    .into(holder.circleImageView);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, EscrowDetailsActivity.class);
                intent.putExtra(Escrow.KEY, Parcels.wrap(escrow));
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mEscrows.size();
    }

    class EscrowListViewHolder extends BaseViewHolder {

        @BindView(R.id.layout_es_escrow)
        LinearLayout root;
        @BindView(R.id.tv_amount_escrow_view_)
        TextView btcTextView;
        @BindView(R.id.tv_name_es_view)
        TextView nameTextView;
        @BindView(R.id.tv_email_es_view)
        TextView emailTextView;
        @BindView(R.id.iv_user_es_view)
        CircleImageView circleImageView;
        @BindView(R.id.tv_narration_es_view)
        TextView narrationTextView;
        @BindView(R.id.tv_es_status)
        TextView statusTextView;

        public EscrowListViewHolder(View itemView) {
            super(itemView);
        }
    }
}
