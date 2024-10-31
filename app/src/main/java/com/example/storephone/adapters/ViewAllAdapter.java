package com.example.storephone.adapters;

import android.annotation.SuppressLint;
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
import com.example.storephone.models.ViewAllModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {

    Context context;
    List<ViewAllModel> list;

    public ViewAllAdapter(Context context, List<ViewAllModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewAllAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.imageView);

        holder.name.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getDescription());
        holder.rating.setText(list.get(position).getRating());

        /*if(list.get(position).getType().equals("egg")){
            holder.price.setText(list.get(position).getPrice() + " /Vỉ");
        }
            Cái này cho BÁN HÀNG nha Nguyên
        if(list.get(position).getType().equals("milk")){
            holder.price.setText(list.get(position).getPrice() + " /Lốc");
        }*/

        // Lấy giá trị price từ list và chuyển đổi sang số nguyên
        int priceValue = Integer.parseInt(list.get(position).getPrice());

        // Định dạng số theo chuẩn Việt Nam (vi-VN)
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = numberFormat.format(priceValue);

        // Hiển thị giá với định dạng
        holder.price.setText(formattedPrice + " đ");

        // Xử lý sự kiện click để mở chi tiết sản phẩm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detail", list.get(position));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, description, price, rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.view_img);
            name = itemView.findViewById(R.id.name_view);
            description = itemView.findViewById(R.id.desc_view);
            price = itemView.findViewById(R.id.price_view);
            rating = itemView.findViewById(R.id.rating_view);
        }
    }
}
