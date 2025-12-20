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
            goToLogin()
            return
        }

        userDbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(currentUser.uid)

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
        binding.emailTxt.text = currentUser.email ?: ""

        currentUser.photoUrl?.let { uri ->
            Glide.with(this).load(uri).into(binding.profilePic)
        }

        userDbRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    binding.nameTxt.text = currentUser.email?.substringBefore("@") ?: "Người dùng"
                    binding.phoneTxt.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val nameDb  = snapshot.child("name").getValue(String::class.java)
                val phoneDb = snapshot.child("phone").getValue(String::class.java)
                val emailDb = snapshot.child("email").getValue(String::class.java)

                binding.nameTxt.text = when {
                    !nameDb.isNullOrEmpty() -> nameDb
                    !currentUser.displayName.isNullOrEmpty() -> currentUser.displayName
                    !currentUser.email.isNullOrEmpty() -> currentUser.email!!.substringBefore("@")
                    else -> "Người dùng"
                }

                if (!phoneDb.isNullOrEmpty()) {
                    binding.phoneTxt.text = phoneDb
                    binding.phoneTxt.visibility = View.VISIBLE
                } else {
                    binding.phoneTxt.visibility = View.GONE
                }

                if (!emailDb.isNullOrEmpty()) {
                    binding.emailTxt.text = emailDb
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi tải thông tin: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.nameTxt.text = currentUser.email?.substringBefore("@") ?: "Người dùng"
                binding.phoneTxt.visibility = View.GONE
            }
    }

    private fun initListeners() {
        // === SỬA LỖI NÚT BACK TẠI ĐÂY ===
        // Bắt sự kiện vào cả cụm (Container) để bấm vào icon hay chữ đều ăn
        binding.backBtnContainer.setOnClickListener {
            finish()
        }

        // === SỬA LỖI NÚT UPDATE (Bị lồng code thừa) ===
        binding.updateBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.menuHistory.setOnClickListener {
            // Chuyển sang màn hình Lịch sử đơn hàng
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        binding.menuAddresses.setOnClickListener {
            Toast.makeText(this, "Chức năng Địa chỉ sẽ được phát triển!", Toast.LENGTH_SHORT).show()
        }

        binding.menuOffers.setOnClickListener {
            Toast.makeText(this, "Chức năng Ưu đãi sẽ được phát triển!", Toast.LENGTH_SHORT).show()
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            goToLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadUserProfileData(currentUser)
        }
    }
}
