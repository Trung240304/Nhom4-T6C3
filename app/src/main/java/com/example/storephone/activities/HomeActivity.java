package com.example.storephone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.storephone.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.processhome);
        progressBar.setVisibility(View.GONE);
        if (auth.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            Toast.makeText(this, "Đang tải . . .", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void login(View view) {
        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    public void register(View view) {
        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
    }

}
