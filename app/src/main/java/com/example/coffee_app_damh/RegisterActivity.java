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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, phone, email, password, confirmpassword;
    private Button backlogin, btnsignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirm_password);
        backlogin = findViewById(R.id.btn_backtologin);
        btnsignup = findViewById(R.id.sign_up);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void register() {
        String userName = name.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString();
        String userConfirmPassword = confirmpassword.getText().toString();

        // Validate
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
        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(this, "Mật khẩu và Xác nhận không khớp.", Toast.LENGTH_LONG).show();
            return;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải >= 6 ký tự.", Toast.LENGTH_LONG).show();
            return;
        }

        // Tạo tài khoản Auth
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {

                                // 1. Cập nhật displayName cho Auth
                                UserProfileChangeRequest profileUpdates =
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName)
                                                .build();
                                user.updateProfile(profileUpdates);

                                // 2. Lưu thêm vào Realtime Database
                                DatabaseReference ref = FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(user.getUid());

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", userName);
                                userMap.put("phone", phoneNumber);
                                userMap.put("email", userEmail);
                                userMap.put("role", "user");
                                ref.setValue(userMap).addOnCompleteListener(taskDb -> {
                                    if (taskDb.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                "Đăng ký thành công.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this,
                                                "Đăng ký OK nhưng lưu DB lỗi: "
                                                        + taskDb.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            // Quay về Login
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(),
                                        "Email đã tồn tại.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Đăng ký thất bại: "
                                                + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
