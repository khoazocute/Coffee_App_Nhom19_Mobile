package com.example.coffee_app_damh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {
    //Khai báo biến là khai báo các edittext, các button và cả FirebaseAuth
    private EditText editTextEmail;
    private Button buttonContinue, buttonBackToLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password1); // gắn layout của trang quên mật khẩu

        // Ánh xạ view đúng ID trong XML
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonContinue = findViewById(R.id.buttonContinue);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        // Khởi tạo Firebase Auth để dùng các hàm
        auth = FirebaseAuth.getInstance();

        buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển về LoginActivity
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Gửi link đặt lại mật khẩu
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Vui lòng nhập email");
                    editTextEmail.requestFocus();
                    return;
                }
                //Kiểm tra mail có hợp lệ hay không
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Email không hợp lệ");
                    editTextEmail.requestFocus();
                    return;
                }

                // Hiển thị loading nếu cần (nếu bạn thêm ProgressBar)
                buttonContinue.setEnabled(false); //Tạm khóa nút tránh người dùng nhập nhiều lần
                // Gọi API Firebase gửi link reset mật khẩu
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            buttonContinue.setEnabled(true);
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Đã gửi link đặt lại mật khẩu đến email của bạn.",
                                        Toast.LENGTH_LONG).show();
                                finish(); // quay về màn hình login
                            } else {
                                String msg = task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Không thể gửi email.";
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Lỗi: " + msg,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Nút quay lại Login
        //buttonBackToLogin.setOnClickListener(v -> finish());
    }
}