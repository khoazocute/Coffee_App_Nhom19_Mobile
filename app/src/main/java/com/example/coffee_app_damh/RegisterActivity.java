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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    //Khai báo các biến EditText và Button
    private EditText name,phone,email,password,confirmpassword;
    private Button backlogin,btnsignup;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();

            //Ánh xạ biến từ giao điện qua, trùng khớp với các id bên giao diện
            name=findViewById(R.id.name);
            phone=findViewById(R.id.phone);
            email=findViewById(R.id.email);
            password=findViewById(R.id.password);
            confirmpassword=findViewById(R.id.confirm_password);
            backlogin=findViewById(R.id.btn_backtologin);
            btnsignup=findViewById(R.id.sign_up);
            //Lắng nghe sự kiện nhấn nút login
            btnsignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    register();
                }
            });

        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển về LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        }

    private void register() {
        // 1. Lấy dữ liệu từ các trường EditText
        String userName = name.getText().toString();
        String phoneNumber = phone.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userConfirmPassword = confirmpassword.getText().toString();

        // 2. Kiểm tra các trường bị bỏ trống (Validation)
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Vui lòng nhập Số điện thoại.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Vui lòng nhập Email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Vui lòng nhập Mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userConfirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập Xác nhận Mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Kiểm tra Mật khẩu và Xác nhận Mật khẩu có khớp nhau không
        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(this, "Mật khẩu và Xác nhận Mật khẩu không khớp.", Toast.LENGTH_LONG).show();
            return;
        }

        // 4. Kiểm tra độ dài Mật khẩu
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(i);
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Email đã tồn tại.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Đăng ký thất bại.", Toast.LENGTH_SHORT).show();
                    }
            }
        };

    });
}};
