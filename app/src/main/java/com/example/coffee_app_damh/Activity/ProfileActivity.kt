// File: ProfileActivity.kt
package com.example.coffee_app_damh.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.coffee_app_damh.LoginActivity
import com.example.coffee_app_damh.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Nếu chưa đăng nhập thì đá về màn Login
            goToLogin()
            return
        }

        // Tham chiếu đến /users/{uid} trên Realtime Database
        userDbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(currentUser.uid)

        // Load thông tin user
        loadUserProfileData(currentUser)

        initListeners()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadUserProfileData(currentUser: FirebaseUser) {
        // Gán trước email từ Auth (phòng trường hợp DB không có)
        binding.emailTxt.text = currentUser.email ?: ""

        // Avatar (nếu sau này em có lưu URL avatar trong DB thì có thể override thêm)
        currentUser.photoUrl?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.profilePic)
        }

        // Gọi DB để lấy name / phone / email đã lưu khi đăng ký
        userDbRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    // Không có dữ liệu user trong DB → dùng tạm từ Auth
                    binding.nameTxt.text = currentUser.email?.substringBefore("@") ?: "Người dùng"
                    binding.phoneTxt.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val nameDb  = snapshot.child("name").getValue(String::class.java)
                val phoneDb = snapshot.child("phone").getValue(String::class.java)
                val emailDb = snapshot.child("email").getValue(String::class.java)

                // Name
                binding.nameTxt.text = when {
                    !nameDb.isNullOrEmpty() -> nameDb
                    !currentUser.displayName.isNullOrEmpty() -> currentUser.displayName
                    !currentUser.email.isNullOrEmpty() -> currentUser.email!!.substringBefore("@")
                    else -> "Người dùng"
                }

                // Phone
                if (!phoneDb.isNullOrEmpty()) {
                    binding.phoneTxt.text = phoneDb
                    binding.phoneTxt.visibility = View.VISIBLE
                } else {
                    binding.phoneTxt.visibility = View.GONE
                }

                // Email (ưu tiên DB nếu có)
                if (!emailDb.isNullOrEmpty()) {
                    binding.emailTxt.text = emailDb
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Không tải được thông tin người dùng: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                // Fallback: dùng tạm thông tin từ Auth
                binding.nameTxt.text =
                    currentUser.email?.substringBefore("@") ?: "Người dùng"
                binding.phoneTxt.visibility = View.GONE
            }
    }

    private fun initListeners() {
        // Nút back trên thanh tiêu đề
        binding.backText.setOnClickListener {
            finish()
        }

        binding.updateBtn.setOnClickListener {
            Toast.makeText(this, "Chức năng Cập nhật sẽ được phát triển!", Toast.LENGTH_SHORT).show()
        }

        binding.menuHistory.setOnClickListener {
            Toast.makeText(this, "Chức năng Lịch sử đơn hàng sẽ được phát triển!", Toast.LENGTH_SHORT).show()
        }

        binding.menuAddresses.setOnClickListener {
            Toast.makeText(this, "Chức năng Địa chỉ sẽ được phát triển!", Toast.LENGTH_SHORT).show()
        }

        binding.menuOffers.setOnClickListener {
            Toast.makeText(this, "Chức năng Ưu đãi sẽ được phát triển!", Toast.LENGTH_SHORT).show()
        }

        // Đăng xuất
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            goToLogin()
        }
    }
}
