package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee_app_damh.LoginActivity
import com.example.coffee_app_damh.R
// Lớp Binding được tạo tự động từ activity_admin_dashboard.xml
import com.example.coffee_app_damh.databinding.ActivityAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminDashboardActivity : AppCompatActivity() {

    // Khai báo Binding (Bắt buộc phải có)
    private lateinit var binding: ActivityAdminDashboardBinding

    // Loại bỏ các khai báo View riêng lẻ đã bị trùng lặp: adminNameTxt, manageProductsCard, ...
    // Các biến này sẽ được truy cập qua 'binding.'

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference

    // Bắt đầu hàm onCreate() - nơi mọi thứ được khởi tạo
    override fun onCreate(savedInstanceState: Bundle?) {
        // LUÔN LUÔN gọi hàm của lớp cha
        super.onCreate(savedInstanceState)

        // 1. Khởi tạo View Binding
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        // 2. Thiết lập View gốc làm nội dung Activity
        setContentView(binding.root)
// Lấy user hiện tại
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser == null) {
            goToLogin()
            return
        }

        // Tham chiếu đến node của user hiện tại trên Firebase: /users/{userId}
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)

        // Tải và hiển thị thông tin của Admin
       // loadAdminInfo()

        // Gán sự kiện click cho các nút
        setupListeners()
    }

//    private fun loadAdminInfo() {
//        // Lấy dữ liệu một lần từ Realtime Database
//        userRef.get().addOnCompleteListener { task ->
//            if (task.isSuccessful && task.result.exists()) {
//                val name = task.result.child("name").getValue(String::class.java)
//
//                if (!name.isNullOrEmpty()) {
//                    // Truy cập TextView qua binding
//                    binding.adminName.text = name
//                } else {
//                    binding.adminName.text = mAuth.currentUser!!.email
//                }
//            } else {
//                binding.adminName.text = mAuth.currentUser!!.email
//            }
//        }
//    }

    private fun setupListeners() {
        // Truy cập CardView qua binding và thiết lập Listener

        binding.manageProductsCard.setOnClickListener {
            //Toast.makeText(this, "Chức năng Quản lý Sản phẩm sắp ra mắt!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ManageProductsActivity::class.java)
            startActivity(intent)
        }

        binding.manageOrdersCard.setOnClickListener {
            //Toast.makeText(this, "Chức năng Quản lý đơn hàng sắp ra mắt!", Toast.LENGTH_SHORT).show()
           val intent = Intent(this, ManageOrdersActivity::class.java)
            startActivity(intent)
        }

        binding.manageUsersCard.setOnClickListener {
            //Toast.makeText(this, "Chức năng Quản lý Người dùng sắp ra mắt!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ManageUsersActivity::class.java)
            startActivity(intent)
        }

        binding.manageStatsCard.setOnClickListener {
            //Toast.makeText(this, "Chức năng Báo cáo & Thống kê sắp ra mắt!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        binding.managePromotionsCard.setOnClickListener {
            // Chuyển sang Quản lý Khuyến mãi
            val intent = Intent(this, ManagePromotionsActivity::class.java)
            startActivity(intent)}

        // Xử lý nút đăng xuất
        binding.logoutBtn.setOnClickListener {
            mAuth.signOut() // Xóa phiên đăng nhập
            goToLogin()      // Quay về màn hình Login
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Xóa hết các Activity cũ để người dùng không thể nhấn "Back" quay lại sau khi đăng xuất
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}