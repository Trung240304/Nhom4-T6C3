package com.example.storephone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storephone.R;
import com.example.storephone.activities.DetailedActivity;
import com.example.storephone.activities.ViewAllActivity;
import com.example.storephone.models.PopularModel;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final List<PopularModel> popularModelList;
    private final Context context;

    public PopularAdapter(Context context, List<PopularModel> popularModelList) {
        this.context = context;
        this.popularModelList = popularModelList;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        // Load hình ảnh từ URL vào ImageView sử dụng Glide
        Glide.with(context).load(popularModelList.get(position).getImg_url()).into(holder.popImg);

        // Hiển thị thông tin từ PopularModel
        holder.name.setText(popularModelList.get(position).getName());
        holder.rating.setText(popularModelList.get(position).getRating());
        holder.description.setText(popularModelList.get(position).getDescription());
        holder.discount.setText(popularModelList.get(position).getDiscount());



        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewAllActivity.class);
            intent.putExtra("type", popularModelList.get(position).getType());  // Kiểm tra giá trị "type"
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return popularModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView popImg;
        TextView name, description, rating, discount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            popImg = itemView.findViewById(R.id.pop_img);
            name = itemView.findViewById(R.id.pop_name);
            description = itemView.findViewById(R.id.pop_des);
            rating = itemView.findViewById(R.id.pop_rate);
            discount = itemView.findViewById(R.id.pop_dis);
        }
    }
}