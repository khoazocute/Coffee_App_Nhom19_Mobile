package com.example.coffee_app_damh;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coffee_app_damh.Activity.AdminDashboardActivity;
import com.example.coffee_app_damh.Activity.MainActivity;
import com.example.coffee_app_damh.Activity.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    //Khai báo các biến EditText và Button
    private EditText email, password;
    private Button btnlogin,btnregister;

   private TextView TvForgotPassword;
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
        TvForgotPassword=findViewById(R.id.tvForgotPassword);

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

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

    }


    private void register() {
        //Khi nhấn vào register sẽ chuyển sang giao diện đăng ký
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }

    private void login() {
        String mail, pass;
        mail = email.getText().toString();
        pass = password.getText().toString();

        if (TextUtils.isEmpty(mail)) {
            Toast.makeText(this, "Vui lòng nhập Email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập Mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Đăng nhập Auth thành công, lấy UserId (uid)
                    String userId = mAuth.getCurrentUser().getUid();

                    // === KIỂM TRA VAI TRÒ BẮT ĐẦU TỪ ĐÂY ===
                    // Truy vấn vào Realtime Database để lấy vai trò
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                    userRef.child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> roleTask) {

                            if (roleTask.isSuccessful() && roleTask.getResult().exists()) {
                                String userRole = roleTask.getResult().getValue(String.class);

                                // Điều hướng dựa trên vai trò
                                if ("admin".equals(userRole)) {
                                    // 1. Nếu là ADMIN, chuyển đến trang Admin Dashboard
                                    // (Bạn sẽ tạo Activity này ở bước sau để quản lý sản phẩm/đơn hàng)
                                    // Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                    Toast.makeText(LoginActivity.this, "Đăng nhập với quyền Admin!", Toast.LENGTH_SHORT).show();
                                    // Tạm thời, để kiểm tra, ta có thể cho admin vào MainActivity
                                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    // 2. Nếu là USER (hoặc khác admin), chuyển đến trang chính
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                // 3. Nếu không lấy được role (tài khoản cũ), cứ cho vào trang user
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            finish(); // Đóng màn hình Login sau khi điều hướng
                        }
                    });
                    // === KẾT THÚC KIỂM TRA VAI TRÒ ===

                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
