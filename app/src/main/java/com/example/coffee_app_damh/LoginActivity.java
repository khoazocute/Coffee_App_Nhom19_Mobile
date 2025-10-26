package com.example.coffee_app_damh;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    //Khai báo các biến EditText và Button
    private EditText email, password;
    private Button btnlogin,btnregister;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        //Ánh xạ biến từ giao điện qua, trùng khớp với các id bên giao diện
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);
        btnlogin=findViewById(R.id.btn_login);
        btnregister=findViewById(R.id.btn_signup_bottom);

        //Lắng nghe sự kiện nhấn nút login
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
            
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        //Khi nhấn vào register sẽ chuyển sang giao diện đăng ký
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }

    private void login() {
        String mail,pass;
        mail=email.getText().toString();
        pass=password.getText().toString();

        if(TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Vui lòng nhập Email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Vui lòng nhập Email.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
