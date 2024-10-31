package com.example.storephone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.storephone.R;
import com.example.storephone.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    Button signUp;
    EditText name, email, password;
    TextView signIn;
    FirebaseAuth auth;
    FirebaseDatabase database;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar = findViewById(R.id.processreg);
        progressBar.setVisibility(View.GONE);

        signUp = findViewById(R.id.reg_btn);
        name = findViewById(R.id.name_reg);
        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);
        signIn = findViewById(R.id.sign_in);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createUser() {
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,"Chưa nhập Tên!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"Chưa nhập Email!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Chưa nhập Mật khẩu!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userPassword.length() < 6){
            Toast.makeText(this,"Không nhập dưới 6 ký tự!!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Create User
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserModel userModel = new UserModel(userName, userEmail, userPassword);
                            String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                            database.getReference().child("Users").child(id).setValue(userModel);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Đăng ký thất bại! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
