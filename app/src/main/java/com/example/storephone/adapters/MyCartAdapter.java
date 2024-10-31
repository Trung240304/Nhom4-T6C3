package com.example.storephone.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storephone.R;
import com.example.storephone.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    Context context;
    List<MyCartModel> cartModelList;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public MyCartAdapter(Context context, List<MyCartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        Glide.with(context).load(cartModelList.get(position).getImg_url()).into(holder.imageView);

        holder.name.setText(cartModelList.get(position).getProductName());
        holder.price.setText(cartModelList.get(position).getProductPrice());
        holder.date.setText(cartModelList.get(position).getCurrentDate());
        holder.time.setText(cartModelList.get(position).getCurrentTime());
        holder.quantity.setText(cartModelList.get(position).getTotalQuantity());
        holder.totalPrice.setText(numberFormat.format(cartModelList.get(position).getTotalPrice()) + " đ");
//        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deleteItemFromDatabase(position);
//            }
//        });
    }

//    private void deleteItemFromDatabase(int position) {
//        MyCartModel item = cartModelList.get(position);
//        firestore.collection("AddToCart")
//                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
//                .collection("CurrentUser")
//                .document(item.getDocumentId())
//                .delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @SuppressLint("NotifyDataSetChanged")
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            cartModelList.remove(position);
//                            notifyItemRemoved(position);
//                            notifyItemRangeChanged(position, cartModelList.size());
//                            updateTotalAmount();
//                            Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(context, "Không thể xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void updateTotalAmount() {
        int newTotalPrice = 0;
        for (MyCartModel item : cartModelList) {
            newTotalPrice += item.getTotalPrice();
        }

        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", newTotalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, date, time, quantity, totalPrice;
        ImageView imageView;
        Button deleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            quantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            imageView = itemView.findViewById(R.id.mycart_image);
//            deleteItem = itemView.findViewById(R.id.delete);
        }
    }
}
