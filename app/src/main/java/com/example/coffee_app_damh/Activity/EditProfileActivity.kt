package com.example.coffee_app_damh.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee_app_damh.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)

        // 1. Tải dữ liệu hiện tại lên giao diện
        loadCurrentUserInfo()

        // 2. Xử lý nút Back
        binding.backBtn.setOnClickListener { finish() }

        // 3. Xử lý nút Lưu
        binding.saveBtn.setOnClickListener {
            if (validateInput()) {
                updateUserProfile()
            }
        }
    }

    private fun loadCurrentUserInfo() {
        val currentUser = auth.currentUser ?: return

        // Hiển thị Email (không cho sửa)
        binding.emailEdt.setText(currentUser.email)

        // Lấy dữ liệu từ Realtime Database để điền vào Name và Phone
        binding.progressBar.visibility = View.VISIBLE
        userRef.get().addOnSuccessListener { snapshot ->
            binding.progressBar.visibility = View.GONE
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""

                binding.nameEdt.setText(name)
                binding.phoneEdt.setText(phone)
            } else {
                // Nếu DB chưa có, lấy tạm display name từ Auth
                binding.nameEdt.setText(currentUser.displayName ?: "")
            }
        }.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(): Boolean {
        if (binding.nameEdt.text.isNullOrEmpty()) {
            binding.nameEdt.error = "Vui lòng nhập tên"
            return false
        }
        if (binding.phoneEdt.text.isNullOrEmpty()) {
            binding.phoneEdt.error = "Vui lòng nhập SĐT"
            return false
        }
        return true
    }

    private fun updateUserProfile() {
        val newName = binding.nameEdt.text.toString().trim()
        val newPhone = binding.phoneEdt.text.toString().trim()
        val currentUser = auth.currentUser ?: return

        binding.progressBar.visibility = View.VISIBLE
        binding.saveBtn.isEnabled = false

        // 1. Cập nhật DisplayName trong Firebase Authentication (để đồng bộ)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        currentUser.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 2. Cập nhật thông tin trong Realtime Database
                val updates = mapOf<String, Any>(
                    "name" to newName,
                    "phone" to newPhone
                )

                userRef.updateChildren(updates).addOnCompleteListener { dbTask ->
                    binding.progressBar.visibility = View.GONE
                    binding.saveBtn.isEnabled = true

                    if (dbTask.isSuccessful) {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        finish() // Đóng màn hình sửa, quay về Profile để thấy thay đổi
                    } else {
                        Toast.makeText(this, "Lỗi lưu Database: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.progressBar.visibility = View.GONE
                binding.saveBtn.isEnabled = true
                Toast.makeText(this, "Lỗi cập nhật Auth: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
