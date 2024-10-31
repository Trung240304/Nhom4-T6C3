package com.example.storephone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storephone.R;
import com.example.storephone.models.OfferModel;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private Context context;
    private List<OfferModel> offers;

    public OfferAdapter(Context context, List<OfferModel> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        OfferModel offer = offers.get(position);
        Glide.with(context)
                .load(offer.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.ivOffer);
        holder.tvTitle.setText(offer.getTitle());
        holder.tvDescription.setText(offer.getDescription());

        holder.btnAction.setOnClickListener(v -> {
            //Toast.makeText(context, "Đang tải . . . ", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOffer;
        TextView tvTitle, tvDescription;
        Button btnAction;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOffer = itemView.findViewById(R.id.ivOffer);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}