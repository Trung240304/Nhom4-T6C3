package com.example.storephone.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.storephone.R;
import com.example.storephone.databinding.ActivityMainBinding;
import com.example.storephone.models.UserModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private ImageView profileImage;
    private TextView userName, userEmail;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String updatedImageUrl = intent.getStringExtra("updatedImageUrl");
            if (updatedImageUrl != null && !updatedImageUrl.isEmpty()) {
                updateNavHeaderImage(updatedImageUrl);
            }
            loadUserData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        auth = FirebaseAuth.getInstance();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        setupNavHeader(navigationView);
        setStatusBarColor();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_category, R.id.nav_offers,
                R.id.nav_new_products, R.id.nav_orders, R.id.nav_carts, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                logout();
                return false;
            } else {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawer.closeDrawer(navigationView);
                }
                return handled;
            }
        });

        // Register the local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("com.example.storephone.USER_DATA_CHANGED"));
    }

    @Override
    protected void onDestroy() {
        // Unregister the local broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void setStatusBarColor() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.mauchudao)); // Thay thế bằng màu của app_bar_main
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setupNavHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.nav_header_profile_image);
        userName = headerView.findViewById(R.id.nav_header_user_name);
        userEmail = headerView.findViewById(R.id.nav_header_user_email);

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        if (user != null) {
                            userName.setText(user.getName());
                            userEmail.setText(user.getEmail());
                            updateNavHeaderImage(user.getProfileImageUrl());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateNavHeaderImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(MainActivity.this)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_profile_image)
                    .into(profileImage);
        }
    }

    private void logout() {
        showLogoutConfirmationDialog();
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Thực hiện đăng xuất khi người dùng xác nhận
                    auth.signOut();
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}