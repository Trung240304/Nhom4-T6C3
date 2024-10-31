package com.example.storephone.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.storephone.R;
import com.example.storephone.models.ViewAllModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView price, rating, description;
    Button addToCart;
    ImageView removeItem, addItem;
    Toolbar toolbar;
    TextView quantity;
    int totalQuantity = 1;
    int totalPrice = 0;
    TextView totalPricePanel;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ViewAllModel viewAllModel = null;

    public String formatPrice(int priceValue) {
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(priceValue);
    }

    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed);

        totalPricePanel = findViewById(R.id.total_price_panel);
        totalPricePanel.setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel) {
            viewAllModel = (ViewAllModel) object;
        }

        quantity = findViewById(R.id.quantity);
        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);

        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        description = findViewById(R.id.detailed_dec);

        totalQuantity = 1;
        quantity.setText(String.valueOf(totalQuantity));

        if (viewAllModel != null) {
            int priceValue = Integer.parseInt(viewAllModel.getPrice());

            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            // Sử dụng phương thức formatPrice để định dạng giá tiền
            price.setText("Giá: " + formatPrice(priceValue) + " đ");
            rating.setText(viewAllModel.getRating());
            description.setText(viewAllModel.getDescription());

            totalPrice = priceValue * totalQuantity;
            totalPricePanel.setText("Tổng tiền: " + formatPrice(totalPrice) + " đ");

            if (viewAllModel.getType().equals("phone")) {
                price.setText("Giá: " + formatPrice(priceValue) + " đ");
                totalPrice = priceValue * totalQuantity;
            }
            if (viewAllModel.getType().equals("laptop")) {
                price.setText("Giá: " + formatPrice(priceValue) + " đ");
                totalPrice = priceValue * totalQuantity;
            }
            if (viewAllModel.getType().equals("sound")) {
                price.setText("Giá: " + formatPrice(priceValue) + " đ");
                totalPrice = priceValue * totalQuantity;
            }
            if (viewAllModel.getType().equals("accessory")) {
                price.setText("Giá: " + formatPrice(priceValue) + " đ");
                totalPrice = priceValue * totalQuantity;
            }
        }

        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addedToCart();
            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity < 10) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = Integer.parseInt(viewAllModel.getPrice()) * totalQuantity;

                    // Update the total price panel
                    totalPricePanel.setText("Tổng tiền: " + formatPrice(totalPrice) + " đ");
                }
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = Integer.parseInt(viewAllModel.getPrice()) * totalQuantity;

                    // Update the total price panel
                    totalPricePanel.setText("Tổng tiền: " + formatPrice(totalPrice) + " đ");
                }
            }
        });
    }

    private void addedToCart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd MM, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final AtomicReference<HashMap<String, Object>> cartMap = new AtomicReference<>(new HashMap<>());

        cartMap.get().put("productName", viewAllModel.getName());
        cartMap.get().put("productPrice", price.getText().toString());
        cartMap.get().put("currentDate", saveCurrentDate);
        cartMap.get().put("currentTime", saveCurrentTime);
        cartMap.get().put("totalQuantity", quantity.getText().toString());
        cartMap.get().put("totalPrice", totalPrice);
        cartMap.get().put("Img_url", viewAllModel.getImg_url());

        firestore.collection("AddToCart")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("CurrentUser")
                .add(cartMap.get())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DetailedActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DetailedActivity.this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(DetailedActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
